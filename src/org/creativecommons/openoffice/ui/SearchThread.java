/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import java.util.ArrayList;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.FlickrConnection;

/**
 *
 * @author Administrator
 */
public class SearchThread extends Thread {
    
    private PictureFlickrDialog flickrDialog;
    private FlickrConnection flickrConn;
    
    public SearchThread(PictureFlickrDialog flickrDialog) {
        
        this.flickrDialog = flickrDialog;
    }

    public void run() {
        
        ArrayList<Image> imgList = flickrConn.instance.searchPhotos(flickrDialog.GetTags(),
                flickrDialog.GetLicense());
        flickrDialog.SetProgressValue(50);
        flickrDialog.showResults(imgList);
        flickrDialog.SetProgressValue(100);
    }
    
}
