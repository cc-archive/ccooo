/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.flickr;

import java.util.ArrayList;
import com.sun.star.awt.SystemPointer;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.FlickrConnection;

/**
 *
 * @author Husleag Mihai
 */
public class SearchThread extends Thread {
    
    private FlickrDialog flickrDialog;
    private String buttonName;
    
    public SearchThread(FlickrDialog flickrDialog, String btnName) {
        
        this.flickrDialog = flickrDialog;
        this.buttonName = btnName;
    }

    @Override
    public void run() {
        
        flickrDialog.setMousePointer(SystemPointer.WAIT);
        flickrDialog.enableControl(FlickrDialog.BTN_NEXT, false);
        flickrDialog.enableControl(FlickrDialog.BTN_PREVIOUS, false);
        flickrDialog.enableControl(FlickrDialog.BTN_SEARCH, false);
        flickrDialog.saveSearch();
        flickrDialog.setProgressValue(0);
        String licenseID = flickrDialog.getLicense();
                
        if (buttonName.equalsIgnoreCase(FlickrDialog.BTN_SEARCH)) {
            
            flickrDialog.setCurrentPage(1);
        }
            else
                if (buttonName.equalsIgnoreCase(FlickrDialog.BTN_PREVIOUS)) {
                
                    flickrDialog.setCurrentPage(flickrDialog.getCurrentPage() - 1);
                }
                else {
            
                    flickrDialog.setCurrentPage(flickrDialog.getCurrentPage() + 1);
                }
        
        ArrayList<Image> imgList = FlickrConnection.instance.searchPhotos(
                flickrDialog.GetTags(),licenseID, flickrDialog.getCurrentPage());
        flickrDialog.setProgressValue(15);
        flickrDialog.showResults(imgList, 15);
        flickrDialog.setProgressValue(100);
        flickrDialog.enableControl(FlickrDialog.BTN_SEARCH, true);
        flickrDialog.setMousePointer(SystemPointer.ARROW);
    }
    
    
}
