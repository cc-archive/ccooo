/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
 
package org.creativecommons.openoffice.ui;

import com.sun.star.uno.AnyConverter;
import com.sun.star.ucb.XFileIdentifierConverter;
import com.sun.star.beans.UnknownPropertyException;
import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.PushButtonType;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XPopupMenu;
import com.sun.star.awt.XMenuExtended;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XMultiPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.awt.XFixedHyperlink;
import com.sun.star.uno.XComponentContext;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.sun.star.graphic.XGraphic;
import org.creativecommons.license.Chooser;
import org.creativecommons.license.IJurisdiction;
import org.creativecommons.license.Jurisdiction;
import org.creativecommons.license.License;
import org.creativecommons.license.Store;
import org.creativecommons.openoffice.*;
import org.creativecommons.openoffice.program.Image;
import com.sun.star.awt.XWindowPeer; 
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.beans.PropertyValue;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.io.BufferedInputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.InputStream;
import org.creativecommons.openoffice.program.FlickrConnection;

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
    private XControl xControl;
    
    private CcOOoAddin addin = null;
    private int currentPositionInList = 0;
    public ArrayList<Image> currentList = null;
    private Image selectedImage = null;
    
    public static final String LBL_TAGS = "lblTags";
    public static final String TXT_TAGS = "txtTags";
    public static final String LBL_LICENSE = "lblLicense";
    public static final String LISTBOX_LICENSE = "cmbLicense";    
    public static final String BTN_SEARCH = "btnSearch";
    public static final String searchButtonLabel = "Search";
    public static final String GB_RESULTS = "gbResults";
    public static final String BTN_NEXT = "btnNext";
    public static final String BTN_NEXTLABEL = "Next";
    
    public static final int SHOWRESULTSPERPAGE = 6;
    public static final int POSITIONWIDTHHEIGHT = 50;
    
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
                "", new Rectangle(100, 100, 250, 400));
        xPSetDialog.setPropertyValue("Title", new String("Insert Picture From Flickr"));
        xPSetDialog.setPropertyValue("Step", (short)1 );        
        
        // get the name container for the dialog for inserting other elements
        this.xNameCont = (XNameContainer)UnoRuntime.queryInterface(
                XNameContainer.class, dlgLicenseSelector);
        
        // get the service manager from the dialog model
        this.xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dlgLicenseSelector);
        
        Object lblTags = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblTags, LBL_TAGS, "Tags", new Rectangle(10, 10, 50, 12));        
        
        Object txtTags = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlEditModel");
        createAWTControl(txtTags, TXT_TAGS, "", new Rectangle(30, 10, 150, 12));
        
        Object lblLicense = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblLicense, LBL_LICENSE, "License", new Rectangle(10, 35, 50, 12));        
        
        Object cmbLicense = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlListBoxModel");   
        XPropertySet xpsLicense = createAWTControl(cmbLicense, LISTBOX_LICENSE, "", new Rectangle(30, 35, 150, 12));                    
        xpsLicense.setPropertyValue("MultiSelection", new Boolean("false"));
        xpsLicense.setPropertyValue("Dropdown", new Boolean("true"));
        xpsLicense.setPropertyValue("Step", new Short((short)1));
        
        Object searchButton = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlButtonModel");   
        XPropertySet xPSetFinishButton = createAWTControl(searchButton, BTN_SEARCH, searchButtonLabel,
                new Rectangle(30, 55, 40, 15));                    
        xPSetFinishButton.setPropertyValue("DefaultButton", new Boolean("true"));
        
        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialog", m_xContext); //esse
        xControl = (XControl)UnoRuntime.queryInterface(XControl.class, dialog );
        XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(XControlModel.class, dlgLicenseSelector);
        xControl.setModel(xControlModel);                
        
        xControlCont = (XControlContainer)UnoRuntime.queryInterface(
                XControlContainer.class, dialog);
        
        Object objSearchButton = xControlCont.getControl(BTN_SEARCH);
        XButton xFinishButton = (XButton)UnoRuntime.queryInterface(XButton.class, objSearchButton);
        xFinishButton.addActionListener(new SearchClickListener(this, this.addin));
                               
        Object oLicense = xControlCont.getControl(LISTBOX_LICENSE);
        XListBox cmbJList = (XListBox)UnoRuntime.queryInterface(XListBox.class, oLicense);
        //cmbJList.addItem("0.None", new Short((short)0));
        cmbJList.addItem("1.Attribution-NonCommercial-ShareAlike License", new Short((short)1));
        cmbJList.addItem("2.Attribution-NonCommercial License", new Short((short)2));
        cmbJList.addItem("3.Attribution-NonCommercial-NoDerivs License", new Short((short)3));
        cmbJList.addItem("4.Attribution License", new Short((short)4));
        cmbJList.addItem("5.Attribution-ShareAlike License", new Short((short)5));
        cmbJList.addItem("6.Attribution-NoDerivs License", new Short((short)6));
        cmbJList.selectItem("4.Attribution License", true);
       // cmbJList.makeVisible((short)4);
        cmbJList.addItemListener(new LicenseListListener(this));
       
        Object oGBResults = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlGroupBoxModel");   
        createAWTControl(oGBResults, GB_RESULTS, "Results", new Rectangle(10, 75, 230, 320));                            
        
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
        
        if ((getNameContainer()!= null) &&  (!getNameContainer().hasByName(ctrlName)))
        {
            getNameContainer().insertByName(ctrlName, objControl);
        }
        
        return xpsProperties;
    }
    
     public String[] GetTags() {
         Object oTags = xControlCont.getControl(TXT_TAGS);
         XControl txtTags = (XControl)UnoRuntime.queryInterface(XControl.class, oTags);
         XControlModel xControlModel = txtTags.getModel();
         XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);
         
         try {
               
         String selTags = (String) xPSet.getPropertyValue("Text");
         return selTags.split(" ");
         
         }
         catch ( Exception ex ) {
             ex.printStackTrace();
         }
      
         return new String[0];
     }
     
     public void showResults(ArrayList<Image> imgList) {
     
         this.currentList = imgList;
         this.currentPositionInList = 0;
         
         showNextPage();        
     }
     
     public void showNextPage()
     {
         if (currentList == null)
         {             
            return;
         }
         
         for (int i = 0;i<SHOWRESULTSPERPAGE;i++)
         {             
             if (currentList.size()>currentPositionInList)
             {
                createImageControl(currentList.get(currentPositionInList), new Rectangle(15, 
                        (i+2)*POSITIONWIDTHHEIGHT-15, POSITIONWIDTHHEIGHT, POSITIONWIDTHHEIGHT), String.valueOf(i));
                currentPositionInList++;
             }
             else
                 createImageControl(null, new Rectangle(15, (i+2)*POSITIONWIDTHHEIGHT-15, POSITIONWIDTHHEIGHT, 
                         POSITIONWIDTHHEIGHT), String.valueOf(i));                                    
        
         }
     
         try
         {             
             Object nextButton = null;
             boolean isNewCreated = false;
             if (getNameContainer().hasByName(BTN_NEXT))
             {
                 nextButton = getNameContainer().getByName(BTN_NEXT);
             }
             else
             {             
                nextButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel");                  
                isNewCreated = true;
             }
          
            createAWTControl(nextButton, BTN_NEXT, BTN_NEXTLABEL, new Rectangle(150, 375, 40, 15)); 
            
            if (isNewCreated)
             {
                XButton xNextButton = (XButton)UnoRuntime.queryInterface(XButton.class, 
                        xControlCont.getControl(BTN_NEXT));
                xNextButton.addActionListener(new NextClickListener(this, this.addin));
            }
                                    
         } catch (Exception ex) {
            ex.printStackTrace();
         }
     }
     
     private void createImageControl(Image img, Rectangle rect, String pos) {
         
         try
         {             
             Object oICModel = null;
             if (getNameContainer().hasByName("ImageControl" + pos))
             {
                 getNameContainer().removeByName("ImageControl"+pos);
             }
             
             oICModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlImageControlModel");
             
            XGraphic xGraphic = null;
            if (img != null) {
               
               xGraphic = getGraphic(img.getImgURL());
            }
             
           XPropertySet xpsImageControl = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oICModel);
 
           xpsImageControl.setPropertyValue("Border", (short)0);           
           xpsImageControl.setPropertyValue("Height", new Integer(rect.height));
           xpsImageControl.setPropertyValue("Name", "ImageControl"+pos);
           xpsImageControl.setPropertyValue("PositionX", new Integer(rect.x));
           xpsImageControl.setPropertyValue("PositionY", new Integer(rect.y));
           xpsImageControl.setPropertyValue("Width", new Integer(rect.width));
            
           getNameContainer().insertByName("ImageControl"+pos, oICModel);    
            
           XControl xImageControl = xControlCont.getControl("ImageControl"+pos); 
           XWindow xWindow = (XWindow) UnoRuntime.queryInterface(XWindow.class, xImageControl);
           xWindow.addMouseListener(new ImageButtonListener(this, this.addin, img));            
           
           xpsImageControl.setPropertyValue("Graphic", xGraphic);
            
           Object lblUser = null;
            if (getNameContainer().hasByName("ImageLabelUser"+pos))
            {
                lblUser = getNameContainer().getByName("ImageLabelUser"+pos);                
            }
            else
                lblUser = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedHyperlinkModel");
          
        String userName = "";
            if (img!= null)
            {
                img.setUserName(FlickrConnection.instance.getUserName(img.getUserID()));
                userName = "Photo taken by :" +img.getUserName();
            }
            else
                userName= " ";
            
        XPropertySet xpsProperties = createAWTControl(lblUser, "ImageLabelUser"+pos, userName, new Rectangle(rect.x+rect.height+3, rect.y, 150, 20));        
        if (img!= null)
        {
            xpsProperties.setPropertyValue("URL", img.getProfile());
        }
        else
            xpsProperties.setPropertyValue("URL", "");
        
        Object lblMainPageImage = null;
            if (getNameContainer().hasByName("ImageLabelMainPage"+pos))
            {
                lblMainPageImage = getNameContainer().getByName("ImageLabelMainPage"+pos);
                
            }
            else
                lblMainPageImage = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedHyperlinkModel");
          
            String title = "";
            if (img!= null)
            {
                title = "Title :" +img.getTitle();
            }
            else
                title = " ";
        
        xpsProperties = createAWTControl(lblMainPageImage, "ImageLabelMainPage"+pos, title, new Rectangle(rect.x+rect.height+3, rect.y+23, 150, 20));        
        if (img!= null)
        {
            xpsProperties.setPropertyValue("URL", img.getImgUrlMainPage());
        }
        else
            xpsProperties.setPropertyValue("URL", "");
            
        //http://hermione.s41.xrea.com/pukiwiki/index.php?OOoBasic%2FDialog%2FFixedHyperLink
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     }
     
     public XPopupMenu executePopupMenu(Image img, Integer positionX, Integer positionY){
        
         //i can change into image control, do a right click thing for popup : UnoMenu2.java, maybe TODO
         
        this.selectedImage = img;        
        Collection sizes = FlickrConnection.instance.getPhotoSizes(img.getPhotoID());
        img.setSelectedImageSizes(sizes);
        
        XPopupMenu xPopupMenu = null;
        try{
        // create a popup menu
        Object oPopupMenu = xMultiComponentFactory.createInstanceWithContext("stardiv.Toolkit.VCLXPopupMenu", m_xContext);
        xPopupMenu = (XPopupMenu) UnoRuntime.queryInterface(XPopupMenu.class, oPopupMenu);

        for (Object p : sizes.toArray())
        {              
            com.aetrion.flickr.photos.Size currentSize = ((com.aetrion.flickr.photos.Size)p);   
            xPopupMenu.insertItem((short) currentSize.getLabel(), 
                    FlickrConnection.instance.getStringSize(currentSize.getLabel()), 
                    (short)0, (short) 0);
        } 
        
        com.sun.star.awt.Rectangle rect = new com.sun.star.awt.Rectangle();
        rect.Height =100;rect.Width = 100;
        rect.X = positionX;
        rect.Y = positionY;
        
        xPopupMenu.addMenuListener(new SizesMenuListener(this, addin));
        xPopupMenu.execute(xControl.getPeer(), rect  , (short)1);
        
       }catch( Exception e ) {
        e.printStackTrace();
    }
        return xPopupMenu;
    } 
          
     // creates a UNO graphic object that can be used to be assigned 
  // to the property "Graphic" of a controlmodel
  public XGraphic getGraphic(String _sImageUrl){
  XGraphic xGraphic = null;
  try{
      // create a GraphicProvider at the global service manager...
      Object oGraphicProvider = xMultiComponentFactory.createInstanceWithContext("com.sun.star.graphic.GraphicProvider", m_xContext);
      XGraphicProvider xGraphicProvider = (XGraphicProvider) UnoRuntime.queryInterface(XGraphicProvider.class, oGraphicProvider);
      // create the graphic object
      PropertyValue[] aPropertyValues = new PropertyValue[1];
      PropertyValue aPropertyValue = new PropertyValue();
      aPropertyValue.Name = "URL";
      aPropertyValue.Value = _sImageUrl;
      aPropertyValues[0] = aPropertyValue;
      xGraphic = xGraphicProvider.queryGraphic(aPropertyValues);
      return xGraphic;
  }catch (com.sun.star.uno.Exception ex){
      throw new java.lang.RuntimeException("cannot happen...");
  }}
     
     public String GetLicense() {
         Object oLicense = xControlCont.getControl(LISTBOX_LICENSE);
         XListBox cmbJList = (XListBox)UnoRuntime.queryInterface(XListBox.class, oLicense);
        
         String selectedItem = cmbJList.getSelectedItem();
         if (selectedItem.length()>0)
         {
             return selectedItem.substring(0, 1);
         }
         
         return "0";
     }
    
     public XNameContainer getNameContainer() {
        return xNameCont;
    }   
     
     public Image getSelectedImage() {
         
         return selectedImage;
     }
    
    public void close() {
        this.xDialog.endExecute();
        
    }
    
    
} // ChooserDialog

