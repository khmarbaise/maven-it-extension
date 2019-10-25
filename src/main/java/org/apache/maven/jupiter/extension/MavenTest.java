package org.apache.maven.jupiter.extension;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;

/**
 * TODO: Might we change the name to MavenITTest ?
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Test
@API(status = EXPERIMENTAL, since = "0.1")
/**
 * @author Karl Heinz Marbaise
 */
public @interface MavenTest {

  String[] goals() default {"clean", "verify"};

  /**
   * You can define the profiles you would like to get activated: For example for a single profile:
   * <pre>
   *    &#x40;MavenTest(profiles = {"run-its"})
   * </pre>
   * For multiple profiles:
   * <pre>
   *    &#x40;MavenTest(profiles = {"run-its", "second-profile"})
   * </pre>
   * This is the equivalent to the command line like: {@code -Prun-its}
   *
   * @return The defines profiles.
   */
  String[] activeProfiles() default {};

  /**
   * This turns on {@code -X} (debug:true) for the Maven run or not (debug:false).
   *
   * @return Debug
   */
  boolean debug() default false;
}
