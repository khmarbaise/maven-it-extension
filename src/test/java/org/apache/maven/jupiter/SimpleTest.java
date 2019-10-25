package org.apache.maven.jupiter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimpleTest {

  @Test
  void firstTest() {
    List<String> earFiles = Arrays.asList("A", "B", "C", "D");
    List<String> x = Arrays.asList("B", "C");

    boolean b = x.stream().allMatch(s -> earFiles.stream().anyMatch(earFile -> earFile.equals(s)));
    System.out.println("b = " + b);

    assertThat(earFiles).doesNotContain("X").containsOnlyOnce("B", "C");
  }
}
