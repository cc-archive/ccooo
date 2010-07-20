/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;

/**
 *
 * @author Husleag Mihai
 */
public abstract class SearchClickListener implements XActionListener{

    protected InsertImageDialog imageDialog;
    protected CcOOoAddin addin;

    public SearchClickListener(InsertImageDialog imageDialog, CcOOoAddin addin){

        this.imageDialog = imageDialog;
        this.addin = addin;
    }
    
    public abstract void actionPerformed(ActionEvent a);
    
    public abstract void disposing(EventObject e);
    
}
