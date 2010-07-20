/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.lang.EventObject;
import com.sun.star.awt.XMenuListener;
import com.sun.star.awt.MenuEvent;
import org.creativecommons.openoffice.CcOOoAddin;

/**
 *
 * @author akila
 */
public class SizesMenuListener implements XMenuListener {

    private InsertImageDialog flickrDialog;
    private CcOOoAddin addin;
            
    public SizesMenuListener(InsertImageDialog _flickrDialog, CcOOoAddin _addin) {
        
        this.flickrDialog = _flickrDialog;
        this.addin = _addin;
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
