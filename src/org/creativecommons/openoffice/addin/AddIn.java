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
 *- Fix the license image bug
 *- Put the original size of the image (without restriction)
 *- Allow only one license attribution per document
 *- Generate a dialog when opening licensed documents
 *- Put CC logo on the interface
 *- Put "more info" option like ms office addin on the dialog
 *- Do a refactor
 *- CC option embeded in the ooo "File" menu instead?
 *- Insert creative commmons RDF in doc metadata?
 *- Exception handling
 *- Set the tab index of the dialog components
 *- Automaticaly choose between drop downs and combo-boxes based on the number of available options.
 *
 */

package org.creativecommons.openoffice.addin;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XComboBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XAutoTextContainer;
import com.sun.star.text.XAutoTextEntry;
import com.sun.star.text.XAutoTextGroup;
import com.sun.star.text.XSimpleText;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.Type;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.creativecommons.api.CcRest;
import org.creativecommons.api.LicenseClass;
import org.creativecommons.api.LicenseField;

/**
 *  The Creative Commons OpenOffice.org AddIn core class.
 * 
 * 
 * 
 * 
 * @author Cassio A. Melo
 * @author Creative Commons
 * @version 0.0.1
 */
public final class AddIn extends WeakBase
        implements com.sun.star.lang.XServiceInfo,
        com.sun.star.frame.XDispatchProvider,
        com.sun.star.lang.XInitialization,
        com.sun.star.frame.XDispatch {
    protected final XComponentContext m_xContext;
    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = AddIn.class.getName();
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


    
    public static CcRest ccr = new CcRest();
    
    /**
     * Constructs a new instance
     *
     * @param context the XComponentContext
     */
    public AddIn( XComponentContext context ) {
        m_xContext = context;
        //namesList = new Vector();
        
        
    };
    
    // Generated method stubs
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;
        
        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(AddIn.class, m_serviceNames);
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
            int iSearchFlags ) {
        if ( aURL.Protocol.compareTo("org.openoffice.addon:") == 0 ) {
            if ( aURL.Path.compareTo("TestMenu") == 0 )
                return this;
        }
        return null;
    }
    
    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
            com.sun.star.frame.DispatchDescriptor[] seqDescriptors ) {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher =
                new com.sun.star.frame.XDispatch[seqDescriptors.length];
        
        for( int i=0; i < nCount; ++i ) {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                    seqDescriptors[i].FrameName,
                    seqDescriptors[i].SearchFlags );
        }
        return seqDispatcher;
    }
    
    // com.sun.star.lang.XInitialization:
    public void initialize( Object[] object )
    throws com.sun.star.uno.Exception {
        if ( object.length > 0 ) {
            m_xFrame = (com.sun.star.frame.XFrame)UnoRuntime.queryInterface(
                    com.sun.star.frame.XFrame.class, object[0]);
        }
    }
    
    public void addStatusListener( com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL ) {
        // add your own code here
    }
    
    public void removeStatusListener( com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL ) {
        // add your own code here
    }
    
    // End of generated method stubs
    
    // com.sun.star.frame.XDispatch:
    public void dispatch( com.sun.star.util.URL aURL,
            com.sun.star.beans.PropertyValue[] aArguments )
            
    {
        if ( aURL.Protocol.compareTo("org.openoffice.addon:") == 0 ) {
            if ( aURL.Path.compareTo("TestMenu") == 0 ) {
                
                try {
                    mxRemoteServiceManager = this.getRemoteServiceManager();
                    
                    
                    if (mxRemoteServiceManager == null) {
                        System.out.println("not available");
                        return;
                    }
                    
                    Object desktop = mxRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop", mxComponentContext);
                    XDesktop xDesktop = (XDesktop)UnoRuntime.queryInterface(XDesktop.class, desktop);
                    this.xCurrentComponent = xDesktop.getCurrentComponent();
                    
                    // query its XTextDocument interface to get the text
                    mxDoc = (XTextDocument)UnoRuntime.queryInterface(
                            XTextDocument.class, this.xCurrentComponent);
                    
                    // get a reference to the body text of the document
                    mxDocText = mxDoc.getText();
                    
                    mxFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                            XMultiServiceFactory.class, mxRemoteServiceManager);
                    
                    mxDocFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                            XMultiServiceFactory.class, mxDoc);
                    
                    mxDocCursor = mxDocText.createTextCursor();
                    
                    // Create the dialog
                   // this.createDialog();
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
            
            m_xDocumentInfo.setUserFieldName((short)0, "License Name");
            m_xDocumentInfo.setUserFieldValue((short)0, licenseName);
            
            m_xDocumentInfo.setUserFieldName((short)1, "License URL");
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
     * Creates and inserts an auto-text containing the license
     *
     * @param licenseName The License Name.
     * @param licenseURL The License URL.
     *      @param licenseImgURL The license "button" URL.
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
            
            // Insert a string at the beginning of the autotext block
            
            xSimpleText.insertString(xText.getStart(), "This work is licensed under a "+licenseName+" license.\n"+licenseURL, false);
            
            // Access the autotext group with this name
            XAutoTextGroup xGroup = (XAutoTextGroup)
            UnoRuntime.queryInterface(XAutoTextGroup.class,
                    xContainer.getByName("Creative Commons"));
            
            
            XAutoTextEntry xEntry = ( XAutoTextEntry )
            UnoRuntime.queryInterface(XAutoTextEntry.class, xGroup.getByName("CC"));
            
            
            // Insert the autotext at the cursor
            xEntry.applyTo(mxDocCursor);
            
            
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
            
             this.embedGraphic(this.mxDocFactory,mxDocCursor,licenseImgURL);
            
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
        
        // unoURL  = "http://i.creativecommons.org/l/by-nc-sa/2.1/es/80x15.png";
        // http://i.creativecommons.org/l/LGPL/2.1/88x62.png
        // http://i.creativecommons.org/l/publicdomain/88x31.png
        
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
            
            xBitmapContainer.insertByName("imgID", imgURL); // TODO fix this weird bug
            internalURL = AnyConverter.toString(xBitmapContainer 
                    .getByName("imgID"));
            
            
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
