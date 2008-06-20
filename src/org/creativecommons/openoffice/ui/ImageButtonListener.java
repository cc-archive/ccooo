/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.MouseEvent;
import com.sun.star.awt.FocusEvent;
import com.sun.star.awt.XButton;
import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlModel;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.license.Chooser;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.program.Calc;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.program.Impress;
import org.creativecommons.openoffice.program.Writer;
import org.creativecommons.openoffice.program.Image;
import org.creativecommons.openoffice.program.FlickrConnection;
import java.util.ArrayList;
import com.sun.star.awt.XMouseListener;
import com.sun.star.awt.MouseButton;

/**
 *
 * @author Administrator
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
        
        if (_mouseEvent.Buttons == MouseButton.RIGHT) {
            
          //we have to add also the position of the image control within the main dialog
          XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, _mouseEvent.Source);
          XControlModel xControlModel = xControl.getModel();
          XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);
      
          Integer posX =0;
          Integer posY =0;
          try
          {
            posX = (Integer) xPSet.getPropertyValue("PositionX");
            posY = (Integer) xPSet.getPropertyValue("PositionY");
          }
          catch (Exception ex) {
              ex.printStackTrace();
          }
            
          flickrDialog.executePopupMenu(this.currentImage, _mouseEvent.X + posX, _mouseEvent.Y + posY);
        }
        
    }    
    
    public void mouseExited(MouseEvent mouseEvent) {
    }
    
    public void mouseEntered(MouseEvent _mouseEvent) {
        
        
        
    }
    
    public void actionPerformed(ActionEvent a) {

      if (a.ActionCommand != "")
      {
          XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, a.Source);
          XControlModel xControlModel = xControl.getModel();
          XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);
      
          Integer posX =100;
          Integer posY =100;
          try
          {
            posX = (Integer) xPSet.getPropertyValue("PositionX");
            posY = (Integer) xPSet.getPropertyValue("PositionY");
          }
          catch (Exception ex) {
              ex.printStackTrace();
          }
          
          Image img = flickrDialog.currentList.get(Integer.parseInt(a.ActionCommand));
          flickrDialog.executePopupMenu(img, posX, posY);
      } 
    }
    
    public void disposing(EventObject e) {
        
    }       

}
