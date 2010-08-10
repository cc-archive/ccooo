/*
 * OpenClipArtSearchClickListener.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.openclipart;

import com.sun.star.awt.ActionEvent;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.ui.SearchClickListener;

/**
 *
 * @author akila
 */
public class OpenClipArtSearchClickListener extends SearchClickListener{

    public OpenClipArtSearchClickListener(OpenClipArtDialog openClipArtDialog, CcOOoAddin addin){
        super( openClipArtDialog,addin);
    }
    
    @Override
    public void actionPerformed(ActionEvent a) {
        if (!imageDialog.IsInputValid()) {
            return;
        }
        SearchThread th = new SearchThread((OpenClipArtDialog) imageDialog,a.ActionCommand);
        th.start();
        
    } // actionPerformed
    
    @Override
    public void disposing(EventObject e) {
    }
    
}
