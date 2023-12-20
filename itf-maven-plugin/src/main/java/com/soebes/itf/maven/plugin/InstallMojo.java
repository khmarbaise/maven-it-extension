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
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifact;
import org.eclipse.aether.DefaultRepositoryCache;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * itf-maven-plugin.
 *
 * @author <a href="mailto:khmarbaise@apache.org">Karl Heinz Marbaise</a>
 * @implNote Several parts are taken from the maven-invoker-plugin.
 */
@Mojo(name = "install", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST,
    requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class InstallMojo extends AbstractMojo {
  @Parameter(defaultValue = "${session}", required = true, readonly = true)
  private MavenSession session;

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   * This defines the location where the installed artifacts are installed for
   * consumption during the integration tests.
   */
  @Parameter(defaultValue = "${project.build.directory}/itf-repo", required = true)
  private File itfRepository;

  @Component
  private RepositorySystem repositorySystem;

  /**
   * The set of Maven projects in the reactor build.
   */
  @Parameter(defaultValue = "${reactorProjects}", readonly = true)
  private Collection<MavenProject> reactorProjects;

  /**
   *
   */
  @Parameter(property = "localRepository", required = true, readonly = true)
  private ArtifactRepository localRepository;

  @Parameter(defaultValue = "runtime")
  private String scope;

  public void execute() throws MojoExecutionException, MojoFailureException {
    createTestRepository();

    Map<String, org.eclipse.aether.artifact.Artifact> resolvedArtifacts = new LinkedHashMap<>();

    try {

      resolveProjectArtifacts(resolvedArtifacts);
      resolveProjectPoms(project, resolvedArtifacts);
      resolveProjectDependencies(resolvedArtifacts);
      // resolveExtraArtifacts(resolvedArtifacts);
      installArtifacts(resolvedArtifacts);

    } catch (DependencyResolutionException
             | InstallationException
             | ArtifactResolutionException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }

  }


  private void createTestRepository()
      throws MojoExecutionException {

    if (!itfRepository.exists() && !itfRepository.mkdirs()) {
      throw new MojoExecutionException("Failed to create directory: " + itfRepository);
    }
  }

  private void resolveProjectArtifacts(Map<String, org.eclipse.aether.artifact.Artifact> resolvedArtifacts) {

    // pom packaging doesn't have a main artifact
    if (project.getArtifact() != null && project.getArtifact().getFile() != null) {
      org.eclipse.aether.artifact.Artifact artifact = RepositoryUtils.toArtifact(project.getArtifact());
      resolvedArtifacts.put(ArtifactIdUtils.toId(artifact), artifact);
    }

    project.getAttachedArtifacts().stream()
        .map(RepositoryUtils::toArtifact)
        .forEach(a -> resolvedArtifacts.put(ArtifactIdUtils.toId(a), a));
  }

  private void resolveProjectPoms(MavenProject project, Map<String, org.eclipse.aether.artifact.Artifact> resolvedArtifacts)
      throws ArtifactResolutionException {

    if (project == null) {
      return;
    }

    org.eclipse.aether.artifact.Artifact projectPom = RepositoryUtils.toArtifact(new ProjectArtifact(project));
    if (projectPom.getFile() != null) {
      resolvedArtifacts.put(projectPom.toString(), projectPom);
    } else {
      org.eclipse.aether.artifact.Artifact artifact = resolveArtifact(projectPom, project.getRemoteProjectRepositories());
      resolvedArtifacts.put(ArtifactIdUtils.toId(artifact), artifact);
    }
    resolveProjectPoms(project.getParent(), resolvedArtifacts);
  }

  private void resolveProjectDependencies(Map<String, org.eclipse.aether.artifact.Artifact> resolvedArtifacts)
      throws ArtifactResolutionException, MojoExecutionException, DependencyResolutionException {

    DependencyFilter classpathFilter = DependencyFilterUtils.classpathFilter(scope);

    ArtifactTypeRegistry artifactTypeRegistry =
        session.getRepositorySession().getArtifactTypeRegistry();

    List<Dependency> managedDependencies = Optional.ofNullable(project.getDependencyManagement())
        .map(DependencyManagement::getDependencies)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(d -> RepositoryUtils.toDependency(d, artifactTypeRegistry))
        .collect(Collectors.toList());

    List<Dependency> dependencies = project.getDependencies().stream()
        .map(d -> RepositoryUtils.toDependency(d, artifactTypeRegistry))
        .collect(Collectors.toList());

    CollectRequest collectRequest = new CollectRequest();
    collectRequest.setRootArtifact(RepositoryUtils.toArtifact(project.getArtifact()));
    collectRequest.setDependencies(dependencies);
    collectRequest.setManagedDependencies(managedDependencies);

    collectRequest.setRepositories(project.getRemoteProjectRepositories());

    DependencyRequest request = new DependencyRequest(collectRequest, classpathFilter);

    DependencyResult dependencyResult =
        repositorySystem.resolveDependencies(session.getRepositorySession(), request);

    List<org.eclipse.aether.artifact.Artifact> artifacts = dependencyResult.getArtifactResults().stream()
        .map(ArtifactResult::getArtifact)
        .collect(Collectors.toList());

    artifacts.forEach(a -> resolvedArtifacts.put(ArtifactIdUtils.toId(a), a));
    resolvePomsForArtifacts(artifacts, resolvedArtifacts, collectRequest.getRepositories());
  }

  private void resolvePomsForArtifacts(
      List<org.eclipse.aether.artifact.Artifact> artifacts,
      Map<String, org.eclipse.aether.artifact.Artifact> resolvedArtifacts,
      List<RemoteRepository> remoteRepositories)
      throws ArtifactResolutionException, MojoExecutionException {

    for (org.eclipse.aether.artifact.Artifact a : artifacts) {
      org.eclipse.aether.artifact.Artifact artifactResult = resolveArtifact(new SubArtifact(a, "", "pom"), remoteRepositories);
      resolvePomWithParents(artifactResult, resolvedArtifacts, remoteRepositories);
    }
  }

  private void resolvePomWithParents(
      org.eclipse.aether.artifact.Artifact artifact, Map<String, org.eclipse.aether.artifact.Artifact> resolvedArtifacts, List<RemoteRepository> remoteRepositories)
      throws MojoExecutionException, ArtifactResolutionException {

    if (resolvedArtifacts.containsKey(ArtifactIdUtils.toId(artifact))) {
      return;
    }

    Model model = PomUtils.loadPom(artifact.getFile());
    Parent parent = model.getParent();
    if (parent != null) {
      org.eclipse.aether.artifact.DefaultArtifact pom =
          new DefaultArtifact(parent.getGroupId(), parent.getArtifactId(), "", "pom", parent.getVersion());
      org.eclipse.aether.artifact.Artifact resolvedPom = resolveArtifact(pom, remoteRepositories);
      resolvePomWithParents(resolvedPom, resolvedArtifacts, remoteRepositories);
    }

    resolvedArtifacts.put(ArtifactIdUtils.toId(artifact), artifact);
  }

  private org.eclipse.aether.artifact.Artifact resolveArtifact(Artifact artifact, List<RemoteRepository> remoteRepositories)
      throws ArtifactResolutionException {

    ArtifactRequest request = new ArtifactRequest();
    request.setArtifact(artifact);
    request.setRepositories(remoteRepositories);
    ArtifactResult artifactResult = repositorySystem.resolveArtifact(session.getRepositorySession(), request);
    return artifactResult.getArtifact();
  }

  private void installArtifacts(Map<String, Artifact> resolvedArtifacts) throws InstallationException {

    RepositorySystemSession systemSessionForLocalRepo = createSystemSessionForLocalRepo();

    // we can have on dependency two artifacts with the same groupId:artifactId
    // with different version, in such case when we install both in one request
    // metadata will contain only one version

    Map<String, List<Artifact>> collect = resolvedArtifacts.values().stream()
        .filter(a -> !hasTheSamePathAsTarget(a, systemSessionForLocalRepo))
        .collect(Collectors.groupingBy(
            a -> String.format("%s:%s:%s", a.getGroupId(), a.getArtifactId(), a.getVersion()),
            LinkedHashMap::new,
            Collectors.toList()));

    for (List<Artifact> artifacts : collect.values()) {
      InstallRequest request = new InstallRequest();
      request.setArtifacts(artifacts);
      repositorySystem.install(systemSessionForLocalRepo, request);
    }
  }


  private boolean hasTheSamePathAsTarget(Artifact artifact, RepositorySystemSession systemSession) {
    try {
      LocalRepositoryManager lrm = systemSession.getLocalRepositoryManager();
      File targetBasedir = lrm.getRepository().getBasedir();
      if (targetBasedir == null) {
        return false;
      }
      File targetFile = new File(targetBasedir, lrm.getPathForLocalArtifact(artifact)).getCanonicalFile();
      File sourceFile = artifact.getFile().getCanonicalFile();
      if (Objects.equals(targetFile, sourceFile)) {
        getLog().debug("Skip install the same target " + sourceFile);
        return true;
      }
      return false;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }


  /**
   * Create a new {@link  RepositorySystemSession} connected with local repo.
   */
  private RepositorySystemSession createSystemSessionForLocalRepo() {
    RepositorySystemSession repositorySystemSession = session.getRepositorySession();
    if (itfRepository != null) {
      // "clone" repository session and replace localRepository
      DefaultRepositorySystemSession newSession =
          new DefaultRepositorySystemSession(session.getRepositorySession());
      // Clear cache, since we're using a new local repository
      newSession.setCache(new DefaultRepositoryCache());
      // keep same repositoryType
      String contentType = newSession.getLocalRepository().getContentType();
      if ("enhanced".equals(contentType)) {
        contentType = "default";
      }
      LocalRepositoryManager localRepositoryManager = repositorySystem.newLocalRepositoryManager(
          newSession, new LocalRepository(itfRepository, contentType));

      newSession.setLocalRepositoryManager(localRepositoryManager);
      repositorySystemSession = newSession;
      getLog().debug("localRepoPath: "
                     + localRepositoryManager.getRepository().getBasedir());
    }

    return repositorySystemSession;
  }


}
