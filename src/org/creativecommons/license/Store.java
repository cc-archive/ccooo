/*
 * Store.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author nathan
 */
public class Store {

    private Model model;
    private static Store _instance = null;

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

        this.model = ModelFactory.createMemModelMaker().createFreshModel();
        model.getReader("RDF/XML").setProperty("WARN_REDEFINITION_OF_ID", "EM_IGNORE");
        // Load the RDF definitions
        this.model.read(this.getClass().getResource("/org/creativecommons/license/rdf/schema.rdf").toString());
        this.model.read(this.getClass().getResource("/org/creativecommons/license/rdf/index.rdf").toString());
        this.model.read(this.getClass().getResource("/org/creativecommons/license/rdf/jurisdictions.rdf").toString());
    } // private Store()
    
    /**
     * Get the jurisdictions
     * @return Jurisdictions list
     */
    @SuppressWarnings("unchecked")
    public List<Jurisdiction> jurisdictions() {
        ArrayList<Jurisdiction> result = new ArrayList<Jurisdiction>();

        ResIterator jurisdiction_iterator = this.getModel().listSubjectsWithProperty(
                RDF.type, CC.Jurisdiction);

        while (jurisdiction_iterator.hasNext()) {
            result.add(new Jurisdiction(jurisdiction_iterator.nextResource().getURI()));
        }
        Collections.sort(result);
        return result;

    } // public List jurisdictions

    protected Model getModel() {
        return model;
    }

    /**
     * Query the RDF
     * @param queryString
     * @return result
     */
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

            if (current.isLiteral()) {
                if (((Literal) current).getLanguage().equals(lang)) {
                    // this is a literal, in the language we care about
                    return (Literal) current;
                } // if current.getLanguage == lang
            }
        } // while hasNext...
        return null;
    } // literal

    public Literal literal(String subject, Property predicate, String lang) {
        return literal(this.model.getResource(subject), predicate, lang);
    }
    /**
     * Get the object of the RDF triple
     * @param subject
     * @param predicate
     * @return objcet
     */
    public Resource object(String subject, Property predicate) {

        RDFNode current;

        Resource subj = this.model.getResource(subject);

        // get an iterator over the objects in case there's more than one'
        StmtIterator iter = subj.listProperties(predicate);
        while (iter.hasNext()) {
            current = iter.nextStatement().getObject();

            if (current.isResource()) {
                return (Resource) current;
            }
        } // while hasNext...
        return null;
    }

    boolean exists(String subject, Property predicate, Resource obj) {
        return this.model.contains(this.model.getResource(subject), predicate, obj);
    }
} // org.creativecommons.license.Store

