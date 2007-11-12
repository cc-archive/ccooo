/*
 * License.java
 *
 * Created on October 29, 2007, 9:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

import com.hp.hpl.jena.rdf.model.Resource;
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
                this.licenseStore.literal(this.license_uri, DCTerms.hasVersion, "").getString() + " " +
                this.getJurisdiction().getTitle();
    }
    
    public IJurisdiction getJurisdiction() {

        Resource jurisdiction = this.licenseStore.object(this.license_uri, CC.jurisdiction);
        
        if (jurisdiction != null)
            return new Jurisdiction(jurisdiction.getURI());
        
        return new Unported();

    } // getJurisdiction
    
    public String getImageUrl() {
        return "";
    }

    public Boolean requireShareAlike() {
        
        return Boolean.valueOf(
                this.licenseStore.exists(this.license_uri, CC.requires, CC.ShareAlike)
                );
        
    }

    public Boolean prohibitCommercial() {
        
        return Boolean.valueOf(
                this.licenseStore.exists(this.license_uri, CC.prohibits, CC.CommercialUse)
                );

    }

    public Boolean allowRemix() {

        return Boolean.valueOf(
                this.licenseStore.exists(this.license_uri, CC.permits, CC.DerivativeWorks)
                );

    }
    
} // License
