import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

public class Image {
    private final int[] a, r, g, b;
    private final int width, height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static boolean isValidImageFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        String[] validExtensions = {"png", "jpg", "jpeg"};
        String name = file.getName().toLowerCase();
        for (String ext : validExtensions) {
            if (name.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        int pixels = width * height;
        this.a = new int[pixels];
        this.r = new int[pixels];
        this.g = new int[pixels];
        this.b = new int[pixels];
    }

    public static Image load(String fileName) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(fileName));
            if (img == null) throw new IOException("Unsupported image format.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + e.getMessage());
        }
        Image out = new Image(img.getWidth(), img.getHeight());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int idx = out.getIndex(x, y);
                int rgb = img.getRGB(x, y);
                out.a[idx] = (rgb >> 24) & 0xFF;
                out.r[idx] = (rgb >> 16) & 0xFF;
                out.g[idx] = (rgb >> 8) & 0xFF;
                out.b[idx] = rgb & 0xFF;
            }
        }
        return out;
    }

    public void save(String fileName) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = getIndex(x, y);
                int rgba = (a[idx] << 24) | (r[idx] << 16) | (g[idx] << 8) | b[idx];
                img.setRGB(x, y, rgba);
            }
        }
        try {
            ImageIO.write(img, "png", new File(fileName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + e.getMessage());
        }
    }

    private int getIndex(int x, int y) {
        return y * width + x;
    }

    private int ensureBound(int val, int max) {
        return Math.max(0, Math.min(val, max - 1));
    }

    /**
     * Multi-threaded blur: image split into chunks, each thread processes a block of rows.
     * Progress printed every 1%.
     */
    public Image applyBlur(int blurRadius, int threadCount) {
        Image output = new Image(width, height);
        ExecutorService exec = Executors.newFixedThreadPool(threadCount);
        AtomicInteger rowsDone = new AtomicInteger(0);
        int totalRows = height;
        int chunkSize = (totalRows + threadCount - 1) / threadCount;

        for (int t = 0; t < threadCount; t++) {
            final int yStart = t * chunkSize;
            final int yEnd   = Math.min(totalRows, yStart + chunkSize);

            exec.submit(() -> {
                for (int y = yStart; y < yEnd; y++) {
                    int baseY = y * width;
                    int y0 = y - blurRadius < 0 ? 0 : y - blurRadius;
                    int y1 = y + blurRadius >= height ? height - 1 : y + blurRadius;

                    for (int x = 0; x < width; x++) {
                        int x0 = x - blurRadius < 0 ? 0 : x - blurRadius;
                        int x1 = x + blurRadius >= width ? width - 1 : x + blurRadius;
                        int aSum = 0, rSum = 0, gSum = 0, bSum = 0, count = 0;

                        for (int yy = y0; yy <= y1; yy++) {
                            int rowBase = yy * width;
                            for (int xx = x0; xx <= x1; xx++) {
                                int idx = rowBase + xx;
                                aSum += a[idx];
                                rSum += r[idx];
                                gSum += g[idx];
                                bSum += b[idx];
                                count++;
                            }
                        }
                        int outIdx = baseY + x;
                        output.a[outIdx] = aSum / count;
                        output.r[outIdx] = rSum / count;
                        output.g[outIdx] = gSum / count;
                        output.b[outIdx] = bSum / count;
                    }

                    int done = rowsDone.incrementAndGet();
                    if (done % Math.max(1, totalRows / 100) == 0) {
                        System.out.printf("Zpracováno: %d%%%n", done * 100 / totalRows);
                    }
                }
            });
        }

        exec.shutdown();
        try {
            exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return output;
    }
}