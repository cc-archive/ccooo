/*
 * OKClickListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;

/**
 *
 * @author Husleag Mihai
 */
class OKClickListener implements XActionListener {

    private LicenseChooserDialog chooserDialog;

    public OKClickListener(LicenseChooserDialog chooserDialog) {

        this.chooserDialog = chooserDialog;
    }

    public void actionPerformed(ActionEvent a) {

        this.chooserDialog.setCancelled(false);
        this.chooserDialog.close();

    } // actionPerformed

    public void disposing(EventObject e) {
    }
} // OnFinishClick
