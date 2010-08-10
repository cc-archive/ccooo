/*
 * WikimediaImageButtonListener.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 */

package org.creativecommons.openoffice.ui.wikimedia;

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
 * @author Husleag Mihai
 */
public class WikimediaImageButtonListener extends ImageButtonListener {

    public WikimediaImageButtonListener(WikimediaDialog wikimediaDialog, CcOOoAddin addin, Image img) {

        super(wikimediaDialog, addin, img);
    }

    @Override
    protected void mousePressedRun(MouseEvent _mouseEvent) {

        //we have to add also the position of the image control within the main dialog
        XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, _mouseEvent.Source);
        XControlModel xControlModel = xControl.getModel();
        XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);

        if (_mouseEvent.Buttons == MouseButton.RIGHT) {
            //   XWindowPeer xImagePeer = (XWindowPeer) UnoRuntime.queryInterface(XControl.class, xControl);
//              wikimediaDialog.executePopupMenu(this.currentImage, _mouseEvent.X,
//                      _mouseEvent.Y, xControl.getPeer());
        } else if (_mouseEvent.Buttons == MouseButton.LEFT && _mouseEvent.ClickCount == 2) {
            imageDialog.close();
            imageDialog.setSelectedImage(currentImage);
            addin.getProgramWrapper().insertPicture(imageDialog.getSelectedImage());
        }
    }
}
