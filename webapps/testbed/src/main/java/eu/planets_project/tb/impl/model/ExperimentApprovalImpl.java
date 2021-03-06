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
/**
 *
 */
package eu.planets_project.tb.impl.model;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import eu.planets_project.tb.api.model.ExperimentApproval;
import eu.planets_project.tb.api.model.ExperimentSetup;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;


/**
 * @author alindley
 *
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD) 
public class ExperimentApprovalImpl extends ExperimentPhaseImpl
implements ExperimentApproval, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8830653439069202361L;
	//the EntityID and it's setter and getters are inherited from ExperimentPhase
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private Vector<Integer> vReqRoles;
    @Lob
    @Column(columnDefinition=ExperimentPersistencyImpl.BLOB_TYPE)
	private Vector<String> vApprovalUsers;
	private String sDecision, sExplanation;
	private boolean bGo;
	
    //a helper reference pointer, for retrieving the experiment in the phase
    @XmlTransient
    private long lExperimentIDRef;

	@SuppressWarnings("unused")
	@Transient
    @XmlTransient
	//This annotation specifies that the property or field is not persistent.
    // This field is not used anywhere, but not removed as old data may contain it!
	private ExperimentSetup expSetup;

	//required for EJB Persistence
	public ExperimentApprovalImpl(){
		//roles as defined in the Class TestbedRoles
		vReqRoles = new Vector<Integer>();
		vApprovalUsers = new Vector<String>();
		bGo = false;
		lExperimentIDRef = -1;
		
		setPhasePointer(PHASE_EXPERIMENTAPPROVAL);
	}
	
	
    /**
     * A helper reference pointer on the experiment's ID to retrieve other phases or the
     * experiment itself if this is required.
     * @return
     */
    public long getExperimentRefID(){
        return this.lExperimentIDRef;
    }

    public void setExperimentRefID(long lExperimentIDRef){
        this.lExperimentIDRef = lExperimentIDRef;
    }
	
    /* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalRequiredRole(int)
	 */
	public void addApprovalRequiredRole(int roleID) {
		this.vReqRoles.addElement(roleID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalRequiredRoles(java.util.Vector)
	 */
	public void addApprovalRequiredRoles(List<Integer> roleIDs) {
		this.vReqRoles.addAll(roleIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalUser(eu.planets_project.tb.api.model.User)
	 */
	public void addApprovalUser(String userID) {
		if(!this.vApprovalUsers.contains(userID)){
			this.vApprovalUsers.addElement(userID);
		}

		//approval required roles were added
		/*if (this.vReqRoles.size()>0){
			Vector<Integer> vUserRoleIDs = user.getRolesIDs();
			Iterator<Integer> itUserRoleIDs = vUserRoleIDs.iterator();
			int i = 0;
			while(itUserRoleIDs.hasNext()){
				//check if user is registered to all required roles for approval
				int iRole = itUserRoleIDs.next();
				i++;
			}
			if (i==vUserRoleIDs.size()){
				this.vApprovalUsers.addElement(user.getUserID());
			}
		}*/
	}


	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#addApprovalUsers(java.util.Vector)
	 */
	public void addApprovalUsers(List<String> userIDs) {
		Iterator<String> itUsers = userIDs.iterator();
		while(itUsers.hasNext()){
			addApprovalUser(itUsers.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getApprovalRequiredRoles()
	 */
	public List<Integer> getApprovalRequiredRoles() {
		return this.vReqRoles;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getApprovalUsers()
	 */
	public List<String> getApprovalUsersIDs() {
		return this.vApprovalUsers;
	}

/*	public Vector<User> getApprovalUsers() {
		Vector<User> vRet = new Vector<User>();
		UserManager userManager = UserManager.getInstance();
		Iterator<Long> vUserIDs = this.vApprovalUsers.iterator();
		while(vUserIDs.hasNext()){
			User user = userManager.getUser(vUserIDs.next());
			vRet.addElement(user);
		}
		return vRet;
	}
*/
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getDecision()
	 */
	public String getDecision() {
		return this.sDecision;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getExplanation()
	 */
	public String getExplanation() {
		return this.sExplanation;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#getGo()
	 */
	public boolean getGo() {
		return this.bGo;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalRequiredRole(int)
	 */
	public void removeApprovalRequiredRole(int roleID) {
		if (this.vReqRoles.contains(roleID)){
			this.vReqRoles.removeElement(roleID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalRequiredRoles(java.util.Vector)
	 */
	public void removeApprovalRequiredRoles(List<Integer> roleIDs) {
		Iterator<Integer> itRoleIDs = roleIDs.iterator();
		while(itRoleIDs.hasNext()){
			removeApprovalRequiredRole(itRoleIDs.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalUser(eu.planets_project.tb.api.model.User)
	 */
	public void removeApprovalUser(String userID) {
		if (this.vApprovalUsers.contains(userID)){
			this.vApprovalUsers.remove(userID);
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#removeApprovalUsers(java.util.Vector)
	 */
	public void removeApprovalUsers(List<String> userIDs) {
		Iterator<String> itApprovalUsers = userIDs.iterator();
		while(itApprovalUsers.hasNext()){
			removeApprovalUser(itApprovalUsers.next());
		}
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalRequiredRole(int)
	 */
	public void setApprovalRequiredRole(int roleID) {
		this.vReqRoles.removeAllElements();
		this.vReqRoles.addElement(roleID);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalRequiredRoles(java.util.Vector)
	 */
	public void setApprovalRequiredRoles(List<Integer> roleIDs) {
		this.vReqRoles.removeAllElements();
		this.vReqRoles.addAll(roleIDs);
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalUser(eu.planets_project.tb.api.model.User)
	 */
/*	public void setApprovalUser(User user) {
		//approval required roles were added
		if (this.vReqRoles.size()>0){
			Vector<Integer> vUserRoleIDs = user.getRolesIDs();
			Iterator<Integer> itUserRoleIDs = vUserRoleIDs.iterator();
			int i = 0;
			while(itUserRoleIDs.hasNext()){
				//check if user is registered to all required roles for approval
				int iRole = itUserRoleIDs.next();
				i++;
			}
			if (i==vUserRoleIDs.size()){
				this.vApprovalUsers.removeAllElements();
				this.vApprovalUsers.addElement(user.getUserID());
			}
		}
	}*/

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setApprovalUsers(java.util.Vector)
	 */
/*	public void setApprovalUsers(Vector<User> users) {
		this.vApprovalUsers.removeAllElements();
		Iterator<User> itUsers = users.iterator();
		while(itUsers.hasNext()){
			User user = itUsers.next();
			//approval required roles were added
			if (this.vReqRoles.size()>0){
				Vector<Integer> vUserRoleIDs = user.getRolesIDs();
				Iterator<Integer> itUserRoleIDs = vUserRoleIDs.iterator();
				int i = 0;
				while(itUserRoleIDs.hasNext()){
					//check if user is registered to all required roles for approval
					int iRole = itUserRoleIDs.next();
					i++;
				}
				if (i==vUserRoleIDs.size()){
					this.vApprovalUsers.addElement(user.getUserID());
				}
			}
		}
	}
*/
	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setDecision(java.lang.String)
	 */
	public void setDecision(String decision) {
		this.sDecision = decision;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setExplanation(java.lang.String)
	 */
	public void setExplanation(String explanation) {
		this.sExplanation = explanation;
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.tb.api.model.ExperimentApproval#setGo(boolean)
	 */
	public void setGo(boolean go) {
		this.bGo = go;
	}

}
