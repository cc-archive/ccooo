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
 * @author Husleag Mihai
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
                
        if (buttonName.equalsIgnoreCase(OpenClipArtDialog.BTN_SEARCH)) {
            
            openClipArtDialog.setCurrentPage(1);
        }
            else
                if (buttonName.equalsIgnoreCase(OpenClipArtDialog.BTN_PREVIOUS)) {
                
                    openClipArtDialog.setCurrentPage(openClipArtDialog.getCurrentPage() - 1);
                }
                else {
            
                    openClipArtDialog.setCurrentPage(openClipArtDialog.getCurrentPage() + 1);
                }
        
        ArrayList<Image> imgList = OpenClipArtConnection.instance.searchPhotos(
                openClipArtDialog.GetTags(),openClipArtDialog.getCurrentPage());
        openClipArtDialog.setProgressValue(15);
        openClipArtDialog.showResults(imgList, 15);
        openClipArtDialog.setProgressValue(100);
        openClipArtDialog.enableControl(OpenClipArtDialog.BTN_SEARCH, true);
        openClipArtDialog.setMousePointer(SystemPointer.ARROW);
    }
    
    
}
