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

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import org.apiguardian.api.API;

/**
 * The different command line options which are supported by Apache Maven.
 *
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.1.0")
public final class MavenOptions {

  /**
   * {@code --builder <arg>}
   */
  public static final String BUILDER = "--builder";

  /**
   * {@code --define <arg>}
   */
  public static final String DEFINE = "--define";

  /**
   * {@code --encrypt-master-password <arg>}
   */
  public static final String ENCRYPT_MASTER_PASSWORD = "--encrypt-master-password";

  /**
   * {@code --encrypt-password <arg>}
   */
  public static final String ENCRYPT_PASSWORD = "--encrypt-password";

  /**
   * {@code --file <arg>}
   */
  public static final String FILE = "--file";

  /**
   * {@code --fail-at-end}
   */
  public static final String FAIL_AT_END = "--fail-at-end";

  /**
   * {@code --fail-fast}
   */
  public static final String FAIL_FAST = "--fail-fast";

  /**
   * {@code --fail-never}
   */
  public static final String FAIL_NEVER = "--fail-never";

  /**
   * {@code --global-settings <arg>}
   */
  public static final String GLOBAL_SETTINGS = "--global-settings";

  /**
   * {@code --global-toolchains <arg>}
   */
  public static final String GLOBAL_TOOLCHAINS = "--global-toolchains";

  /**
   * {@code --help}
   */
  public static final String HELP = "--help";

  /**
   * {@code --non-recursive}
   */
  public static final String NON_RECURSIVE = "--non-recursive";

  /**
   * {@code --offline}
   */
  public static final String OFFLINE = "--offline";

  /**
   * {@code --projects <args>}
   */
  public static final String PROJECTS = "--projects";

  /**
   * {@code --quiet}
   */
  public static final String QUIET = "--quiet";

  /**
   * {@code --resume-from <arg>}
   */
  public static final String RESUME_FROM = "--resume-from";

  /**
   * {@code --settings <arg>}
   */
  public static final String SETTINGS = "--settings";

  /**
   * {@code --toolchains <arg>}
   */
  public static final String TOOLCHAINS = "--toolchains";

  /**
   * {@code --threads <arg>}
   */
  public static final String THREADS = "--threads";

  /**
   * {@code --update-snapshots}
   */
  public static final String UPDATE_SNAPSHOTS = "--update-snapshots";

  /**
   * {@code --version}
   */
  public static final String VERSION = "--version";

  /**
   * {@code --errors}
   */
  public static final String ERRORS = "--errors";

  /**
   * {@code --lax-checksums}
   */
  public static final String LAX_CHECKSUMS = "--lax-checksums";

  /**
   * {@code --strict-checksums}
   */
  public static final String STRICT_CHECKSUM = "--strict-checksums";

  /**
   * {@code --also-make-dependents}
   */
  public static final String ALSO_MAKE_DEPENDENCIES = "--also-make-dependents";

  /**
   * {@code --also-make}
   */
  public static final String ALSO_MAKE = "--also-make";

  /**
   * {@code --debug}
   */
  public static final String DEBUG = "--debug";

  /**
   * {@code --no-transfer-progress}
   */
  public static final String NO_TRANSFER_PROGRESS = "--no-transfer-progress";

  /**
   * {@code -V}
   */
  public static final String SHOW_VERSION = "-V";

  /**
   * {@code --batch-mode}
   */
  public static final String BATCH_MODE = "--batch-mode";

  /**
   * You have to add the {@code <arg>} in your {@code options} definition.
   *
   * {@code --log-file <arg>}
   */
  public static final String LOG_FILE = "--log-file";

  /**
   * {@code --activate-profiles <arg>}
   */
  public static final String ACTIVATE_PROFILES = "--activate-profiles";

  private MavenOptions() {
    // prevent instantiation.
  }

}
