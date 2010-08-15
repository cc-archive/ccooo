/*
 * Chooser.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.license;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Choose the license for the given parameters
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
    /**
     * Select the Creative Commons license for given parameters.
     * @param allowRemixing Is reamixing allowed
     * @param prohibitCommercialUse Commercial usage
     * @param requireShareAlike Eequires share alike
     * @param jurisdiction Jurisdiction of the license
     * @return Selected license.
     */
    public License selectLicense(
            boolean allowRemixing, boolean prohibitCommercialUse, boolean requireShareAlike,
            IJurisdiction jurisdiction) {

        // execute a simple query
        String queryString = makeLicenseQuery(allowRemixing, prohibitCommercialUse,
                requireShareAlike, jurisdiction);

        // Execute the query and obtain results
        QueryExecution query = this.licenseStore.query(queryString);
        ResultSet results = query.execSelect();

        // Get the first result
        while (results.hasNext()) {
            String uri = results.nextSolution().getResource("?license").toString();

            if (uri.contains("sampling")) {
                continue;
            }

            // Important - free up resources used running the query
            query.close();
            return new License(uri);

        }

        return null;

    } // selectLicense
    /**
     * Make the query string to query the RDF.
     * @param allowRemixing Is reamixing allowed
     * @param prohibitCommercialUse Commercial usage
     * @param requireShareAlike Eequires share alike
     * @param jurisdiction Jurisdiction of the license
     * @return queryString Query string
     */
    private String makeLicenseQuery(boolean allowRemixing,
            boolean prohibitCommercialUse, boolean requireShareAlike,
            IJurisdiction jurisdiction) {

        // Create the basic query
        String queryString =
                "PREFIX cc: <http://creativecommons.org/ns#> "
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
                + "PREFIX dcq: <http://purl.org/dc/terms/> "
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "SELECT ?license "
                + "WHERE {"
                + "      ?license cc:requires cc:Attribution . "
                + "      ?license cc:permits  cc:Distribution . "
                + "OPTIONAL {?license cc:deprecatedOn ?deprecatedDate } . "
                + "OPTIONAL {?license dcq:isReplacedBy ?replacedBy } . ";

        String filter = "!bound(?deprecatedDate) && !bound(?replacedBy) ";

        // add jurisdiction filter
        if (jurisdiction == null || Unported.class.isInstance(jurisdiction)) {
            // limit results to unported
            queryString += "OPTIONAL { ?license cc:jurisdiction ?jurisdiction } . ";
            filter += "&& !bound(?jurisdiction) ";

        } else {
            // add a qualifier for the specific jurisdiction
            queryString += "?license cc:jurisdiction <" + jurisdiction.toString() + "> . ";
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
    /**
     * Select the appropriate public deomain tool from the RDF.
     * @param territory Selected territory
     * @param toolType Select between CC0(2) or PD(3)
     * @return Selected PD tool
     */
    public License selectPDTools(String territory, int toolType) {

        // execute a simple query
        String queryString = makePDToolQuery();

        // Execute the query and obtain results
        QueryExecution query = this.licenseStore.query(queryString);
        ResultSet results = query.execSelect();

        // Get the first result
        while (results.hasNext()) {
            String uri = results.nextSolution().getResource("?license").toString();

            if (toolType == 2 && uri.contains("publicdomain") && uri.contains("zero")) {
                // Important - free up resources used running the query
                query.close();
                return new License(uri, territory);
            } else if (toolType == 3 && uri.contains("publicdomain") && !uri.contains("zero")) {
                // Important - free up resources used running the query
                query.close();
                return new License(uri);
            }
        }
        return null;

    } // selectLicense

    /**
     * Make the query for CC0 and PD
     * @return queryString Query string
     */
    private String makePDToolQuery() {

        // Create the basic query
        String queryString =
                "PREFIX cc: <http://creativecommons.org/ns#> "
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
                + "PREFIX dcq: <http://purl.org/dc/terms/> "
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "SELECT ?license "
                + "WHERE {"
                + "      ?license cc:permits  cc:Distribution . "
                + "      ?license cc:permits  cc:DerivativeWorks . "
                + "      ?license cc:permits  cc:Reproduction . "
                + "OPTIONAL {?license cc:deprecatedOn ?deprecatedDate } . "
                + "OPTIONAL {?license dcq:isReplacedBy ?replacedBy } . ";

        String filter = "!bound(?deprecatedDate) && !bound(?replacedBy) ";
        // close the query
        queryString += "FILTER(" + filter + ")      }";

        return queryString;
    } // makePDToolQuery
} // Chooser

