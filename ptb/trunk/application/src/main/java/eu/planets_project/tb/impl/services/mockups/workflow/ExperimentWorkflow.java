/**
 * 
 */
package eu.planets_project.tb.impl.services.mockups.workflow;

import java.util.Collection;
import java.util.HashMap;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.tb.impl.model.eval.MeasurementImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public interface ExperimentWorkflow {

    /**
     * @return A list of all of the properties that can be measured during this experiment workflow.
     */
    public abstract Collection<MeasurementImpl> getObservables();

    /**
     * @param parameters The set of parameters to use when invoking this workflow.
     * @throws Exception Throws an exception if there are any problems with the parameters.  FIXME Is using an Exception sane?
     */
    public void setParameters( HashMap<String,String> parameters ) throws Exception;

    /**
     * Executes the workflow.
     * 
     * @param dob The Digital Object the workflow should be executed upon.
     * @return The WorkflowResult containing results, measurements, logs etc.
     */
    public abstract WorkflowResult execute( DigitalObject dob );

}