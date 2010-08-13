/*
 * SearchThread.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.wikimedia;

import java.util.ArrayList;
import com.sun.star.awt.SystemPointer;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.WikimediaConnection;

/**
 *
 * @author akila
 */
public class SearchThread extends Thread {

    private WikimediaDialog wikimediaDialog;
    private String buttonName;

    public SearchThread(WikimediaDialog wikimediaDialog, String btnName) {

        this.wikimediaDialog = wikimediaDialog;
        this.buttonName = btnName;
    }

    @Override
    public void run() {

        wikimediaDialog.setMousePointer(SystemPointer.WAIT);
        wikimediaDialog.enableControl(WikimediaDialog.BTN_NEXT, false);
        wikimediaDialog.enableControl(WikimediaDialog.BTN_PREVIOUS, false);
        wikimediaDialog.enableControl(WikimediaDialog.BTN_SEARCH, false);
        wikimediaDialog.saveSearch();
        wikimediaDialog.setProgressValue(0);
        ArrayList<Image> imgList = null;
        if (buttonName.equalsIgnoreCase(WikimediaDialog.BTN_SEARCH)) {
            wikimediaDialog.setCurrentPage(1);
            String[] licenses = wikimediaDialog.getLicenses();
            WikimediaConnection.instance.searchPhotos(wikimediaDialog.GetTags(), licenses);
            imgList = new ArrayList<Image>();
            int currentPage = wikimediaDialog.getCurrentPage();
            int noOfImg = WikimediaDialog.SHOWRESULTSPERCOLUMN * WikimediaDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < WikimediaConnection.imgList.size()
                    ? currentPage * noOfImg + 1 : WikimediaConnection.imgList.size() - 1;
            for (int i = 0; i <= limit; i++) {
                imgList.add(WikimediaConnection.imgList.get(i));
            }

        } else if (buttonName.equalsIgnoreCase(WikimediaDialog.BTN_PREVIOUS)) {

            wikimediaDialog.setCurrentPage(wikimediaDialog.getCurrentPage() - 1);
            imgList = new ArrayList<Image>();
            int currentPage = wikimediaDialog.getCurrentPage();
            int noOfImg = WikimediaDialog.SHOWRESULTSPERCOLUMN * WikimediaDialog.SHOWRESULTSPERROW;
            int start = currentPage - 1 < 0 ? 0 : (currentPage - 1) * noOfImg;
            int limit = currentPage * noOfImg + 1 < WikimediaConnection.imgList.size()
                    ? currentPage * noOfImg + 1 : WikimediaConnection.imgList.size() - 1;
            for (int i = start; i <= limit; i++) {
                imgList.add(WikimediaConnection.imgList.get(i));
            }
        } else {

            wikimediaDialog.setCurrentPage(wikimediaDialog.getCurrentPage() + 1);
            imgList = new ArrayList<Image>();
            int currentPage = wikimediaDialog.getCurrentPage();
            int noOfImg = WikimediaDialog.SHOWRESULTSPERCOLUMN * WikimediaDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < WikimediaConnection.imgList.size()
                    ? currentPage * noOfImg + 1 : WikimediaConnection.imgList.size() - 1;
            for (int i = (currentPage - 1) * noOfImg; i <= limit; i++) {
                imgList.add(WikimediaConnection.imgList.get(i));
            }
        }
        wikimediaDialog.setProgressValue(15);
        wikimediaDialog.showResults(imgList, 15);
        wikimediaDialog.setProgressValue(100);
        wikimediaDialog.enableControl(WikimediaDialog.BTN_SEARCH, true);
        wikimediaDialog.setMousePointer(SystemPointer.ARROW);

    }
}
