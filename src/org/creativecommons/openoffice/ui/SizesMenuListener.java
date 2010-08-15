/*
 * SizesMenuListener.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 * 
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.lang.EventObject;
import com.sun.star.awt.XMenuListener;
import com.sun.star.awt.MenuEvent;

/**
 * Listens to sizes menu events
 * @author akila
 */
public class SizesMenuListener implements XMenuListener {

    private InsertImageDialog imageDialog;
            
    public SizesMenuListener(InsertImageDialog imageDialog) {
        
        this.imageDialog = imageDialog;
    }
    
    public void highlight(MenuEvent me) {
        
    }
    
    public void select(MenuEvent me){
    }
    
    public void activate(MenuEvent me) {       
    }
    
    public void deactivate(MenuEvent me) {       
    }  
    
    public void disposing(EventObject e) {        
    }  
}
