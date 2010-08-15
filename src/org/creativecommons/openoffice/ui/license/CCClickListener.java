/*
 * CCClickListener.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;

/**
 * Show Creative Commons tab.
 * @author akila
 */
public class CCClickListener implements XActionListener {

    private LicenseChooserDialog dialog;

    public CCClickListener(LicenseChooserDialog dialog) {
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent arg0) {
        this.dialog.setLicenseType(1);
    }

    @Override
    public void disposing(EventObject arg0) {
    }
}
