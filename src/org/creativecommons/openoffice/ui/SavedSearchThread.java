/*
 * SavedSearchThread.java
 *
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 * 
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
