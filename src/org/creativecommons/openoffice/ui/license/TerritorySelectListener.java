/*
 * TerritorySelectListener.java
 *
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 * 
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XItemListener;

/**
 * Get the user selected territory.
 * @author akila
 */
public class TerritorySelectListener implements XItemListener {

    private LicenseChooserDialog dialog;

    public TerritorySelectListener(LicenseChooserDialog dialog) {
        this.dialog = dialog;
    }

    public void itemStateChanged(ItemEvent event) {
        this.dialog.setSelectedTerritory(event.Selected);
    }

    public void disposing(com.sun.star.lang.EventObject arg0) {
    }
}
