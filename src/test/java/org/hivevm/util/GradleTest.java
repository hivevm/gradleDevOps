
package org.hivevm.util;

import java.io.File;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.util.GradleVersion;
import org.junit.jupiter.api.Test;

public class GradleTest {

  @Test
  void testGradle() {
    File projectDir = new File(".").getAbsoluteFile();
    System.out.printf("Gradle v.%s on '%s'\n", GradleVersion.current().getVersion(), projectDir);

    GradleConnector connector = GradleConnector.newConnector();
    // connector.useInstallation(gradleInstallationDir1);
    connector.forProjectDirectory(projectDir);

    try (ProjectConnection conn = connector.connect()) {
      // ModelBuilder<DevOpsConfig> builder = conn.model(DevOpsConfig.class);
      // builder.withArguments("--init-script", "init.gradle");
      // DevOpsConfig config = builder.get();

      BuildLauncher build = conn.newBuild();
      build.setStandardOutput(System.out);
      build.setStandardError(System.err);
      build.forTasks("tasks");
      build.run();
    }
  }
}
