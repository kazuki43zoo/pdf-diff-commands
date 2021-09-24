package com.example.tools;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PdfDiffCommandsApplicationRunner implements ApplicationRunner {

  private final PdfDiffCommandsProperties properties;

  public PdfDiffCommandsApplicationRunner(PdfDiffCommandsProperties properties) {
    this.properties = properties;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (args.getSourceArgs().length == 0 || args.containsOption("h") || args.containsOption("help")) {
      System.out.println("");
      System.out.println("[Command arguments]");
      System.out.println("  --command       : diff-file");
      System.out.println("  --h (--help)    : print help");
      System.out.println("");
      System.out.println("[Configuration arguments(Optional)]");
      System.out.println("  --tools.pdf.image-dpi  : customize an image dpi when converting to image file (default: 300)");
      System.out.println("                           note that if big value specified, processing time become a too long.");
      System.out.println("  --tools.pdf.image-type : customize an image type when converting to image file (default: RGB)");
      System.out.println("  --tools.pdf.diff-color : customize a color for emphasizing the difference (default: MAGENTA)");
      System.out.println("");
      System.out.println("[Usage: diff-file]");
      System.out.println("  Checking difference for pdf content after converting to image file.");
      System.out.println("  format: --command=diff-file {files}");
      System.out.println("  e.g.) --command=diff-file src/test/resources/Book2.pdf src/test/resources/Book3.pdf");
      System.out.println("");
      return;
    }

    String command;
    if (args.containsOption("command")) {
      command = args.getOptionValues("command").stream().findFirst()
          .orElseThrow(() -> new IllegalArgumentException("'command' value is required. valid-commands:[diff-file]"));
    } else {
      throw new IllegalArgumentException("'command' is required. valid-commands:[diff-file]");
    }

    List<Runnable> errorLogDelayPrinters = new ArrayList<>();
    execute(command, args, errorLogDelayPrinters);
    errorLogDelayPrinters.forEach(Runnable::run);

  }

  private void execute(String command, ApplicationArguments args, List<Runnable> errorLogDelayPrinters) {
    switch (command) {
      case "diff-file":
        List<String> nonOptionValues = args.getNonOptionArgs();
        if (nonOptionValues.size() < 2) {
          throw new IllegalArgumentException("{files} need two files.");
        }
        DiffFileProcessor.INSTANCE.execute(nonOptionValues.get(0), nonOptionValues.get(1), errorLogDelayPrinters, properties);
        break;
      default:
        throw new UnsupportedOperationException(String.format("'%s' command not support. valid-commands:%s", command, "[diff-file]"));
    }
  }
}
