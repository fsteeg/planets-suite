/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package eu.planets_project.tb.api.model;

import java.util.List;

import eu.planets_project.tb.api.model.benchmark.BenchmarkGoal;
import eu.planets_project.tb.api.services.TestbedServiceTemplate;
import eu.planets_project.tb.impl.exceptions.InvalidInputException;

/**
 * @author alindley
 * The phase ExperimentSetup covers the steps of 1-3 of the Planets Testbed
 * process model.
 * Step 1 define basic properties;
 * Step 2 design the experiment's workflow (= services, data, configuration)
 * Step 3 specify ressources
 * @author alindley
 *
 */
public interface ExperimentSetup extends ExperimentPhase{

	//Step 1:
		//BasicProperties
	public void setBasicProperties(BasicProperties props);
	public BasicProperties getBasicProperties();
	
		//BenchmarkGoals
	/**
	 * Adds a given BenchmarkGoal to the ones used within this Experiment
	 * @param goal
	 */
	public void addBenchmarkGoal(BenchmarkGoal goal);
	public void addBenchmarkGoals(List<BenchmarkGoal> goal);
	public void removeBenchmarkGoal(String sBenchmarkGoalXMLID);
	public void removeBenchmarkGoal(String sCategory, String sName);
	public void removeBenchmarkGoals(List<String> goalXMLIDs);
	
	/**
	 * Sets a list of BenchmarkGoals for this Experiment.
	 * "set" always overrides "add".
	 * @param goals
	 */
	public void setBenchmarkGoals(List<BenchmarkGoal> goals);

	public BenchmarkGoal getBenchmarkGoal(String sGoalXMLID);
	
	/**
	 * Returns all BenchmarkGoals that are used within this Experiment.
	 * @return Objective
	 */
	public List<BenchmarkGoal> getAllAddedBenchmarkGoals();
	
	/**
	 * When the BenchmarkGoalsList is set to final, it's not possible to add or remove
	 * BenchmarkObjectives to/from the Experiment
	 * <p>
	 */
	//public void setBenchmarkGoalListFinal();
	//public boolean isBenchmarkGoalListFinal();

		//ExperimentType
	/**
	 * @param iTypeID
	 * @see AdminManager.getExperimentTypeID
	 */
	public void setExperimentType(String sexpTypeID)throws InvalidInputException;
	public String getExperimentTypeName();
	public String getExperimentTypeID();
	
	
	//Step 2:
	/**
	 * Inits also inits the experiment's executable (contains servicesTemplate, data, configuration)
	 * @param workflow
	 */
	public void setServiceTemplate(TestbedServiceTemplate template);
	/**
	 * Retrieves the TBServiceTemplate form the executable part of an experiment
	 * @return null if non has been registered
	 */
	public TestbedServiceTemplate getServiceTemplate();
	/**
	 * Removes an already added ServiceTemplate (i.e. the information which endpoint to call, etc.)
	 */
	public void removeServiceTemplate();
	

	//Step 3: specify resources
	public void setExperimentResources(ExperimentResources experimentResources);
	public ExperimentResources getExperimentResources();
	
	
	final int SUBSTAGE1 = 1;
	final int SUBSTAGE2 = 2;
	final int SUBSTAGE3 = 3;
	
	//Temporary Helper:
	public void setSubStage(int iSubStage);
	public int getSubStage();


}
