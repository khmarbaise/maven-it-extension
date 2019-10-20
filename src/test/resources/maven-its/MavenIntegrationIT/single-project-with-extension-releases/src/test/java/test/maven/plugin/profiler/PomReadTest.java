package test.maven.plugin.profiler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.annotations.Test;

public class PomReadTest extends TestBase {

    public class PomReadFromResources {

        @Test
        public void shouldReturnGAVOfThePom() throws IOException, XmlPullParserException {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/first-pom.xml");
            PomRead pomRead = new PomRead();
            Model model = pomRead.readModel(resourceAsStream);

            assertThat(model.getGroupId()).isNull();
            assertThat(model.getArtifactId()).isEqualTo("mod-rest");
            assertThat(model.getVersion()).isNull();
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void shouldFailCauseTheArtifactDoesNotDefineAVersion() throws IOException, XmlPullParserException {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/first-pom.xml");
            PomRead pomRead = new PomRead();
            Model model = pomRead.readModel(resourceAsStream);
            String expectedVersion = pomRead.getPomVersion(model);
            assertThat(expectedVersion).isEqualTo("0.1.0-SNAPSHOT");
        }

        @Test
        public void shouldReturnVersion() throws IOException, XmlPullParserException {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom-without-parent.xml");
            PomRead pomRead = new PomRead();
            Model model = pomRead.readModel(resourceAsStream);
            String expectedVersion = pomRead.getPomVersion(model);
            assertThat(expectedVersion).isEqualTo("0.1.0-SNAPSHOT");
        }

        @Test
        public void shouldReturnVersionOfTheArtifactAndNotOfTheParent() throws IOException, XmlPullParserException {
            InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom-with-parent-and-version.xml");
            PomRead pomRead = new PomRead();
            Model model = pomRead.readModel(resourceAsStream);
            String expectedVersion = pomRead.getPomVersion(model);
            assertThat(expectedVersion).isEqualTo("0.2.0-SNAPSHOT");
        }
    }

    public class PomReadFromFile {

        @Test
        public void shouldReturnVersionOfTheArtifactReadFromFileViaGetPomVersion() throws IOException, XmlPullParserException {
            String pomFile = getTestResourcesDirectory() + File.separator + "pom-with-parent-and-version.xml";
            File f = new File(pomFile);

            PomRead pomRead = new PomRead();
            Model model = pomRead.readModel(f);
            String expectedVersion = pomRead.getPomVersion(model);
            assertThat(expectedVersion).isEqualTo("0.2.0-SNAPSHOT");
        }

        @Test
        public void shouldReturnVersionOfTheArtifactReadFromFileViaGetVersionFromPom() throws IOException, XmlPullParserException {
            String pomFile = getTestResourcesDirectory() + File.separator + "pom-with-parent-and-version.xml";
            File f = new File(pomFile);

            PomRead pomRead = new PomRead();
            String expectedVersion = pomRead.getVersionFromPom(f);
            assertThat(expectedVersion).isEqualTo("0.2.0-SNAPSHOT");
        }

    }
}
