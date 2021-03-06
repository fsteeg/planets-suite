/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.validator.Length;

/**
 * Entity bean storing additional information about an Evaluation-Step.
 *
 * @author Mark Guttenbrunner
 */
@Entity
public class PlanDefinition implements Serializable, ITouchable {

    private static final long serialVersionUID = 8385664635418556928L;

    @Id @GeneratedValue
    private int id;

    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    @OneToOne(cascade=CascadeType.ALL)
    private TriggerDefinition triggers = new TriggerDefinition();
    
    private String currency = new String("EUR");

    private String costsIG;
 
    private String costsPE;

    private String costsQA;
    
    private String costsRM;
    
    private String costsPA;

    private String costsREI;

    private String costsTCO;

    @Length(max = 32672)
    @Column(length = 32672)
    private String costsRemarks;

    private String responsibleExecution;

    private String responsibleMonitoring;
    
    public int getId() {
        return id;
    }

    public String getResponsibleMonitoring() {
        return responsibleMonitoring;
    }

    public void setResponsibleMonitoring(String responsibleMonitoring) {
        this.responsibleMonitoring = responsibleMonitoring;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChangeLog getChangeLog() {
        return this.changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }
    public boolean isChanged() {
        return changeLog.isAltered();
    }
    public void touch() {
        getChangeLog().touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h){
        h.visit(this);
    }
    

    public String getCostsIG() {
        return costsIG;
    }

    public void setCostsIG(String costsIG) {
        this.costsIG = costsIG;
    }

    public String getCostsPA() {
        return costsPA;
    }

    public void setCostsPA(String costsPA) {
        this.costsPA = costsPA;
    }

    public String getCostsPE() {
        return costsPE;
    }

    public void setCostsPE(String costsPE) {
        this.costsPE = costsPE;
    }

    public String getCostsQA() {
        return costsQA;
    }

    public void setCostsQA(String costsQA) {
        this.costsQA = costsQA;
    }

    public String getCostsREI() {
        return costsREI;
    }

    public void setCostsREI(String costsREI) {
        this.costsREI = costsREI;
    }

    public String getCostsRM() {
        return costsRM;
    }

    public void setCostsRM(String costsRM) {
        this.costsRM = costsRM;
    }

    public String getCostsRemarks() {
        return costsRemarks;
    }

    public void setCostsRemarks(String costsRemarks) {
        this.costsRemarks = costsRemarks;
    }

    public String getCostsTCO() {
        return costsTCO;
    }

    public void setCostsTCO(String costsTCO) {
        this.costsTCO = costsTCO;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getResponsibleExecution() {
        return responsibleExecution;
    }

    public void setResponsibleExecution(String responsibleExecutor) {
        this.responsibleExecution = responsibleExecutor;
    }

    public TriggerDefinition getTriggers() {
        return triggers;
    }

    public void setTriggers(TriggerDefinition triggers) {
        this.triggers = triggers;
    }

}
