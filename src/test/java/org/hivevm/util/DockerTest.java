
package org.hivevm.util;

import java.util.List;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DockerClientBuilder;

import org.junit.jupiter.api.Test;

public class DockerTest {

  @Test
  void testDocker() {
    DockerClient client = DockerClientBuilder.getInstance().build();

    List<Container> containers = client.listContainersCmd().exec();
    containers.forEach(c -> System.out.printf("Container: %s\n", c.getId()));

    List<Image> images = client.listImagesCmd().exec();
    images.forEach(i -> System.out.printf("Image: %s\n", i.getId()));
  }
}
