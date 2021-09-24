# pdf-diff-commands

Diff display utilities for pdf file.

## Features

Support following features.

* Diff two files.

## How to run

### Using Spring Boot Maven Plugin

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=""
```

```
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------------< com.example:pdf-diff-commands >--------------------
[INFO] Building pdf-diff-commands 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] >>> spring-boot-maven-plugin:2.5.5:run (default-cli) > test-compile @ pdf-diff-commands >>>
[INFO] 
[INFO] --- maven-resources-plugin:3.2.0:resources (default-resources) @ pdf-diff-commands ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'UTF-8' encoding to copy filtered properties files.
[INFO] Copying 1 resource
[INFO] Copying 0 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ pdf-diff-commands ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:3.2.0:testResources (default-testResources) @ pdf-diff-commands ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Using 'UTF-8' encoding to copy filtered properties files.
[INFO] Copying 3 resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.1:testCompile (default-testCompile) @ pdf-diff-commands ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] <<< spring-boot-maven-plugin:2.5.5:run (default-cli) < test-compile @ pdf-diff-commands <<<
[INFO] 
[INFO] 
[INFO] --- spring-boot-maven-plugin:2.5.5:run (default-cli) @ pdf-diff-commands ---
[INFO] Attaching agents: []

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.5)

2021-09-24 11:33:58.484  INFO 12743 --- [           main] c.e.tools.PdfDiffCommandsApplication     : Starting PdfDiffCommandsApplication using Java 11.0.1 on shimizuakanoMBP.toshima.ne.jp with PID 12743 (/Users/xxx/git-pub/pdf-diff-commands/target/classes started by xxx in /Users/xxx/git-pub/pdf-diff-commands)
2021-09-24 11:33:58.487  INFO 12743 --- [           main] c.e.tools.PdfDiffCommandsApplication     : No active profile set, falling back to default profiles: default
2021-09-24 11:33:58.959  INFO 12743 --- [           main] c.e.tools.PdfDiffCommandsApplication     : Started PdfDiffCommandsApplication in 0.825 seconds (JVM running for 1.206)

[Command arguments]
  --command       : diff-file
  --h (--help)    : print help

[Configuration arguments(Optional)]
  --tools.pdf.image-dpi  : customize an image dpi when converting to image file (default: 300)
                           note that if big value specified, processing time become a too long.
  --tools.pdf.image-type : customize an image type when converting to image file (default: RGB)
  --tools.pdf.diff-color : customize a color for emphasizing the difference (default: MAGENTA)

[Usage: diff-file]
  Checking difference for pdf content after converting to image file.
  format: --command=diff-file {files}
  e.g.) --command=diff-file src/test/resources/Book2.pdf src/test/resources/Book3.pdf

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.952 s
[INFO] Finished at: 2021-09-24T11:33:58+09:00
[INFO] ------------------------------------------------------------------------
```

### Using standalone Java Application

```bash
$ ./mvnw clean verify -DskipTests
```

```
$ java -jar target/pdf-diff-commands-0.0.1-SNAPSHOT.jar
```