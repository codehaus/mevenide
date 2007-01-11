package de.qu.idea.plugin.maven.common;

import de.qu.idea.plugin.maven.build.MavenOptions;
import de.qu.idea.plugin.maven.build.util.BuildConstants;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Configuration;
import org.testng.annotations.Test;

/**
 * MavenBuildPluginSettings Tester.
 *
 * @author peter
 * @version $Revision$, $Date$
 * @created Dezember 26, 2006
 * @since 1.0
 */
public class MavenBuildPluginSettingsTest {
  private MavenBuildPluginSettings settings;

  @Configuration(beforeTest = true)
  public void setUp() {
  }

  @Test
  public void testGetMavenOptions() {
    settings = new MavenBuildPluginSettings();
    Assert.assertNotNull(settings.getMavenOptions());
    MavenOptions options = new MavenOptions();
    options.setActivateProfiles("activateProfile");
    options.setBatchMode(true);
    Assert.assertNotNull(options);
    settings.setMavenOptions(options);
    Assert.assertNotNull(settings.getMavenOptions());
  }

  @Test
  public void testSetMavenOptions() {
    settings = new MavenBuildPluginSettings();
    MavenOptions options = new MavenOptions();
    options.setActivateProfiles("activateProfile");
    options.setBatchMode(true);
    Assert.assertNotNull(options);
    settings.setMavenOptions(options);
    Assert.assertNotNull(settings.getMavenOptions());
  }

  @Test
  public void testGetMavenSettingsFile() {
    settings = new MavenBuildPluginSettings();
    String systemProperty =
        System.getProperty("user.home") + System.getProperty("file.separator") + ".m2"
            + System.getProperty("file.separator") + BuildConstants.FILENAME_MAVEN_SETTINGS_FILE;
    if (StringUtils.isBlank(systemProperty)) {
      Assert.assertNull(settings.getMavenSettingsFile());
    } else {
      Assert.assertNotNull(settings.getMavenSettingsFile());
    }
  }

  @Test
  public void testSetMavenSettingsFile() {
    settings = new MavenBuildPluginSettings();
    settings.setMavenSettingsFile("A settings file");
    Assert.assertNotNull(settings.getMavenSettingsFile());
  }

  @Test
  public void testGetMavenCommandLineParams() {
    settings = new MavenBuildPluginSettings();
    settings.setMavenCommandLineParams("A command line param");
    Assert.assertNotNull(settings.getMavenCommandLineParams());
  }

  @Test
  public void testGetMavenHome() {
    settings = new MavenBuildPluginSettings();
    settings.setMavenHome("Maven Home");
    Assert.assertNotNull(settings.getMavenHome());
  }

  @Test
  public void testGetMavenRepository() {
    settings = new MavenBuildPluginSettings();
    settings.setMavenRepository("Maven Repository");
    Assert.assertNotNull(settings.getMavenRepository());
  }

  @Test
  public void testIsScanForExistingPoms() {
    settings = new MavenBuildPluginSettings();
    Assert.assertFalse(settings.isScanForExistingPoms());
    settings.setScanForExistingPoms(true);
    Assert.assertTrue(settings.isScanForExistingPoms());
  }

  @Test
  public void testIsUseMavenEmbedder() {
    settings = new MavenBuildPluginSettings();
    Assert.assertFalse(settings.isUseMavenEmbedder());
    settings.setUseMavenEmbedder(true);
    Assert.assertTrue(settings.isUseMavenEmbedder());
  }
}
