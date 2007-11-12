/*
 * UpdateLicenseListener.java
 *
 * Created on November 12, 2007, 9:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XItemListener;
import com.sun.star.lang.EventObject;

/**
 *
 * @author nathan
 */
public class UpdateLicenseListener 
        implements XItemListener {

    private ChooserDialog dialog;
    
    /** Creates a new instance of UpdateLicenseListener */
    public UpdateLicenseListener(ChooserDialog dialog) {
        
        this.setDialog(dialog);
    }

    public void itemStateChanged(ItemEvent event) {
        
        this.getDialog().updateSelectedLicense();
    }

    public void disposing(EventObject event) {
    }

    public ChooserDialog getDialog() {
        return dialog;
    }

    protected void setDialog(ChooserDialog dialog) {
        this.dialog = dialog;
    }
    
}
