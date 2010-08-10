/*
 * PicasaSearchClickListener.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.picasa;

import com.sun.star.awt.ActionEvent;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.ui.SearchClickListener;

/**
 *
 * @author Husleag Mihai
 */
public class PicasaSearchClickListener extends SearchClickListener{

    public PicasaSearchClickListener(PicasaDialog picasaDialog, CcOOoAddin addin){
        super( picasaDialog,addin);
    }

    public void actionPerformed(ActionEvent a) {

        if (!imageDialog.IsInputValid()) {
            return;
        }
        SearchThread th = new SearchThread((PicasaDialog) imageDialog,a.ActionCommand);
        th.start();

    } // actionPerformed

    public void disposing(EventObject e) {
    }

}
