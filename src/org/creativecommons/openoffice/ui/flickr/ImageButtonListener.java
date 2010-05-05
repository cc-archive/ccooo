/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.flickr;

import java.util.Collection;
import com.sun.star.awt.MouseEvent;
import com.sun.star.awt.FocusEvent;
import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlModel;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.EventObject;
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.FlickrConnection;
import com.sun.star.awt.XMouseListener;
import com.sun.star.awt.MouseButton;
import com.sun.star.awt.XWindowPeer;

/**
 *
 * @author Husleag Mihai
 */
public class ImageButtonListener implements XMouseListener{
    
    private PictureFlickrDialog flickrDialog;
    private CcOOoAddin addin;
    private Image currentImage;

    public ImageButtonListener(PictureFlickrDialog flickrDialog, CcOOoAddin addin, Image img){

        this.flickrDialog = flickrDialog;
        this.addin = addin;
        this.currentImage = img;
    }
    
     public void focusGained(FocusEvent focusEvent) {
    }    
    
    public void mouseReleased(MouseEvent mouseEvent) {
    }
    
    public void mousePressed(MouseEvent _mouseEvent) {
        
        if ((_mouseEvent.Buttons == MouseButton.RIGHT && !_mouseEvent.PopupTrigger)||
            (_mouseEvent.Buttons == MouseButton.LEFT && !_mouseEvent.PopupTrigger)) {
            
          //we have to add also the position of the image control within the main dialog
          XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, _mouseEvent.Source);
          XControlModel xControlModel = xControl.getModel();
          XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);      
            
          if (_mouseEvent.Buttons == MouseButton.RIGHT) {
           
           //   XWindowPeer xImagePeer = (XWindowPeer) UnoRuntime.queryInterface(XControl.class, xControl);
              flickrDialog.executePopupMenu(this.currentImage, _mouseEvent.X,
                      _mouseEvent.Y, xControl.getPeer());
          }
          else
              if (_mouseEvent.Buttons == MouseButton.LEFT && _mouseEvent.ClickCount == 2) 
              {
                  flickrDialog.close();
                  flickrDialog.setSelectedImage(currentImage);
                  Collection sizes = FlickrConnection.instance.getPhotoSizes(currentImage.getPhotoID());
                  currentImage.setSelectedImageSizes(sizes);
                  flickrDialog.getSelectedImage().RefreshSelectedSizeImageData((short)com.aetrion.flickr.photos.Size.MEDIUM);                  
                  com.aetrion.flickr.photos.Photo  ph = FlickrConnection.instance.getPhotoInfo(currentImage.getPhotoID(),
                          currentImage.getSecret());
                  currentImage.setLicenseID(ph.getLicense());
                  currentImage.setLicenseURL(flickrDialog.getLicenseURL(ph.getLicense()));
                  currentImage.setLicenseNumber(flickrDialog.getLicenseNumber(currentImage.getLicenseURL()));                  
                  currentImage.setLicenseCode(flickrDialog.getLicenseCode(currentImage.getLicenseURL()));                  
                  addin.getProgramWrapper().insertPicture(flickrDialog.getSelectedImage());
              }
        }
    }
             
    public void mouseExited(MouseEvent mouseEvent) {
    }
    
    public void mouseEntered(MouseEvent _mouseEvent) {    
    }
    
    public void actionPerformed(ActionEvent a) {
    }
    
    public void disposing(EventObject e) {    
    }       

}
