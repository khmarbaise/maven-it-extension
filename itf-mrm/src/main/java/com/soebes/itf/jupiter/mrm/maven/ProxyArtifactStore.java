package com.soebes.itf.jupiter.mrm.maven;

/*
 * Copyright 2011 Stephen Connolly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang.StringUtils;
import org.apache.maven.archetype.ArchetypeManager;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.GroupRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.SnapshotArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.mrm.api.maven.ArchetypeCatalogNotFoundException;
import org.codehaus.mojo.mrm.api.maven.Artifact;
import org.codehaus.mojo.mrm.api.maven.ArtifactNotFoundException;
import org.codehaus.mojo.mrm.api.maven.BaseArtifactStore;
import org.codehaus.mojo.mrm.api.maven.MetadataNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An {@link org.codehaus.mojo.mrm.api.maven.ArtifactStore} that serves content from a running Maven instance.
 */
public class ProxyArtifactStore
    extends BaseArtifactStore
{
    /**
     * The {@link RepositoryMetadataManager} provided by Maven.
     */
    private final RepositoryMetadataManager repositoryMetadataManager;

    /**
     * The remote plugin repositories provided by Maven.
     */
    private final List<ArtifactRepository> remotePluginRepositories;

    /**
     * The {@link ArtifactRepository} provided by Maven.
     */
    private final ArtifactRepository localRepository;

    /**
     * The {@link ArtifactFactory} provided by Maven.
     */
    private final ArtifactFactory artifactFactory;

    /**
     * The remote repositories that we will query.
     */
    private final List<ArtifactRepository> remoteRepositories;

    /**
     * The {@link ArtifactResolver} provided by Maven.
     */
    private final ArtifactResolver artifactResolver;
    
    
    private final ArchetypeManager archetypeManager;

    /**
     * The {@link Log} to log to.
     */
    private final Log log;

    /**
     * A version range that matches any version
     */
    private final VersionRange anyVersion;

    /**
     * A cache of what artifacts are present.
     */
    private final Map<String, Map<String, Artifact>> children = new HashMap<>();

    /**
     * Creates a new instance.
     *
     * @param repositoryMetadataManager  the {@link RepositoryMetadataManager} to use.
     * @param remoteArtifactRepositories the repsoitories to use.
     * @param remotePluginRepositories   the plugin repositories to use.
     * @param localRepository            the local repository to use.
     * @param artifactFactory            the {@link ArtifactFactory} to use.
     * @param artifactResolver           the {@link ArtifactResolver} to use.
     * @param log                        the {@link Log} to log to.
     */
    public ProxyArtifactStore( RepositoryMetadataManager repositoryMetadataManager,
                               List<ArtifactRepository> remoteArtifactRepositories,
                               List<ArtifactRepository> remotePluginRepositories, ArtifactRepository localRepository,
                               ArtifactFactory artifactFactory, ArtifactResolver artifactResolver, ArchetypeManager archetypeManager, Log log )
    {
        this.repositoryMetadataManager = repositoryMetadataManager;
        this.remotePluginRepositories = remotePluginRepositories;
        this.localRepository = localRepository;
        this.artifactFactory = artifactFactory;
        this.artifactResolver = artifactResolver;
        this.archetypeManager = archetypeManager;
        this.log = log;
        remoteRepositories = new ArrayList<>();
        remoteRepositories.addAll( remoteArtifactRepositories );
        remoteRepositories.addAll( remotePluginRepositories );
        try
        {
            anyVersion = VersionRange.createFromVersionSpec( "[0,]" );
        }
        catch ( InvalidVersionSpecificationException e )
        {
            // must never happen... so if it does make sure we stop
            IllegalStateException ise =
                new IllegalStateException( "[0,] should always be a valid version specification" );
            ise.initCause( e );
            throw ise;
        }
    }

    /**
     * Update the {@link #children} with a resolved artifact.
     *
     * @param artifact the artifact that was resolved.
     */
    private synchronized void addResolved( Artifact artifact )
    {
        String path =
            artifact.getGroupId().replace( '.', '/' ) + '/' + artifact.getArtifactId() + "/" + artifact.getVersion();
        Map<String, Artifact> artifactMapper = this.children.computeIfAbsent(path, k -> new HashMap<String, Artifact>());
        artifactMapper.put( artifact.getName(), artifact );
        addResolved( path );
    }

    /**
     * Update the {@link #children} with a resolved path.
     *
     * @param path the path that was resolved.
     */
    private synchronized void addResolved( String path )
    {
        for ( int index = path.lastIndexOf( '/' ); index > 0; index = path.lastIndexOf( '/' ) )
        {
            String name = path.substring( index + 1 );
            path = path.substring( 0, index );
            Map<String, Artifact> artifactMapper = this.children.computeIfAbsent(path, k -> new HashMap<>());
            artifactMapper.put( name, null );
        }
        if ( !StringUtils.isEmpty( path ) )
        {
            Map<String, Artifact> artifactMapper = this.children.computeIfAbsent("", k -> new HashMap<>());
            artifactMapper.put( path, null );
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Set<String> getGroupIds( String parentGroupId )
    {
        String path = parentGroupId.replace( '.', '/' );
        Map<String, Artifact> artifactMapper = this.children.get( path );
        if ( artifactMapper == null )
        {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        for ( Map.Entry<String, Artifact> e : artifactMapper.entrySet() )
        {
            if ( e.getValue() == null )
            {
                result.add( e.getKey() );
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Set<String> getArtifactIds( String groupId )
    {
        String path = groupId.replace( '.', '/' );
        Map<String, Artifact> artifactMapper = this.children.get( path );
        if ( artifactMapper == null )
        {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        for ( Map.Entry<String, Artifact> e : artifactMapper.entrySet() )
        {
            if ( e.getValue() == null )
            {
                result.add( e.getKey() );
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Set<String> getVersions( String groupId, String artifactId )
    {
        String path = groupId.replace( '.', '/' ) + '/' + artifactId;
        Map<String, Artifact> artifactMapper = this.children.get( path );
        if ( artifactMapper == null )
        {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        for ( Map.Entry<String, Artifact> e : artifactMapper.entrySet() )
        {
            if ( e.getValue() == null )
            {
                result.add( e.getKey() );
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Set<Artifact> getArtifacts( String groupId, String artifactId, String version )
    {
        String path = groupId.replace( '.', '/' ) + '/' + artifactId + "/" + version;
        Map<String, Artifact> artifactMapper = this.children.get( path );
        if ( artifactMapper == null )
        {
            return Collections.emptySet();
        }
        Set<Artifact> result = new HashSet<>();
        for ( Artifact a : artifactMapper.values() )
        {
            if ( a != null )
            {
                result.add( a );
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified( Artifact artifact )
        throws IOException, ArtifactNotFoundException
    {
        org.apache.maven.artifact.Artifact mavenArtifact =
            artifactFactory.createArtifactWithClassifier( artifact.getGroupId(), artifact.getArtifactId(),
                                                          artifact.getTimestampVersion(), artifact.getType(),
                                                          artifact.getClassifier() );
        try
        {
            artifactResolver.resolve( mavenArtifact, remoteRepositories, localRepository );
            final File file = mavenArtifact.getFile();
            if ( file != null && file.isFile() )
            {
                addResolved( artifact );
                return file.lastModified();
            }
            throw new ArtifactNotFoundException( artifact );
        }
        catch ( org.apache.maven.artifact.resolver.ArtifactNotFoundException e )
        {
            ArtifactNotFoundException anfe = new ArtifactNotFoundException( artifact );
            anfe.initCause( e );
            throw anfe;
        }
        catch ( ArtifactResolutionException e )
        {
            IOException ioe = new IOException( e.getMessage() );
            ioe.initCause( e );
            throw ioe;
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getSize( Artifact artifact )
        throws IOException, ArtifactNotFoundException
    {
        org.apache.maven.artifact.Artifact mavenArtifact =
            artifactFactory.createArtifactWithClassifier( artifact.getGroupId(), artifact.getArtifactId(),
                                                          artifact.getTimestampVersion(), artifact.getType(),
                                                          artifact.getClassifier() );
        try
        {
            artifactResolver.resolve( mavenArtifact, remoteRepositories, localRepository );
            final File file = mavenArtifact.getFile();
            if ( file != null && file.isFile() )
            {
                addResolved( artifact );
                return file.length();
            }
            throw new ArtifactNotFoundException( artifact );
        }
        catch ( org.apache.maven.artifact.resolver.ArtifactNotFoundException e )
        {
            throw new ArtifactNotFoundException( artifact, e );
        }
        catch ( ArtifactResolutionException e )
        {
            IOException ioe = new IOException( e.getMessage() );
            ioe.initCause( e );
            throw ioe;
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream get( Artifact artifact )
        throws IOException, ArtifactNotFoundException
    {
        org.apache.maven.artifact.Artifact mavenArtifact =
            artifactFactory.createArtifactWithClassifier( artifact.getGroupId(), artifact.getArtifactId(),
                                                          artifact.getTimestampVersion(), artifact.getType(),
                                                          artifact.getClassifier() );
        try
        {
            artifactResolver.resolve( mavenArtifact, remoteRepositories, localRepository );
            final File file = mavenArtifact.getFile();
            if ( file != null && file.isFile() )
            {
                addResolved( artifact );
                return new FileInputStream( file );
            }
            throw new ArtifactNotFoundException( artifact );
        }
        catch ( org.apache.maven.artifact.resolver.ArtifactNotFoundException e )
        {
            ArtifactNotFoundException anfe = new ArtifactNotFoundException( artifact, e );
//          Causes a java.lang.IllegalStateException: Can't overwrite cause            
//            anfe.initCause( e );
            throw anfe;
        }
        catch ( ArtifactResolutionException e )
        {
            IOException ioe = new IOException( e.getMessage() );
            ioe.initCause( e );
            throw ioe;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void set( Artifact artifact, InputStream content )
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Metadata getMetadata( String path )
        throws IOException, MetadataNotFoundException
    {
        path = StringUtils.strip( path, "/" );
        int index = path.lastIndexOf( '/' );
        int index2 = index == -1 ? -1 : path.lastIndexOf( '/', index - 1 );

        String version = index2 == -1 ? null : path.substring( index + 1 );
        String artifactId = index2 == -1 ? null : path.substring( index2 + 1, index );
        String groupId = index2 == -1 ? null : path.substring( 0, index2 ).replace( '/', '.' );

        Metadata metadata = new Metadata();

        boolean foundSomething = false;

        // is this path a groupId:artifactId pair?
        if ( version != null && version.endsWith( "-SNAPSHOT" ) && !StringUtils.isEmpty( artifactId )
            && !StringUtils.isEmpty( groupId ) )
        {
            final org.apache.maven.artifact.Artifact artifact =
                artifactFactory.createDependencyArtifact( groupId, artifactId,
                                                          VersionRange.createFromVersion( version ), "pom", null,
                                                          "compile" );
            final SnapshotArtifactRepositoryMetadata artifactRepositoryMetadata =
                new SnapshotArtifactRepositoryMetadata( artifact );
            try
            {
                repositoryMetadataManager.resolve( artifactRepositoryMetadata, remoteRepositories, localRepository );

                final Metadata artifactMetadata = artifactRepositoryMetadata.getMetadata();
                if ( artifactMetadata.getVersioning() != null
                    && artifactMetadata.getVersioning().getSnapshot() != null )
                {
                    foundSomething = true;
                    metadata.setGroupId( groupId );
                    metadata.setArtifactId( artifactId );
                    metadata.setVersion( version );
                    metadata.merge( artifactMetadata );
                }
                try
                {
                    if ( artifactMetadata.getVersioning() != null
                        && !artifactMetadata.getVersioning().getSnapshotVersions().isEmpty() )
                    {
                        // TODO up to and including Maven 3.0.3 we do not get a populated SnapshotVersions
                        for ( SnapshotVersion v : artifactMetadata.getVersioning().getSnapshotVersions() )
                        {
                            metadata.getVersioning().addSnapshotVersion( v );
                            if ( v.getVersion().endsWith( "-SNAPSHOT" ) )
                            {
                                addResolved(
                                    new Artifact( groupId, artifactId, version, v.getClassifier(), v.getExtension() ) );
                            }
                        }
                    }
                }
                catch ( NoSuchMethodError e )
                {
                    // ignore Maven 2.x doesn't give us the info
                }
            }
            catch ( RepositoryMetadataResolutionException e )
            {
                log.debug( e );
            }
        }

        // is this path a groupId:artifactId pair?
        artifactId = index == -1 ? null : path.substring( index + 1 );
        groupId = index == -1 ? null : path.substring( 0, index ).replace( '/', '.' );
        if ( !StringUtils.isEmpty( artifactId ) && !StringUtils.isEmpty( groupId ) )
        {
            final org.apache.maven.artifact.Artifact artifact =
                artifactFactory.createDependencyArtifact( groupId, artifactId, anyVersion, "pom", null, "compile" );
            final ArtifactRepositoryMetadata artifactRepositoryMetadata = new ArtifactRepositoryMetadata( artifact );
            try
            {
                repositoryMetadataManager.resolve( artifactRepositoryMetadata, remoteRepositories, localRepository );

                final Metadata artifactMetadata = artifactRepositoryMetadata.getMetadata();
                if ( artifactMetadata.getVersioning() != null )
                {
                    foundSomething = true;
                    if ( StringUtils.isEmpty( metadata.getGroupId() ) )
                    {
                        metadata.setGroupId( groupId );
                        metadata.setArtifactId( artifactId );
                    }
                    metadata.merge( artifactMetadata );
                    for ( String v : artifactMetadata.getVersioning().getVersions() )
                    {
                        addResolved( path + "/" + v );
                    }
                }
            }
            catch ( RepositoryMetadataResolutionException e )
            {
                log.debug( e );
            }
        }

        // if this path a groupId on its own?
        groupId = path.replace( '/', '.' );
        final GroupRepositoryMetadata groupRepositoryMetadata = new GroupRepositoryMetadata( groupId );
        try
        {
            repositoryMetadataManager.resolve( groupRepositoryMetadata, remotePluginRepositories, localRepository );
            foundSomething = true;
            metadata.merge( groupRepositoryMetadata.getMetadata() );
            for ( Plugin plugin : groupRepositoryMetadata.getMetadata().getPlugins() )
            {
                addResolved( path + "/" + plugin.getArtifactId() );
            }
        }
        catch ( RepositoryMetadataResolutionException e )
        {
            log.debug( e );
        }

        if ( !foundSomething )
        {
            throw new MetadataNotFoundException( path );
        }
        addResolved( path );
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public long getMetadataLastModified( String path )
        throws IOException, MetadataNotFoundException
    {
        Metadata metadata = getMetadata( path );
        if ( metadata != null )
        {
            if ( !StringUtils.isEmpty( metadata.getGroupId() ) || !StringUtils.isEmpty( metadata.getArtifactId() )
                || !StringUtils.isEmpty( metadata.getVersion() )
                || ( metadata.getPlugins() != null && !metadata.getPlugins().isEmpty() ) || (
                metadata.getVersioning() != null && ( !StringUtils.isEmpty( metadata.getVersioning().getLastUpdated() )
                    || !StringUtils.isEmpty( metadata.getVersioning().getLatest() )
                    || !StringUtils.isEmpty( metadata.getVersioning().getRelease() )
                    || ( metadata.getVersioning().getVersions() != null
                    && !metadata.getVersioning().getVersions().isEmpty() ) || ( metadata.getVersioning().getSnapshot()
                    != null ) ) ) )
            {
                return System.currentTimeMillis();
            }
        }
        throw new MetadataNotFoundException( path );
    }

    public ArchetypeCatalog getArchetypeCatalog() {
        return archetypeManager.getDefaultLocalCatalog();
    }

    public long getArchetypeCatalogLastModified()
        throws ArchetypeCatalogNotFoundException
    {
        if( archetypeManager.getDefaultLocalCatalog() != null )
        {
            return System.currentTimeMillis();
        }
        else
        {
            throw new ArchetypeCatalogNotFoundException();
        }
    }
}
