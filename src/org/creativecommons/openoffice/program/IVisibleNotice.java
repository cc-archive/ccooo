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

    /**
     * Set the license meta data.
     * @param license
     */
    public void setDocumentLicense(License license);

    /**
     * Get the licesne for the document
     * @return License
     */
    public License getDocumentLicense();
    // public boolean hasDocumentLicense();

    /*
     * Insert pictures from the internet.
     */
    public void insertPicture(Image img);
}
