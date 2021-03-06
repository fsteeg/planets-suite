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
package eu.planets_project.tb.impl.model.exec;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * @author <a href="mailto:andrew.lindley@ait.ac.at">Andrew Lindley</a>
 * @since 30.10.2009
 * This class is required as some elements the Wee WorkflowResult object contains,
 * do not implement java.io.Serializable which is required for JPA
 * TODO: check http://n2.nabble.com/Combining-JPA-and-Jaxb-td209854.html#a14466676
 * which describes a way of combining JAXB and Serializable
 *
 */
@Entity
@XmlRootElement(name = "BatchWorkflowResultLog")
@XmlAccessorType(XmlAccessType.FIELD) 
public class BatchWorkflowResultLogImpl implements Serializable {
	
	/** */
    private static final long serialVersionUID = -6364553755666036646L;

    @SuppressWarnings("unused")
	@Id
    @GeneratedValue
    @XmlTransient
    private long id;

    @SuppressWarnings("unused")
	@Transient
	@XmlTransient
	private static Log log = LogFactory.getLog(BatchWorkflowResultLogImpl.class);
	
	//keeps a JAXB serialized String containing the WorkflowResultLog
    @Column(columnDefinition=ExperimentPersistencyImpl.TEXT_TYPE)
	private String serializedWorkflowResult = new String();
	
	//keep even if empty - JPA requires it.
	public BatchWorkflowResultLogImpl(){
	}

	/**
	 * @see getWorkflowResult for a unmarshalled object
	 * This method is required for JPA
	 * @return
	 */
	public String getSerializedWorkflowResult() {
		return serializedWorkflowResult;
	}


	/**
	 * @see setWorkflowResult
	 * This method is required for JPA
	 * @param serializedWorkflowResult
	 */
	public void setSerializedWorkflowResult(String serializedWorkflowResult) {
		this.serializedWorkflowResult = serializedWorkflowResult;
	}
	
}
