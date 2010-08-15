/*
 * PicasaSearchClickListener.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.picasa;

import com.sun.star.awt.ActionEvent;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.ui.SearchClickListener;

/**
 * Start a search when click on the search button.
 * @author akila
 */
public class PicasaSearchClickListener extends SearchClickListener {

    public PicasaSearchClickListener(PicasaDialog picasaDialog, CcOOoAddin addin) {
        super(picasaDialog, addin);
    }

    public void actionPerformed(ActionEvent a) {

        if (!imageDialog.IsInputValid()) {
            return;
        }
        SearchThread th = new SearchThread((PicasaDialog) imageDialog, a.ActionCommand);
        th.start();

    } // actionPerformed

    public void disposing(EventObject e) {
    }
}
