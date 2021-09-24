package com.example.tools;

import org.apache.pdfbox.rendering.ImageType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tools.pdf")
public class PdfDiffCommandsProperties {
  private float imageDpi = 300;
  private ImageType imageType = ImageType.RGB;
  private String diffColor = "MAGENTA";

  public float getImageDpi() {
    return imageDpi;
  }

  public void setImageDpi(float imageDpi) {
    this.imageDpi = imageDpi;
  }

  public ImageType getImageType() {
    return imageType;
  }

  public void setImageType(ImageType imageType) {
    this.imageType = imageType;
  }

  public String getDiffColor() {
    return diffColor;
  }

  public void setDiffColor(String diffColor) {
    this.diffColor = diffColor;
  }
}
