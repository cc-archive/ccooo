/*
 * Store.java
 *
 * Created on October 29, 2007, 9:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author nathan
 */
public class Store {
    
    private static Store _instance = null;
    private Model model;
    
    public static Store get() {
        
        if (Store._instance == null) {
            Store._instance = new Store();
        }
        
        return Store._instance;
    }
    
    /**
     * Creates a new instance of Store
     */
    private Store() {
        
        // TODO: determine our actual path in the JAR file
        String base = "/home/nathan/p/ccooo/trunk/src/org/creativecommons/license/rdf/";
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith(".") && name.endsWith(".rdf");
            }
        };
        InputStream in = null;
        
        // Create an empty in-memory model and populate it from the graph
        this.model = ModelFactory.createMemModelMaker().createFreshModel();
        
        try {
            File rdf_dir = new File(base);
            
            String[] children = rdf_dir.list(filter);
            for (int i=0; i<children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                
                // TODO: convert to java logging
                System.out.println("loading " + filename);
                
                in = new FileInputStream(new File(base + filename));
                this.model.read(in,null);
                in.close();
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    } // private Store()
    
    protected Model getModel() {
        return model;
    }
    
    public List jurisdictions() {
        
        ArrayList result = new ArrayList();
        
        ResIterator jurisdiction_iterator = this.getModel().listSubjectsWithProperty(
                RDF.type, CC.Jurisdiction);
        
        while (jurisdiction_iterator.hasNext()) {
            result.add(new Jurisdiction(jurisdiction_iterator.nextResource().getURI()));
        }
        
        Collections.sort(result);
        
        return result;
        
    } // public List jurisdictions
    
    public QueryExecution query(String queryString) {
        
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        
        return qe;
    }
    
    public Literal literal(Resource subject, Property predicate, String lang) {
        
        RDFNode current; 
        
        // get an iterator over the objects in case there's more than one'
        StmtIterator iter = subject.listProperties(predicate);
        while (iter.hasNext()) {
            current = iter.nextStatement().getObject();
            
            if ( current.isLiteral() ) {

                System.out.println(((Literal)current).getLanguage());
                if ( ((Literal)current).getLanguage().equals(lang) ) {

                    // this is a literal, in the language we care about
                    return (Literal)current;
                    
                } // if current.getLanguage == lang
                
            } 
            
        } // while hasNext...

        return null;
        
    } // literal
    
    public Literal literal(String subject, Property predicate, String lang) {
        return literal(this.model.getResource(subject), predicate, lang);
    }

    public Resource object(String subject, Property predicate) {

        RDFNode current; 
        
        Resource subj = this.model.getResource(subject);

        // get an iterator over the objects in case there's more than one'
        StmtIterator iter = subj.listProperties(predicate);
        while (iter.hasNext()) {
            current = iter.nextStatement().getObject();
            
            if ( current.isResource() ) {

                return (Resource)current;
                
            } 
            
        } // while hasNext...

        return null;
    }
    
} // org.creativecommons.license.Store
