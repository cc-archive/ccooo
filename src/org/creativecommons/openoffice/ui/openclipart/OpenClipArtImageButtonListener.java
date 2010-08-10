/*
 * OpenClipArtImageButtonListener.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui.openclipart;

import com.sun.star.awt.MouseEvent;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlModel;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.program.Image;
import com.sun.star.awt.MouseButton;
import org.creativecommons.openoffice.ui.ImageButtonListener;

/**
 *
 * @author akila
 */
public class OpenClipArtImageButtonListener extends ImageButtonListener{

    public OpenClipArtImageButtonListener(OpenClipArtDialog openClipArtDialog, CcOOoAddin addin, Image img){

        super(openClipArtDialog, addin,img);
        this.addin = addin;
        this.currentImage = img;
    }

    protected void mousePressedRun(MouseEvent _mouseEvent) {
        //we have to add also the position of the image control within the main dialog
        XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, _mouseEvent.Source);
        XControlModel xControlModel = xControl.getModel();
        XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);

        if (_mouseEvent.Buttons == MouseButton.RIGHT) {
            //   XWindowPeer xImagePeer = (XWindowPeer) UnoRuntime.queryInterface(XControl.class, xControl);
//              openClipArtDialog.executePopupMenu(this.currentImage, _mouseEvent.X,
//                      _mouseEvent.Y, xControl.getPeer());
        } else if (_mouseEvent.Buttons == MouseButton.LEFT && _mouseEvent.ClickCount == 2) {
            imageDialog.close();
            imageDialog.setSelectedImage(currentImage);
            //currentImage.setLicenseNumber(openClipArtDialog.getLicenseNumber(currentImage.getLicenseURL()));
            //currentImage.setLicenseCode(openClipArtDialog.getLicenseCode(currentImage.getLicenseURL()));
            addin.getProgramWrapper().insertPicture(imageDialog.getSelectedImage());
        }
    }
}
