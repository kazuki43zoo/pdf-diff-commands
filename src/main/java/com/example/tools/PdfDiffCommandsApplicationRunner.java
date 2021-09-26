package com.example.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PdfDiffCommandsApplicationRunner implements ApplicationRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(PdfDiffCommandsApplicationRunner.class);

  private final PdfDiffCommandsProperties properties;
  private final ExitCodeManager exitCodeManager;

  public PdfDiffCommandsApplicationRunner(PdfDiffCommandsProperties properties, ExitCodeManager exitCodeManager) {
    this.properties = properties;
    this.exitCodeManager = exitCodeManager;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (args.getSourceArgs().length == 0 || args.containsOption("h") || args.containsOption("help")) {
      System.out.println();
      System.out.println("[Command arguments]");
      System.out.println("  --command");
      System.out.println("       diff-file, diff-dir");
      System.out.println("  --h (--help)");
      System.out.println("       print help");
      System.out.println();
      System.out.println("[Exit Code]");
      System.out.println("  0 : There is no difference");
      System.out.println("  1 : There is no difference but there are skipped files");
      System.out.println("  2 : There is difference");
      System.out.println();
      System.out.println("[Configuration arguments(Optional)]");
      System.out.println("  --tools.pdf.image-dpi");
      System.out.println("       customize an image dpi when converting to image file (default: 300)");
      System.out.println("       note that if big value specified, processing time become a too long.");
      System.out.println("  --tools.pdf.image-type");
      System.out.println("       customize an image type when converting to image file (default: RGB)");
      System.out.println("  --tools.pdf.diff-color");
      System.out.println("       customize a color for emphasizing the difference (default: MAGENTA)");
      System.out.println("  --tools.pdf.output-dir");
      System.out.println("       customize a output directory of reporting file (default: ./target)");
      System.out.println("  --tools.pdf.ignore-ranges");
      System.out.println("       specify pixel range to be ignored (default: N/A)");
      System.out.println("       if you want to specify multiple range, please separate configuration using ','.");
      System.out.println("       value format: {target page}/{start width position(pix)}:{start height position(pix)}/{end width position(pix)}:{end height position(pix)}");
      System.out.println("       e.g.) --tools.pdf.ignore-ranges=1/850:250/890:390");
      System.out.println("                 ignore range(width:850-890pix height:250-290pix) on first page");
      System.out.println("       e.g.) --tools.pdf.ignore-ranges=1/850:250/890:390,1/1050:250/1090:390");
      System.out.println("                 ignore ranges(width:850-890pix height:250-290pix, width:1050-1090pix height:250-290pix) on first page");
      System.out.println();
      System.out.println("[Usage: diff-file]");
      System.out.println("  checking difference for pdf content after converting to image file.");
      System.out.println("  format: --command=diff-file {files}");
      System.out.println("  e.g.) --command=diff-file src/test/resources/Book2.pdf src/test/resources/Book3.pdf");
      System.out.println();
      System.out.println("[Usage: diff-dir]");
      System.out.println("  checking difference for pdf content that stored into a specified directory after converting to image file.");
      System.out.println("  format: --command=diff-dir (--file-name-pattern='{file name extracting regex pattern}') {directories}");
      System.out.println("  e.g.) --command=diff-dir src/test/resources/pattern1/actual src/test/resources/pattern1/expected");
      System.out.println("  e.g.) --command=diff-dir --file-name-pattern='(Book)(.).*(\\.pdf)' src/test/resources/pattern2/actual src/test/resources/pattern2/expected");
      System.out.println();
      return;
    }

    String command;
    if (args.containsOption("command")) {
      command = args.getOptionValues("command").stream().findFirst()
          .orElseThrow(() -> new IllegalArgumentException("'command' value is required. valid-commands:[diff-file]"));
    } else {
      throw new IllegalArgumentException("'command' is required. valid-commands:[diff-file]");
    }

    List<Runnable> infoLogDelayPrinters = new ArrayList<>();
    List<Runnable> warnLogDelayPrinters = new ArrayList<>();
    List<Runnable> errorLogDelayPrinters = new ArrayList<>();
    try {
      LOGGER.info("----- Start comparing -------");
      execute(command, args, infoLogDelayPrinters, warnLogDelayPrinters, errorLogDelayPrinters);
    } finally {
      LOGGER.info("----- End comparing -------");
      LOGGER.info("----- Start reporting -------");
      infoLogDelayPrinters.forEach(Runnable::run);
      warnLogDelayPrinters.forEach(Runnable::run);
      errorLogDelayPrinters.forEach(Runnable::run);
      LOGGER.info("----- End reporting -------");
    }
    if (!warnLogDelayPrinters.isEmpty()) {
      exitCodeManager.registerExitCode(1);
    }
    if (!errorLogDelayPrinters.isEmpty()) {
      exitCodeManager.registerExitCode(2);
    }

  }

  private void execute(String command, ApplicationArguments args, List<Runnable> infoLogDelayPrinters, List<Runnable> warnLogDelayPrinters, List<Runnable> errorLogDelayPrinters) {
    List<String> nonOptionValues = args.getNonOptionArgs();
    switch (command) {
      case "diff-file":
        if (nonOptionValues.size() < 2) {
          throw new IllegalArgumentException("{files} need two files.");
        }
        DiffFileProcessor.INSTANCE.execute(nonOptionValues.get(0), nonOptionValues.get(1), infoLogDelayPrinters, errorLogDelayPrinters, properties, "diff-report");
        break;
      case "diff-dir":
        if (nonOptionValues.size() < 2) {
          throw new IllegalArgumentException("{directories} need two directories.");
        }
        String pattern = args.containsOption("file-name-pattern") ?
            args.getOptionValues("file-name-pattern").stream().findFirst().orElse(null) :
            null;
        DiffDirProcessor.INSTANCE.execute(nonOptionValues.get(0), nonOptionValues.get(1), pattern, infoLogDelayPrinters, warnLogDelayPrinters, errorLogDelayPrinters, properties);
        break;
      default:
        throw new UnsupportedOperationException(String.format("'%s' command not support. valid-commands:%s", command, "[diff-file, diff-dir]"));
    }
  }

  @Component
  static class ExitCodeManager implements ExitCodeGenerator {

    private int exitCode;

    public void registerExitCode(int exitCode) {
      this.exitCode = exitCode;
    }

    @Override
    public int getExitCode() {
      return exitCode;
    }

  }

}
