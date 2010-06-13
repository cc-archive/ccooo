/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.picasa;

/**
 *
 * @author Husleag Mihai
 */
public class SavedSearchThread extends Thread {
    
    private PicasaDialog picasaDialog;
    
    public SavedSearchThread(PicasaDialog picasaDialog) {
        
        this.picasaDialog = picasaDialog;
    }

    @Override
    public void run() {
    
        picasaDialog.startSavedSearch();
    }

}
