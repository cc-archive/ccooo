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
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.Constants;

/**
 *
 * @author nathan
 */
public abstract class OOoProgram implements IVisibleNotice {
    
    
    protected XComponent component;
    
    /** Creates a new instance of OOoProgram */
    public OOoProgram(XComponent component) {
        this.component = component;
    }
    
    
    public License getDocumentLicense() {
        
        // Return the License for the active document, if it exists       
        XDocumentInfoSupplier xDocumentInfoSupplier = (XDocumentInfoSupplier)UnoRuntime.queryInterface(
                XDocumentInfoSupplier.class, this.component);
        
        XDocumentInfo docInfo = xDocumentInfoSupplier.getDocumentInfo();        
        XPropertySet docProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, docInfo);
        
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
        
        XDocumentInfoSupplier xDocumentInfoSupplier = (XDocumentInfoSupplier)UnoRuntime.queryInterface(
                XDocumentInfoSupplier.class, this.component);
        
        XDocumentInfo docInfo = xDocumentInfoSupplier.getDocumentInfo();        
        XPropertySet docProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, docInfo);
        
        if (!docProperties.getPropertySetInfo().hasPropertyByName(Constants.LICENSE_URI)) {
            
            // add the necessary properties to this document
            XPropertyContainer docPropertyContainer = (XPropertyContainer) UnoRuntime.queryInterface(XPropertyContainer.class, 
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

    }
    
    
    public XComponent getComponent() {
        return component;
    }
    
    
    public void setComponent(XComponent component) {
        this.component = component;
    }
    
}
