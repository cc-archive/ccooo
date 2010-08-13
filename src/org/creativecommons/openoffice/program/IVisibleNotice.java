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
     */
    public void insertVisibleNotice();
    
    /**
     * Update visible notices to current license
     */
    public void updateVisibleNotice();
    
    public void setDocumentLicense(License license);
    public License getDocumentLicense();
    // public boolean hasDocumentLicense();
    
    public void insertPicture(Image img);
}
