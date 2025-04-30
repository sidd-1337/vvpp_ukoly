package eng.simpleImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Image {

  public static <T> T load(String fileName, Class<? extends T> imageType) {
    BufferedImage img = null;
    try {
      img = ImageIO.read(new File(fileName));
    } catch (IOException e) {
      throw new ImageProcessingException("Failed to load image from file " + fileName + ".", e);
    }

    Method method;
    try {
      method = imageType.getMethod("fromBufferedImage", new Class[]{BufferedImage.class});
    } catch (NoSuchMethodException e) {
      throw new ImageProcessingException("Failed to find method 'fromBufferedImage' in type '" + imageType.getName() + "'.", e);
    }

    T ret;
    try {
      ret = (T) method.invoke(null, new Object[]{img});
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new ImageProcessingException("Failed to convert BufferedImage into '" + imageType + "'.", e);
    }

    return ret;
  }

  protected abstract int getWidth();

  protected abstract int getHeight();

  protected abstract BufferedImage toBufferedImage();

  public void save(String fileName) {
    BufferedImage img = this.toBufferedImage();

    try {
      // retrieve image
      File outputfile = new File(fileName);
      ImageIO.write(img, "png", outputfile);
    } catch (IOException ex) {
      throw new ImageProcessingException("Failed to save image to file " + fileName + ".", ex);
    }
  }

  protected int ensureValueInWidth(int x) {
    if (x < 0)
      x = 0;
    if (x >= this.getWidth())
      x = this.getWidth() - 1;
    return x;
  }

  protected int ensureValueInHeight(int y) {
    if (y < 0)
      y = 0;
    if (y >= this.getHeight())
      y = this.getHeight() - 1;
    return y;
  }

  protected int ensureValueInColorRange(int val) {
    if (val < 0)
      val = 0;
    if (val > 255)
      val = 255;
    return val;
  }

  protected int getIndex(int x, int y) {
    int ret = x * getHeight() + y;
    return ret;
  }
}
