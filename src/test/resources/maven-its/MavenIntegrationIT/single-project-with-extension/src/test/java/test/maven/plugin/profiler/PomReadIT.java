package test.maven.plugin.profiler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.maven.plugin.profiler.PomRead;

public class PomReadIT extends TestBase {

    public final static String LINE_FEED = System.getProperty("line.separator");

    private StringBuilder consoleOutputStdErr;
    private StringBuilder consoleOutputStdOut;

    public class InterceptorStdErr extends PrintStream {

        public InterceptorStdErr(OutputStream out) {
            super(out, true);
        }

        public void println(String s) {
            consoleOutputStdErr.append(s);
            consoleOutputStdErr.append(LINE_FEED);
        }

        public void print(String s) {
            consoleOutputStdErr.append(s);
        }
    }

    public class InterceptorStdOut extends PrintStream {

        public InterceptorStdOut(OutputStream out) {
            super(out, true);
        }

        public void println(String s) {
            consoleOutputStdOut.append(s);
            consoleOutputStdOut.append(LINE_FEED);
        }

        public void print(String s) {
            consoleOutputStdOut.append(s);
        }
    }

    private PrintStream originalStdOut;
    private PrintStream originalStdErr;

    @BeforeMethod
    public void beforeMethod() {
        consoleOutputStdErr = new StringBuilder();
        consoleOutputStdOut = new StringBuilder();

        originalStdErr = System.err;
        originalStdOut = System.out;

        PrintStream interceptorStdOut = new InterceptorStdOut(originalStdOut);
        PrintStream interceptorStdErr = new InterceptorStdErr(originalStdErr);

        System.setOut(interceptorStdOut);
        System.setErr(interceptorStdErr);
    }

    @Test
    public void shouldFailWithErrorBasedOnTooLessArguments() {
        PomRead.main(new String[] {});

        //@formatter:off
        String expectedLines = 
              "Invalid number of arguments." + LINE_FEED
            + LINE_FEED
            + "usage: pom.xml" + LINE_FEED;
        //@formatter:on

        assertThat( consoleOutputStdErr.toString()).isEqualTo(expectedLines);
        assertThat(consoleOutputStdOut.toString()).isEmpty();
    }

    @Test
    public void shouldFailWithExceptionCauseArtifactDoesNotDefineAVersion() {
        String testResourcesLocation = getTestResourcesDirectory();
        PomRead.main(new String[] { testResourcesLocation + File.separator + "first-pom.xml" });

        //@formatter:off
        String expectedErrorLines = 
                "The artifact does not define a version." + LINE_FEED;
        //@formatter:on
        assertThat(consoleOutputStdErr.toString()).isEqualTo(expectedErrorLines);
        assertThat(consoleOutputStdOut.toString()).isEmpty();
    }

    @Test
    public void shouldFailWithErrorBasedOnTooMuchArguments() {
        PomRead.main(new String[] { "first", "second" });

        //@formatter:off
        String expectedLines = 
              "Invalid number of arguments." + LINE_FEED
            + LINE_FEED
            + "usage: pom.xml" + LINE_FEED;
        //@formatter:on

        assertThat(consoleOutputStdErr.toString()).isEqualTo(expectedLines);
        assertThat(consoleOutputStdOut.toString()).isEmpty();
    }

    @Test
    public void shouldFailWithErrorBasedOnNonExistingFile() {
        String testResourcesLocation = getTestResourcesDirectory() + File.separator + "pom.xml";
        PomRead.main(new String[] { testResourcesLocation });

        String expectedLine = "File " + testResourcesLocation + " can not be accessed or does not exist." + LINE_FEED;
        assertThat(consoleOutputStdErr.toString()).isEqualTo(expectedLine);
        assertThat(consoleOutputStdOut.toString()).isEmpty();
    }

    @Test
    public void shouldReadPomFileAndGiveBackVersionNumber() {
        String testResourcesLocation = getTestResourcesDirectory();
        PomRead.main(new String[] { testResourcesLocation + File.separator + "pom-with-parent-and-version.xml" });

        String expectedLine = "0.2.0-SNAPSHOT" + LINE_FEED;
        assertThat(consoleOutputStdErr.toString()).isEmpty();
        assertThat(consoleOutputStdOut.toString()).isEqualTo(expectedLine);
    }

}
