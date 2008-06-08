/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
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

/**
 *
 * @author Administrator
 */
public class SearchClickListener implements XActionListener{

    private PictureFlickrDialog flickrDialog;
    private CcOOoAddin addin;
    private FlickrConnection flickrConn;
    public SearchClickListener(PictureFlickrDialog flickrDialog, CcOOoAddin addin){

        this.flickrDialog = flickrDialog;
        this.addin = addin;
    }
    
    public void actionPerformed(ActionEvent a) {
        
        ArrayList<Image> imgList = flickrConn.instance.searchPhotos(flickrDialog.GetTags(),
                flickrDialog.GetLicense());
        flickrDialog.showResults(imgList);        
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
    
}
