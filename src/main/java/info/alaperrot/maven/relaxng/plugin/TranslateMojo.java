package info.alaperrot.maven.relaxng.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.thaiopensource.relaxng.translate.Driver;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * <p>Translate Relax NG schemas to XML Schemas.</p>
 */
@Mojo(name = "translate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class TranslateMojo extends AbstractMojo {

    /**
     * <p>Source directory for Relax NG schema files.</p>
     *
     * <p>All Relax NG schema files (.rnc and .rng) in this directory are translated to
     * XML Schema files (.xsd).</p>
     *
     * @since 2.0
     */
	@Parameter(defaultValue = "${project.build.sourceDirectory}/main/rng", required = true)
    private File sourceDirectory;

	/**
	 * <p>Source schema file encoding.</p>
	 *
	 * <p>Useful for Relax NG Compact or Relax NG with missing or incorrect file encoding
	 * specification.</p>
     *
     * @since 2.0
	 */
	@Parameter
	private String sourceEncoding;

	/**
	 * <p>Source filename exclude patterns, relative to {@link #sourceDirectory}.</p>
	 *
	 * <p>Defaults to none.</p>
     *
     * @since 2.0
	 */
	@Parameter
	private String[] sourceExcludes;

	/**
	 * <p>Source filename include patterns, relative to {@link #sourceDirectory}.</p>
	 *
	 * <p>Defaults to "*.rnc", "*.rng".</p>
     *
     * @since 2.0
	 */
	@Parameter
	private String[] sourceIncludes;

    /**
     * <p>Output directory for translated XML Schema files.</p>
     *
     * @since 2.0
     */
	@Parameter(defaultValue = "${project.build.directory}/generated-sources/xsd", required = true)
    private File targetDirectory;

	/**
	 * <p>Target schema file encoding.</p>
	 *
	 * <p>Defaults to <code>${project.build.sourceEncoding}</code> if specified, else to
	 * UTF-8.</p>
     *
     * @since 2.0
	 */
	@Parameter(defaultValue = "${project.build.sourceEncoding}")
	private String targetEncoding;

    @Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!sourceDirectory.isDirectory()) {
			throw new MojoExecutionException(String.format("Invalid source directory %s", sourceDirectory));
		}

		if (!targetDirectory.mkdirs()) {
			throw new MojoExecutionException(String.format("Failed to create target directory %s", targetDirectory));
		}

    	if (sourceExcludes == null) {
    		sourceExcludes = new String[] {};
    	}

    	if (sourceIncludes == null || sourceIncludes.length == 0) {
    		sourceIncludes = new String[] {"*.rnc", "*.rng"};
    	}

    	if (StringUtils.isEmpty(targetEncoding)) {
    		targetEncoding = StandardCharsets.UTF_8.name();
    	}

    	getLog().debug(String.format("Source directory: %s", sourceDirectory));
    	getLog().debug(String.format("Source encoding:  %s", StringUtils.defaultString(sourceEncoding)));
    	getLog().debug(String.format("Source excludes:  %s", StringUtils.join(sourceExcludes, ", ")));
    	getLog().debug(String.format("Source includes:  %s", StringUtils.join(sourceIncludes, ", ")));
    	getLog().debug(String.format("Target directory: %s", targetDirectory));
    	getLog().debug(String.format("Target encoding:  %s", targetEncoding));

    	try {
			final String excludes = StringUtils.join(sourceExcludes, ",");
	        final String includes = StringUtils.join(sourceIncludes, ",");
			final List<File> files = FileUtils.getFiles(sourceDirectory, includes, excludes);

	        files.stream().forEach(this::translateSchema);

    	} catch (final IOException exception) {
    		throw new MojoExecutionException("Failed to translate Relax NG schemas", exception);
    	}
    }

    /**
     * Translate a Relax NG schema to an XML Schema.
     *
     * @param sourceFile Source Relax NG schema
     */
    private void translateSchema(final File sourceFile) {
    	final String targetName = FileUtils.removeExtension(sourceFile.getName()).concat(".xsd");
    	final File targetFile = new File(targetDirectory, targetName);

    	getLog().info(String.format("Translating %s to %s", sourceFile.getName(), targetFile.getName()));

    	final List<String> args = new ArrayList<>();

    	if (StringUtils.isNotEmpty(sourceEncoding)) {
    		args.add("-i");
    		args.add(String.format("encoding=%s", sourceEncoding));
    	}

    	if (StringUtils.isNotEmpty(targetEncoding)) {
    		args.add("-o");
    		args.add(String.format("encoding=%s", targetEncoding));
    	}

    	args.add(sourceFile.getAbsolutePath());
    	args.add(targetFile.getAbsolutePath());

    	final Driver driver = new Driver();
    	driver.run(args.stream().toArray(String[]::new));
    }

}
