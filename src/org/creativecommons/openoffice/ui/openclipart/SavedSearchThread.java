/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.openclipart;

/**
 *
 * @author Husleag Mihai
 */
public class SavedSearchThread extends Thread {
    
    private OpenClipArtDialog openClipArtDialog;
    
    public SavedSearchThread(OpenClipArtDialog openClipArtDialog) {
        
        this.openClipArtDialog = openClipArtDialog;
    }

    @Override
    public void run() {
    
        openClipArtDialog.startSavedSearch();
    }

}
