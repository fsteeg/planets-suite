package eu.planets_project.tb.gui.backing;

import java.io.Serializable;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;


public class BenchmarkBean extends TreeNodeBase implements Serializable {

    static final long serialVersionUID = 2343216343436521112l;  
    static private PlanetsLogger log = PlanetsLogger.getLogger(FileTreeNode.class);
  	    
    String definition;
    String name;
    String description;
    String id;
    boolean selected = false;
    String srcValue;
    String trgValue;
    String evalValue;
    String weight;
    String type = "";
    String typename;
    String scale;
    String category;
    boolean autoEvalServiceRegistered = false;
    TestbedServiceTemplate autoEvalServiceTemplate;
		
    
    public BenchmarkBean() {
    	
    }
    
	public BenchmarkBean(BenchmarkGoal bm) {
		this.id = bm.getID();
		this.name = bm.getName();
		this.definition = bm.getDefinition();
		this.description = bm.getDescription();	
		this.srcValue = bm.getSourceValue();
		this.trgValue = bm.getTargetValue();
		this.evalValue = bm.getEvaluationValue();		
		this.weight = String.valueOf(bm.getWeight());
        this.type = bm.getType();
        this.typename = this.assignTypename();
        this.scale = "Very Good";
        this.category=bm.getCategory();
	} 
		
    public boolean getSelected() {
      return selected;
    }
    public void setSelected(boolean selected) {
      this.selected = selected;
    }
 
    public String getSourceValue() {
    	return srcValue;
    }
    
    public void setSourceValue(String value) {
    	this.srcValue = value;
    }

    public String getTargetValue() {
    	return trgValue;
    }
    
    public void setTargetValue(String value) {
    	this.trgValue = value;
    }

    public String getEvaluationValue() {
    	return evalValue;
    }
    
    public void setEvaluationValue(String value) {
    	this.evalValue = value;
    }

    
	public String getWeight() {
		return this.weight;
	}

	public void setWeight(String weight) {
		this.weight=weight;
	}
    
    
	public String getDefinition() {
		return this.definition;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}
				
	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id=id;
	}
	
	protected void setName(String name){
		this.name = name;
	}
	
	protected void setDefinition(String definition){
		this.definition = definition;
	}
		
	public void setDescription(String description){
		this.description = description;
	}

	public String getCategory(){
		return this.category;
	}
	
	public String getType() {            
            return type;
	}

	public void setType(String type) {
		this.type=type;
	}
        
        public String getTypename() {
         return typename;
        }
        
        public String assignTypename(){
            
            String tn="";
                        if(Integer.class.getCanonicalName().equals(this.type))
                    tn = "Integer";
            //check if the value input matches teh supported type: java.lang.Long
            if(Long.class.getCanonicalName().equals(this.type))
                    tn = "Long";
            //check if the value input matches teh supported type: java.lang.Float
            if(Float.class.getCanonicalName().equals(this.type))
                    tn = "Float";
            //check if the value input matches teh supported type: java.lang.String
            if(String.class.getCanonicalName().equals(this.type))
                    tn = "String";
            //check if the value input matches teh supported type: java.lang.Boolean
            if(Boolean.class.getCanonicalName().equals(this.type))
                    tn = "Boolean";
            return tn;
        }
        
	public String getScale() {            
            return scale;
	}

	public void setScale(String scale) {
		this.scale=scale;
	}
	
	/**
	 * Indicates if a auto evaluation service is available.
	 * i.e. imported via the import template gui
	 * @return
	 */
	public boolean isAutoEvalServiceAvailable(){
		return this.autoEvalServiceRegistered;
	}
	
	public void setAutoEvalServiceAvailable(boolean b){
		this.autoEvalServiceRegistered = b;
	}
	
	public void setAutoEvalService(TestbedServiceTemplate autoEvalTemplate){
		this.autoEvalServiceTemplate = autoEvalTemplate;
	}
	
	public TestbedServiceTemplate getAutoEvalService(){
		return this.autoEvalServiceTemplate;
	}

}
