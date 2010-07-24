/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.ui.flickr;

import java.util.Collection;
import com.sun.star.awt.MouseEvent;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlModel;
import com.sun.star.beans.XPropertySet;
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.FlickrConnection;
import com.sun.star.awt.MouseButton;
import org.creativecommons.openoffice.ui.ImageButtonListener;

/**
 *
 * @author Husleag Mihai
 */
public class FlickrImageButtonListener extends ImageButtonListener {

    public FlickrImageButtonListener(FlickrDialog flickrDialog, CcOOoAddin addin, Image img) {

        super(flickrDialog, addin, img);
    }

    @Override
    protected void mousePressedRun(MouseEvent _mouseEvent) {
        //we have to add also the position of the image control within the main dialog
        XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, _mouseEvent.Source);
        XControlModel xControlModel = xControl.getModel();
        XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, xControlModel);

        if (_mouseEvent.Buttons == MouseButton.RIGHT) {

            //   XWindowPeer xImagePeer = (XWindowPeer) UnoRuntime.queryInterface(XControl.class, xControl);
            imageDialog.executePopupMenu(this.currentImage, _mouseEvent.X,
                    _mouseEvent.Y, xControl.getPeer());
        } else if (_mouseEvent.Buttons == MouseButton.LEFT && _mouseEvent.ClickCount == 2) {
            imageDialog.close();
            imageDialog.setSelectedImage(currentImage);
            Collection sizes = FlickrConnection.instance.getPhotoSizes(currentImage.getPhotoID());
            currentImage.setSelectedImageSizes(sizes);
            imageDialog.getSelectedImage().RefreshSelectedSizeImageData(
                    (short) com.aetrion.flickr.photos.Size.MEDIUM);
            com.aetrion.flickr.photos.Photo ph =
                    FlickrConnection.instance.getPhotoInfo(currentImage.getPhotoID(),
                    currentImage.getSecret());
            currentImage.setLicenseID(ph.getLicense());
            currentImage.setLicenseURL(((FlickrDialog) imageDialog).getLicenseURL(ph.getLicense()));
            currentImage.setLicenseNumber(((FlickrDialog) imageDialog).getLicenseNumber(currentImage.getLicenseURL()));
            currentImage.setLicenseCode(((FlickrDialog) imageDialog).getLicenseCode(currentImage.getLicenseURL()));
            addin.getProgramWrapper().insertPicture(imageDialog.getSelectedImage());
        }
    }
}
