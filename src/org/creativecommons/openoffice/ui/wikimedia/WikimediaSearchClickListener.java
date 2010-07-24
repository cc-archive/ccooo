/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.wikimedia;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.ui.SearchClickListener;

/**
 *
 * @author Husleag Mihai
 */
public class WikimediaSearchClickListener extends SearchClickListener{

    public WikimediaSearchClickListener(WikimediaDialog wikimediaDialog, CcOOoAddin addin){
        super( wikimediaDialog,addin);
    }
    
    public void actionPerformed(ActionEvent a) {

        if (!imageDialog.IsInputValid()) {
            return;
        }
        SearchThread th = new SearchThread((WikimediaDialog) imageDialog,a.ActionCommand);
        th.start();
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
    
}
