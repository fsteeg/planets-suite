package eu.planets_project.services.datatypes;

import java.util.List;

public class FileFormatProperty extends Property {
	
	private String id;
	private Metrics metrics = new Metrics();
	
	public FileFormatProperty() {
		super(null, null);
		this.id = null;
		this.unit = null;
		this.type = null;
		this.description = null;
		this.metrics = new Metrics();
	}
	
	public FileFormatProperty(String name, String value) {
		super(name, value);
		this.id = null;
		this.unit = null;
		this.type = null;
		this.description = null;
		this.metrics = new Metrics();
	}
	
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	/**
	 * @return the metric
	 */
	public Metrics getMetrics() {
		if(this.metrics!=null) {
			return this.metrics;
		}
		else {
			return new Metrics();
		}
		
	}
	
	
	/**
	 * @param metric the metric to set
	 */
	public void setMetrics(List<Metric> metricsToSet) {
		this.metrics.setMetrics(metricsToSet);
	}
	
	public void setMetrics(Metrics metricsToSet) {
		this.metrics = metricsToSet;
	}
	
	public void setName(String name) {
		super.setName(name);
	}
	
	public String getName() {
		return super.getName();
	}
	
	public void setValue(String value) {
		super.setValue(value);
	}
	
	public String getValue() {
		return super.getValue();
	}



	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		String toString = 
				"----------------------------------\r\n" + 
			    "Property name: " + super.getName() + "\r\n" + 
				"         id: " + id + "\r\n" + 
				"         description: " + description + "\r\n" + 
				"         unit: " + unit + "\r\n" + 
				"         type: " + type + "\r\n\r\n";
		
				if(metrics.getList()!=null) {
					if(metrics.getList().size() > 0) {
						int i = 1;
						for (Metric metric : metrics.getList()) {
							buf.append("\r\n" + i + ") " + metric.getName() + "\r\n");
							buf.append("   " + metric.getDescription() + "\r\n");
							i++;
						}
						toString = toString + "Property metrics: " + buf.toString() + "\r\n" + 
						"----------------------------------\r\n\r\n";
					}
					else {
						toString = toString + "Property metrics count: " + 0 + "\r\n" + 
						"----------------------------------\r\n\r\n";
					}
				}
				else {
					toString = toString + "Property metrics count: " + 0 + "\r\n" + 
					"----------------------------------\r\n\r\n";
				}
				return toString;
				
	}
	
	
}
