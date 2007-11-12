/*
 * Unported.java
 *
 * Created on November 12, 2007, 7:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

/**
 *
 * @author nathan
 */
public class Unported implements IJurisdiction {
    
    /** Creates a new instance of Unported */
    public Unported() {
    }

    public String getTitle() {
        return this.getTitle("en");
    }

    public String getTitle(String lang) {
        return "Unported";
    }

    public int compareTo(Object other) {
        
        // Unported is semi-bogus, so we don't support sorting'
        throw new ClassCastException();
        
    }
    
}
