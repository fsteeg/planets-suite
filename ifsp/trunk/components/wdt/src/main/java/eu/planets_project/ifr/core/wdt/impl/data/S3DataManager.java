package eu.planets_project.ifr.core.wdt.impl.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.DigitalObject;

public class S3DataManager implements DigitalObjectManager {

    public static final String SAMPLES_PROPERTIES_NAME = "s3.properties";
    public static final String AWS_ACCESS_KEY_PROPERTY_NAME = "awsAccessKey";
    public static final String AWS_SECRET_KEY_PROPERTY_NAME = "awsSecretKey";
    public static final String AWS_S3_ENDPOINT = "awsS3Endpoint";

    // A logger for this:
    private static PlanetsLogger log = PlanetsLogger.getLogger(S3DataManager.class);

    private static AWSCredentials awsCredentials;
    private static URI localDataURI;
    private String bucket;
    private S3Service s3Service;
    private Date expiryDate;
    private S3Bucket planetsBucket = null;
    
    public S3DataManager() {  
    	awsCredentials = null;
    	s3Service = null;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        expiryDate = cal.getTime();
    	try {
    		awsCredentials = loadAWSCredentials();
    	} catch (IOException ex) {
			log.error("IO exception - unable to load " + SAMPLES_PROPERTIES_NAME);
			log.error(ex.getMessage());
			log.error(ex.getStackTrace());
    	}
    	if (awsCredentials!=null) {
	    	try {
				s3Service = new RestS3Service(awsCredentials);
			} catch (S3ServiceException e) {
				log.error("S3 service exception - unable to connect with given credentials.");
				log.error(e.getMessage());
				log.error(e.getStackTrace());
			}
    	}
		if (s3Service!=null) {
			try {
				planetsBucket = s3Service.getBucket(bucket);
			} catch (S3ServiceException e) {
				e.printStackTrace();
				log.error("S3DataManager: unable to load planets bucket: " + bucket);
			}
		}
    }
    
	public List<Class<? extends Query>> getQueryTypes() {
		// S3 is not a queryable data source
		return null;
	}

	public boolean isWritable(URI pdURI) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<URI> list(URI pdURI) {
//		System.out.println("list method called with URI: " + pdURI);
		String query = pdURI.getQuery();
		if (query!=null) {
			// This is a file - S3 directories do not have a query association
			return null;
		}
		String path = pdURI.getPath();
		if (!path.startsWith(new String("/"+bucket))) {
			log.error("S3DataManager URI Exception: does not refer to configured planets bucket: " + pdURI);
			return null;
		}
		if (planetsBucket==null) {
			log.error("S3DataManager: unable to load planets bucket: " + bucket);
			return null;
		}
		ArrayList<URI> aldo = new ArrayList<URI>();
		int i = path.lastIndexOf('/');
		String filter = "";
		if (i>bucket.length()) {
			filter = path.substring(bucket.length()+2,i+1);
		}
		S3Object[] filteredObjects = null;
		if (filter.equals("")) {
			try {
				filteredObjects = s3Service.listObjects(planetsBucket);
			} catch (S3ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				filteredObjects = s3Service.listObjects(planetsBucket, filter, null);
			} catch (S3ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (filteredObjects!=null) {
	        for (int o = 0; o < filteredObjects.length; o++) {
	        	String key = filteredObjects[o].getKey();
	        	String modkey = key;
	            if (!filter.equals("")) {
		            int k = key.indexOf(filter) + filter.length();
		            modkey = key.substring(k);
	            }
	            int j = modkey.lastIndexOf('/');
	            if (modkey.length()>0) {
		            if (j==modkey.length()-1) {
	            		// it is a directory
	            		String dirUrl = localDataURI.toString() + "/" + filter + modkey;
		                try {
							aldo.add(new URI(dirUrl));
						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	} else if (j==-1){
	            		// it is a file
		                String signedUrl = "";
						try {
							signedUrl = S3Service.createSignedGetUrl(planetsBucket.getName(), key, 
							        awsCredentials, expiryDate, false);
						} catch (S3ServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                try {
							aldo.add(new URI(signedUrl));
						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	}
	            }
	        }
		}
		return aldo;
	}

	public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
		// S3 is not a queryable data source
		return null;
	}

	public DigitalObject retrieve(URI pdURI)
			throws DigitalObjectNotFoundException {
		URL dobURL = null;
		try {
			dobURL = pdURI.toURL();
		} catch (MalformedURLException e) {
			log.error("\nSelected digital object has an invalid URL!");
			log.error(e.getStackTrace());
		}
		DigitalObject o = new DigitalObject.Builder(Content
				.byReference(dobURL)).build();
		return o;
	}

	public void store(URI pdURI, DigitalObject digitalObject)
			throws DigitalObjectNotStoredException {
		// TODO Auto-generated method stub

	}
	
    public URI getRootURI() {
        return localDataURI;
    }
   
    /**
     * Loads AWS Credentials from the file <tt>samples.properties</tt>
     * ({@link #SAMPLES_PROPERTIES_NAME}) that must be available in the  
     * classpath, and must contain settings <tt>awsAccessKey</tt> and 
     * <tt>awsSecretKey</tt>.
     * 
     * @return
     * the AWS credentials loaded from the samples properties file.
     */
    public AWSCredentials loadAWSCredentials() throws IOException {

        Properties testProperties = new Properties();        
        testProperties.load(this.getClass().getResourceAsStream(SAMPLES_PROPERTIES_NAME));
        
        if (testProperties == null) {
            throw new RuntimeException("Unable to load test properties file from classpath: " 
                + SAMPLES_PROPERTIES_NAME);
        }
        
        if (!testProperties.containsKey(AWS_ACCESS_KEY_PROPERTY_NAME)) {
            throw new RuntimeException(
                "Properties file 'test.properties' does not contain required property: " + AWS_ACCESS_KEY_PROPERTY_NAME); 
        }        
        if (!testProperties.containsKey(AWS_SECRET_KEY_PROPERTY_NAME)) {
            throw new RuntimeException(
                "Properties file 'test.properties' does not contain required property: " + AWS_SECRET_KEY_PROPERTY_NAME); 
        }
        
        if (!testProperties.containsKey(AWS_S3_ENDPOINT)) {
            throw new RuntimeException(
                "Properties file 'test.properties' does not contain required property: " + AWS_S3_ENDPOINT); 
        }
        
        AWSCredentials awsCredentials = new AWSCredentials(
            testProperties.getProperty(AWS_ACCESS_KEY_PROPERTY_NAME),
            testProperties.getProperty(AWS_SECRET_KEY_PROPERTY_NAME));
        
        String uriString = testProperties.getProperty(AWS_S3_ENDPOINT);
        bucket = uriString.substring(uriString.lastIndexOf('/')+1);
        
        try {
			localDataURI = new URI(uriString);
		} catch (URISyntaxException e) {
			log.error("URI syntax exception - unable to form URI from " + uriString);
			log.error(e.getMessage());
			log.error(e.getStackTrace());
		}
        
        return awsCredentials;        
    }

}