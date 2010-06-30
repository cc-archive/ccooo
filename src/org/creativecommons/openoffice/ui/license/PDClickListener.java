/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;

/**
 *
 * @author akila
 */
public class PDClickListener implements XActionListener {

    private ChooserDialog dialog;
    public PDClickListener(ChooserDialog dialog) {
        this.dialog=dialog;
    }

    public void actionPerformed(ActionEvent arg0) {
        this.dialog.setLicenseType(3);
    }

    @Override
    public void disposing(EventObject arg0) {
    }
}
