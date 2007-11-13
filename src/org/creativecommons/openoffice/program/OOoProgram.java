/*
 * OOoProgram.java
 *
 * Created on November 13, 2007, 9:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.program;

import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import java.util.HashMap;
import java.util.Map;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.AddInConstants;

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
        
        if (this.retrieveLicenseMetadata().containsKey(AddInConstants.LICENSE_URI)) {
            return new License((String) this.retrieveLicenseMetadata().get(AddInConstants.LICENSE_URI));
        }
        
        return null;
    }

    public abstract boolean hasVisibleNotice();

    /**
     * Create and insert an auto-text containing the license
     * 
     * @param licenseName The License Name.
     * @param licenseURL The License URL.
     * @param licenseImgURL The license "button" URL.
     */
    public abstract void insertVisibleNotice();

    
    /**
     * Retrieve the license properties from the document's metadata
     *
     * @return Map Returns a map containing the license properties
     *
     */
    protected Map retrieveLicenseMetadata(){
        Map licenseProp = new HashMap();
        
        XDocumentInfo m_xDocumentInfo;
        
        XDocumentInfoSupplier xDocumentInfoSupplier = (XDocumentInfoSupplier)UnoRuntime.queryInterface(
                XDocumentInfoSupplier.class, this.component);
        
        m_xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
        
        try {
            
            short fieldsnum = m_xDocumentInfo.getUserFieldCount();
            
            // if XDocumentInfoSupplier had a hasFieldName(String fieldName) we wouldn't have done this...
            
            // TODO   poderia ter um atributo imutavel indicando se licenciado ou nao,
            // assim buscariamos primeiro por esse atributo antes de varrer os fields
            for (short i = 0; i < fieldsnum; i++) {
                String temp = m_xDocumentInfo.getUserFieldName(i);
                
                if (temp.startsWith(AddInConstants.CC_METADATA_IDENTIFIER)) {
                    licenseProp.put(temp.substring(AddInConstants.CC_METADATA_IDENTIFIER.length()),m_xDocumentInfo.getUserFieldValue(i));
                }
            }
            
            
        }  catch (com.sun.star.lang.ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return licenseProp;
    }

    
    public void setDocumentLicense(License license) {
        
        // TODO Store metadata as in MSOffice addin?
        
        try {
            XDocumentInfoSupplier xDocumentInfoSupplier = (XDocumentInfoSupplier)UnoRuntime.queryInterface(
                    XDocumentInfoSupplier.class, this.component);
            
            XDocumentInfo m_xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
            
            m_xDocumentInfo.setUserFieldName((short) 0, AddInConstants.CC_METADATA_IDENTIFIER + AddInConstants.LICENSE_NAME);
            m_xDocumentInfo.setUserFieldValue((short) 0, license.getName());
            
            m_xDocumentInfo.setUserFieldName((short) 1, AddInConstants.CC_METADATA_IDENTIFIER + AddInConstants.LICENSE_URI);
            m_xDocumentInfo.setUserFieldValue((short) 1, license.getLicenseUri());
            
            XStorable xStorable = (XStorable)UnoRuntime.queryInterface(
                    XStorable.class, this.component);
            xStorable.store();
            
        }  catch (Exception ex) {
            // just swallow..
            //ex.printStackTrace();
        }
    }


    public XComponent getComponent() {
        return component;
    }


    public void setComponent(XComponent component) {
        this.component = component;
    }
    
}
