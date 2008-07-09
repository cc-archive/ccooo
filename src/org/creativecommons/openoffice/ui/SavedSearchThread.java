/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

/**
 *
 * @author Administrator
 */
public class SavedSearchThread extends Thread {
    
    private PictureFlickrDialog flickrDialog;
    
    public SavedSearchThread(PictureFlickrDialog flickrDialog) {
        
        this.flickrDialog = flickrDialog;
    }

    public void run() {
    
        flickrDialog.startSavedSearch();
    }

}
