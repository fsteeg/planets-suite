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
package eu.planets_project.tb.gui.backing;

import java.net.URI;

import eu.planets_project.tb.api.data.DigitalObjectReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

/**
 * @author AnJackson
 *
 */
public class FileTreeNode extends TreeNodeBase implements java.io.Serializable {
    static final long serialVersionUID = 82362318283823293l;
    
    @SuppressWarnings("unused")
	static private Log log = LogFactory.getLog(FileTreeNode.class);
    
    private DigitalObjectReference dob;
    private String displayName;
    private boolean selected;
    private boolean selectable;
    private boolean expanded = false;
    
    /**
     * Constructor based on Digital Object:
     */
    public FileTreeNode( DigitalObjectReference dob ) {
        this.setDob(dob);
    }
    
    /**
     * @return the dob
     */
    public DigitalObjectReference getDob() {
        return dob;
    }
    /**
     * @param dob the dob to set
     */
    public void setDob(DigitalObjectReference dob) {
        this.dob = dob;
        // Pick up configuration from the DO:
        if( this.isDirectory() ) {
            this.setType("folder");
            this.setLeaf(false);
            this.setSelectable(false);
        } else {
            this.setType("file");
            this.setLeaf(true);
            this.setSelectable(true);
        }
        this.displayName = dob.getLeafname();
    }
    
    /**
     * @return the underlying URI
     */
    public URI getUri() {
        return dob.getUri();
    }
    
    /**
     * @return the leafname
     */
    public String getLeafname() {
        return dob.getLeafname();
    }
    
    /**
     * @return the directory
     */
    public boolean isDirectory() {
        return dob.isDirectory();
    }
    
    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    /**
     * @return the selectable
     */
    public boolean isSelectable() {
        return selectable;
    }
    
    /**
     * @param selectable the selectable to set
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * @param expanded the expanded to set
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dob == null) ? 0 : dob.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FileTreeNode other = (FileTreeNode) obj;
        if (dob == null) {
            if (other.dob != null)
                return false;
        } else if (!dob.equals(other.dob))
            return false;
        return true;
    }
 
    
}
