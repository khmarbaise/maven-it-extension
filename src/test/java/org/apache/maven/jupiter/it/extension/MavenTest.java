package org.apache.maven.jupiter.it.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Test
public @interface MavenTest {

  /**
   * This turns on {@code -X} (debug:true) for the Maven run
   * or not (debug:false).
   * @return Debug
   */
  boolean debug() default false;
}
