/**
 * Copyright (c) 2007, 2008 The Planets Project Partners.
 * 
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * GNU Lesser General Public License v3 which 
 * accompanies this distribution, and is available at 
 * http://www.gnu.org/licenses/lgpl.html
 * 
 */
package eu.planets_project.tb.impl.system;

import javax.annotation.security.RunAs;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.storage.api.DataManagerLocal;
import eu.planets_project.tb.api.system.TestbedStatelessAdmin;

/**
 * @author AnJackson
 *
 */
@Stateless
@Local(TestbedStatelessAdmin.class)
@Remote(TestbedStatelessAdmin.class)
@LocalBinding(jndiBinding="planets-project.eu/tb/TestbedAdminBean")
@RemoteBinding(jndiBinding="planets-project.eu/tb/TestbedAdminBean")
@SecurityDomain("PlanetsRealm")
@RunAs("admin")
public class TestbedStatelessAdminBean implements TestbedStatelessAdmin {

    // A logger:
    private static PlanetsLogger log = PlanetsLogger.getLogger(TestbedStatelessAdminBean.class);

    /**
     * Hook up to a local instance of the Planets Data Manager.
     * 
     * NOTE Trying to get the remote DM and narrow it to the local one did not work.
     * 
     * TODO Switch to the DigitalObjectManager form.
     * 
     * @return A DataManagerLocal, as discovered via JNDI.
     */
    public DataManagerLocal getPlanetsDataManagerAsAdmin() {
        try{
            Context jndiContext = new javax.naming.InitialContext();
            DataManagerLocal um = (DataManagerLocal) 
                jndiContext.lookup("planets-project.eu/DataManager/local");
            return um;
        }catch (NamingException e) {
            log.error("Failure during lookup of the local DataManager: "+e.toString());
            return null;
        }
    }
    

    /**
     * @return A reference to this EJB.
     */
    public static TestbedStatelessAdmin getTestbedAdminBean() {
        try{
            Context jndiContext = new javax.naming.InitialContext();
            TestbedStatelessAdmin um = (TestbedStatelessAdmin) 
                jndiContext.lookup("planets-project.eu/tb/TestbedAdminBean");
            return um;
        }catch (NamingException e) {
            log.error("Failure during lookup of the local TestbedStatelessAdmin: "+e.toString());
            return null;
        }
    }
}