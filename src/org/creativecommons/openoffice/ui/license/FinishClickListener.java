/*
 * FinishClickListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */

package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;

/**
 *
 * @author Husleag Mihai
 */
class FinishClickListener implements XActionListener {

    private ChooserDialog chooserDialog;
    private CcOOoAddin addin;
    
    public FinishClickListener(ChooserDialog chooserDialog, CcOOoAddin addin){

        this.chooserDialog = chooserDialog;
        this.addin = addin;
        
    } // OnFinishClick - public constructor
        
    public void actionPerformed(ActionEvent a) {
               
        this.chooserDialog.setCancelled(false);
        this.chooserDialog.close();
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
} // OnFinishClick