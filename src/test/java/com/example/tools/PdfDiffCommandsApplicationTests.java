package com.example.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@SpringBootTest
class PdfDiffCommandsApplicationTests {

  @Test
  void contextLoads() {
    Pattern extractPattern = Pattern.compile("(Book)(.).*(.pdf)");
    String fileName = "Book1_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")) + "_" + UUID.randomUUID().toString().replace("-", "") + ".pdf";
//    String fileName = "Book.pdf";
//    String fileName = "Book.csv";
//    String fileName = "Book.pdf";
    Matcher m = extractPattern.matcher(fileName);
    StringBuilder sb = new StringBuilder();
    if (m.matches()) {
      for (int i = 0; i < m.groupCount(); i++) {
        sb.append(m.group(i + 1));
      }
    } else {
      sb.append(fileName);

    }
    System.out.println(sb.toString());
  }

}
