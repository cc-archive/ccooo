/*
 * AddIn.java
 *
 * copyright 2007, Creative Commons
 * licensed under the MIT License; see docs/LICENSE for details.
 *
 * Created on Jun 20, 2007
 *
 */


/**
 *
 *TODO:
 *- Put the original size of the image (without restriction)
 *- Put CC logo on the interface
 *- Put "more info" option like ms office addin on the dialog
 *- CC option embeded in the ooo "File" menu instead?
 *- Insert creative commmons RDF in doc metadata?
 *- Exception handling (including timeout)
 *- Set the tab index of the dialog components
 *- Automaticaly choose between drop downs and combo-boxes based on the number of available options.
 *- Progress bar where necessary
 *- Internationalization
 *- put the addin version in the metadata
 */

package org.creativecommons.openoffice;

import com.sun.star.awt.MessageBoxButtons;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XMessageBox;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.document.XEventBroadcaster;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.text.XAutoTextContainer;
import com.sun.star.text.XAutoTextEntry;
import com.sun.star.text.XAutoTextGroup;
import com.sun.star.text.XSimpleText;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.AnyConverter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.creativecommons.api.CcRest;

import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XWindowPeer;
import java.io.File;

/**
 *  The Creative Commons OpenOffice.org AddIn core class.
 * 
 * @author Cassio A. Melo
 * @author Creative Commons
 * @version 0.0.1
 */
public final class ccooo extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.frame.XDispatchProvider,
              com.sun.star.lang.XInitialization,
              com.sun.star.frame.XDispatch
{
    private final XComponentContext m_xContext;
    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = ccooo.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.frame.ProtocolHandler" };

    private XComponentContext mxComponentContext = null;
    private XTextDocument mxDoc = null;
    private XMultiServiceFactory mxDocFactory = null;
    private XMultiServiceFactory mxFactory = null;
    private XText mxDocText = null;
    private XTextCursor mxDocCursor = null;

    private XComponent xCurrentComponent = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    protected XMultiComponentFactory mxRemoteServiceManager = null;
    
    
    Properties labels = new Properties();
    

   public static CcRest ccr = new CcRest();
   
      
    /**
     * Constructs a new instance
     *
     * @param context the XComponentContext
     */
    public ccooo( XComponentContext context )
    {

        m_xContext = context;
        try {
            mxRemoteServiceManager = this.getRemoteServiceManager();
            // get the service manager from the component context
        this.xMultiComponentFactory = this.m_xContext.getServiceManager();
        
           
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    };

    // Generated method stubs
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(ccooo.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
         return m_implementationName;
    }

    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch queryDispatch( com.sun.star.util.URL aURL,
                                                       String sTargetFrameName,
                                                       int iSearchFlags )
    {
        if ( aURL.Protocol.compareTo("org.creativecommons.openoffice.ccooo:") == 0 )
        {
            if ( aURL.Path.compareTo("Command0") == 0 )
                return this;
        }
        return null;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
         com.sun.star.frame.DispatchDescriptor[] seqDescriptors )
    {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher =
            new com.sun.star.frame.XDispatch[seqDescriptors.length];

        for( int i=0; i < nCount; ++i )
        {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                                             seqDescriptors[i].FrameName,
                                             seqDescriptors[i].SearchFlags );
        }
        return seqDispatcher;
    }

    // com.sun.star.lang.XInitialization:
    public void initialize( Object[] object )
        throws com.sun.star.uno.Exception
    {
        if ( object.length > 0 )
        {
            /*try {*/
               
          
              /* String file = Ressources.getFile(AddInConstants.LANGUAGE_FILE_NAME);
               System.out.println("file:" +file);
                labels.load(new FileInputStream(new File(file)));*/
                
                
            
            m_xFrame = (com.sun.star.frame.XFrame)UnoRuntime.queryInterface(
                com.sun.star.frame.XFrame.class, object[0]);
             this.AddOnLoadDocumentListener();
             
            /* } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/
        }
    }
    public void addStatusListener( com.sun.star.frame.XStatusListener xControl,
                                    com.sun.star.util.URL aURL )
    {
        // add your own code here
    }

    public void removeStatusListener( com.sun.star.frame.XStatusListener xControl,
                                       com.sun.star.util.URL aURL )
    {
        // add your own code here
    }
    
    // End of generated method stubs
    // com.sun.star.frame.XDispatch:
     public void dispatch( com.sun.star.util.URL aURL,
                           com.sun.star.beans.PropertyValue[] aArguments )
    {
         if ( aURL.Protocol.compareTo("org.creativecommons.openoffice.ccooo:") == 0 )
        {
            if ( aURL.Path.compareTo("Command0") == 0 )
            {
                
                 try {
                     Map prop = this.retrieveLicenseMetadata();

                     if (!prop.isEmpty()) { // Document is already licensed
                         
                         /* // is not working yet..
                          short answer =  this.createQueryBox(labels.getProperty("document.licensed.querybox.title"),
                                 labels.getProperty("document.licensed.querybox.text"));*/
                         
                          short answer =  this.createQueryBox("Warning",
                                  "This document is already licensed. It's not recommended putting different licenses in the same document. \n\nWould you like do proceed anyway? (Only the last license chosen will be valid)");
                         
             
                         if (answer != 1) {// clicked on Cancel
                            return;
                         }
                     
                     }

                    if (mxRemoteServiceManager == null) {
                        System.out.println("not available");
                        return;
                    }
                   // this.xCurrentComponent = this.updateCurrentComponent();
                   
                    // query its XTextDocument interface to get the text
                    mxDoc = (XTextDocument)UnoRuntime.queryInterface(
                            XTextDocument.class, this.xCurrentComponent);
                    
                    mxFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                            XMultiServiceFactory.class, mxRemoteServiceManager);
                    
                    mxDocFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                            XMultiServiceFactory.class, mxDoc);
                    
                    
                     // get a reference to the body text of the document
                    mxDocText = mxDoc.getText();
                 
                    mxDocCursor = mxDocText.createTextCursor();
 
                    // Create the dialog
                   AddInUI dialog = new AddInUI(this, this.m_xContext);
                    dialog.createDialog();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                
                return;
            }
        }
    }

     /**
     * Creates an infobox with the title and text given
     *
     * @param title The title of the dialog.
     * @param msg The text of the dialog.
     *
     */
     private void createInfoBox(String title, String msg){
         XMessageBoxFactory factory;
         try {
             factory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class, this.xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext));
             
             Rectangle ret = new Rectangle();
             WindowDescriptor wd;
             
             XWindowPeer parent = (XWindowPeer)UnoRuntime.queryInterface(
                     XWindowPeer.class, m_xFrame.getContainerWindow());
             //This document is already licensed.\n\nWould you like do proceed anyway?"
             XMessageBox box = factory.createMessageBox(parent,ret,"infobox",MessageBoxButtons.BUTTONS_OK,title,msg);
             
             box.execute();
         } catch (com.sun.star.uno.Exception ex) {
             ex.printStackTrace();
         }
     
     }
     
     /**
     * Creates a querybox with the title and text given
     *
     * @param title The title of the dialog.
     * @param msg The text of the dialog.
     *
     * @return short Returns the answer code of the querybox (1 - OK , 0 - Cancel)
     *
     */
      private short createQueryBox(String title, String msg){
         XMessageBoxFactory factory;
         try {
             factory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class, this.xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext));
             
             Rectangle ret = new Rectangle();
             WindowDescriptor wd;
             
             XWindowPeer parent = (XWindowPeer)UnoRuntime.queryInterface(
                     XWindowPeer.class, m_xFrame.getContainerWindow());
             // TODO put listeners to the OK and Cancel buttons!
             XMessageBox box = factory.createMessageBox(parent,ret,"querybox",MessageBoxButtons.BUTTONS_OK_CANCEL,title,msg);
             
             return box.execute();

         } catch (com.sun.star.uno.Exception ex) {
             ex.printStackTrace();
         }
         
         return -1; // Fail
     
     }
     
    
     /**
     *  Get the remote office context
     */
    private XMultiComponentFactory getRemoteServiceManager()
    throws java.lang.Exception {
        if (xMultiComponentFactory == null && mxRemoteServiceManager == null) {
            
            mxRemoteServiceManager = m_xContext.getServiceManager();
        }
        return mxRemoteServiceManager;
    }
     
     /**
     * Updates the Desktop current component in case of opening, creating or swapping
     * to other document
     *
     * @return XComponent Returns the current component of Desktop object
     *
     */
     public XComponent updateCurrentComponent (){
        
         XComponent ret = null;
         Object desktop;
         try {
             desktop = mxRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop", mxComponentContext);         
             XDesktop xDesktop = (XDesktop)UnoRuntime.queryInterface(XDesktop.class, desktop);
             ret = xDesktop.getCurrentComponent();
             
         } catch (com.sun.star.uno.Exception ex) {
             ex.printStackTrace();
         }
         return ret;

     }
     
     
     /**
     * Inserts the license name and the license URL into the document's metadata
     *
     * @param licenseName The License Name.
     * @param licenseURL The License URL.
     *
     */
    public void insertLicenseMetadata(String licenseName, String licenseURL){
        // TODO Store metadata as in MSOffice addin?
        try {
            XDocumentInfo m_xDocumentInfo;
            
            XDocumentInfoSupplier xDocumentInfoSupplier =
                    (XDocumentInfoSupplier)UnoRuntime.queryInterface(
                    XDocumentInfoSupplier.class, this.xCurrentComponent);
            
            m_xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
            
            m_xDocumentInfo.setUserFieldName((short)0, AddInConstants.CC_METADATA_IDENTIFIER+"License Name");
            m_xDocumentInfo.setUserFieldValue((short)0, licenseName);
            
            m_xDocumentInfo.setUserFieldName((short)1, AddInConstants.CC_METADATA_IDENTIFIER+"License URL");
            m_xDocumentInfo.setUserFieldValue((short)1, licenseURL);
            
            XStorable xStorable = (XStorable)UnoRuntime.queryInterface(
                    XStorable.class, xCurrentComponent);
            xStorable.store();
            
        } catch (Exception ex) {
            // just swallow..
            //ex.printStackTrace();
        }
        
    }
     
     /**
     * Retrieve the license properties from the document's metadata
     *
     * @return Map Returns a map containing the license properties
     *
     */
     public Map retrieveLicenseMetadata (){
         Map licenseProp = new HashMap();
         
         XDocumentInfo m_xDocumentInfo;
         
         xCurrentComponent = updateCurrentComponent();
         
         XDocumentInfoSupplier xDocumentInfoSupplier =
                 (XDocumentInfoSupplier)UnoRuntime.queryInterface(
                 XDocumentInfoSupplier.class, xCurrentComponent);
         
         m_xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
         
         try {
             
             short fieldsnum = m_xDocumentInfo.getUserFieldCount();
             
             // if XDocumentInfoSupplier had a hasFieldName(String fieldName) we wouldn't have done this...
             
             // TODO   poderia ter um atributo imutavel indicando se é licenciado ou nao,
             // assim buscariamos primeiro por esse atributo antes de varrer os fields
             for (short i = 0 ; i < fieldsnum; i++) {
                 String temp  = m_xDocumentInfo.getUserFieldName(i);
                     
                     if (temp.startsWith(AddInConstants.CC_METADATA_IDENTIFIER)) {
                        licenseProp.put(temp.substring(AddInConstants.CC_METADATA_IDENTIFIER.length()),m_xDocumentInfo.getUserFieldValue(i));
                     }
             }

             
         } catch (com.sun.star.lang.ArrayIndexOutOfBoundsException ex) {
             ex.printStackTrace();
         }
         return licenseProp;
     }
     
     /**
     * Add a listener to the openoffice to be triggered when OnLoad events occur
     *
     */
     private void AddOnLoadDocumentListener(){
        Object xGlobalBroadCaster;
        
        try {
            xGlobalBroadCaster = mxRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.GlobalEventBroadcaster", m_xContext);
            
            XEventBroadcaster xEventBroad = (XEventBroadcaster)UnoRuntime.queryInterface(XEventBroadcaster.class, xGlobalBroadCaster);
            
            xEventBroad.addEventListener(new com.sun.star.document.XEventListener() {
                public void notifyEvent(com.sun.star.document.EventObject oEvent) {
                    
                    // Is there any other way more efficient, without having to check this?
                    // for all events in ooo this check happen...
                    // would be nice if it had an OnLoadListener interface, wouldn't it?
                    if (oEvent.EventName.equalsIgnoreCase("OnLoad")) {

                        Map licenseProps = retrieveLicenseMetadata();
                        if (!licenseProps.isEmpty()) {
                            String body ="This work is licensed under a Creative Commons License. \n\n";
                            Set list = licenseProps.entrySet();
                            Iterator it = list.iterator();
                         
                            while (it.hasNext()) {
                               Entry temp = (Entry) it.next();
                               body += temp.getKey()+": "+temp.getValue()+"\n";
                               //System.out.println(temp.getKey() + " -> "+temp.getValue());
                            }
                            
                            createInfoBox("Creative Commons Licensed Document",body);
                        }
                        
                    }
                }
                public void disposing(com.sun.star.lang.EventObject e) {
                    System.out.println("On Dispose");
                }
            });
         
         } catch (Exception ex) {
            ex.printStackTrace();
        }
     
     }


    
    /**
     * Creates and inserts an auto-text containing the license
     *
     * @param licenseName The License Name.
     * @param licenseURL The License URL.
     * @param licenseImgURL The license "button" URL.
     *
     */
    public void createAutoText(String licenseName, String licenseURL, String licenseImgURL){
        
        try {
          
            // Create a new Auto-Text
            
            // Get an XNameAccess interface to all auto text groups from the
            // document factory
            XNameAccess xContainer = (XNameAccess) UnoRuntime.queryInterface(
                    XNameAccess.class, mxFactory.createInstance(
                    "com.sun.star.text.AutoTextContainer" ) );
            
            
            XAutoTextContainer container = (XAutoTextContainer) UnoRuntime.queryInterface(XAutoTextContainer.class, xContainer);
            
            if (xContainer.hasByName("Creative Commons")) {
                
                container.removeByName("Creative Commons");
            }
            
            
            XAutoTextGroup newgroup = container.insertNewByName("Creative Commons");
            XAutoTextEntry newentry = newgroup.insertNewByName("CC", "CCommons", mxDocCursor);
            
            
            // Get the XSimpleText and XText interfaces of the new autotext block
            
            XSimpleText xSimpleText = (XSimpleText) UnoRuntime.queryInterface(XSimpleText.class, newentry);
            XText xText = (XText) UnoRuntime.queryInterface(XText.class, newentry);
            
            
            // Insert the license image in the autotext
            this.embedGraphic(this.mxDocFactory,xSimpleText.createTextCursor(),licenseImgURL);

            // Insert a string at the beginning of the autotext block
            xSimpleText.insertString(xText.getEnd(), "\nThis work is licensed under a "+licenseName+" license.\n"+licenseURL+"\n", false);
            
            // Access the autotext group with this name
            XAutoTextGroup xGroup = (XAutoTextGroup)
            UnoRuntime.queryInterface(XAutoTextGroup.class,
                    xContainer.getByName("Creative Commons"));
            
            
            XAutoTextEntry xEntry = ( XAutoTextEntry )
            UnoRuntime.queryInterface(XAutoTextEntry.class, xGroup.getByName("CC"));

            
            // get the XModel interface from the component
            
            XModel xModel = (XModel)UnoRuntime.queryInterface(XModel.class, xCurrentComponent);
            
            // the model knows its controller
            XController xController = xModel.getCurrentController();
            
            // the controller gives us the TextViewCursor
            // query the viewcursor supplier interface
            XTextViewCursorSupplier xViewCursorSupplier =
                    (XTextViewCursorSupplier)UnoRuntime.queryInterface(XTextViewCursorSupplier.class, xController);
            
            // get the cursor
            XTextViewCursor xViewCursor = xViewCursorSupplier.getViewCursor();
            
            
            // Insert the autotext at the cursor
            xEntry.applyTo(xViewCursor);
            
            
            
            
            // To insert the auto-text at the header/footer of the document, uncomment the code below
            /*
            XText oObj = null;
            XPropertySet PropSet;
            XNameAccess PageStyles = null;
            XStyle StdStyle = null;
             
            XStyleFamiliesSupplier StyleFam = (XStyleFamiliesSupplier)
            UnoRuntime.queryInterface(XStyleFamiliesSupplier.class, mxDoc);
            XNameAccess StyleFamNames = StyleFam.getStyleFamilies();
             
            // obtains style 'Standard' from style family 'PageStyles'
             
            PageStyles = (XNameAccess) AnyConverter.toObject(
                    new Type(XNameAccess.class),StyleFamNames.getByName("PageStyles"));
            StdStyle = (XStyle) AnyConverter.toObject(
                    new Type(XStyle.class),PageStyles.getByName("Standard"));
             
            PropSet = (XPropertySet)
            UnoRuntime.queryInterface( XPropertySet.class, StdStyle);
             
            // Choose between header and footer (or both) here
            // PropSet.setPropertyValue("HeaderIsOn", new Boolean(true));
            PropSet.setPropertyValue("FooterIsOn", new Boolean(true));
             
            oObj = (XText) UnoRuntime.queryInterface(
                    XText.class, PropSet.getPropertyValue("FooterText"));
                 // ..or "HeaderText"
             
            xEntry.applyTo(oObj);
             */
            
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    
    /**
     * Embeds the license "button" into a Textdocument at the given cursor position
     *
     * @param xMSF    the factory to create services from
     * @param xCursor the cursor where to insert the graphic
     * @param imgURL  URL of the license button
     *
     */
    public void embedGraphic( XMultiServiceFactory xMSF, XTextCursor xCursor, String imgURL) {
        
        
        XNameContainer xBitmapContainer = null;
        XText xText = xCursor.getText();
        XTextContent xImage = null;
        String internalURL = null;
        
        try {
            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, xMSF.createInstance(
                    "com.sun.star.drawing.BitmapTable"));
            xImage = (XTextContent) UnoRuntime.queryInterface(
                    XTextContent.class,     xMSF.createInstance(
                    "com.sun.star.text.TextGraphicObject"));
            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xImage);
            
            // helper-stuff to let OOo create an internal name of the graphic
            // that can be used later (internal name consists of various checksums)
            
            xBitmapContainer.insertByName("imgID", imgURL); 

            Object obj = xBitmapContainer.getByName("imgID");
            internalURL = AnyConverter.toString(obj);
            
            
            xProps.setPropertyValue("AnchorType",
                    com.sun.star.text.TextContentAnchorType.AS_CHARACTER);
            xProps.setPropertyValue("GraphicURL", internalURL);
            xProps.setPropertyValue("Width", (int) 4000); // original: 88 px
            xProps.setPropertyValue("Height", (int) 1550); // original: 31 px
            
            // inser the graphic at the cursor position
            xText.insertTextContent(xCursor, xImage, false);
            
            // remove the helper-entry
            xBitmapContainer.removeByName("imgID");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
