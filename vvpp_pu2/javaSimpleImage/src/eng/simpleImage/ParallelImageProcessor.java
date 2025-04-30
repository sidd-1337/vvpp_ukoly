package eng.simpleImage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelImageProcessor {

    public static class ResultEntry implements Comparable<ResultEntry> {
        private int x, y;
        private double metric;

        public ResultEntry(int x, int y, double metric) {
            this.x = x;
            this.y = y;
            this.metric = metric;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public double getMetric() {
            return metric;
        }

        @Override
        public int compareTo(ResultEntry other) {
            return Double.compare(this.metric, other.metric);
        }

        @Override
        public String toString() {
            return String.format("Position [%d;%d] - Metric: %.6f", x, y, metric);
        }
    }

    private static class Worker implements Runnable {
        private final GrayscaleImage sourceImg;
        private final GrayscaleImage patternImg;
        private final int startY;
        private final int endY;
        private final List<ResultEntry> resultsList;

        public Worker(GrayscaleImage sourceImg, GrayscaleImage patternImg,
                      int startY, int endY, List<ResultEntry> resultsList) {
            this.sourceImg = sourceImg;
            this.patternImg = patternImg;
            this.startY = startY;
            this.endY = endY;
            this.resultsList = resultsList;
        }

        @Override
        public void run() {
            int patternWidth = patternImg.getWidth();
            int patternHeight = patternImg.getHeight();
            int sourceWidth = sourceImg.getWidth();
            int sourceHeight = sourceImg.getHeight();
            int patternPixels = patternWidth * patternHeight;

            for (int y = startY; y <= endY; y++) {
                if (y + patternHeight > sourceHeight) {
                    continue;
                }

                for (int x = 0; x < sourceWidth; x++) {
                    if (x + patternWidth > sourceWidth) {
                        continue;
                    }

                    double sumSquaredDiff = 0;
                    for (int py = 0; py < patternHeight; py++) {
                        for (int px = 0; px < patternWidth; px++) {
                            int sourceValue = sourceImg.getColor(x + px, y + py);
                            int patternValue = patternImg.getColor(px, py);
                            int diff = sourceValue - patternValue;
                            sumSquaredDiff += diff * diff;
                        }
                    }

                    double metric = sumSquaredDiff / patternPixels;

                    synchronized (resultsList) {
                        resultsList.add(new ResultEntry(x, y, metric));
                    }
                }
            }
        }
    }

    public List<ResultEntry> findBestMatches(String sourceImagePath, String patternImagePath) 
            throws IOException, InterruptedException, ImageProcessingException {
        GrayscaleImage sourceImage = GrayscaleImage.load(sourceImagePath);
        GrayscaleImage patternImage = GrayscaleImage.load(patternImagePath);
        
        return findBestMatches(sourceImage, patternImage);
    }
    
    public List<ResultEntry> findBestMatches(GrayscaleImage sourceImage, GrayscaleImage patternImage) 
            throws InterruptedException {
        List<ResultEntry> results = Collections.synchronizedList(new ArrayList<>());
        
        int numProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
        
        int sourceHeight = sourceImage.getHeight();
        int rowsPerWorker = Math.max(1, sourceHeight / numProcessors);
        
        for (int i = 0; i < numProcessors; i++) {
            int startY = i * rowsPerWorker;
            int endY = (i == numProcessors - 1) ? sourceHeight - 1 : (i + 1) * rowsPerWorker - 1;
            
            executor.submit(new Worker(
                sourceImage, patternImage, startY, endY, results));
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        
        Collections.sort(results);
        
        return results.size() > 0 ? 
                new ArrayList<>(results.subList(0, Math.min(10, results.size()))) : 
                new ArrayList<>();
    }
    
    public int[] getImageDimensions(String sourceImagePath, String patternImagePath) 
            throws IOException, ImageProcessingException {
        GrayscaleImage sourceImage = GrayscaleImage.load(sourceImagePath);
        GrayscaleImage patternImage = GrayscaleImage.load(patternImagePath);
        
        return new int[] {
            sourceImage.getWidth(),
            sourceImage.getHeight(),
            patternImage.getWidth(),
            patternImage.getHeight()
        };
    }
    
    public void writeResultsToFile(List<ResultEntry> results, long processingTime, String outputFilePath) 
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            writer.println("Processing time: " + processingTime + " ms");
            writer.println("\nTop 10 Results (lowest metrics):");
            
            for (int i = 0; i < results.size(); i++) {
                writer.println((i + 1) + ". " + results.get(i));
            }
        }
    }
} 