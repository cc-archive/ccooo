/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.lang.EventObject;
import com.sun.star.awt.XMenuListener;
import com.aetrion.flickr.photos.Size;
import com.sun.star.awt.MenuEvent;
import org.creativecommons.openoffice.CcOOoAddin;

/**
 *
 * @author mihu
 */
public class SizesMenuListener implements XMenuListener {

    private PictureFlickrDialog flickrDialog;
    private CcOOoAddin addin;
            
    public SizesMenuListener(PictureFlickrDialog _flickrDialog, CcOOoAddin _addin) {
        
        this.flickrDialog = _flickrDialog;
        this.addin = _addin;
    }
    
    public void highlight(MenuEvent me) {
        
    }
    
    public void select(MenuEvent me) {
        
        switch(me.MenuId)
        {
            case (short) Size.THUMB:
            case (short) Size.SQUARE:
            case (short) Size.SMALL:
            case (short) Size.ORIGINAL:
            case (short) Size.MEDIUM:
            case (short) Size.LARGE:
                flickrDialog.close();
                flickrDialog.getSelectedImage().RefreshSelectedImageData(me.MenuId);
                addin.getProgramWrapper().insertPictureFlickr(flickrDialog.getSelectedImage());
                break;
        }
    }
    
    public void activate(MenuEvent me) {
        
    }
    
    public void deactivate(MenuEvent me) {
        
    }  
    
    public void disposing(EventObject e) {
        
    }  
}
