package eu.planets_project.ifr.core.services.migration.genericwrapper2;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.planets_project.ifr.core.services.migration.genericwrapper2.exceptions.MigrationException;
import eu.planets_project.services.datatypes.Parameter;

/**
 * Public accessible interface for migration paths.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public interface MigrationPath {

	/**
	 * Test whether the tool expects its input from a temporary input file or
	 * standard input. If the response is <code>true</code> then the caller must
	 * call {@link #getTempSourceFile()} to get hold of the temp input file
	 * 
	 * @return <code>true</code> if the tool must have its input via a temporary
	 *         file and otherwise <code>false</code>
	 */
	boolean useTempSourceFile();

	/**
	 * Get the temp file object for the input fule
	 * 
	 * @return the temp file
	 */
	TempFile getTempSourceFile();

	/**
	 * Test whether the tool needs to write its output to a temporary file or
	 * standard input. If the response is <code>true</code> then the caller must
	 * call {@link #getTempOutputFile} to get the output file
	 * 
	 * @return <code>true</code> if the tool needs a temporary file to be
	 *         created for its output and otherwise <code>false</code>
	 */
	boolean useTempDestinationFile();

	/**
	 * Get the temp output file
	 */
	TempFile getTempOutputFile();

	/**
	 * Get the command line with the parameter identifiers substituted with the
	 * parameters specified by <code>toolParameters</code>, and any tempfiles
	 * replaces with their absolute location.
	 * 
	 * The command line should be ready to feed into the processrunner.
	 * 
	 * Note that all the temp files must have been initialised with File objects
	 * by this time, as the absolute location of these files are replaced into
	 * the command line
	 * 
	 * @param toolParameters
	 *            the parameters to the tool
	 * @return String containing the processed command line, ready for
	 *         execution.
	 * @throws MigrationException
	 *             if not all necessary parameters, or temporary files were
	 *             defined in order to substitute all the identifiers in the
	 *             command line.
	 */
	List<String> getCommandLine(Collection<Parameter> toolParameters)
			throws MigrationException;

	/**
	 * Get the unprocessed command line.
	 * 
	 * @return String containing the unprocessed command line, containing
	 *         parameter identifiers/keys.
	 */
	List<String> getCommandLine();

	/**
	 * Get the ID of the default preset category to apply if no preset or
	 * parameters are specified by the user of this migration path.
	 * 
	 * @return ID of the default preset category
	 */
	String getDefaultPreset();

	/**
	 * Get the destination format <code>URI</code> of this migration path.
	 * 
	 * @return <code>URI</code> identifying the destination format of this
	 *         migration path.
	 */
	URI getDestinationFormat();

	/**
	 * Get the source format <code>URI</code> of this migration path.
	 * 
	 * @return <code>URI</code> identifying the source format of this migration
	 *         path.
	 */
	URI getSourceFormat();

	/**
	 * Get a map defining the relationship between the identifiers in the
	 * command line that should be substituted with file names of temporary
	 * files with the actual names of these. However, not all labels (keys in
	 * the map) are guaranteed to be associated with a file name, thus the
	 * caller of this method will have to add these mappings before passing them
	 * on to the {@link getCommandLine} method.
	 * 
	 * @return a map containing a paring of temp. file labels and optionally a
	 *         file name.
	 */
	Map<String, String> getTempFileDeclarations();

	/**
	 * Get all the parameters that must be initialised in order to execute the
	 * command line. The returned <code>Parameter</code> instances have no value
	 * specified, thus, their values must be initialised prior calling the
	 * {@link #getCommandLine} method to obtain the actual command line to
	 * execute.
	 * 
	 * @return <code>Collection</code> containing an <code>Parameter</code>
	 *         instance for each parameter that must be specified in order to
	 *         execute the command line of this migration path.
	 */
	Collection<Parameter> getToolParameters();

	/**
	 * Get a collection of all available preset categories for this migration
	 * path.
	 * 
	 * @return <code>Collection</code> containing the names/IDs of all the
	 *         available preset categories.
	 */
	Collection<String> getToolPresetCategories();

	//TODO: Consider killing this method. It makes this interface swiss-armyknifish
	eu.planets_project.services.datatypes.MigrationPath getAsPlanetsPath();
}