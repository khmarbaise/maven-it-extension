package org.apache.maven.jupiter.it.extension;

import static org.apache.maven.jupiter.it.extension.maven.MavenVersion.M3_6_2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.maven.jupiter.it.extension.maven.MavenCache;
import org.apache.maven.jupiter.it.extension.maven.MavenVersion;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MavenITExtension.class)
public @interface MavenIT {

  MavenVersion[] versions() default M3_6_2;

  MavenCache mavenCache() default MavenCache.Local;

  boolean mavenDebug() default false;

}
