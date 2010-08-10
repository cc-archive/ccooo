/*
 * SavedSearchThread.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

/**
 *
 * @author akila
 */
public class SavedSearchThread extends Thread {
    
    private InsertImageDialog imageDialog;
    
    public SavedSearchThread(InsertImageDialog imageDialog) {
        
        this.imageDialog = imageDialog;
    }

    @Override
    public void run() {
        
        imageDialog.startSavedSearch();
    }

}
