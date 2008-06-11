/*
 * IVisibleNotice.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
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
    
    public void insertPictureFlickr(Image img);
}
