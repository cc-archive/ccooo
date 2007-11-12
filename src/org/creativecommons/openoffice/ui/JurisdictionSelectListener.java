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
public class JurisdictionSelectListener extends UpdateLicenseListener
        implements XItemListener {

    public JurisdictionSelectListener(ChooserDialog dialog) {
        super(dialog);
    }    
    
    public void itemStateChanged(ItemEvent event) {
        this.getDialog().setSelectedJurisdiction(
                (Jurisdiction)this.getDialog().getJurisdictionList().get(event.Selected)
                );
        
        super.itemStateChanged(event);
        
    }

    public void disposing(EventObject event) {
    }
    
}
