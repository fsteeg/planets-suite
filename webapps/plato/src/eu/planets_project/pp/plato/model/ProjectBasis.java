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
import org.hibernate.validator.Valid;

import eu.planets_project.pp.plato.model.tree.PolicyTree;

@Entity
public class ProjectBasis implements Serializable, ITouchable {

    private static final long serialVersionUID = -3106069473781552004L;

    @Id
    @GeneratedValue
    private int id;

    private String identificationCode;

    /**
     * Description of the document types in the collection.
     *
     * Hibernate note: Standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String documentTypes;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String mandate;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String planningPurpose;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String designatedCommunity;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String applyingPolicies;

    

    @OneToOne(cascade = CascadeType.ALL)
    private TriggerDefinition triggers = new TriggerDefinition();
    
    /**
     * Relevant organisational procedures and workflows.
     *
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String organisationalProcedures;

    /**
     * Contracts and agreements specifying preservation rights.
     *
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String preservationRights;

    /**
     * Reference to agreements of maintenance and access.
     *
     * Hibernaet note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String referenceToAgreements;

    /**
     * Reference to agreements of maintenance and access.
     *
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Length(max = 32672)
    @Column(length = 32672)
    private String planRelations;

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private PolicyTree policyTree = new PolicyTree();

    public String getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(String documentTypes) {
        this.documentTypes = documentTypes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
    }


    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getApplyingPolicies() {
        return applyingPolicies;
    }

    public void setApplyingPolicies(String applyingPolicies) {
        this.applyingPolicies = applyingPolicies;
    }

    public String getDesignatedCommunity() {
        return designatedCommunity;
    }

    public void setDesignatedCommunity(String designatedCommunity) {
        this.designatedCommunity = designatedCommunity;
    }

    public String getMandate() {
        return mandate;
    }

    public void setMandate(String mandate) {
        this.mandate = mandate;
    }

    public String getPlanningPurpose() {
        return planningPurpose;
    }

    public void setPlanningPurpose(String planningPurpose) {
        this.planningPurpose = planningPurpose;
    }

    public String getOrganisationalProcedures() {
        return organisationalProcedures;
    }

    public void setOrganisationalProcedures(String organisationalProcedures) {
        this.organisationalProcedures = organisationalProcedures;
    }

    public String getPreservationRights() {
        return preservationRights;
    }

    public void setPreservationRights(String preservationRights) {
        this.preservationRights = preservationRights;
    }

    public String getReferenceToAgreements() {
        return referenceToAgreements;
    }

    public void setReferenceToAgreements(String referenceToAgreements) {
        this.referenceToAgreements = referenceToAgreements;
    }


    public String getPlanRelations() {
        return planRelations;
    }

    public void setPlanRelations(String planRelations) {
        this.planRelations = planRelations;
    }

    public PolicyTree getPolicyTree() {
        return policyTree;
    }

    public void setPolicyTree(PolicyTree policyTree) {
        this.policyTree = policyTree;
    }

    public TriggerDefinition getTriggers() {
        return triggers;
    }

    public void setTriggers(TriggerDefinition triggers) {
        this.triggers = triggers;
    }
}
