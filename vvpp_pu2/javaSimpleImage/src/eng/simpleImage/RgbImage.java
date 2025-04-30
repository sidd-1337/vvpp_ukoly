package eng.simpleImage;

import java.awt.image.BufferedImage;

/**
 * @author Marek Vajgl
 */
public class RgbImage extends Image {

  public static RgbImage fromBufferedImage(BufferedImage img) {
    RgbImage ret = new RgbImage(img.getWidth(), img.getHeight());
    for (int i = 0; i < img.getWidth(); i++) {
      for (int j = 0; j < img.getHeight(); j++) {
        int index = ret.getIndex(i, j);

        int rgb = img.getRGB(i, j);

        int a = (rgb >> 24) & 0x0ff;
        int r = (rgb >> 16) & 0x0ff;
        int g = (rgb >> 8) & 0x0ff;
        int b = (rgb) & 0x0ff;

        ret.a[index] = a;
        ret.r[index] = r;
        ret.g[index] = g;
        ret.b[index] = b;
      }
    }
    return ret;
  }

  public static RgbImage load(String fileName) {
    return Image.load(fileName, RgbImage.class);
  }

  private final int[] a;
  private final int[] r;
  private final int[] g;
  private final int[] b;
  private final int width;
  private final int height;

  public RgbImage(int width, int height) {
    int pixelCount = width * height;
    this.a = new int[pixelCount];
    this.r = new int[pixelCount];
    this.g = new int[pixelCount];
    this.b = new int[pixelCount];
    this.height = height;
    this.width = width;
  }

  public int getA(int x, int y) {
    int index = this.getIndex(x, y);
    return this.a[index];
  }

  public int getA(int x, int y, boolean lenient) {
    if (lenient) {
      x = ensureValueInWidth(x);
      y = ensureValueInHeight(y);
    }
    return getA(x, y);
  }

  public int getB(int x, int y) {
    int index = this.getIndex(x, y);
    return this.b[index];
  }

  public int getB(int x, int y, boolean lenient) {
    if (lenient) {
      x = ensureValueInWidth(x);
      y = ensureValueInHeight(y);
    }
    return getB(x, y);
  }

  public int getG(int x, int y) {
    int index = this.getIndex(x, y);
    return this.g[index];
  }

  public int getG(int x, int y, boolean lenient) {
    if (lenient) {
      x = ensureValueInWidth(x);
      y = ensureValueInHeight(y);
    }
    return getG(x, y);
  }

  public int getHeight() {
    return height;
  }

  public int getR(int x, int y) {
    int index = this.getIndex(x, y);
    return this.r[index];
  }

  public int getR(int x, int y, boolean lenient) {
    if (lenient) {
      x = ensureValueInWidth(x);
      y = ensureValueInHeight(y);
    }
    return getR(x, y);
  }

  public int getWidth() {
    return width;
  }

  public void setB(int x, int y, int val) {
    val = ensureValueInColorRange(val);
    int index = getIndex(x, y);
    this.b[index] = val;
  }

  public void setA(int x, int y, int val) {
    val = ensureValueInColorRange(val);
    int index = getIndex(x, y);
    this.a[index] = val;
  }

  public void setG(int x, int y, int val) {
    val = ensureValueInColorRange(val);
    int index = getIndex(x, y);
    this.g[index] = val;
  }

  public void setR(int x, int y, int val) {
    val = ensureValueInColorRange(val);
    int index = getIndex(x, y);
    this.r[index] = val;
  }

  @Override
  protected BufferedImage toBufferedImage() {
    BufferedImage ret = new BufferedImage(this.width, this.height, 5);

    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        int index = this.getIndex(i, j);
        int a = this.a[index];
        int r = this.r[index];
        int g = this.g[index];
        int b = this.b[index];

        int rgb = a;
        rgb = (rgb << 8) + r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;

        ret.setRGB(i, j, rgb);
      }
    }
    return ret;
  }
}
