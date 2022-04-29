package com.example.tools;

import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PdfDiffCommandsController {

  private final PdfDiffCommandsApplicationRunner runner;

  PdfDiffCommandsController(PdfDiffCommandsApplicationRunner runner) {
    this.runner = runner;
  }

  @PostMapping("/diff")
  String diff(@RequestBody String command) {
    runner.run(new DefaultApplicationArguments(command.split(" ")));
    return String.valueOf(runner.getExitCode());
  }

}
