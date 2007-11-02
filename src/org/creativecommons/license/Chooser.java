/*
 * Chooser.java
 *
 * Created on October 29, 2007, 9:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import org.creativecommons.license.Jurisdiction;

/**
 *
 * @author nathan
 */
public class Chooser {
    
    Store licenseStore;
    
    /**
     * Creates a new instance of Chooser
     */
    public Chooser() {
        
        this.licenseStore = Store.get();
        
    }
    
    public License selectLicense(boolean allowRemixing, boolean prohibitCommercialUse, boolean requireShareAlike) {
        
        // execute a simple query
        String queryString = makeLicenseQuery(true, false, false, null);
        System.out.println(queryString);

        // Execute the query and obtain results
        QueryExecution query = this.licenseStore.query(queryString);
        ResultSet results = query.execSelect();
        
        // Get the first result
        License result = new License(results.nextSolution().getResource("?license").toString());
        
        // Important - free up resources used running the query
        query.close();
        
        return result;

    } // selectLicense

    private String makeLicenseQuery(boolean allowRemixing, boolean prohibitCommercialUse, boolean requireShareAlike,
                Jurisdiction jurisdiction) {
        
        // Create the basic query
        String queryString =
                "PREFIX cc: <http://creativecommons.org/ns#> " +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                
                "SELECT ?license " +
                "WHERE {" +
                "      ?license cc:requires cc:Attribution . " +
                "      ?license cc:permits  cc:Distribution . "+
                "OPTIONAL {?license cc:deprecatedOn ?deprecatedDate } . ";

        String filter = "!bound(?deprecatedDate) ";
        
        // add jurisdiction filter
        if (jurisdiction == null) {
            // limit results to unported
            queryString += "OPTIONAL { ?license cc:jurisdiction ?jurisdiction } .";
            filter += "&& !bound(?jurisdiction) ";
            
        } else {
            // add a qualifier for the specific jurisdiction
            queryString += "?license cc:jurisdiction " + jurisdiction + " . ";
        }
        
        // add optional qualifiers
        if (allowRemixing) {
            queryString += "?license cc:permits cc:DerivativeWorks . ";
        } else {
            // only -nd licenses
            queryString += "OPTIONAL { ?license ?prohibitsRemixing cc:DerivativeWorks } . ";
            filter += "&& !bound(?prohibitsRemixing) ";
        }
        
        if (prohibitCommercialUse) {
            queryString += "?license cc:prohibits cc:CommercialUse . ";
        } else {
            // filter out -nc licenses
            queryString += "OPTIONAL { ?license ?allowCommercialUse cc:CommercialUse } . ";
            filter += "&& !bound(?allowCommercialUse) ";
        }
        
        if (requireShareAlike) {
            queryString += "?license cc:requires cc:ShareAlike . ";
        } else {
            // filter out -sa licenses
            queryString += "OPTIONAL { ?license ?noShareAlike cc:ShareAlike } . ";
            filter += "&& !bound(?noShareAlike) ";            
        }

        // close the query
        queryString += "FILTER(" + filter + ")      }";
        
        return queryString;
    } // makeLicenseQuery
        
} // Chooser
