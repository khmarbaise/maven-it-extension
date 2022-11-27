package com.soebes.itf.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.soebes.itf.jupiter.extension.AnnotationHelper.findMavenSettingsSourcesAnnotation;
import static com.soebes.itf.jupiter.extension.MavenSettingsSources.DEFAULT_SETTINGS_XML;
import static com.soebes.itf.jupiter.extension.MavenSettingsSources.DEFAULT_SOURCE;
import static com.soebes.itf.jupiter.extension.ResourceUsage.NONE;

class Settings {

  private final ExtensionContext context;
  private final DirectoryResolverResult directoryResolverResult;

  Settings(ExtensionContext context, DirectoryResolverResult directoryResolverResult) {
    this.context = context;
    this.directoryResolverResult = directoryResolverResult;
  }

  List<String> settingsSetup() {
    Optional<MavenSettingsSources> mavenSettingsSourcesAnnotation = findMavenSettingsSourcesAnnotation(context);
    boolean mavenSettingsResourcesIts = mavenSettingsSourcesAnnotation
        .map(s -> s.resourcesUsage().equals(NONE))
        .orElse(false);

    if (mavenSettingsSourcesAnnotation.isPresent()) {
      Path settingsXml = directoryResolverResult.getProjectDirectory().resolve(DEFAULT_SETTINGS_XML);

      if (mavenSettingsResourcesIts) {
        // We assume the location of the settings.xml file is given by the annotation
        // If sources is empty .. default location will be assumed
        // otherwise use the given location.
        if (!mavenSettingsSourcesAnnotation.get().sources().equals(DEFAULT_SOURCE)) {
          //Given different path

          settingsXml = directoryResolverResult
              .getProjectDirectory()
              .resolve(mavenSettingsSourcesAnnotation.get().sources())
              .resolve(mavenSettingsSourcesAnnotation.get().settingsXml());
        }
      }
      return Arrays.asList(MavenCLIOptions.SETTINGS, settingsXml.toAbsolutePath().toString());
    }
    return Collections.emptyList();
  }

}
