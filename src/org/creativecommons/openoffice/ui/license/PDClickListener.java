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
public class PDClickListener extends UpdateLicenseListener implements XActionListener {

    public PDClickListener(ChooserDialog dialog) {
        super(dialog);
    }

    public void actionPerformed(ActionEvent arg0) {
        this.getDialog().setLicenseType(3);
    }

    @Override
    public void disposing(EventObject arg0) {
    }
}
