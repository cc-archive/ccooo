/*
 * JurisdictionSelectListener.java
 *
 * Created on November 12, 2007, 7:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XItemListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.license.Jurisdiction;

/**
 *
 * @author nathan
 */
public class JurisdictionSelectListener implements XItemListener {
    
    private ChooserDialog chooserDialog;
    
    /** Creates a new instance of JurisdictionSelectListener */
    public JurisdictionSelectListener(ChooserDialog dialog) {
        this.chooserDialog = dialog;
    }

    public void itemStateChanged(ItemEvent event) {
        this.chooserDialog.setSelectedJurisdiction(
                (Jurisdiction)this.chooserDialog.getJurisdictionList().get(event.Selected)
                );
        
        this.chooserDialog.updateSelectedLicense();
    }

    public void disposing(EventObject event) {
    }
    
}
