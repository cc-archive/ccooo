/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import java.util.ArrayList;
import com.sun.star.awt.SystemPointer;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.FlickrConnection;

/**
 *
 * @author Administrator
 */
public class SearchThread extends Thread {
    
    private PictureFlickrDialog flickrDialog;
    
    public SearchThread(PictureFlickrDialog flickrDialog) {
        
        this.flickrDialog = flickrDialog;
    }

    public void run() {
        
        flickrDialog.setMousePointer(SystemPointer.WAIT);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, false);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_NEXT, false);
        //flickrDialog.enableControl(PictureFlickrDialog.BTN_PREVIOUS, false);
        flickrDialog.setProgressValue(0);
        flickrDialog.saveSearch();
        String licenseID = flickrDialog.getLicense();
       // String licenseURL = flickrDialog.getLicenseURL(licenseID);
        //String licenseNumber = flickrDialog.getLicenseNumber(licenseURL);        
        ArrayList<Image> imgList = FlickrConnection.instance.searchPhotos(flickrDialog.GetTags(),licenseID
                //, licenseURL, licenseNumber
                );        
        flickrDialog.setProgressValue(30);
        flickrDialog.showResults(imgList, 30);
        flickrDialog.setProgressValue(100);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, true);
//        flickrDialog.enableControl(PictureFlickrDialog.BTN_NEXT, true);
        //flickrDialog.enableControl(PictureFlickrDialog.BTN_PREVIOUS, true);
        flickrDialog.setMousePointer(SystemPointer.ARROW);
    }
    
}
