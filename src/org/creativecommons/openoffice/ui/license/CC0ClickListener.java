/*
 * CC0ClickListener.java
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
public class CC0ClickListener implements XActionListener {
    
    private ChooserDialog dialog;
    public CC0ClickListener(ChooserDialog dialog) {
        this.dialog=dialog;
    }

    public void actionPerformed(ActionEvent arg0) {
        this.dialog.setLicenseType(2);
    }

    @Override
    public void disposing(EventObject arg0) {
    }
}
