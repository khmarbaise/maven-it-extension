package org.apache.maven.jupiter.extension;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.maven.jupiter.extension.maven.MavenCache;
import org.apache.maven.jupiter.extension.maven.MavenVersion;
import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MavenITExtension.class)
@Documented
@API(status = EXPERIMENTAL, since = "0.1")
/**
 * @author Karl Heinz Marbaise
 */
public @interface MavenIT {

  MavenVersion[] versions() default MavenVersion.M3_6_2;

  MavenCache mavenCache() default MavenCache.Local;

  boolean mavenDebug() default false;

}
