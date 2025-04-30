package eng.simpleImage;

import java.awt.image.BufferedImage;

public class GrayscaleImage extends Image {

  public static GrayscaleImage fromBufferedImage(BufferedImage img) {
    GrayscaleImage ret = new GrayscaleImage(img.getWidth(), img.getHeight());
    for (int i = 0; i < img.getWidth(); i++) {
      for (int j = 0; j < img.getHeight(); j++) {
        int index = ret.getIndex(i, j);

        int rgb = img.getRGB(i, j);

        int r = (rgb >> 16) & 0x0ff;
        int g = (rgb >> 8) & 0x0ff;
        int b = (rgb) & 0x0ff;

        ret.c[index] = (int) ((r + g + b) / 3d);
      }
    }
    return ret;
  }

  public static GrayscaleImage load(String fileName) {
    return Image.load(fileName, GrayscaleImage.class);
  }

  private final int[] c;
  private final int width;
  private final int height;

  public GrayscaleImage(int width, int height) {
    int pixelCount = width * height;
    this.c = new int[pixelCount];
    this.height = height;
    this.width = width;
  }

  public int getColor(int x, int y) {
    int index = this.getIndex(x, y);
    return this.c[index];
  }

  public int getColor(int x, int y, boolean lenient) {
    if (lenient) {
      x = ensureValueInWidth(x);
      y = ensureValueInHeight(y);
    }
    return getColor(x, y);
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int getWidth() {
    return width;
  }

  public void setColor(int x, int y, int val) {
    val = ensureValueInColorRange(val);
    int index = getIndex(x, y);
    this.c[index] = val;
  }

  @Override
  protected BufferedImage toBufferedImage() {
    BufferedImage ret = new BufferedImage(this.width, this.height, 5);

    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        int index = this.getIndex(i, j);
        int c = this.c[index];
        int a = 255;

        int rgb = c;
        rgb = (rgb << 8) + c;
        rgb = (rgb << 8) + c;
        rgb = (rgb << 8) + c;

        ret.setRGB(i, j, rgb);
      }
    }
    return ret;
  }

}
