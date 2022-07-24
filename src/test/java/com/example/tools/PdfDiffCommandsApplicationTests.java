package com.example.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

class PdfDiffCommandsApplicationTests {

  PdfDiffCommandsProperties properties = new PdfDiffCommandsProperties();
  private final PdfDiffCommandsApplicationRunner runner = new PdfDiffCommandsApplicationRunner(properties);

  @Test
  void diffDir() {
    String[] args = {"--command=diff-dir", "--file-name-pattern=(Book)(.).*(\\.pdf)", "src/test/resources/pattern2/actual", "src/test/resources/pattern2/expected"};
    runner.run(new DefaultApplicationArguments(args));
  }

}
