/*
 * License.java
 *
 * Created on October 29, 2007, 9:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 *
 * @author nathan
 */
public class License {
    
    private String license_uri;
    private Store licenseStore;
    
    /**
     * Creates a new instance of License
     */
    public License(String license_uri) {
        
        this.license_uri = license_uri;
        this.licenseStore = Store.get();
    } 
    
    public String getLicenseUri() {
        return this.license_uri;
    }
    
    public String getName() {
        
        return this.licenseStore.literal(this.license_uri, DC.title, "en").getString() + " " +
                this.licenseStore.literal(this.license_uri, DCTerms.hasVersion, "").getString(); /*  + " " + 
                this.licenseStore.literal(
                    this.licenseStore.object(this.license_uri, CC.jurisdiction), DC.title).getString();
                                                                                              */
    }
    
    public String getImageUrl() {
        return "";
    }
    
} // License
