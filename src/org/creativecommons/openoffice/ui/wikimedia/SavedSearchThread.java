/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.wikimedia;

/**
 *
 * @author Husleag Mihai
 */
public class SavedSearchThread extends Thread {
    
    private WikimediaDialog wikimediaDialog;
    
    public SavedSearchThread(WikimediaDialog wikimediaDialog) {
        
        this.wikimediaDialog = wikimediaDialog;
    }

    @Override
    public void run() {
    
        wikimediaDialog.startSavedSearch();
    }

}
