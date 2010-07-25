/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.ui.picasa;

import java.util.ArrayList;
import com.sun.star.awt.SystemPointer;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.PicasaConnection;

/**
 *
 * @author akila
 */
public class SearchThread extends Thread {

    private PicasaDialog picasaDialog;
    private String buttonName;

    public SearchThread(PicasaDialog picasaDialog, String btnName) {

        this.picasaDialog = picasaDialog;
        this.buttonName = btnName;
    }

    @Override
    public void run() {

        picasaDialog.setMousePointer(SystemPointer.WAIT);
        picasaDialog.enableControl(PicasaDialog.BTN_NEXT, false);
        picasaDialog.enableControl(PicasaDialog.BTN_PREVIOUS, false);
        picasaDialog.enableControl(PicasaDialog.BTN_SEARCH, false);
        picasaDialog.saveSearch();
        picasaDialog.setProgressValue(0);
        String licenseID = picasaDialog.getLicense();
        ArrayList<Image> imgList = null;
        if (buttonName.equalsIgnoreCase(PicasaDialog.BTN_SEARCH)) {
            //PicasaConnection p=PicasaConnection.instance;
            PicasaConnection.instance.searchPhotos(picasaDialog.GetTags(),licenseID);
            picasaDialog.setCurrentPage(1);
            imgList = new ArrayList<Image>();
            int currentPage = picasaDialog.getCurrentPage();
            int noOfImg = PicasaDialog.SHOWRESULTSPERCOLUMN
                    * PicasaDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < PicasaConnection.imgList.size() ?
                currentPage * noOfImg + 1 : PicasaConnection.imgList.size() - 1;
            for (int i = 0; i <= limit; i++) {
                imgList.add(PicasaConnection.imgList.get(i));
            }
        } else if (buttonName.equalsIgnoreCase(PicasaDialog.BTN_PREVIOUS)) {

            picasaDialog.setCurrentPage(picasaDialog.getCurrentPage() - 1);
            imgList = new ArrayList<Image>();
            int currentPage = picasaDialog.getCurrentPage();
            int noOfImg = PicasaDialog.SHOWRESULTSPERCOLUMN
                    * PicasaDialog.SHOWRESULTSPERROW;
            int start = currentPage - 1 < 0 ? 0 : (currentPage - 1) * noOfImg;
            int limit = currentPage * noOfImg + 1 < PicasaConnection.imgList.size()
                    ? currentPage * noOfImg + 1 : PicasaConnection.imgList.size() - 1;
            for (int i = start; i <= limit; i++) {
                imgList.add(PicasaConnection.imgList.get(i));
            }
        } else {

            picasaDialog.setCurrentPage(picasaDialog.getCurrentPage() + 1);
            imgList=new ArrayList<Image>();
            int currentPage = picasaDialog.getCurrentPage();
            int noOfImg = PicasaDialog.SHOWRESULTSPERCOLUMN
                    * PicasaDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < PicasaConnection.imgList.size() ?
                currentPage * noOfImg + 1 : PicasaConnection.imgList.size() - 1;
            for (int i = (currentPage - 1) * noOfImg; i <= limit; i++) {
                imgList.add(PicasaConnection.imgList.get(i));
            }
        }

        picasaDialog.setProgressValue(15);
        picasaDialog.showResults(imgList, 15);
        picasaDialog.setProgressValue(100);
        picasaDialog.enableControl(PicasaDialog.BTN_SEARCH, true);
        picasaDialog.setMousePointer(SystemPointer.ARROW);
    }
}
