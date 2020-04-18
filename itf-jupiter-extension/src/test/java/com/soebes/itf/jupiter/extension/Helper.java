package com.soebes.itf.jupiter.extension;

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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationDescription.Builder;

import java.lang.annotation.Annotation;

/**
 * Helper Class to prevent code duplication in {@link MavenITTest} and {@link MavenRepositoryTest}.
 *
 * @author Karl Heinz Marbaise
 */
class Helper {

  static <ANNOTATION extends Annotation> ANNOTATION createAnnotation(Class<?> theClass,
                                                                     Class<ANNOTATION> annotationType) {
    AnnotationDescription annotationDescription = Builder.ofType(annotationType).build();

    Class<?> objectBuilder = new ByteBuddy().subclass(Object.class)
        .annotateType(annotationDescription)
        .make()
        .load(theClass.getClassLoader())
        .getLoaded();
    return objectBuilder.getAnnotation(annotationType);

  }

}
