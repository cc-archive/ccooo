/*
 * JurisdictionSelectListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XItemListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.license.Jurisdiction;

/**
 * Get the user selected jurisdiction.
 * @author nathan
 */
public class JurisdictionSelectListener extends UpdateLicenseListener
        implements XItemListener {

    public JurisdictionSelectListener(LicenseChooserDialog dialog) {
        super(dialog);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {

        this.getDialog().setSelectedJurisdiction(
                (Jurisdiction) this.getDialog().getJurisdictionList().get(event.Selected));

        super.itemStateChanged(event);

    }

    @Override
    public void disposing(EventObject event) {
    }
}
