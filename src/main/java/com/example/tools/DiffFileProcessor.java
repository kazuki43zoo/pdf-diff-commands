package com.example.tools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class DiffFileProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(DiffFileProcessor.class);
  static final DiffFileProcessor INSTANCE = new DiffFileProcessor();

  void execute(String filePath1, String filePath2, List<Runnable> errorLogDelayPrinters, PdfDiffCommandsProperties properties, String diffReportDir) {

    LOGGER.info("Start to compare pdf content. first-file[{}] second-file[{}]", filePath1, filePath2);

    int errorSizeAtBegin = errorLogDelayPrinters.size();
    Path file1 = Paths.get(filePath1);
    Path file2 = Paths.get(filePath2);
    if (!file1.toFile().exists()) {
      errorLogDelayPrinters.add(() -> LOGGER.error("The first file does not exists. file-path[{}]", filePath1));
    }
    if (!file2.toFile().exists()) {
      errorLogDelayPrinters.add(() -> LOGGER.error("The second file does not exists. file-path[{}}]", filePath2));
    }
    if (errorLogDelayPrinters.size() != errorSizeAtBegin) {
      return;
    }

    try (InputStream inputStream1 = Files.newInputStream(file1); InputStream inputStream2 = Files.newInputStream(file2);
         PDDocument document1 = PDDocument.load(inputStream1); PDDocument document2 = PDDocument.load(inputStream2)) {

      if (document1.getNumberOfPages() != document2.getNumberOfPages()) {
        errorLogDelayPrinters.add(() -> LOGGER.error("The page size is different. first-file[{}] second-file[{}]", document1.getNumberOfPages(), document2.getNumberOfPages()));
        return;
      }

      Color diffColor = (Color) Optional.ofNullable(ReflectionUtils.findField(Color.class, properties.getDiffColor()))
          .orElseThrow(() -> new IllegalArgumentException("The unsupported color was detected in diff-color. color[" + properties.getDiffColor() + "]")).get(null);

      PDFRenderer renderer1 = new PDFRenderer(document1);
      PDFRenderer renderer2 = new PDFRenderer(document2);

      List<PdfDiffCommandsProperties.IgnoreRange> ignoreRanges = properties.toIgnoreRanges();

      for (int i = 0; i < document1.getNumberOfPages(); i++) {
        int pagePosition = i + 1;
        BufferedImage image1 = renderer1.renderImageWithDPI(i, properties.getImageDpi(), properties.getImageType());
        BufferedImage image2 = renderer2.renderImageWithDPI(i, properties.getImageDpi(), properties.getImageType());

        int errorSizeAtBeginForPageProcessing = errorLogDelayPrinters.size();
        if (image1.getWidth() != image2.getWidth()) {
          errorLogDelayPrinters.add(() -> LOGGER.error("The page width is different. page[{}] first-file[{}] second-file[{}]", pagePosition, image1.getWidth(), image2.getWidth()));
        }
        if (image1.getHeight() != image2.getHeight()) {
          errorLogDelayPrinters.add(() -> LOGGER.error("The page height is different. page[{}] first-file[{}] second-file[{}]", pagePosition, image1.getHeight(), image2.getHeight()));
        }
        if (errorLogDelayPrinters.size() != errorSizeAtBeginForPageProcessing) {
          continue;
        }

        Path diffFile = Files.createTempFile(file1.getFileName().toString().replace(".pdf", "") + "-diff-page-" + pagePosition + "-", ".png");
        boolean matched = true;
        try (OutputStream diffOutStream = Files.newOutputStream(diffFile)) {
          for (int x = 0; x < image1.getWidth(); x++) {
            for (int y = 0; y < image1.getHeight(); y++) {
              int color1 = image1.getRGB(x, y);
              int color2 = image2.getRGB(x, y);
              if (color1 != color2) {
                int xPosition = x;
                int yPosition = y;
                if (ignoreRanges.stream().noneMatch(ignoreRange -> ignoreRange.contains(pagePosition, xPosition, yPosition))) {
                  matched = false;
                  image1.setRGB(x, y, diffColor.getRGB());
                }
              }
            }
          }
          if (!matched) {
            ImageIO.write(image1, "png", diffOutStream);
          }
        }
        if (matched) {
          Files.delete(diffFile);
        } else {
          Path diffReportFile = Paths.get("target", diffReportDir, diffFile.getFileName().toString());
          errorLogDelayPrinters.add(() -> LOGGER.error("The page content is different. page[{}] diff-report-file[{}] first-file[{}] second-file[{}]", pagePosition, diffReportFile, filePath1, filePath2));
          Files.createDirectories(diffReportFile.getParent());
          Files.move(diffFile, diffReportFile);
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }

    if (errorLogDelayPrinters.size() == errorSizeAtBegin) {
      LOGGER.info("The pdf content is same. first-file[{}] second-file[{}]", filePath1, filePath2);
    }

  }

}
