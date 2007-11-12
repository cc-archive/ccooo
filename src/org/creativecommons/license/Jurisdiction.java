/*
 * Jurisdiction.java
 *
 * Created on October 29, 2007, 9:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.DC;

/**
 *
 * @author nathan
 */
public class Jurisdiction 
    implements Comparable {
    
    private static final String JURIS_BASE = "http://creativecommons.org/international/";
    private static final String UNPORTED = "http://creativecommons.org/international/-/";
    
    private String uri = null;
    
    /** Creates a new instance of Jurisdiction */
    public Jurisdiction(String uri) {
        this.uri = uri;
    } 
    
    
    public static Jurisdiction byId(String id) {
        return new Jurisdiction(JURIS_BASE + id + "/");
    }
    
    public String toString() {
        
        return this.uri;
        
    } // toString

    public String getTitle() {
        return this.getTitle("en");
    }
    
    public String getTitle(String lang) {
        
        Literal title = Store.get().literal(this.uri, DC.title, lang);
        
        if (title != null) {
            return title.getString();
        }
        
        return "";
    }

    public int compareTo(Object other) {
        
        if (this.uri.equals(this.UNPORTED)) return -1;
        if (((Jurisdiction)other).uri.equals(this.UNPORTED)) return 1;
        
        return this.getTitle().compareTo(((Jurisdiction)other).getTitle());

    }
} // Jurisdiction
