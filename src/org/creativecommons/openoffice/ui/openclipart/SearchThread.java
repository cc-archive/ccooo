/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.ui.openclipart;

import java.util.ArrayList;
import com.sun.star.awt.SystemPointer;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.OpenClipArtConnection;

/**
 *
 * @author akila
 */
public class SearchThread extends Thread {

    private OpenClipArtDialog openClipArtDialog;
    private String buttonName;

    public SearchThread(OpenClipArtDialog openClipArtDialog, String btnName) {

        this.openClipArtDialog = openClipArtDialog;
        this.buttonName = btnName;
    }

    @Override
    public void run() {

        openClipArtDialog.setMousePointer(SystemPointer.WAIT);
        openClipArtDialog.enableControl(OpenClipArtDialog.BTN_NEXT, false);
        openClipArtDialog.enableControl(OpenClipArtDialog.BTN_PREVIOUS, false);
        openClipArtDialog.enableControl(OpenClipArtDialog.BTN_SEARCH, false);
        openClipArtDialog.saveSearch();
        openClipArtDialog.setProgressValue(0);
        ArrayList<Image> imgList = null;
        if (buttonName.equalsIgnoreCase(OpenClipArtDialog.BTN_SEARCH)) {
            OpenClipArtConnection.instance.searchPhotos(
                    openClipArtDialog.GetTags(), openClipArtDialog.getCurrentPage());
            openClipArtDialog.setCurrentPage(1);
            imgList = new ArrayList<Image>();
            int currentPage = openClipArtDialog.getCurrentPage();
            int noOfImg = OpenClipArtDialog.SHOWRESULTSPERCOLUMN
                    * OpenClipArtDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < OpenClipArtConnection.imgList.size() ?
                currentPage * noOfImg + 1 : OpenClipArtConnection.imgList.size() - 1;
            for (int i = 0; i <= limit; i++) {
                imgList.add(OpenClipArtConnection.imgList.get(i));
                System.out.println(i);
            }
        } else if (buttonName.equalsIgnoreCase(OpenClipArtDialog.BTN_PREVIOUS)) {

            openClipArtDialog.setCurrentPage(openClipArtDialog.getCurrentPage() - 1);
            imgList = new ArrayList<Image>();
            int currentPage = openClipArtDialog.getCurrentPage();
            int noOfImg = OpenClipArtDialog.SHOWRESULTSPERCOLUMN
                    * OpenClipArtDialog.SHOWRESULTSPERROW;
            int start = currentPage-1<0?0:(currentPage-1);
            int limit = currentPage * noOfImg + 1 < OpenClipArtConnection.imgList.size() ?
                currentPage * noOfImg + 1 : OpenClipArtConnection.imgList.size() - 1;
            for (int i = start; i <= limit; i++) {
                imgList.add(OpenClipArtConnection.imgList.get(i));
                System.out.println(i);
            }
        } else {

            openClipArtDialog.setCurrentPage(openClipArtDialog.getCurrentPage() + 1);
            imgList=new ArrayList<Image>();
            int currentPage = openClipArtDialog.getCurrentPage();
            int noOfImg = OpenClipArtDialog.SHOWRESULTSPERCOLUMN
                    * OpenClipArtDialog.SHOWRESULTSPERROW;
            int limit = currentPage * noOfImg + 1 < OpenClipArtConnection.imgList.size() ?
                currentPage * noOfImg + 1 : OpenClipArtConnection.imgList.size() - 1;
            for(int i=(currentPage-1)*noOfImg;i<=limit;i++){
                imgList.add(OpenClipArtConnection.imgList.get(i));
                System.out.println(i);
            }
        }


        openClipArtDialog.setProgressValue(15);
        openClipArtDialog.showResults(imgList, 15);
        openClipArtDialog.setProgressValue(100);
        openClipArtDialog.enableControl(OpenClipArtDialog.BTN_SEARCH, true);
        openClipArtDialog.setMousePointer(SystemPointer.ARROW);
    }
}
