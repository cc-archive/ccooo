/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
 
package org.creativecommons.openoffice.ui;

import com.sun.star.beans.UnknownPropertyException;
import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.Iterator;
import java.util.List;
import org.creativecommons.license.Chooser;
import org.creativecommons.license.IJurisdiction;
import org.creativecommons.license.Jurisdiction;
import org.creativecommons.license.License;
import org.creativecommons.license.Store;
import org.creativecommons.openoffice.*;

/**
 *  The Creative Commons OpenOffice.org AddIn GUI class.
 *
 *
 *
 *
 * @author Cassio A. Melo
 * @author Creative Commons
 * @version 0.0.1
 */
public class PictureFlickrDialog {
    private XMultiServiceFactory xMultiServiceFactory = null;
    protected XComponentContext m_xContext = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    private XNameContainer xNameCont = null;
    private XControlContainer xControlCont = null;
    
    private XDialog xDialog = null;
    private CcOOoAddin addin = null;
    
    private boolean cancelled = true;
    
    /**
     * Creates a new instance of ChooserDialog
     */
    public PictureFlickrDialog(CcOOoAddin addin, XComponentContext m_xContext) {
        this.addin = addin;
        this.m_xContext = m_xContext;
    }
    
    /**
     * Method for creating a dialog at runtime
     *
     */
    public void showDialog() throws com.sun.star.uno.Exception {
        
        try
        {
            
        // get the service manager from the component context
        this.xMultiComponentFactory = this.m_xContext.getServiceManager();
        
        // create the dialog model and set the properties
        Object dlgLicenseSelector = xMultiComponentFactory.createInstanceWithContext
                ("com.sun.star.awt.UnoControlDialogModel", m_xContext);
        XMultiServiceFactory msfLicenseSelector = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dlgLicenseSelector);
        
            XPropertySet xPSetDialog = createAWTControl(dlgLicenseSelector, "dlgMainForm",
                "", new Rectangle(100, 100, 310, 225));
            xPSetDialog.setPropertyValue("Title", new String("Insert Picture From Flickr"));
            xPSetDialog.setPropertyValue("Step", (short)1 );        
        
        // get the name container for the dialog for inserting other elements
        this.xNameCont = (XNameContainer)UnoRuntime.queryInterface(
                XNameContainer.class, dlgLicenseSelector);
        
        // get the service manager from the dialog model
        this.xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dlgLicenseSelector);
        
        Object lblTags = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblTags, "lblTags", "Tags", new Rectangle(10, 10, 50, 15));
        
        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialog", m_xContext); //esse
        XControl xControl = (XControl)UnoRuntime.queryInterface(XControl.class, dialog );
        XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(XControlModel.class, dlgLicenseSelector);
        xControl.setModel(xControlModel);
        
        // create a peer
        Object toolkit = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
        XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(XToolkit.class, toolkit);
        XWindow xWindow = (XWindow)UnoRuntime.queryInterface(XWindow.class, xControl);
        xWindow.setVisible(false);
        xControl.createPeer(xToolkit, null);
        
        // execute the dialog
        this.xDialog = (XDialog)UnoRuntime.queryInterface(XDialog.class, dialog);
        this.xDialog.execute();
        
        // dispose the dialog
        XComponent xComponent = (XComponent)UnoRuntime.queryInterface(XComponent.class, dialog);
        xComponent.dispose();
        
         } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    private XPropertySet createAWTControl(Object objControl, String ctrlName,
            String ctrlCaption, Rectangle posSize ) throws Exception {
                
        XPropertySet xpsProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, objControl);
        
        xpsProperties.setPropertyValue("PositionX", new Integer(posSize.x));
        xpsProperties.setPropertyValue("PositionY", new Integer(posSize.y));
        xpsProperties.setPropertyValue("Width", new Integer(posSize.width));
        xpsProperties.setPropertyValue("Height", new Integer(posSize.height));
        xpsProperties.setPropertyValue  ("Name", ctrlName);
        if (ctrlCaption != "")
            xpsProperties.setPropertyValue("Label", ctrlCaption);
        
        if (getNameContainer()!= null)
        {
            getNameContainer().insertByName(ctrlName, objControl);
        }
        
        return xpsProperties;
    }
    
     public XNameContainer getNameContainer() {
        return xNameCont;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public void close() {
        this.xDialog.endExecute();
        
    }
    
    
} // ChooserDialog

