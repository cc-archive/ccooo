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
    
    private static final String NS ="http://creativecommons.org/ns#";
    private static Model m = ModelFactory.createDefaultModel();
    
    public static final Resource NAMESPACE = m.createResource( NS );
    
    public static final Resource Jurisdiction = m.createResource(NS + "Jurisdiction");
    public static final Property legalcode = m.createProperty(NS, "legalcode");
    public static final Property jurisdiction = m.createProperty(NS, "jurisdiction");
   
} // CC
