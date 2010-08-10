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
 *
 * @author akila
 */
public class TerritorySelectListener implements XItemListener {

    private ChooserDialog dialog;
    public TerritorySelectListener(ChooserDialog dialog) {
        this.dialog=dialog;
    }

    public void itemStateChanged(ItemEvent event) {
        this.dialog.setSelectedTerritory(event.Selected);
        //this.dialog;
    }

    public void disposing(com.sun.star.lang.EventObject arg0) {
    }
}