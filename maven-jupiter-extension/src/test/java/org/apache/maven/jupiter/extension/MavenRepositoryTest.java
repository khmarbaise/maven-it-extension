package org.apache.maven.jupiter.extension;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.assertj.core.api.Assertions.assertThat;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link MavenRepository} annotation.
 *
 * <p>This test is intended to check the default values which have been defined
 * within the annotation that they won't be changed unintentionally.</p>
 *
 * @author Karl Heinz Marbaise
 */
@DisplayName("The annotation should keep")
class MavenRepositoryTest {

  private MavenRepository mavenITAnnotation;

  @BeforeEach
  private void beforeEach() {
    AnnotationDescription annotation = AnnotationDescription.Builder.ofType(MavenRepository.class).build();

    Class<?> objectBuilder = new ByteBuddy().subclass(Object.class)
      .annotateType(annotation)
      .make()
      .load(this.getClass().getClassLoader())
      .getLoaded();
    this.mavenITAnnotation = objectBuilder.getAnnotation(MavenRepository.class);
  }

  @Test
  void the_default_value_for_repository_which_is_local_repository() {
    assertThat(mavenITAnnotation.value()).isEqualTo(".m2/repository");
  }

}
