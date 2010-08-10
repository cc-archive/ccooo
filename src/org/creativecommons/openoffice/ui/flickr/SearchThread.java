/*
 * SearchThread.java
 *
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 * 
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
        ArrayList<Image> imgList = null;
        if (buttonName.equalsIgnoreCase(FlickrDialog.BTN_SEARCH)) {
            flickrDialog.setCurrentPage(1);
            FlickrConnection.instance.searchPhotos(flickrDialog.GetTags(),licenseID);
            imgList = new ArrayList<Image>();
            int currentPage = flickrDialog.getCurrentPage();
            int noOfImg = FlickrDialog.SHOWRESULTSPERCOLUMN * FlickrDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < FlickrConnection.imgList.size() ?
                currentPage * noOfImg + 1 : FlickrConnection.imgList.size() - 1;
            for (int i = 0; i <= limit; i++) {
                imgList.add(FlickrConnection.imgList.get(i));
            }

        } else if (buttonName.equalsIgnoreCase(FlickrDialog.BTN_PREVIOUS)) {

            flickrDialog.setCurrentPage(flickrDialog.getCurrentPage() - 1);
            imgList = new ArrayList<Image>();
            int currentPage = flickrDialog.getCurrentPage();
            int noOfImg = FlickrDialog.SHOWRESULTSPERCOLUMN * FlickrDialog.SHOWRESULTSPERROW;
            int start = currentPage - 1 < 0 ? 0 : (currentPage - 1) * noOfImg;
            int limit = currentPage * noOfImg + 1 < FlickrConnection.imgList.size()
                    ? currentPage * noOfImg + 1 : FlickrConnection.imgList.size() - 1;
            for (int i = start; i <= limit; i++) {
                imgList.add(FlickrConnection.imgList.get(i));
            }
        } else {

            flickrDialog.setCurrentPage(flickrDialog.getCurrentPage() + 1);
            imgList = new ArrayList<Image>();
            int currentPage = flickrDialog.getCurrentPage();
            int noOfImg = FlickrDialog.SHOWRESULTSPERCOLUMN * FlickrDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < FlickrConnection.imgList.size() ?
                currentPage * noOfImg + 1 : FlickrConnection.imgList.size() - 1;
            for (int i = (currentPage - 1) * noOfImg; i <= limit; i++) {
                imgList.add(FlickrConnection.imgList.get(i));
            }
        }

        flickrDialog.setProgressValue(15);
        flickrDialog.showResults(imgList, 15);
        flickrDialog.setProgressValue(100);
        flickrDialog.enableControl(FlickrDialog.BTN_SEARCH, true);
        flickrDialog.setMousePointer(SystemPointer.ARROW);
    }
    
    
}
