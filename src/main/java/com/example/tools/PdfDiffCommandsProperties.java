package com.example.tools;

import org.apache.pdfbox.rendering.ImageType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(prefix = "tools.pdf")
public class PdfDiffCommandsProperties {
  private float imageDpi = 300;
  private ImageType imageType = ImageType.RGB;
  private String diffColor = "MAGENTA";
  private final List<String> ignoreRanges = new ArrayList<>();

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

  public List<String> getIgnoreRanges() {
    return ignoreRanges;
  }

  public List<IgnoreRange> toIgnoreRanges() {
    return ignoreRanges.stream().map(x -> {
      String[] values = x.split("/");
      IgnoreRange range = new IgnoreRange();
      range.page = StringUtils.hasLength(values[0]) ? Integer.parseInt(values[0]) : null;
      String[] startPositions = values[1].split(":");
      range.startX = Integer.parseInt(startPositions[0]);
      range.startY = Integer.parseInt(startPositions[1]);
      String[] endPositions = values[2].split(":");
      range.endX = Integer.parseInt(endPositions[0]);
      range.endY = Integer.parseInt(endPositions[1]);
      return range;
    }).collect(Collectors.toList());
  }


  public static class IgnoreRange {
    private Integer page;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public Integer getPage() {
      return page;
    }

    public int getStartX() {
      return startX;
    }

    public int getStartY() {
      return startY;
    }

    public int getEndX() {
      return endX;
    }

    public int getEndY() {
      return endY;
    }

    public boolean contains(int page, int x, int y) {
      return (this.page == null || page == this.page)
          && (x >= this.startX && y >= this.startY && x <= this.endX && y <= this.endY);
    }

  }

}
