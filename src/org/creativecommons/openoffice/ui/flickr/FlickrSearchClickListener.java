/*
 * FlickrSearchClickListener.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.flickr;

import com.sun.star.awt.ActionEvent;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.ui.SearchClickListener;

/**
 *
 * @author Husleag Mihai
 */
public class FlickrSearchClickListener extends SearchClickListener {

    public FlickrSearchClickListener(FlickrDialog flickrDialog, CcOOoAddin addin) {

        super(flickrDialog, addin);
    }

    public void actionPerformed(ActionEvent a) {

        if (!imageDialog.IsInputValid()) {

            return;
        }

        SearchThread th = new SearchThread((FlickrDialog) imageDialog, a.ActionCommand);
        th.start();

    } // actionPerformed

    public void disposing(EventObject e) {
    }
}
