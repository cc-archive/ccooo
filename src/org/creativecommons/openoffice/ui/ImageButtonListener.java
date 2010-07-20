/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.ui;

import com.sun.star.awt.MouseEvent;
import com.sun.star.awt.FocusEvent;
import com.sun.star.awt.ActionEvent;
import com.sun.star.lang.EventObject;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.program.Image;
import com.sun.star.awt.XMouseListener;
import com.sun.star.awt.MouseButton;

/**
 *
 * @author akila
 */
public abstract class ImageButtonListener implements XMouseListener {

    protected InsertImageDialog imageDialog;
    protected CcOOoAddin addin;
    protected Image currentImage;

    public ImageButtonListener(InsertImageDialog imageDialog, CcOOoAddin addin, Image img) {

        this.imageDialog = imageDialog;
        this.addin = addin;
        this.currentImage = img;
    }

    public void focusGained(FocusEvent focusEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent _mouseEvent) {

        if ((_mouseEvent.Buttons == MouseButton.RIGHT && !_mouseEvent.PopupTrigger)
                || (_mouseEvent.Buttons == MouseButton.LEFT && !_mouseEvent.PopupTrigger)) {
                mousePressedRun(_mouseEvent);
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

    protected abstract void mousePressedRun(MouseEvent _mouseEvent);
}
