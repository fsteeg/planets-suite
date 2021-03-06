package eu.planets_project.ifr.core.services.identification.droid.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.FileFormatHit;
import uk.gov.nationalarchives.droid.IdentificationFile;

import com.sun.xml.ws.developer.StreamingAttachment;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.common.conf.ServiceConfig;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Tool;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.utils.DigitalObjectUtils;
import eu.planets_project.services.utils.ServiceUtils;

/**
 * Droid identification service.
 * @author Fabian Steeg, Carl Wilson
 */
@Stateless
@WebService(
		name = Droid.NAME, 
		serviceName = Identify.NAME, 
		targetNamespace = PlanetsServices.NS, 
		endpointInterface = "eu.planets_project.services.identify.Identify")
@MTOM
@StreamingAttachment( parseEagerly=true, memoryThreshold=ServiceUtils.JAXWS_SIZE_THRESHOLD )
public final class Droid implements Identify, Serializable {
    private static Logger log = Logger.getLogger(Droid.class.getName());

    /**
     * The e number of ms. we want to wait in one waiting step for the
     * identification to finish
     */
    private static final int WAIT_INTERVAL = 300;
    /**
     * The maximum times we want to sleep for WAIT_INTERVAL ms. to wait for the
     * identification to finish
     */
    private static final int WAIT_MAX = 3000;
    /***/
    private static final long serialVersionUID = -7116493742376868770L;
    /***/
    static final String NAME = "Droid";

    private String classificationText;

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject,
     *      java.util.List)
     */
    public IdentifyResult identify(final DigitalObject digitalObject,
            final List<Parameter> parameters) {
        File file = DigitalObjectUtils.toFile(digitalObject);
        List<URI> types = identifyOneBinary(file);
        ServiceReport report = null;
        if (types == null || types.size() == 0) {
            report = new ServiceReport(Type.ERROR, Status.TOOL_ERROR,
                    "No identification result for: " + file);
        } else {
            report = new ServiceReport(Type.INFO, Status.SUCCESS,
                    this.classificationText);
        }
        IdentifyResult.Method method = null;
        if (AnalysisController.FILE_CLASSIFICATION_POSITIVE_TEXT
                .equals(this.classificationText)) {
            method = IdentifyResult.Method.MAGIC;
        } else if (AnalysisController.FILE_CLASSIFICATION_TENTATIVE_TEXT
                .equals(this.classificationText)) {
            method = IdentifyResult.Method.EXTENSION;
        }
        IdentifyResult result = new IdentifyResult(types, method, report);
        return result;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.identify.Identify#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription.Builder sd = new ServiceDescription.Builder(
                "DROID Identification Service", Identify.class
                        .getCanonicalName());
        sd.classname(this.getClass().getCanonicalName());
        sd
                .description("Identification service based on Droid (DROID 3.0, Signature File 16).");
        sd.author("Carl Wilson, Fabian Steeg");
        sd.tool(Tool.create(null, "DROID", "3.0-16", null,
                "http://droid.sourceforge.net/"));
        sd.furtherInfo(URI.create("http://droid.sourceforge.net/"));
        // Taking this out as logo is no longer hosted there, and this is bad practice anyway - should be hosted locally.
        //sd.logo( URI.create("http://droid.sourceforge.net/wiki/skins/snaphouston/droidlogo.gif"));
        sd.serviceProvider("The Planets Consortium.");
        return sd.build();
    }

    /**
     * Identify a file represented as a byte array using Droid.
     * @param tempFile The file to identify using Droid
     * @return Returns the Pronom IDs found for the file as URIs in a Types
     *         object
     */
    private List<URI> identifyOneBinary(final File tempFile) {
        // Determine the config directory:
        String sigFileLocation = sigFileFolder();
        // Here we start using the Droid API:
        AnalysisController controller = new AnalysisController();
        try {
            controller.readSigFile(sigFileLocation);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Failed to read sig file");
        }
        log.info("Attempting to identify " + tempFile.getAbsolutePath());
        log.info("File is of length " + tempFile.length());
        controller.addFile(tempFile.getAbsolutePath());
        controller.setVerbose(false);
        controller.runFileFormatAnalysis();
        Iterator<IdentificationFile> iterator = controller.getFileCollection()
                .getIterator();
        // We identify one file only:
        if (iterator.hasNext()) {
            IdentificationFile file = iterator.next();
            waitFor(file);
            URI[] uris = new URI[file.getNumHits()];
            log.info("Looking at results: #" + file.getNumHits());
            // Retrieve the results:
            try {
                for (int hitCounter = 0; hitCounter < file.getNumHits(); hitCounter++) {
                    FileFormatHit formatHit = file.getHit(hitCounter);
                    uris[hitCounter] = new URI("info:pronom/"
                            + formatHit.getFileFormatPUID());
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            List<URI> result = Arrays.asList(uris);
            this.classificationText = file.getClassificationText();
            return result;
        }
        return null;
    }

    /**
     * @param file The identification file to wait for
     */
    private void waitFor(final IdentificationFile file) {
        int slept = 0;
        /*
         * Droid runs the identification in a Thread, so we have to wait until
         * it finishes...
         */
        while (!classificationIsDone(file) /* ...but we won't wait forever: */
                && slept < WAIT_MAX) {
            sleep();
            slept++;
        }
    }

    /**
     * @param file The file object
     * @return True, if the file has a final, non-tentative classification
     */
    private boolean classificationIsDone(final IdentificationFile file) {
        /*
         * We seem to be hitting are rare case sometimes: The file is
         * classified, we think we are done with waiting, but that
         * classification is preliminary and would be replaced, if we'd wait a
         * little longer, e.g. having RTF 1.0 as a preliminary result, which
         * will be updated to RTF 1.5 a moment later. So we not only check if we
         * have a classification, but also if that is final, i.e. not tentative:
         */
        return file.isClassified()
                && file.getClassification() != AnalysisController.FILE_CLASSIFICATION_TENTATIVE;
    }

    /**
     * Let the current thread sleep for WAIT_INTERVAL ms.
     */
    private void sleep() {
        try {
            Thread.sleep(WAIT_INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String sigFileFolder() {
    	log.info("Droid.sigFileFolder()");
        String sigFileLocation = null;
    	Configuration conf = ServiceConfig.getConfiguration("Droid");
    	sigFileLocation = conf.getString("droid.sigfile.location") +
    		File.separator + conf.getString("droid.sigfile.name");
    	log.info("Opening signature file: " + sigFileLocation);
    	return sigFileLocation;
    }

}
