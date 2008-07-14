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
    private String buttonName;
    
    public SearchThread(PictureFlickrDialog flickrDialog, String btnName) {
        
        this.flickrDialog = flickrDialog;
        this.buttonName = btnName;
    }

    public void run() {
        
        flickrDialog.setMousePointer(SystemPointer.WAIT);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_NEXT, false);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_PREVIOUS, false);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, false);
        flickrDialog.saveSearch();
        flickrDialog.setProgressValue(0);
        String licenseID = flickrDialog.getLicense();
                
        if (buttonName.equalsIgnoreCase(PictureFlickrDialog.BTN_SEARCH)) {
            
            flickrDialog.setCurrentPage(1);
        }
            else
                if (buttonName.equalsIgnoreCase(PictureFlickrDialog.BTN_PREVIOUS)) {
                
                    flickrDialog.setCurrentPage(flickrDialog.getCurrentPage() - 1);
                }
                else {
            
                    flickrDialog.setCurrentPage(flickrDialog.getCurrentPage() + 1);
                }
        
        ArrayList<Image> imgList = FlickrConnection.instance.searchPhotos(flickrDialog.GetTags(),licenseID, 
                flickrDialog.getCurrentPage());        
        flickrDialog.setProgressValue(15);
        flickrDialog.showResults(imgList, 15);
        flickrDialog.setProgressValue(100);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, true);
        flickrDialog.setMousePointer(SystemPointer.ARROW);
    }
    
    
}
