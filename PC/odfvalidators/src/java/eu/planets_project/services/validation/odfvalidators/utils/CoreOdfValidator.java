package eu.planets_project.services.validation.odfvalidators.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ProcessRunner;

public class CoreOdfValidator {
	
	private static final String DOC_STRICT_SCHEMA_URL_MARKER = "doc-strict-schema-url=";
	private static final String MANIFEST_SCHEMA_URL_MARKER = "manifest-schema-url=";
	private static final String DOC_SCHEMA_URL_MARKER = "doc-schema-url=";
	
	private static final String USER_DOC_STRICT_SCHEMA_PARAM = "user-doc-strict-schema";
	private static final String USER_DOC_SCHEMA_PARAM = "user-doc-schema";
	private static final String USER_MANIFEST_SCHEMA_PARAM = "user-manifest-schema";
	private static String STRICT_PARAM_NAME = "strictValidation";
	
	private static final String FORMULA_MIMETYPE = "application/vnd.oasis.opendocument.formula";
	
	// Flag section
	private static boolean STRICT_VALIDATION = false;
	private static boolean USE_USER_DOC_SCHEMA = false;
	private static boolean USE_USER_DOC_STRICT_SCHEMA = false;
	private static boolean USE_USER_MANIFEST_SCHEMA = false;
	
	// User schema files, if provided
	private static File USER_DOC_SCHEMA = null;
	private static File USER_DOC_STRICT_SCHEMA = null;
	private static File USER_MANIFEST_SCHEMA = null;
	
	private static HashMap<String, File> schemaList = new HashMap<String, File>();
	
	private static OdfSchemaHandler schemaHandler = new OdfSchemaHandler();
	private static OdfContentHandler contentHandler = null;
	
	private static String version = null;
		
	private static final String JING_HOME = System.getenv("JING_HOME");
	private static final String JING = "jing.jar";
	
	private static Log log = LogFactory.getLog(CoreOdfValidator.class);
	
	private static OdfValidatorResult result = null;
	
	private static String mimeType = null;
	
	
	public OdfValidatorResult validate(File odfFile, List<Parameter> parameters) {
		contentHandler = new OdfContentHandler(odfFile);
		result = new OdfValidatorResult(odfFile);
		
		// check if the input file is an ODF file at all
		if(!contentHandler.isOdfFile()) {
			result.setError(odfFile, "The input file '" + odfFile.getName() + "' is NOT an ODF file!");
			return result;
		}
		result.setIsOdfFile(contentHandler.isOdfFile());
		
		
		// File is ODF spec compliant, i.e. all mandatory files are included in container?
		boolean isCompliant = contentHandler.isOdfCompliant();
		result.setIsOdfCompliant(isCompliant);
		// if it is not compliant, e.g. if manifest entries are found that are not present in the container,
		// list the missing entries in the result.
		if(!isCompliant) {
			result.setMissingManifestEntries(contentHandler.getMissingManifestEntries());
		}
		
		// list the contained subfiles in this ODF container, as not all sub files are mandatory!
		List<File> xmlParts = new ArrayList<File>(contentHandler.getXmlComponents());
		// list them in the result
		result.setXmlComponents(xmlParts);
		
		// get the version of this ODF file and note in result
		version = contentHandler.getOdfVersion();
		result.setOdfVersion(version);
		
		// get the mimetype of this ODF file
		mimeType = contentHandler.getMimeType();
		// if this is a formula file, get the version of the embedded MathML
		if(mimeType.equalsIgnoreCase(FORMULA_MIMETYPE)) {
			result.setMathMLVersion(contentHandler.getMathMLVersion()); // and set it in the result
		}
		// set the mimetype
		result.setMimeType(mimeType);
		
		// check parameters
		parseParameters(parameters);
		
		// get all necessary schemas, depending on ODF version and mimetype (MathML/formula) 
		collectSchemas();
		
		log.info("Validating input file of mimeType = '" + mimeType + "'");
		
		// validate all relevant sub files in this ODF container
		for (File file : xmlParts) {
			result = validateFile(file, result);
		}
		
		// validated in strict mode? Note it in result...
		if(STRICT_VALIDATION) {
			result.setUsedStrictValidation(STRICT_VALIDATION);
		}
		reset();
		return result;
	}
	
	private static OdfValidatorResult validateFile(File odfXmlComponent, OdfValidatorResult result) {
		String name = odfXmlComponent.getName();

		// Do we have a FORMULA (MathML) file?		
		if(name.equalsIgnoreCase(OdfContentHandler.CONTENT_XML)) {
			if(mimeType.equalsIgnoreCase(FORMULA_MIMETYPE)) {
				result = validateMathML(odfXmlComponent, schemaHandler.retrieveMathML101Dtd(), schemaList.get("mathml"), result);
			}
			else {
				result = validateSubFile(odfXmlComponent, schemaList.get("doc"), result);
			}
		}
		
		// do we have the manifest.xml file here? Then validate it against the manifest schema!
		if(name.equalsIgnoreCase(OdfContentHandler.MANIFEST_XML)) {
			result = validateSubFile(odfXmlComponent, schemaList.get("manifest"), result);
		}
		
		// do we have a signature file here, then validate it against the dsig schema
		if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2) && name.equalsIgnoreCase(OdfContentHandler.DOC_DSIGS_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.MACRO_DSIGS_XML)) {
			result = validateSubFile(odfXmlComponent, schemaList.get("dsig"), result);
		}
		
		// Or do we have a 'normal' ODF subfile (content.xml, settings.xml, styles.xml, meta.xml)?
		if(name.equalsIgnoreCase(OdfContentHandler.SETTINGS_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.STYLES_XML)
				|| name.equalsIgnoreCase(OdfContentHandler.META_XML)) {
			result = validateSubFile(odfXmlComponent, schemaList.get("doc"), result);
		}
		return result;
	}
	
	
	private static OdfValidatorResult validateSubFile(File odfSubFile, File schema, OdfValidatorResult result) {
		ProcessRunner validator = new ProcessRunner();
		validator.setCommand(getJingValidateCmd(odfSubFile, schema));
		validator.run();
		
		String out = validator.getProcessOutputAsString();
	
		if(out.equalsIgnoreCase("")) {
			result.setValid(odfSubFile, true);
			log.info("'" + odfSubFile.getName() + "' is valid: " + result.componentIsValid(odfSubFile));
		}
		else {
			result.setValid(odfSubFile, false);
			result.setError(odfSubFile, out);
			log.error("'" + odfSubFile.getName() + "' is valid: " + result.componentIsValid(odfSubFile));
			log.error("Message: " + out);
		}
		return result;
	}

	private static OdfValidatorResult validateMathML(File mathmlFile, File mathmlDtd, File mathmlSchema, OdfValidatorResult result) {
		File dtdDest = new File(contentHandler.getCurrentXmlTmpDir(), mathmlDtd.getName());
		FileUtils.copyFileTo(mathmlDtd, dtdDest);
		
		File cleanedMathML = contentHandler.removeMathMLDocTypeAndNS(mathmlFile);
		StringBuffer warnings = new StringBuffer();
		if(contentHandler.containsDocTypeDeclaration(mathmlFile)) {
			result.setWarning(mathmlFile, "This files contains a DOCTYPE declaration, " +
					"which has been removed before validation!" +
					System.getProperty("line.separator") +
					"Correct Namespace declaration has been added to enable validation against the MathMl 2.0 schema!"
					+ System.getProperty("line.separator"));
		}
		
		URL schemaUrl = FileUtils.getUrlFromFile(mathmlSchema);
		
		SAXBuilder builder = new  SAXBuilder (false);
		builder.setProperty ("http://apache.org/xml/properties/schema/external-schemaLocation", schemaUrl);
		Document doc = null;
		try {
			doc = builder.build(cleanedMathML);
		} catch (JDOMException e) {
			result.setValid(mathmlFile, false);
			result.setError(mathmlFile, e.getLocalizedMessage());
			log.error("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
			log.error("Message: " + e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		result.setValid(mathmlFile, true);
		log.info("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
		log.warn("[CoreOdfValidator] validateMathML(): VALIDATION SKIPPED! This ODF file " +
				"contains MathML and is at least wellformed.");
		result.setWarning(mathmlFile, "VALIDATION SKIPPED! This ODF file " +
				"contains MathML and is at least wellformed.");
		
//		ProcessRunner mathmlValidator = new ProcessRunner(getJingValidateCmd(cleanedMathML, mathmlSchema));
//		mathmlValidator.run();
//		String out = mathmlValidator.getProcessOutputAsString();
//		String error = mathmlValidator.getProcessErrorAsString();
//		
//		if(out.equalsIgnoreCase("")) {
//			result.setValid(mathmlFile, true);
//			log.info("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
//		}
//		else {
//			result.setValid(mathmlFile, false);
//			result.setError(mathmlFile, out);
//			log.error("'" + mathmlFile.getName() + "' is valid: " + result.componentIsValid(mathmlFile));
//			log.error("Message: " + out);
//		}
		return result;
	}
	
	
	private static ArrayList<String> getJingValidateCmd(File odfXmlFile, File schemaFile) {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add("java");
		cmd.add("-jar");
		cmd.add(JING_HOME + File.separator + JING);
		cmd.add("-i");
		cmd.add(schemaFile.getAbsolutePath());
		cmd.add(odfXmlFile.getAbsolutePath());
		return cmd;
	}
	
	private void collectSchemas() {
		if(mimeType.equalsIgnoreCase(FORMULA_MIMETYPE)) {
			schemaList.put("mathml", schemaHandler.retrieveMathMLSchemaOrDtdFile(result.getMathMLVersion()));
			result.setMathMLSchema(schemaList.get("mathml"));
		}
		
		if(USE_USER_DOC_SCHEMA) {
			if(USE_USER_DOC_STRICT_SCHEMA) {
				schemaList.put("doc", USER_DOC_STRICT_SCHEMA);
				result.setStrictDocSchema(USER_DOC_STRICT_SCHEMA);
				result.setDocumentSchema(USER_DOC_STRICT_SCHEMA);
			}
			else {
				schemaList.put("doc", USER_DOC_SCHEMA);
				result.setDocumentSchema(USER_DOC_SCHEMA);
			}
		}
		else {
			schemaList.put("doc", schemaHandler.retrieveOdfDocSchemaFile(version, STRICT_VALIDATION));
			result.setDocumentSchema(schemaList.get("doc"));
		}
		if(USE_USER_MANIFEST_SCHEMA) {
			schemaList.put("manifest", USER_MANIFEST_SCHEMA);
			result.setManifestSchema(USER_MANIFEST_SCHEMA);
		}
		else {
			schemaList.put("manifest", schemaHandler.retrieveOdfManifestSchemaFile(version));
			result.setManifestSchema(schemaList.get("manifest"));
		}
		if(contentHandler.containsDsigSubFiles()) {
			if(version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2)) {
				schemaList.put("dsig", schemaHandler.retrieveDsigSchema(version));
				result.setDsigSchema(schemaList.get("dsig"));
			}
		}
	}

	private static void parseParameters(List<Parameter> parameters) {
		if(parameters!=null && parameters.size()>0) {
			for (Parameter parameter : parameters) {
				String name = parameter.getName();
				if(name.equalsIgnoreCase(STRICT_PARAM_NAME)) {
					if(!version.equalsIgnoreCase(OdfSchemaHandler.ODF_v1_2)) {
						STRICT_VALIDATION = Boolean.parseBoolean(parameter.getValue());
					}
					continue;
				}
				// Check for USER_MANIFEST_SCHEMA
				if(name.equalsIgnoreCase(USER_MANIFEST_SCHEMA_PARAM)) {
					String value = parameter.getValue();
					// check if a URL to a schema is passed?
					URL manifest_schema_url = parseForURL(value);
					if(manifest_schema_url!=null) {
						USER_MANIFEST_SCHEMA = schemaHandler.createUserManifestSchemaFromUrl(version, manifest_schema_url);
					}
					else {
						USER_MANIFEST_SCHEMA = schemaHandler.createUserManifestSchema(value);
					}
					USE_USER_MANIFEST_SCHEMA = true;
					continue;
				}
				// check for USER_DOC_SCHEMA?
				if(name.equalsIgnoreCase(USER_DOC_SCHEMA_PARAM)) {
					String value = parameter.getValue();
					URL doc_schema_url = parseForURL(value);
					if(doc_schema_url!=null) {
						USER_DOC_SCHEMA = schemaHandler.createUserDocSchemaFromUrl(version, doc_schema_url);
					}
					else {
						USER_DOC_SCHEMA = schemaHandler.createUserDocSchema(version, value);
					}
					USE_USER_DOC_SCHEMA = true;
					continue;
				}
				
				if(name.equalsIgnoreCase(USER_DOC_STRICT_SCHEMA_PARAM)) {
					String value = parameter.getValue();
					URL strict_schema_url = parseForURL(value);
					
					if(USE_USER_DOC_SCHEMA) {
						if(strict_schema_url!=null) {
							USER_DOC_STRICT_SCHEMA = schemaHandler.createUserDocStrictSchemaFromUrl(version, strict_schema_url, USER_DOC_SCHEMA);
						}
						else {
							USER_DOC_STRICT_SCHEMA = schemaHandler.createUserDocStrictSchema(version, value, USER_DOC_SCHEMA);
						}
						USE_USER_DOC_STRICT_SCHEMA = true;
						STRICT_VALIDATION = true;
						continue;
					}
					else {
						log.warn("Strict user schema provided, but missing doc schema! Please provide the doc schema first, because it is referenced in the strict schema! Then try again, thanks!");
						log.warn("Using default schemas instead!");
					}
				}
			}
			if(STRICT_VALIDATION && USE_USER_DOC_SCHEMA && !USE_USER_DOC_STRICT_SCHEMA) {
				log.warn("WARNING: You have enabled STRICT VALIDATION and passed only a not-strict user-doc-schema! Disabling STRICT_VALIDATION!");
				STRICT_VALIDATION = false;
			}
		}
		
	}

	private static URL parseForURL(String parameterValue) {
		URL url = null;
		if(parameterValue.contains(DOC_SCHEMA_URL_MARKER) 
				|| parameterValue.contains(MANIFEST_SCHEMA_URL_MARKER)
				|| parameterValue.contains(DOC_STRICT_SCHEMA_URL_MARKER)) {
			try {
				url = URI.create(parameterValue.substring(parameterValue.indexOf("=")+1)).toURL();
			} catch (MalformedURLException e) {
				log.error("No valid URL found in this Parameter!");
				return null;
			}
		}
		return url;
	}

	private void reset() {
		STRICT_VALIDATION = false;
		USE_USER_DOC_SCHEMA = false;
		USE_USER_DOC_STRICT_SCHEMA = false;
		USE_USER_MANIFEST_SCHEMA = false;
		USER_DOC_SCHEMA = null;
		USER_DOC_STRICT_SCHEMA = null;
		USER_MANIFEST_SCHEMA = null;
		mimeType = null;
	}
}
