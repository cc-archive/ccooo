/*
 * JurisdictionSelectListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
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
