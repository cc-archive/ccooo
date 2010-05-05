/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.wikimedia;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;

/**
 *
 * @author Husleag Mihai
 */
public class SearchClickListener implements XActionListener{

    private WikimediaDialog wikimediaDialog;
    private CcOOoAddin addin;

    public SearchClickListener(WikimediaDialog wikimediaDialog, CcOOoAddin addin){
        this.wikimediaDialog = wikimediaDialog;
        this.addin = addin;
    }
    
    public void actionPerformed(ActionEvent a) {
        SearchThread th = new SearchThread(wikimediaDialog,a.ActionCommand);
        th.start();
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
    
}
