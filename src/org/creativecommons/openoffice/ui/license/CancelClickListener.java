/*
 * CancelClickListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;

class CancelClickListener implements XActionListener {

    private final ChooserDialog chooserDialog;

    public CancelClickListener(ChooserDialog chooserDialog) {
        this.chooserDialog = chooserDialog;
    }

    public void actionPerformed(ActionEvent a) {

        this.chooserDialog.close();
    }

    public void disposing(EventObject e) {
    }
}
