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

  void execute(String dirPath1, String dirPath2, String patternString, List<Runnable> errorLogDelayPrinters, PdfDiffCommandsProperties properties) {

    LOGGER.info("Start to compare pdf content that stored into directory. first-dir[{}] second-dir[{}]", dirPath1, dirPath2);

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
      Map<String, Path> dir1Files = new LinkedHashMap<>();
      Files.walk(dir1)
          .filter(Files::isRegularFile)
          .forEach(x -> dir1Files.put(Paths.get(x.getParent().toString().replace(dir1.toString(), ""), extractFileName(x.getFileName().toString(), fileNameExtractPattern)).toString(), x));
      Map<String, Path> dir2Files = new LinkedHashMap<>();
      Files.walk(dir2)
          .filter(Files::isRegularFile)
          .forEach(x -> dir2Files.put(Paths.get(x.getParent().toString().replace(dir2.toString(), ""), extractFileName(x.getFileName().toString(), fileNameExtractPattern)).toString(), x));

      Set<String> intersectionFileNames = new LinkedHashSet<>(dir1Files.keySet());
      intersectionFileNames.retainAll(dir2Files.keySet());

      Map<Path, Path> targetFiles = new LinkedHashMap<>();
      intersectionFileNames.stream().sorted().forEach(x -> targetFiles.put(dir1Files.get(x), dir2Files.get(x)));

      intersectionFileNames.forEach(dir1Files::remove);
      intersectionFileNames.forEach(dir2Files::remove);

      targetFiles.forEach((x, y) -> DiffFileProcessor.INSTANCE.execute(x.toString(), y.toString(), errorLogDelayPrinters, properties, "diff-dir-report-" + timestamp + x.getParent().toString().replace(dir1.toString(), "")));

      dir1Files.values().stream().map(Path::toString).sorted()
          .forEach(x -> LOGGER.warn("Skip to compare pdf content because file not exist in second-dir. file[{}]", x));
      dir2Files.values().stream().map(Path::toString).sorted()
          .forEach(x -> LOGGER.warn("Skip to compare pdf content because file not exist in first-dir. file[{}]", x));

      if (dir1Files.isEmpty() && dir2Files.isEmpty() && errorLogDelayPrinters.isEmpty()) {
        LOGGER.info("The pdf content that stored into directory is all same. first-dir[{}] second-dir[{}]", dirPath1, dirPath2);
      }

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

  }

  private String extractFileName(String originalFileName, Pattern extractPattern) {
    if (extractPattern == null) {
      return originalFileName;
    }
    Matcher matcher = extractPattern.matcher(originalFileName);
    StringBuilder sb = new StringBuilder();
    if (matcher.matches()) {
      for (int i = 0; i < matcher.groupCount(); i++) {
        sb.append(matcher.group(i + 1));
      }
      return sb.toString();
    } else {
      return originalFileName;
    }
  }

}
