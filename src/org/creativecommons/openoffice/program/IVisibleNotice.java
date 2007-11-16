/*
 * IVisibleNotice.java
 *
 * Created on November 13, 2007, 8:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.program;

import org.creativecommons.license.License;

/**
 *
 * @author nathan
 */
public interface IVisibleNotice {
    
    public boolean hasVisibleNotice();

    /**
     * Create and insert an auto-text containing the license
     * 
     * @param licenseName The License Name.
     * @param licenseURL The License URL.
     * @param licenseImgURL The license "button" URL.
     */
    public void insertVisibleNotice();
    
    public void setDocumentLicense(License license);
    public License getDocumentLicense();
    // public boolean hasDocumentLicense();
    
}
