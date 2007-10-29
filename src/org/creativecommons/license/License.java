/*
 * License.java
 *
 * Created on October 29, 2007, 9:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

/**
 *
 * @author nathan
 */
public class License {
    
    private String _license_uri;
    private Store licenseStore;
    
    /**
     * Creates a new instance of License
     */
    public License(String license_uri) {
        
        this._license_uri = license_uri;
        this.licenseStore = Store.get();
    } 
    
    
} // License
