/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.openclipart;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;

/**
 *
 * @author Husleag Mihai
 */
public class SearchClickListener implements XActionListener{

    private OpenClipArtDialog openClipArtDialog;
    private CcOOoAddin addin;

    public SearchClickListener(OpenClipArtDialog openClipArtDialog, CcOOoAddin addin){
        this.openClipArtDialog = openClipArtDialog;
        this.addin = addin;
    }
    
    public void actionPerformed(ActionEvent a) {
        SearchThread th = new SearchThread(openClipArtDialog,a.ActionCommand);
        th.start();
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
    
}
