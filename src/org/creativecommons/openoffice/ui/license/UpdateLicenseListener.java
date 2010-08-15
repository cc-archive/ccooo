/*
 * UpdateLicenseListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XItemListener;
import com.sun.star.lang.EventObject;

/**
 * Update the selected license lable.
 * @author nathan
 */
public class UpdateLicenseListener
        implements XItemListener {

    private LicenseChooserDialog dialog;

    /** 
     * Creates a new instance of UpdateLicenseListener
     */
    public UpdateLicenseListener(LicenseChooserDialog dialog) {

        this.setDialog(dialog);
    }

    public void itemStateChanged(ItemEvent event) {

        this.getDialog().updateSelectedLicense();
    }

    public void disposing(EventObject event) {
    }

    public LicenseChooserDialog getDialog() {
        return dialog;
    }

    protected void setDialog(LicenseChooserDialog dialog) {
        this.dialog = dialog;
    }
}
