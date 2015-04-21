package info.alaperrot.maven.relaxng.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

public class TranslateMojoTest {

	@Rule
	public MojoRule mojoRule = new MojoRule() {};

	@Rule
	public TestResources testResources = new TestResources();

    @Test
    public void testDefaults() throws Exception {
    	final File baseDirectory = testResources.getBasedir("defaults");
    	final File defaultSourceDirectory = new File(baseDirectory, "src/main/rng");
    	final File defaultTargetDirectory = new File(baseDirectory, "target/generated-sources/xsd");
    	final String[] defaultSourceIncludes = new String[] {"*.rnc", "*.rng"};

    	final File pom = new File(baseDirectory, "pom.xml");

        final TranslateMojo mojo = (TranslateMojo) mojoRule.lookupMojo("translate", pom);
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceDirectory"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceEncoding"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceExcludes"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceIncludes"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "targetDirectory"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "targetEncoding"), is(nullValue()));

        mojoRule.setVariableValueToObject(mojo, "sourceDirectory", defaultSourceDirectory);
        mojoRule.setVariableValueToObject(mojo, "sourceIncludes", defaultSourceIncludes);
        mojoRule.setVariableValueToObject(mojo, "targetDirectory", defaultTargetDirectory);

        mojo.execute();

        assertThat(defaultTargetDirectory.isDirectory(), is(true));

        assertThat(new File(defaultTargetDirectory, "bar.xsd").isFile(), is(true));
        assertThat(new File(defaultTargetDirectory, "foo.xsd").isFile(), is(true));
    }

    @Test
    public void testExcludes() throws Exception {
    	final File baseDirectory = testResources.getBasedir("excludes");
    	final File defaultSourceDirectory = new File(baseDirectory, "src/main/rng");
    	final File defaultTargetDirectory = new File(baseDirectory, "target/generated-sources/xsd");
    	final String[] defaultSourceIncludes = new String[] {"*.rnc", "*.rng"};

    	final String[] expectedSourceExcludes = new String[] {"foo.*"};

    	final File pom = new File(baseDirectory, "pom.xml");

        final TranslateMojo mojo = (TranslateMojo) mojoRule.lookupMojo("translate", pom);
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceDirectory"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceEncoding"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceExcludes"), equalTo(expectedSourceExcludes));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceIncludes"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "targetDirectory"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "targetEncoding"), is(nullValue()));

        mojoRule.setVariableValueToObject(mojo, "sourceDirectory", defaultSourceDirectory);
        mojoRule.setVariableValueToObject(mojo, "sourceIncludes", defaultSourceIncludes);
        mojoRule.setVariableValueToObject(mojo, "targetDirectory", defaultTargetDirectory);

        mojo.execute();

        assertThat(defaultTargetDirectory.isDirectory(), is(true));

        assertThat(new File(defaultTargetDirectory, "bar.xsd").isFile(), is(true));
        assertThat(new File(defaultTargetDirectory, "foo.xsd").exists(), is(false));
    }

    @Test
    public void testIncludes() throws Exception {
    	final File baseDirectory = testResources.getBasedir("includes");
    	final File defaultSourceDirectory = new File(baseDirectory, "src/main/rng");
    	final File defaultTargetDirectory = new File(baseDirectory, "target/generated-sources/xsd");

    	final String[] expectedSourceIncludes = new String[] {"foo.*"};

    	final File pom = new File(baseDirectory, "pom.xml");

        final TranslateMojo mojo = (TranslateMojo) mojoRule.lookupMojo("translate", pom);
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceDirectory"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceEncoding"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceExcludes"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "sourceIncludes"), equalTo(expectedSourceIncludes));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "targetDirectory"), is(nullValue()));
        assertThat(mojoRule.getVariableValueFromObject(mojo, "targetEncoding"), is(nullValue()));

        mojoRule.setVariableValueToObject(mojo, "sourceDirectory", defaultSourceDirectory);
        mojoRule.setVariableValueToObject(mojo, "targetDirectory", defaultTargetDirectory);

        mojo.execute();

        assertThat(defaultTargetDirectory.isDirectory(), is(true));

        assertThat(new File(defaultTargetDirectory, "bar.xsd").exists(), is(false));
        assertThat(new File(defaultTargetDirectory, "foo.xsd").isFile(), is(true));
    }

}
