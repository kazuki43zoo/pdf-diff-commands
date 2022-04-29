package com.example.tools;

import org.apache.pdfbox.rendering.ImageType;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;

@RestController
public class PdfDiffCommandsController {

  private final PdfDiffCommandsProperties properties;

  PdfDiffCommandsController(PdfDiffCommandsProperties properties) {
    this.properties = properties;
  }

  @PostMapping("/diff")
  String diff(@RequestBody String command) {
    PdfDiffCommandsProperties requestProperties = new PdfDiffCommandsProperties();
    BeanUtils.copyProperties(properties, requestProperties);
    ApplicationArguments arguments = new DefaultApplicationArguments(command.split(" "));
    if (arguments.containsOption("tools.pdf.image-dpi")) {
      requestProperties.setImageDpi(Float.parseFloat(arguments.getOptionValues("tools.pdf.image-dpi").get(0)));
    }
    if (arguments.containsOption("tools.pdf.image-type")) {
      requestProperties.setImageType(ImageType.valueOf(arguments.getOptionValues("tools.pdf.image-type").get(0)));
    }
    if (arguments.containsOption("tools.pdf.diff-color")) {
      requestProperties.setDiffColor(arguments.getOptionValues("tools.pdf.diff-color").get(0));
    }
    if (arguments.containsOption("tools.pdf.output-dir")) {
      requestProperties.setOutputDir(Paths.get(arguments.getOptionValues("tools.pdf.output-dir").get(0)));
    }
    if (arguments.containsOption("tools.pdf.ignore-ranges")) {
      arguments.getOptionValues("tools.pdf.ignore-ranges")
          .forEach(x -> requestProperties.getIgnoreRanges().addAll(StringUtils.commaDelimitedListToSet(x)));
    }
    PdfDiffCommandsApplicationRunner runner = new PdfDiffCommandsApplicationRunner(requestProperties);
    runner.run(arguments);
    return String.valueOf(runner.getExitCode());
  }

}
