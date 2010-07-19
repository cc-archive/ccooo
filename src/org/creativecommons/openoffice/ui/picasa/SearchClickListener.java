/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.picasa;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;

/**
 *
 * @author Husleag Mihai
 */
public class SearchClickListener implements XActionListener{

    private PicasaDialog picasaDialog;
    private CcOOoAddin addin;

    public SearchClickListener(PicasaDialog picasaDialog, CcOOoAddin addin){
        this.picasaDialog = picasaDialog;
        this.addin = addin;
    }

    public void actionPerformed(ActionEvent a) {

        if (!picasaDialog.IsInputValid()) {
            return;
        }
        SearchThread th = new SearchThread(picasaDialog,a.ActionCommand);
        th.start();

    } // actionPerformed

    public void disposing(EventObject e) {
    }

}
