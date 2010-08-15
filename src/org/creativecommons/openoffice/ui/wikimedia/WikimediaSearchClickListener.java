/*
 * WikimediaSearchClickListener.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 * 
 */
package org.creativecommons.openoffice.ui.wikimedia;

import com.sun.star.awt.ActionEvent;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.ui.SearchClickListener;

/**
 * Start a search when click on the search button.
 * @author akila
 */
public class WikimediaSearchClickListener extends SearchClickListener {

    public WikimediaSearchClickListener(WikimediaDialog wikimediaDialog, CcOOoAddin addin) {
        super(wikimediaDialog, addin);
    }

    public void actionPerformed(ActionEvent a) {

        if (!imageDialog.IsInputValid()) {
            return;
        }
        SearchThread th = new SearchThread((WikimediaDialog) imageDialog, a.ActionCommand);
        th.start();

    } // actionPerformed

    public void disposing(EventObject e) {
    }
}
