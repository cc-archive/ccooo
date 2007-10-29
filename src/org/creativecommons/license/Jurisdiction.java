/*
 * Jurisdiction.java
 *
 * Created on October 29, 2007, 9:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

/**
 *
 * @author nathan
 */
public class Jurisdiction {
    
    private static final String JURIS_BASE = "http://creativecommons.org/international/";
    
    private String id = null;
    
    /** Creates a new instance of Jurisdiction */
    public Jurisdiction(String id) {
        this.id = id;
    }
    
    public String toString() {
        
        return JURIS_BASE + this.id + "/";
        
    } // toString

    public String getJURIS_BASE() {
        return JURIS_BASE;
    }
    
    public String getId() {
        return this.id;
    }
    
} // Jurisdiction
