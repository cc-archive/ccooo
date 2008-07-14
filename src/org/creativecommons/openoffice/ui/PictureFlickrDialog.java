/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
 
package org.creativecommons.openoffice.ui;

import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.SystemPointer;
import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XPointer;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XPopupMenu;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XMultiPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.ArrayList;
import java.util.Collection;
import com.sun.star.graphic.XGraphic;
import org.creativecommons.openoffice.*;
import org.creativecommons.openoffice.program.Image;
import com.sun.star.awt.XWindowPeer; 
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.beans.PropertyValue;
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
    private XWindowPeer xWindowPeer;
    
    private CcOOoAddin addin = null;
    private int currentPositionInList = 0;
    public ArrayList<Image> currentList = null;
    private Image selectedImage = null;
    private Collection flickrLicenses = null;
    private String savedTags;
    private short savedCommercialStatus;
    private short savedUpdateStatus;
    private short savedShareAlikeStatus;
    private int currentPage = 0;
    private boolean isLoadable = false;
    
    public static final String LBL_TAGS = "lblTags";
    public static final String TXT_TAGS = "txtTags";
    public static final String CHK_COMMERCIALNAME = "chkCommercial";
    public static final String CHK_COMMERCIALLABEL = "Search for works I can use for commercial purposes";
    public static final String CHK_UPDATENAME = "chkUpdate";
    public static final String CHK_UPDATELABEL = "Search for works I can modify, adapt, or build upon";
    public static final String CHK_SHAREALKENAME = "chkShareAlike";
    public static final String CHK_SHAREALKELABEL = "Include content which requires my to Share-Alike";
    //public static final String LBL_LICENSE = "lblLicense";
    //public static final String LISTBOX_LICENSE = "cmbLicense";    
    public static final String BTN_SEARCH = "btnSearch";
    public static final String searchButtonLabel = "Search";
    public static final String GB_RESULTS = "gbResults";
    public static final String BTN_NEXT = "btnNext";    
    public static final String BTN_NEXTLABEL = "Next";
    public static final String BTN_PREVIOUS = "btnPrevious";
    public static final String BTN_PREVIOUSLABEL = "Previous";
    public static final String PB_NAME = "progressBar";
    
    public static final int SHOWRESULTSPERROW = 4;
    public static final int SHOWRESULTSPERCOLUMN = 4;
    public static final int POSITIONWIDTHHEIGHT = 50;
    public static final int LOCATIONIMAGESY = 100;
    
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
    public void showDialog(boolean defaultSearch) throws com.sun.star.uno.Exception {
        
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
                "", new Rectangle(100, 100, 260, 440));
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
        
        Object chkCommercial = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlCheckBoxModel");   
        XPropertySet xpsCHKProperties = createAWTControl(chkCommercial, CHK_COMMERCIALNAME, CHK_COMMERCIALLABEL, 
                new Rectangle(10, 32, 150, 12));                                    
        xpsCHKProperties.setPropertyValue("TriState", Boolean.FALSE);
        xpsCHKProperties.setPropertyValue("State", new Short((short) 1));
        
        Object chkUpdate = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlCheckBoxModel");   
        xpsCHKProperties = createAWTControl(chkUpdate, CHK_UPDATENAME, CHK_UPDATELABEL, 
                new Rectangle(10, 49, 150, 12));                                    
        xpsCHKProperties.setPropertyValue("TriState", Boolean.FALSE);
        xpsCHKProperties.setPropertyValue("State", new Short((short) 1));
        
        Object chkShareAlike = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlCheckBoxModel");   
        xpsCHKProperties = createAWTControl(chkShareAlike, CHK_SHAREALKENAME, CHK_SHAREALKELABEL, 
                new Rectangle(50, 66, 150, 12));                                    
        xpsCHKProperties.setPropertyValue("TriState", Boolean.FALSE);
        xpsCHKProperties.setPropertyValue("State", new Short((short) 0));
        
        Object searchButton = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlButtonModel");   
        XPropertySet xPSetFinishButton = createAWTControl(searchButton, BTN_SEARCH, searchButtonLabel,
                new Rectangle(140, 85, 40, 15));                    
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
        xFinishButton.setActionCommand(BTN_SEARCH);
        
        this.flickrLicenses = FlickrConnection.instance.getLicenses();
       
        Object oGBResults = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlGroupBoxModel");   
        createAWTControl(oGBResults, GB_RESULTS, "Results", new Rectangle(10, LOCATIONIMAGESY, 240, 315));                            
        
        Object oPBar = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlProgressBarModel");   
        XMultiPropertySet xPBModelMPSet = (XMultiPropertySet) UnoRuntime.queryInterface(XMultiPropertySet.class, oPBar);
      // Set the properties at the model - keep in mind to pass the property names in alphabetical order!
        xPBModelMPSet.setPropertyValues(
            new String[] {"Height", "Name", "PositionX", "PositionY", "Width"},
            new Object[] { new Integer(8), PB_NAME, new Integer(10), new Integer(418), new Integer(240)});
 
       // The controlmodel is not really available until inserted to the Dialog container
       getNameContainer().insertByName(PB_NAME, oPBar);
       XPropertySet xPBModelPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oPBar);

       xPBModelPSet.setPropertyValue("ProgressValueMin", new Integer(0));
       xPBModelPSet.setPropertyValue("ProgressValueMax", new Integer(100));
        
        // create a peer
        Object toolkit = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
        XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(XToolkit.class, toolkit);
        XWindow xWindow = (XWindow)UnoRuntime.queryInterface(XWindow.class, xControl);
        xWindow.setVisible(false);
        xControl.createPeer(xToolkit, null);
        xWindowPeer = xControl.getPeer();
        
        if (defaultSearch) {
            
            SavedSearchThread thread = new SavedSearchThread(this);
            thread.start();
        }
        
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
         if (selTags.trim().equalsIgnoreCase("")) {
             
             return new String[0];
         }
         
         return selTags.split(" ");
         
         }
         catch ( Exception ex ) {
             ex.printStackTrace();
         }
      
         return new String[0];
     }
     
     public void showResults(ArrayList<Image> imgList, int progressValue) {
     
         this.currentList = imgList;
         this.currentPositionInList = 0;
         
         showNextPage(progressValue);        
     }
     
     private void showNextPage(int progressValue)
     {
         if (currentList == null)
         {             
            return;
         }
         
         enableControl(BTN_PREVIOUS, false);
         
         double rateProgress = (double)(95 - progressValue) / currentList.size();
         double currentProgress = progressValue;
         int currentY = LOCATIONIMAGESY - POSITIONWIDTHHEIGHT - 5;         
         int currentX = 15 - POSITIONWIDTHHEIGHT - 10;
         
         for (int i = 0;i<SHOWRESULTSPERROW;i++) {       
             
             currentY += POSITIONWIDTHHEIGHT + 20;  
             currentX = 15 - POSITIONWIDTHHEIGHT - 10;
             
             for (int j = 0;j<SHOWRESULTSPERCOLUMN;j++) {       
                 
                currentX += POSITIONWIDTHHEIGHT + 10;
             
                if (currentList.size()>currentPositionInList)
                {
                    createImageControl(currentList.get(currentPositionInList), new Rectangle(currentX, 
                           currentY , POSITIONWIDTHHEIGHT, POSITIONWIDTHHEIGHT), String.valueOf(currentPositionInList));                
                
                                   
                 }
                 else
                     createImageControl(null, new Rectangle(currentX, currentY, POSITIONWIDTHHEIGHT, 
                         POSITIONWIDTHHEIGHT),  String.valueOf(currentPositionInList));                                            
             
                currentPositionInList++; 
                currentProgress+= rateProgress;
                this.setProgressValue((int)currentProgress );
                }
         }
     
         try
         {             
             Object button = null;
             boolean isNewCreated = false;
             if (getNameContainer().hasByName(BTN_NEXT))
             {
                 button = getNameContainer().getByName(BTN_NEXT);
             }
             else
             {             
                button = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel");                  
                isNewCreated = true;
             }
          
            createAWTControl(button, BTN_NEXT, BTN_NEXTLABEL, new Rectangle(150, 395, 40, 15)); 
            
            if (isNewCreated)
             {
                XButton xNextButton = (XButton)UnoRuntime.queryInterface(XButton.class, 
                        xControlCont.getControl(BTN_NEXT));
                if (xNextButton != null) {
                    xNextButton.addActionListener(new SearchClickListener(this, this.addin));
                    xNextButton.setActionCommand(BTN_NEXT);
                }
            }
            
             isNewCreated = false;
             if (getNameContainer().hasByName(BTN_PREVIOUS))
             {
                 button = getNameContainer().getByName(BTN_PREVIOUS);
             }
             else
             {             
                button = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel");                  
                isNewCreated = true;
             }
          
            createAWTControl(button, BTN_PREVIOUS, BTN_PREVIOUSLABEL, new Rectangle(50, 395, 40, 15)); 
            
            if (isNewCreated)
             {
                XButton xPrevButton = (XButton)UnoRuntime.queryInterface(XButton.class, 
                        xControlCont.getControl(BTN_PREVIOUS));
                if (xPrevButton != null){
                    xPrevButton.addActionListener(new SearchClickListener(this, this.addin));
                    xPrevButton.setActionCommand(BTN_PREVIOUS);
                }
            }
            
            if (currentPage <= 1) {
                enableControl(BTN_PREVIOUS, false);
            }
            else {
                enableControl(BTN_PREVIOUS, true);
            }

            if (currentList.size()<SHOWRESULTSPERROW*SHOWRESULTSPERCOLUMN) {
                
                enableControl(BTN_NEXT, false);
            }
            else {
                
                enableControl(BTN_NEXT, true);
            }
                
         } catch (Exception ex) {
            ex.printStackTrace();
         }
     }
     
     private void createImageControl(Image img, Rectangle rect, String pos) {
         
         if (!isLoadable) {
             
             return;
         }
         
         try
         {             
             Object oICModel = null;
             if (getNameContainer().hasByName("ImageControl" + pos))
             {
                 XControl xImageControl = xControlCont.getControl("ImageControl"+pos); 
                 if (xImageControl!= null) {
                    xImageControl.dispose();   
                 }
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
          
            String title = "";
            if (img!= null)
            {
                title = img.getTitle();
            }
            else
                title = "";

           xpsImageControl.setPropertyValue("HelpText", title);
            
           getNameContainer().insertByName("ImageControl"+pos, oICModel);    
            
           XControl xImageControl = xControlCont.getControl("ImageControl"+pos); 
           XWindow xWindow = (XWindow) UnoRuntime.queryInterface(XWindow.class, xImageControl);
           if (xWindow != null) {
                xWindow.addMouseListener(new ImageButtonListener(this, this.addin, img));            
           }
           
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
            userName = "From " +img.getUserName();
        }
        else
            userName= "";
            
        XPropertySet xpsProperties = createAWTControl(lblUser, "ImageLabelUser"+pos, userName, 
                new Rectangle(rect.x, rect.y + rect.height+3, 50, 20));                
        if (img!= null)
        {
            xpsProperties.setPropertyValue("URL", img.getProfile());
        }
        else
            xpsProperties.setPropertyValue("URL", "");   
        xpsProperties.setPropertyValue("Label", userName);
      //  Object preferedSize = xpsProperties.getPropertyValue("PreferredSize");
        
//        Object lblMainPageImage = null;
//        if (getNameContainer().hasByName("ImageLabelMainPage"+pos))
//        {
//            lblMainPageImage = getNameContainer().getByName("ImageLabelMainPage"+pos);                
//        }
//        else
//            lblMainPageImage = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedHyperlinkModel");
//          
//        String title = "";
//        if (img!= null)
//        {
//            title = "Title :" +img.getTitle();
//        }
//        else
//            title = "";
//        
//        xpsProperties = createAWTControl(lblMainPageImage, "ImageLabelMainPage"+pos, title, new Rectangle(rect.x+rect.height+3, rect.y+17, 150, 20));        
//        if (img!= null)
//        {
//            xpsProperties.setPropertyValue("URL", img.getImgUrlMainPage());
//        }
//        else
//            xpsProperties.setPropertyValue("URL", "");
//        xpsProperties.setPropertyValue("Label", title);            
//        
         } catch (Exception ex) {
            ex.printStackTrace();
        }
     }
     
     public void enableControl(String controlName, boolean enable) {
          try
         {
              if (getNameContainer().hasByName(controlName)) {
                Object oControl = getNameContainer().getByName(controlName);
                XPropertySet xModelPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oControl);

                xModelPSet.setPropertyValue("Enabled", enable);
              }
         } catch (Exception ex) {
            ex.printStackTrace();
         }   
     }
     
     public void setProgressValue(int step) {
         
         if (!getNameContainer().hasByName(PB_NAME)) {
             
             return;
         }
         
         try
         {
         Object oPBar = getNameContainer().getByName(PB_NAME);
         XPropertySet xPBModelPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oPBar);

         xPBModelPSet.setPropertyValue("ProgressValue", new Integer(step));
         } catch (Exception ex) {
            ex.printStackTrace();
         }
     }
     
     public XPopupMenu executePopupMenu(Image img, Integer positionX, Integer positionY){
        
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
  
  public String getLicenseURL(String licenseID) {
      
      for (Object p : this.flickrLicenses.toArray())
        {
            com.aetrion.flickr.photos.licenses.License currentLicense = ((com.aetrion.flickr.photos.licenses.License)p);   
            if ( (currentLicense !=  null)&& (currentLicense.getId().equalsIgnoreCase(licenseID))) {
                
                return currentLicense.getUrl();
            }
        }
      
      return "";
  }
  
  public String getLicenseNumber(String licenseURL) {
      
      String licenseNumber = "";
      if (!licenseURL.equalsIgnoreCase("")) {
            
            if (licenseURL.endsWith("/")) {
                licenseURL = licenseURL.substring(0, licenseURL.length()-1);
            }
            licenseNumber = licenseURL.substring(licenseURL.lastIndexOf("/")+1);
        }
      
      return licenseNumber;
  }
  
  /**
      * Canges the mouse pointer.
      *
      * @param xContext
      * @param xWindowPeer
      * @param nSystemPointer
      */
     public void setMousePointer( int nSystemPointer)
     {
         if ( xWindowPeer == null )
             return;
         
         try {
             Object oPointer =  xMultiComponentFactory.createInstanceWithContext(
                     "com.sun.star.awt.Pointer", m_xContext);
             if ( oPointer!=null ) {
                 XPointer xPointer = (XPointer) UnoRuntime.queryInterface(
                         XPointer.class, oPointer);
                 xPointer.setType( new Integer( nSystemPointer ) );
                 xWindowPeer.setPointer(xPointer);
             }
         } catch (java.lang.Exception ex) {
             ex.printStackTrace();
         }
     }
     
     public String getLicense() {
         
         boolean commercial = getCheckBoxStatus(CHK_COMMERCIALNAME);
         boolean update = getCheckBoxStatus(CHK_UPDATENAME);
         boolean shareAlike = getCheckBoxStatus(CHK_SHAREALKENAME);
         
         if (commercial && update && shareAlike) {
             return "4,5";
         }
         else
             if (commercial && update) {
                 return "1,2";
             }
             else
                 if (update && shareAlike) {
                     return "1,2,4,5";
                 }
                 else
                     if  (commercial)  {
                         return "4,5,6";
                     }
                     else
                         if (update) {
                             return "2,4";
                         }
         
         //default atribution license
         return "4";
     }
    
     public boolean getCheckBoxStatus(String ctrlName) {
         
         Object oLicense = xControlCont.getControl(ctrlName);
         XCheckBox checkBox = (XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, oLicense);
                
         Object value = checkBox.getState();
         if (value != null) {
             
             short chkStatus = new Short(value.toString());
             if (chkStatus == 1)  {
                 
                 return true;
             }
         }
         
         return false;
     }
     
     public XNameContainer getNameContainer() {
        return xNameCont;
    }   
     
     public Image getSelectedImage() {
         
         return selectedImage;
     }
     
     public void setSelectedImage(Image selImage) {
         
         this.selectedImage = selImage;
     }
     
     public int getCurrentPage() {
         
         return currentPage;
     }
     
     public void setCurrentPage(int _currentPage) {
         
         this.currentPage = _currentPage;
     } 
    
    public void close() {
        
        this.xDialog.endExecute();        
    }
    
    public boolean IsInputValid() {
        
        if (this.GetTags().length == 0) {
            return false;
        }
        
         boolean commercial = getCheckBoxStatus(CHK_COMMERCIALNAME);
         boolean update = getCheckBoxStatus(CHK_UPDATENAME);
         boolean shareAlike = getCheckBoxStatus(CHK_SHAREALKENAME);
         
         if (!commercial && !update && !shareAlike) {
             
             return false;
         }
         
         if (commercial && !update && shareAlike)  {
             
             return false;
         }
         
         if (!commercial && !update && shareAlike)  {
             
             return false;
         }
         
         return true;
    }
    
    public void saveSearch () {
        
         try {
         
            Object oTags = xControlCont.getControl(TXT_TAGS);
            XControl txtTags = (XControl)UnoRuntime.queryInterface(XControl.class, oTags);
            XControlModel xControlModel = txtTags.getModel();
            XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);
            String selTags = (String) xPSet.getPropertyValue("Text");         
            this.savedTags = selTags.trim();
            
            if (getCheckBoxStatus(CHK_COMMERCIALNAME)) 
                savedCommercialStatus = 1;
            else
                savedCommercialStatus = 0;
            
            if (getCheckBoxStatus(CHK_UPDATENAME)) 
                savedUpdateStatus = 1;
            else
                savedUpdateStatus = 0;
            
            if (getCheckBoxStatus(CHK_SHAREALKENAME)) 
                savedShareAlikeStatus = 1;
            else
                savedShareAlikeStatus = 0;
                        
         }
         catch ( Exception ex ) {
             ex.printStackTrace();
         }        
    }
    
    public void startSavedSearch() {
    
        setMousePointer(SystemPointer.WAIT);
        enableControl(PictureFlickrDialog.BTN_SEARCH, false);
        enableControl(PictureFlickrDialog.BTN_NEXT, false);
        currentPositionInList = 0;
        
        try
        {
            Object oTags = xControlCont.getControl(TXT_TAGS);
            XControl txtTags = (XControl)UnoRuntime.queryInterface(XControl.class, oTags);
            XControlModel xControlModel = txtTags.getModel();
            XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);
            xPSet.setPropertyValue("Text", this.savedTags); 
        
            Object oLicense = xControlCont.getControl(CHK_COMMERCIALNAME);
            XCheckBox checkBox = (XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, oLicense);
            checkBox.setState(savedCommercialStatus);
            
            oLicense = xControlCont.getControl(CHK_UPDATENAME);
            checkBox = (XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, oLicense);
            checkBox.setState(savedUpdateStatus);
            
            oLicense = xControlCont.getControl(CHK_SHAREALKENAME);
            checkBox = (XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, oLicense);
            checkBox.setState(savedShareAlikeStatus);
        }
        catch ( Exception ex ) {
             ex.printStackTrace();
        }
        
        showResults(currentList,0);
        setProgressValue(100);
        enableControl(PictureFlickrDialog.BTN_SEARCH, true);
       // enableControl(PictureFlickrDialog.BTN_NEXT, true);
        setMousePointer(SystemPointer.ARROW);
    }
    
   public void setLoadable(boolean val) {
   
       this.isLoadable = val;
   }
   
    
} // PictureFlickrDialog
