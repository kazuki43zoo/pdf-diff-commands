package com.example.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PdfDiffCommandsApplication {

  public static void main(String[] args) {
    System.exit(SpringApplication.exit(SpringApplication.run(PdfDiffCommandsApplication.class, args)));
  }

}
