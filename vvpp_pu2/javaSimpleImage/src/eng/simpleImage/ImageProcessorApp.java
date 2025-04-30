package eng.simpleImage;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ImageProcessorApp {
    
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            
            System.out.print("Enter source image path: ");
            String inputImagePath = scanner.nextLine().trim();
            
            System.out.print("Enter pattern image path: ");
            String patternImagePath = scanner.nextLine().trim();
            
            System.out.print("Enter output file path: ");
            String outputFilePath = scanner.nextLine().trim();
            
            ParallelImageProcessor processor = new ParallelImageProcessor();
            
            try {
                int[] dimensions = processor.getImageDimensions(inputImagePath, patternImagePath);
                System.out.println("Source image: " + dimensions[0] + "x" + dimensions[1]);
                System.out.println("Pattern image: " + dimensions[2] + "x" + dimensions[3]);
            } catch (Exception e) {
                System.err.println("Error getting image dimensions: " + e.getMessage());
                return;
            }
            
            System.out.println("Processing images...");
            long startTime = System.currentTimeMillis();
            
            List<ParallelImageProcessor.ResultEntry> results;
            try {
                results = processor.findBestMatches(inputImagePath, patternImagePath);
            } catch (Exception e) {
                System.err.println("Error processing images: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;
            
            try {
                processor.writeResultsToFile(results, processingTime, outputFilePath);
            } catch (IOException e) {
                System.err.println("Error writing results: " + e.getMessage());
            }
            
            System.out.println("Processing completed in " + processingTime + " ms");
            System.out.println("Results written to: " + outputFilePath);
            
            if (results.size() > 0) {
                System.out.println("\nTop " + Math.min(10, results.size()) + " Results:");
                for (int i = 0; i < results.size(); i++) {
                    System.out.println((i + 1) + ". " + results.get(i));
                }
            } else {
                System.out.println("\nNo valid positions found. Make sure the pattern image is smaller than the source image.");
            }
            
            System.out.println("\nPress Enter to exit...");
            System.in.read();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 