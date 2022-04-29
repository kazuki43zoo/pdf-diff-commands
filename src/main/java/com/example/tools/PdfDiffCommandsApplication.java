package com.example.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PdfDiffCommandsApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(PdfDiffCommandsApplication.class, args);
    if (! (context instanceof WebApplicationContext)) {
      System.exit(SpringApplication.exit(context));
    }
  }

}
