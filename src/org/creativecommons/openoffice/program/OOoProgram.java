/*
 * OOoProgram.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */

package org.creativecommons.openoffice.program;

import com.sun.star.beans.IllegalTypeException;
import com.sun.star.beans.PropertyExistException;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertyContainer;
import com.sun.star.beans.XPropertySet;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.rdf.Literal;
import com.sun.star.rdf.URI;
import com.sun.star.rdf.XDocumentMetadataAccess;
import com.sun.star.rdf.XLiteral;
import com.sun.star.rdf.XNamedGraph;
import com.sun.star.rdf.XURI;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.Constants;

/**
 *
 * @author nathan
 */
public abstract class OOoProgram implements IVisibleNotice {
    
    
    protected XComponent component;
    protected XComponentContext m_xContext;
    
    /** Creates a new instance of OOoProgram */
    public OOoProgram(XComponent component,XComponentContext m_xContext) {
        this.component = component;
        this.m_xContext=m_xContext;
    }
    
    
    public License getDocumentLicense() {
        
        // Return the License for the active document, if it exists       
        XDocumentInfoSupplier xDocumentInfoSupplier = (XDocumentInfoSupplier)
                UnoRuntime.queryInterface(XDocumentInfoSupplier.class, this.component);
        
        XDocumentInfo docInfo = xDocumentInfoSupplier.getDocumentInfo();        
        XPropertySet docProperties = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, docInfo);
        
        if (docProperties.getPropertySetInfo().hasPropertyByName(Constants.LICENSE_URI)) {
            
            try {
                
                return new License((String)docProperties.getPropertyValue(Constants.LICENSE_URI));
            } catch (WrappedTargetException ex) {
                ex.printStackTrace();
            } catch (UnknownPropertyException ex) {
                ex.printStackTrace();
            }
            
        }
        
        return null;
        
    } // getDocumentLicense
    
    public abstract boolean hasVisibleNotice();
    
    /**
     * Create and insert an auto-text containing the license
     *
     * @param licenseName The License Name.
     * @param licenseURL The License URL.
     * @param licenseImgURL The license "button" URL.
     */
    public abstract void insertVisibleNotice();
    
    public void setDocumentLicense(License license) {
        
        XDocumentInfoSupplier xDocumentInfoSupplier = (XDocumentInfoSupplier)
                UnoRuntime.queryInterface(XDocumentInfoSupplier.class, this.component);
        
        XDocumentInfo docInfo = xDocumentInfoSupplier.getDocumentInfo();        
        XPropertySet docProperties = (XPropertySet)
                UnoRuntime.queryInterface(XPropertySet.class, docInfo);
        
        if (!docProperties.getPropertySetInfo().hasPropertyByName(Constants.LICENSE_URI)) {
            
            // add the necessary properties to this document
            XPropertyContainer docPropertyContainer = (XPropertyContainer)
                    UnoRuntime.queryInterface(XPropertyContainer.class,
                        docInfo);
            try {
                docPropertyContainer.addProperty(Constants.LICENSE_URI, 
                            com.sun.star.beans.PropertyAttribute.MAYBEVOID, 
                            "");
                docPropertyContainer.addProperty(Constants.LICENSE_NAME, 
                            com.sun.star.beans.PropertyAttribute.MAYBEVOID, 
                            "");
            } catch (com.sun.star.lang.IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (PropertyExistException ex) {
                ex.printStackTrace();
            } catch (IllegalTypeException ex) {
                ex.printStackTrace();
            }

        }
        
        try {
            
            docProperties.setPropertyValue(Constants.LICENSE_URI, license.getLicenseUri());
            docProperties.setPropertyValue(Constants.LICENSE_NAME, license.getName());
            
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        }

        try {
            XDocumentMetadataAccess xDMA = (XDocumentMetadataAccess)
                    UnoRuntime.queryInterface(XDocumentMetadataAccess.class, this.getComponent());
            XURI xType1 = URI.create(m_xContext, "http://purl.org/dc/elements/1.1/");
            XURI xType2 = URI.create(m_xContext, "http://purl.org/dc/terms/");
            //XURI xType = URI.createKnown(m_xContext, URIs.XSD_DATE);

            try {
                xDMA.removeMetadataFile(URI.create(m_xContext, xDMA.getNamespace()+"meta.rdf"));
            } catch (java.lang.Exception eRemove) {
                eRemove.printStackTrace();
            }

            XURI xGraphName = xDMA.addMetadataFile("meta.rdf", new XURI[]{xType1,xType2});
            XNamedGraph xGraph = xDMA.getRDFRepository().getGraph(xGraphName);

            XURI nodeRights = URI.create(m_xContext, "http://purl.org/dc/elements/1.1/rights");
            XLiteral valRights = Literal.create(m_xContext, license.getName());
            xGraph.addStatement(xType1, nodeRights, valRights);

            XURI nodeLicense = URI.create(m_xContext, "http://purl.org/dc/terms/license");
            XLiteral valLicense = Literal.create(m_xContext, license.getLicenseUri());
            xGraph.addStatement(xType2, nodeLicense, valLicense);

            XURI noderightsHolder = URI.create(m_xContext, "http://purl.org/dc/terms/rightsHolder");
            XLiteral valrightsHolder = Literal.create(m_xContext, "AUTHOR");
            xGraph.addStatement(xType2, noderightsHolder, valrightsHolder);

//            XURI nodeTableName = URI.createKnown(m_xContext, URIs.RDF_SUBJECT);
//            XLiteral valTableName = Literal.create(m_xContext, "A_TABLE");
//            xGraph.addStatement(xType1, nodeTableName, valTableName);

        } catch (java.lang.Exception e) {
            e.printStackTrace();

        }

    }
    
    
    public XComponent getComponent() {
        return component;
    }
    
    
    public void setComponent(XComponent component) {
        this.component = component;
    }
    
}
