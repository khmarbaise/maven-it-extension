package com.soebes.maven.plugins.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.filtering.MavenFileFilter;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;

/**
 * This mojo will get the main artifact of the current project unpack it and use it to create configured artifacts
 * appropriate to the configuration folder structure.
 * 
 * @author Karl-Heinz Marbaise <a href="mailto:khmarbaise@soebes.de">khmarbaise@soebes.de</a>
 */
@Mojo( name = "configuration", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true )
public class ConfigurationMojo
    extends AbstractConfigurationMojo
{

    /**
     * The JAR archiver needed for archiving the environments.
     */
    @Component( role = Archiver.class, hint = "jar" )
    private JarArchiver jarArchiver;

    @Component
    private ArchiverManager manager;

    @Component( role = MavenResourcesFiltering.class, hint = "default" )
    private MavenResourcesFiltering mavenResourcesFiltering;

    @Component( role = MavenFileFilter.class, hint = "default" )
    private MavenFileFilter mavenFileFilter;

    /**
     * Expression preceded with the String won't be interpolated \${foo} will be replaced with ${foo}
     */
    @Parameter
    private String escapeString;

    /**
     * Whether to escape backslashes and colons in windows-style paths.
     */
    @Parameter( defaultValue = "true" )
    private boolean escapeWindowsPaths;

    /**
     * The list of extra filter properties files to be used along with System properties, project properties, and filter
     * properties files specified in the POM build/filters section, which should be used for the filtering during the
     * current mojo execution. <br/>
     * Normally, these will be configured from a plugin's execution section, to provide a different set of filters for a
     * particular execution. For instance, starting in Maven 2.2.0, you have the option of configuring executions with
     * the id's <code>default-resources</code> and <code>default-testResources</code> to supply different configurations
     * for the two different types of resources. By supplying <code>extraFilters</code> configurations, you can separate
     * which filters are used for which type of resource.
     */
    @Parameter
    private List<String> filters;

    /**
     * Support filtering of filenames folders etc.
     */
    @Parameter( defaultValue = "false" )
    private boolean fileNameFiltering;

    /**
     * <p>
     * Set of delimiters for expressions to filter within the resources. These delimiters are specified in the form
     * 'beginToken*endToken'. If no '*' is given, the delimiter is assumed to be the same for start and end.
     * </p>
     * <p>
     * So, the default filtering delimiters might be specified as:
     * </p>
     * 
     * <pre>
     * &lt;delimiters&gt;
     *   &lt;delimiter&gt;${*}&lt;/delimiter&gt;
     *   &lt;delimiter&gt;@&lt;/delimiter&gt;
     * &lt;/delimiters&gt;
     * </pre>
     * <p>
     * Since the '@' delimiter is the same on both ends, we don't need to specify '@*@' (though we can).
     * </p>
     */
    @Parameter
    private LinkedHashSet<String> delimiters;

    /**
     * Use default delimiters in addition to custom delimiters, if any.
     */
    @Parameter( defaultValue = "true" )
    private boolean useDefaultDelimiters;

    /**
     * Additional file extensions to not apply filtering (already defined are : jpg, jpeg, gif, bmp, png)
     */
    @Parameter
    private List<String> nonFilteredFileExtensions;

    /**
     * stop searching endToken at the end of line
     */
    @Parameter( defaultValue = "false" )
    private boolean supportMultiLineFiltering;

    /**
     * Create the unpack folder for later unpacking of the main artifact.
     * 
     * @return The folder which has been created.
     * @throws MojoExecutionException in case of failures.
     */
    private File createUnpackFolder()
        throws MojoFailureException, MojoExecutionException
    {
        // TODO: Should we use a different name or temp file? File.createTempFile( prefix, suffix );
        File unpackFolder = new File( getOutputDirectory(), "configuration-maven-plugin-unpack" );

        deleteFolderOfPreviousRunIfExist( unpackFolder );

        if ( !unpackFolder.mkdirs() )
        {
            throw new MojoExecutionException( "The unpack folder " + unpackFolder.getAbsolutePath()
                + " couldn't generated!" );
        }
        return unpackFolder;
    }

    private void deleteFolderOfPreviousRunIfExist( File folderOfPreviousRun )
        throws MojoExecutionException
    {
        
        if ( folderOfPreviousRun.exists() )
        {
            try
            {
                FileUtils.deleteDirectory( folderOfPreviousRun );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Failure while deleting " + folderOfPreviousRun.getAbsolutePath(),
                                                  e );
            }
        }
    }

    private void filterResources( File outputDirectory )
        throws MojoExecutionException
    {

        Resource res = new Resource();
        // TODO: Check how to prevent hard coding here?
        res.setDirectory( getSourceDirectory().getAbsolutePath() );
        res.setFiltering( true );
        // TODO: Check if it makes sense to make this list configurable?
        res.setIncludes( Collections.singletonList( "**/*" ) );

        List<String> filtersFile = new ArrayList<String>();
        MavenResourcesExecution execution =
            new MavenResourcesExecution( Collections.singletonList( res ), outputDirectory, getMavenProject(),
                                         getEncoding(), filtersFile, nonFilteredFileExtensions, getMavenSession() );

        execution.setEscapeString( escapeString );
        execution.setSupportMultiLineFiltering( supportMultiLineFiltering );
        // TODO: Check if we need a parameter?
        execution.setIncludeEmptyDirs( true );
        execution.setEscapeWindowsPaths( escapeWindowsPaths );
        execution.setFilterFilenames( fileNameFiltering );
        //// execution.setFilters( filters );
        //
        // // TODO: Check if we need a parameter?
        execution.setOverwrite( true );
        execution.setDelimiters( delimiters, useDefaultDelimiters );
        execution.setEncoding( getEncoding() );
        //
        // execution.setUseDefaultFilterWrappers( true );

        if ( nonFilteredFileExtensions != null )
        {
            execution.setNonFilteredFileExtensions( nonFilteredFileExtensions );
        }

        try
        {
            mavenResourcesFiltering.filterResources( execution );
        }
        catch ( MavenFilteringException e )
        {
            getLog().error( "Failure during filtering.", e );
            throw new MojoExecutionException( "Failure during filtering", e );
        }

    }

    private String getArchiveExtensionOfTheProjectMainArtifact()
        throws MojoExecutionException
    {
        if ( getMavenProject().getArtifact() == null )
        {
            throw new MojoExecutionException( "No main artifact has been set yet." );
        }

        if ( getMavenProject().getArtifact().getFile() == null )
        {
            throw new MojoExecutionException( "No main artifact file has been set yet." );
        }

        return FileUtils.getExtension( getMavenProject().getArtifact().getFile().getAbsolutePath() ).toLowerCase();

    }

    private File createPluginResourceOutput()
        throws MojoExecutionException
    {
        // TODO: Should we use a different name? Or temp File?
        File resourceResult = new File( getOutputDirectory(), "configuration-maven-plugin-resource-output" );

        deleteFolderOfPreviousRunIfExist( resourceResult );

        if ( !resourceResult.mkdirs() )
        {
            throw new MojoExecutionException( "Failure while trying to create " + resourceResult.getAbsolutePath() );
        }

        return resourceResult;
    }

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {

        String archiveExt = getArchiveExtensionOfTheProjectMainArtifact();

        File unpackFolder = createUnpackFolder();

        File resourceResult = createPluginResourceOutput();

        filterResources( resourceResult );

        // Currently we use the main artifact of the project
        // TODO: May be should make this configurable?
        unarchiveFile( getMavenProject().getArtifact().getFile(), unpackFolder, archiveExt );

        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir( resourceResult );
        ds.setExcludes( new String[] { ".", "" } ); // Work a round ?
        ds.addDefaultExcludes();

        ds.scan();

        String[] includedDirectories = ds.getIncludedDirectories();

        if ( includedDirectories.length == 0 )
        {
            getLog().warn( "No folders found." );
            return;
        }

        getLog().info( "" );
        getLog().info( "We have found " + includedDirectories.length + " environments." );

        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < includedDirectories.length; i++ )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ',' );
            }
            sb.append( includedDirectories[i] );
        }

        getLog().info( "We have the following environments: " + sb.toString() );
        getLog().info( "" );

        for ( String folder : includedDirectories )
        {
            getLog().info( "Building Environment: '" + folder + "'" );

            // Check why this can happen?
            if ( folder.isEmpty() )
            {
                getLog().warn( "The given folder '" + folder + "' is empty." );
                continue;
            }

            try
            {
                File targetFolder = new File( resourceResult, folder );
                File createArchiveFile = createArchiveFile( unpackFolder, targetFolder, folder, archiveExt );
                getProjectHelper().attachArtifact( getMavenProject(), getMavenProject().getPackaging(), folder,
                                                   createArchiveFile );
            }
            catch ( NoSuchArchiverException e )
            {
                getLog().error( "Archive creation failed.", e );
            }
            catch ( IOException e )
            {
                getLog().error( "IO Exception.", e );
            }
        }

    }

    private void createGZIPArchive( String includes )
        throws IOException, NoSuchArchiverException
    {
        try
        {
            File baseFolder = new File( getSourceDirectory(), includes );
            File theOriginalFile = new File( baseFolder, "first.properties" );
            Archiver gzipArchiver = manager.getArchiver( "gzip" );

            gzipArchiver.addFile( theOriginalFile, "first.properties.gz" );

            gzipArchiver.setDestFile( new File( baseFolder, "first.properties.gz" ) );
            gzipArchiver.createArchive();
        }
        catch ( ArchiverException e )
        {
            getLog().error( "Archive creation failed.", e );
        }

    }

    private void unarchiveFile( File sourceFile, File destDirectory, String archiveExt )
        throws MojoExecutionException
    {
        try
        {
            UnArchiver unArchiver = manager.getUnArchiver( archiveExt );

            unArchiver.setSourceFile( sourceFile );
            unArchiver.setUseJvmChmod( true );
            unArchiver.setDestDirectory( destDirectory );
            unArchiver.setOverwrite( true );
            unArchiver.extract();
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "Error unpacking file [" + sourceFile.getAbsolutePath() + "]" + " to ["
                + destDirectory.getAbsolutePath() + "]", e );
        }
        catch ( NoSuchArchiverException e )
        {
            getLog().error( "Unknown archiver." + " with unknown extension [" + archiveExt + "]" );
        }
    }

    private File createArchiveFile( File unpackFolder, File targetFolder, String folder, String archiveExt )
        throws NoSuchArchiverException, IOException, MojoExecutionException
    {
        final MavenArchiver mavenArchiver = new MavenArchiver();

        mavenArchiver.setArchiver( jarArchiver );

        jarArchiver.addFileSet( new DefaultFileSet( targetFolder ) );
        jarArchiver.addFileSet( new DefaultFileSet( unpackFolder ) );
        // jarArchiver.setDuplicateBehavior( duplicate );

        File resultArchive = getArchiveFile( getOutputDirectory(), getFinalName(), folder, archiveExt );

        mavenArchiver.setOutputFile( resultArchive );
        try
        {
            mavenArchiver.createArchive( getMavenSession(), getMavenProject(), getArchive() );
        }
        catch ( ArchiverException | ManifestException | DependencyResolutionRequiredException e )
        {
            getLog().error( e.getMessage(), e );
            throw new MojoExecutionException( e.getMessage(), e );
        }

        return resultArchive;

    }

    /**
     * Returns the archive file to generate, based on an optional classifier.
     *
     * @param basedir the output directory
     * @param finalName the name of the ear file
     * @param classifier an optional classifier
     * @return the file to generate
     */
    private File getArchiveFile( File basedir, String finalName, String classifier, String archiveExt )
    {
        if ( basedir == null )
        {
            throw new IllegalArgumentException( "basedir is not allowed to be null" );
        }
        if ( finalName == null )
        {
            throw new IllegalArgumentException( "finalName is not allowed to be null" );
        }

        StringBuilder fileName = new StringBuilder( finalName );

        if ( hasClassifier( classifier ) )
        {
            fileName.append( "-" ).append( classifier );
        }

        fileName.append( '.' );
        fileName.append( archiveExt );

        return new File( basedir, fileName.toString() );
    }

    private boolean hasClassifier( String classifier )
    {
        boolean result = false;
        if ( classifier != null && classifier.trim().length() > 0 )
        {
            result = true;
        }

        return result;
    }

}
