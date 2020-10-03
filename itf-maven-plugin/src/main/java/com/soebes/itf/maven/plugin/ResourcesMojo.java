package com.soebes.itf.maven.plugin;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Copy resources from src/test/resources-its to the appropriate location.
 *
 * @implNote Copied the majority of the code from maven-resources-plugin.
 */
@Mojo(name = "resources-its", defaultPhase = LifecyclePhase.PROCESS_TEST_RESOURCES, requiresProject = true, threadSafe = true)
public class ResourcesMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;

  /**
   * The character encoding to use when reading and writing filtered resources.
   */
  @Parameter(defaultValue = "${project.build.sourceEncoding}")
  private String encoding;

  /**
   * The character encoding to use when reading and writing filtered properties files.
   * If not specified, it will default to the value of the "encoding" parameter.
   */
  @Parameter
  private String propertiesEncoding;

  /**
   * The list of additional filter properties files to be used along with System and project properties, which would
   * be used for the filtering.
   *
   */
  @Parameter(defaultValue = "${project.build.filters}", readonly = true)
  private List<String> buildFilters;

  @Component(role = MavenResourcesFiltering.class, hint = "default")
  private MavenResourcesFiltering mavenResourcesFiltering;

  /**
   * Expressions preceded with this string won't be interpolated. Anything else preceded with this string will be
   * passed through unchanged. For example {@code \${foo}} will be replaced with {@code ${foo}} but {@code \\${foo}}
   * will be replaced with {@code \\value of foo}, if this parameter has been set to the backslash.
   */
  @Parameter
  private String escapeString;

  /**
   * Copy any empty directories included in the Resources.
   */
  @Parameter(defaultValue = "false")
  private boolean includeEmptyDirs;

  /**
   * The following extensions are already defined and will not being filtered.
   * <ul>
   *   <li>jpg</li>
   *   <li>jar</li>
   *   <li>war</li>
   *   <li>ear</li>
   *   <li>aar</li>
   *   <li>rar</li>
   *   <li>zip</li>
   *   <li>tar</li>
   *   <li>tar.gz</li>
   * </ul>
   */
  @Parameter
  private List<String> nonFilteredFileExtensions;
  /**
   * Whether to escape backslashes and colons in windows-style paths.
   */
  @Parameter(defaultValue = "true")
  private boolean escapeWindowsPaths;

  /**
   * <p>
   * Set of delimiters for expressions to filter within the resources. These delimiters are specified in the form
   * {@code beginToken*endToken}. If no {@code *} is given, the delimiter is assumed to be the same for start and end.
   * </p>
   * <p>
   * So, the default filtering delimiters might be specified as:
   * </p>
   *
   * <pre>
   * &lt;delimiters&gt;
   *   &lt;delimiter&gt;@&lt;/delimiter&gt;
   * &lt;/delimiters&gt;
   * </pre>
   * <p>
   * Since the {@code @} delimiter is the same on both ends, we don't need to specify {@code @*@} (though we can).
   * </p>
   */
  @Parameter(defaultValue = "@")
  private LinkedHashSet<String> delimiters;

  /**
   * By default files like {@code .gitignore}, {@code .cvsignore} etc. are NOT excluded which means they will being
   * copied. If you need them for a particular reason you can do that by settings this to {@code }. This means
   * all files like the following will be copied.
   * <ul>
   * <li>Misc: &#42;&#42;/&#42;~, &#42;&#42;/#&#42;#, &#42;&#42;/.#&#42;, &#42;&#42;/%&#42;%, &#42;&#42;/._&#42;</li>
   * <li>CVS: &#42;&#42;/CVS, &#42;&#42;/CVS/&#42;&#42;, &#42;&#42;/.cvsignore</li>
   * <li>RCS: &#42;&#42;/RCS, &#42;&#42;/RCS/&#42;&#42;</li>
   * <li>SCCS: &#42;&#42;/SCCS, &#42;&#42;/SCCS/&#42;&#42;</li>
   * <li>VSSercer: &#42;&#42;/vssver.scc</li>
   * <li>MKS: &#42;&#42;/project.pj</li>
   * <li>SVN: &#42;&#42;/.svn, &#42;&#42;/.svn/&#42;&#42;</li>
   * <li>GNU: &#42;&#42;/.arch-ids, &#42;&#42;/.arch-ids/&#42;&#42;</li>
   * <li>Bazaar: &#42;&#42;/.bzr, &#42;&#42;/.bzr/&#42;&#42;</li>
   * <li>SurroundSCM: &#42;&#42;/.MySCMServerInfo</li>
   * <li>Mac: &#42;&#42;/.DS_Store</li>
   * <li>Serena Dimension: &#42;&#42;/.metadata, &#42;&#42;/.metadata/&#42;&#42;</li>
   * <li>Mercurial: &#42;&#42;/.hg, &#42;&#42;/.hg/&#42;&#42;</li>
   * <li>GIT: &#42;&#42;/.git, &#42;&#42;/.gitignore, &#42;&#42;/.gitattributes, &#42;&#42;/.git/&#42;&#42;</li>
   * <li>Bitkeeper: &#42;&#42;/BitKeeper, &#42;&#42;/BitKeeper/&#42;&#42;, &#42;&#42;/ChangeSet,
   * &#42;&#42;/ChangeSet/&#42;&#42;</li>
   * <li>Darcs: &#42;&#42;/_darcs, &#42;&#42;/_darcs/&#42;&#42;, &#42;&#42;/.darcsrepo,
   * &#42;&#42;/.darcsrepo/&#42;&#42;&#42;&#42;/-darcs-backup&#42;, &#42;&#42;/.darcs-temp-mail
   * </ul>
   */
  @Parameter(defaultValue = "false")
  private boolean addDefaultExcludes;

  /**
   * The output directory into which to copy the resources.
   */
  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File outputDirectory;

  /**
   * The list of resources we want to transfer.
   * <p>
   * <li>default: <code>src/test/resources-its</code></li>
   * </p>
   */
  @Parameter(readonly = true)
  private List<Resource> resources;

  /**
   * Overwrite existing files even if the destination files are newer.
   */
  @Parameter(defaultValue = "false")
  private boolean overwrite;

  /**
   * <p>
   * List of plexus components hint which implements
   * {@link MavenResourcesFiltering#filterResources(MavenResourcesExecution)}. They will be executed after the
   * resources copying/filtering.
   * </p>
   */
  @Parameter
  private List<String> mavenFilteringHints;

  private List<MavenResourcesFiltering> mavenFilteringComponents = new ArrayList<MavenResourcesFiltering>();

  /**
   * stop searching endToken at the end of line
   */
  @Parameter(defaultValue = "false")
  private boolean supportMultiLineFiltering;

  /**
   * Support filtering of filenames folders etc.
   */
  @Parameter(defaultValue = "false")
  private boolean fileNameFiltering;

  /**
   * {@inheritDoc}
   */
  public void execute()
      throws MojoExecutionException {

    Resource resource = new Resource();
    resource.setDirectory("src/test/resources-its");
    resource.setTargetPath(project.getBuild().getTestOutputDirectory());
    ArrayList<String> includes = new ArrayList<>();
    includes.add("**/**");
    resource.setIncludes(includes);

    //TODO: Document the default excludes we are using?
    resource.setExcludes(Collections.emptyList());

    //TODO: Need to clean up.
    this.resources = new ArrayList<>();
    this.resources.add(resource);

    if (Helper.isBlank(encoding) && isFilteringEnabled(this.resources)) {
      getLog().warn("File encoding has not been set, using platform encoding "
          + System.getProperty("file.encoding")
          + ". Build is platform dependent!");
      getLog().warn("See https://maven.apache.org/general.html#encoding-warning");
    }

    try {
      MavenResourcesExecution mavenResourcesExecution =
          new MavenResourcesExecution(this.resources, this.outputDirectory, project, encoding, Collections.emptyList(),
              Collections.emptyList(), session);

      mavenResourcesExecution.setEscapeWindowsPaths(escapeWindowsPaths);

      // never include project build filters in this call, since we've already accounted for the POM build filters
      // above, in getCombinedFiltersList().
      mavenResourcesExecution.setInjectProjectBuildFilters(false);

      mavenResourcesExecution.setEscapeString(escapeString);
      mavenResourcesExecution.setOverwrite(overwrite);
      mavenResourcesExecution.setIncludeEmptyDirs(includeEmptyDirs);
      mavenResourcesExecution.setSupportMultiLineFiltering(supportMultiLineFiltering);
      mavenResourcesExecution.setFilterFilenames(fileNameFiltering);
      mavenResourcesExecution.setAddDefaultExcludes(addDefaultExcludes);

      // We don't define supplemental properties at the moment.
      mavenResourcesExecution.setAdditionalProperties(null);

      //Using only `@project.version@`...
      mavenResourcesExecution.setDelimiters(delimiters);

      mavenResourcesExecution.setPropertiesEncoding(propertiesEncoding);

      //Default list of extensions which are not filtered.
      List<String> filter = Arrays.asList("jpg", "jar", "war", "ear", "aar", "rar", "zip", "tar", "tar.gz");
      mavenResourcesExecution.setNonFilteredFileExtensions(filter);
      if (nonFilteredFileExtensions != null) {
        mavenResourcesExecution.setNonFilteredFileExtensions(nonFilteredFileExtensions);
      }
      mavenResourcesFiltering.filterResources(mavenResourcesExecution);

    } catch (MavenFilteringException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  /**
   * Determines whether filtering has been enabled for any resource.
   *
   * @param theResources The set of resources to check for filtering, may be <code>null</code>.
   * @return <code>true</code> if at least one resource uses filtering, <code>false</code> otherwise.
   */
  private boolean isFilteringEnabled(Collection<Resource> theResources) {
    if (theResources != null) {
      return theResources.stream().anyMatch(resource -> resource.isFiltering());
    }
    return false;
  }

}
