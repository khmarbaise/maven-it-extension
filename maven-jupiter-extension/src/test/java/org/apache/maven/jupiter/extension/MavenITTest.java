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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link MavenIT} annotation.
 *
 * <p>This test is intended to check the default values which have been defined
 * within the annotation that they won't be changed unintentionally.</p>
 *
 * @author Karl Heinz Marbaise
 */
@DisplayName("The annotation should keep")
class MavenITTest {

  private MavenIT mavenITAnnotation;

  @BeforeEach
  private void beforeEach() {
    this.mavenITAnnotation = Helper.createAnnotation(this.getClass(), MavenIT.class);
  }

  @Test
  void the_default_value_for_debug_as_false() {
    assertThat(mavenITAnnotation.debug()).isFalse();
  }

  @Test
  void the_default_value_for_goals_is_package() {
    assertThat(mavenITAnnotation.goals()).containsExactly("package");
  }

}
