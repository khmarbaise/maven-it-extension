package test.maven.plugin.profiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class PomRead {

    public String getPomVersion(Model model) {
        String result = model.getVersion();
        if (result == null) {
            throw new IllegalArgumentException("The artifact does not define a version.");
        }
        return result;
    }

    public Model readModel(InputStream is) throws IOException, XmlPullParserException {
        MavenXpp3Reader model = new MavenXpp3Reader();
        Model read = model.read(is);
        return read;
    }

    public Model readModel(File file) throws IOException, XmlPullParserException {
        FileInputStream fis = new FileInputStream(file);
        return readModel(fis);
    }

    public String getVersionFromPom(File pomFile) throws IOException, XmlPullParserException {
        Model model = readModel(pomFile);
        return getPomVersion(model);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid number of arguments.");
            System.err.println("");
            System.err.println("usage: pom.xml");
            return;
        }

        String pom = args[0];
        File pomFile = new File(pom);
        if (!pomFile.exists() || !pomFile.isFile() || !pomFile.canRead()) {
            System.err.println("File " + pomFile + " can not be accessed or does not exist.");
            return;
        }

        PomRead pomRead = new PomRead();
        try {
            String version = pomRead.getVersionFromPom(pomFile);
            System.out.println(version);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (XmlPullParserException e) {
            System.err.println(e.getMessage());
        }

    }
}
