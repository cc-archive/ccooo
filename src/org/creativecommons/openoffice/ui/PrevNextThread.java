/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.SystemPointer;

/**
 *
 * @author mihu
 */
public class PrevNextThread extends Thread{

    private PictureFlickrDialog flickrDialog;
    private String buttonName;
    
    public PrevNextThread(PictureFlickrDialog flickrDialog, String btnName ) {
        
        this.flickrDialog = flickrDialog;
        this.buttonName = btnName;
    }

    public void run() {
        
        flickrDialog.setMousePointer(SystemPointer.WAIT);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_NEXT, false);
      //  flickrDialog.enableControl(PictureFlickrDialog.BTN_PREVIOUS, false);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, false);
        flickrDialog.setProgressValue(0);
        if (buttonName.equalsIgnoreCase(PictureFlickrDialog.BTN_PREVIOUS))
            flickrDialog.showNextPage(0, false);
        else
            flickrDialog.showNextPage(0, true);            
        flickrDialog.setProgressValue(100);
//        flickrDialog.enableControl(PictureFlickrDialog.BTN_NEXT, true);
        //flickrDialog.enableControl(PictureFlickrDialog.BTN_PREVIOUS, true);
        flickrDialog.enableControl(PictureFlickrDialog.BTN_SEARCH, true);
        flickrDialog.setMousePointer(SystemPointer.ARROW);
    }

}
