package com.example.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiffDirProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(DiffDirProcessor.class);
  static final DiffDirProcessor INSTANCE = new DiffDirProcessor();

  void execute(String dirPath1, String dirPath2, String patternString, List<Runnable> infoLogDelayPrinters, List<Runnable> warnLogDelayPrinters, List<Runnable> errorLogDelayPrinters, PdfDiffCommandsProperties properties) {

    Path dir1 = Paths.get(dirPath1);
    Path dir2 = Paths.get(dirPath2);
    if (!dir1.toFile().exists()) {
      errorLogDelayPrinters.add(() -> LOGGER.error("The first directory does not exists. dir-path[{}]", dirPath1));
    }
    if (!dir2.toFile().exists()) {
      errorLogDelayPrinters.add(() -> LOGGER.error("The second directory does not exists. dir-path[{}}]", dirPath2));
    }
    if (!errorLogDelayPrinters.isEmpty()) {
      return;
    }

    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss"));
    Pattern fileNameExtractPattern = StringUtils.hasLength(patternString) ? Pattern.compile(patternString) : null;
    try {
      Map<String, Path> dir1Files = extractPdfFiles(dir1, fileNameExtractPattern);
      Map<String, Path> dir2Files = extractPdfFiles(dir2, fileNameExtractPattern);

      Set<String> intersectionFileNames = new LinkedHashSet<>(dir1Files.keySet());
      intersectionFileNames.retainAll(dir2Files.keySet());

      Map<Path, Path> targetFiles = new LinkedHashMap<>();
      intersectionFileNames.stream().sorted().forEach(x -> targetFiles.put(dir1Files.get(x), dir2Files.get(x)));

      intersectionFileNames.forEach(dir1Files::remove);
      intersectionFileNames.forEach(dir2Files::remove);

      LOGGER.info("Start to compare pdf content that stored into directory. first-dir[{}] second-dir[{}] comparing-file-count[{}] skipping-file-count[{}]", dirPath1, dirPath2, targetFiles.size(), dir1Files.size() + dir2Files.size());

      targetFiles.forEach((x, y) -> DiffFileProcessor.INSTANCE.execute(x.toString(), y.toString(), infoLogDelayPrinters, errorLogDelayPrinters, properties, "diff-dir-report-" + timestamp + x.getParent().toString().replace(dir1.toString(), "")));

      dir1Files.values().stream().map(Path::toString).sorted()
          .forEach(x -> warnLogDelayPrinters.add(() -> LOGGER.warn("Skip to compare pdf content because file not exist in second-dir. file[{}]", x)));
      dir2Files.values().stream().map(Path::toString).sorted()
          .forEach(x -> warnLogDelayPrinters.add(() -> LOGGER.warn("Skip to compare pdf content because file not exist in first-dir. file[{}]", x)));

      if (warnLogDelayPrinters.isEmpty() && errorLogDelayPrinters.isEmpty()) {
        infoLogDelayPrinters.add(() -> LOGGER.info("The pdf content that stored into directory is all same. first-dir[{}] second-dir[{}]", dirPath1, dirPath2));
      }

      LOGGER.info("End to compare pdf content that stored into directory.");


    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  private Map<String, Path> extractPdfFiles(Path dir, Pattern fileNameExtractPattern) throws IOException {
    Map<String, Path> files = new LinkedHashMap<>();
    Map<String, Integer> sameFileNameCounters = new HashMap<>();
    Files.walk(dir)
        .filter(Files::isRegularFile)
        .filter(x -> x.toString().toLowerCase().endsWith(".pdf"))
        .sorted()
        .forEach(x -> files.put(Paths.get(x.getParent().toString().replace(dir.toString(), ""), extractFileName(x.getFileName().toString(), fileNameExtractPattern, sameFileNameCounters)).toString(), x));
    return files;
  }

  private String extractFileName(String originalFileName, Pattern extractPattern, Map<String, Integer> sameFileNameCounters) {
    String name;
    if (extractPattern == null) {
      name = originalFileName;
    } else {
      Matcher matcher = extractPattern.matcher(originalFileName);
      StringBuilder sb = new StringBuilder();
      if (matcher.matches()) {
        for (int i = 0; i < matcher.groupCount(); i++) {
          sb.append(matcher.group(i + 1));
        }
        name = sb.toString();
      } else {
        name = originalFileName;
      }
    }
    int counter = sameFileNameCounters.getOrDefault(name, 0) + 1;
    sameFileNameCounters.put(name, counter);
    return name + "_" + counter;
  }

}
