/*
 * CC.java
 *
 * Created on November 1, 2007, 7:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

import com.hp.hpl.jena.rdf.model.*;

/**
 *
 * @author Nathan R. Yergler <nathan@creativecommons.org>
 *
 * CC vocabulary class for namespace http://creativecommons.org/ns#
 *
 */
public class CC {
    
    protected static final String uri ="http://creativecommons.org/ns#";

    /** returns the URI for this schema
     * @return the URI for this schema
     */
    public static String getURI() {
          return uri;
    }

    private static Model m = ModelFactory.createDefaultModel();
    
    public static final Property legalcode = m.createProperty(uri, "legalcode");
    public static final Property jurisdiction = m.createProperty(uri, "jurisdiction");
    
}
