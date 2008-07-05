/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

/**
 *
 * @author mihu
 */
public class NextThread extends Thread{

    private PictureFlickrDialog flickrDialog;
    
    public NextThread(PictureFlickrDialog flickrDialog) {
        
        this.flickrDialog = flickrDialog;
    }

    public void run() {
        
        flickrDialog.enableControl(PictureFlickrDialog.BTN_NEXT, false);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, false);
        flickrDialog.setProgressValue(0);
        flickrDialog.showNextPage(0);
        flickrDialog.setProgressValue(100);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_NEXT, true);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, true);
    }

}
