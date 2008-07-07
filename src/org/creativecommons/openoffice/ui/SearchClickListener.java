/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.program.FlickrConnection;

/**
 *
 * @author Administrator
 */
public class SearchClickListener implements XActionListener{

    private PictureFlickrDialog flickrDialog;
    private CcOOoAddin addin;
    private FlickrConnection flickrConn;
    public SearchClickListener(PictureFlickrDialog flickrDialog, CcOOoAddin addin){

        this.flickrDialog = flickrDialog;
        this.addin = addin;
    }
    
    public void actionPerformed(ActionEvent a) {
        
        SearchThread th = new SearchThread(flickrDialog);
        th.start();
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
    
}
