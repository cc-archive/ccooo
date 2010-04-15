/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.openclipart;

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
import com.sun.star.awt.XMouseListener;
import com.sun.star.awt.MouseButton;

/**
 *
 * @author Husleag Mihai
 */
public class ImageButtonListener implements XMouseListener{
    
    private OpenClipArtDialog openClipArtDialog;
    private CcOOoAddin addin;
    private Image currentImage;

    public ImageButtonListener(OpenClipArtDialog flickrDialog, CcOOoAddin addin, Image img){

        this.openClipArtDialog = flickrDialog;
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
//              openClipArtDialog.executePopupMenu(this.currentImage, _mouseEvent.X,
//                      _mouseEvent.Y, xControl.getPeer());
          }
          else
              if (_mouseEvent.Buttons == MouseButton.LEFT && _mouseEvent.ClickCount == 2) 
              {
                  openClipArtDialog.close();
                  openClipArtDialog.setSelectedImage(currentImage);
                  //currentImage.setLicenseNumber(openClipArtDialog.getLicenseNumber(currentImage.getLicenseURL()));
                  //currentImage.setLicenseCode(openClipArtDialog.getLicenseCode(currentImage.getLicenseURL()));
                  addin.getProgramWrapper().insertPictureFlickr(openClipArtDialog.getSelectedImage());
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
