/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.flickr;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XItemListener;
import com.sun.star.lang.EventObject;

/**
 *
 * @author Administrator
 */
public class LicenseListListener extends UpdateFlickrListener 
        implements XItemListener {

    public LicenseListListener(PictureFlickrDialog dialog) {
        super(dialog);
    }
    
    @Override
    public void itemStateChanged(ItemEvent event) {
             
        
        super.itemStateChanged(event);
    }

    @Override
    public void disposing(EventObject event) {
    }
}
