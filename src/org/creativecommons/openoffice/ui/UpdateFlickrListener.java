/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XItemListener;
import com.sun.star.lang.EventObject;

/**
 *
 * @author Administrator
 */
public class UpdateFlickrListener
    implements XItemListener{

    private PictureFlickrDialog dialog;
    
    public UpdateFlickrListener(PictureFlickrDialog dialog) {
        
        this.setDialog(dialog);
    }
    
    public void itemStateChanged(ItemEvent event) {
        
       
    }

    public void disposing(EventObject event) {
    }

    public PictureFlickrDialog getDialog() {
        return dialog;
    }

    protected void setDialog(PictureFlickrDialog dialog) {
        this.dialog = dialog;
    }
}
