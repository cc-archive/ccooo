/*
 * AddIn.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
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
import com.sun.star.document.XEventBroadcaster;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;

import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.lang.XServiceInfo;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.program.Calc;
import org.creativecommons.openoffice.program.IVisibleNotice;
import org.creativecommons.openoffice.program.Impress;
import org.creativecommons.openoffice.program.Writer;
import org.creativecommons.openoffice.ui.ChooserDialog;

/**
 *  The Creative Commons OpenOffice.org AddIn core class.
 *
 * @author Cassio A. Melo
 * @author Creative Commons
 * @version 0.3.0
 */
public final class CcOOoAddin extends WeakBase
        implements com.sun.star.lang.XServiceInfo,
        com.sun.star.frame.XDispatchProvider,
        com.sun.star.lang.XInitialization,
        com.sun.star.frame.XDispatch {
    private final XComponentContext m_xContext;
    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = CcOOoAddin.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.frame.ProtocolHandler" };
    
    private XComponentContext mxComponentContext = null;
    private XTextDocument mxTextDoc = null;
    
    private XMultiServiceFactory mxDocFactory = null;
    private XMultiServiceFactory mxFactory = null;
    private XText mxDocText = null;
    private XTextCursor mxDocCursor = null;
    
    private XComponent xCurrentComponent = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    protected XMultiComponentFactory mxRemoteServiceManager = null;
    
    
    /**
     * Constructs a new instance
     *
     * @param context the XComponentContext
     */
    public CcOOoAddin( XComponentContext context ) {
        
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
            xFactory = Factory.createComponentFactory(CcOOoAddin.class, m_serviceNames);
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
    
    public XComponent getCurrentComponent(){
        return this.xCurrentComponent;
    }
    
    protected IVisibleNotice getProgramWrapper(XComponent component) {
        
        XServiceInfo xServiceInfo = (XServiceInfo)UnoRuntime.queryInterface(
                XServiceInfo.class, this.getCurrentComponent());
        
        if (xServiceInfo.supportsService("com.sun.star.sheet.SpreadsheetDocument")) {
            
            return new Calc(this.getCurrentComponent());
            
        } else if (xServiceInfo.supportsService("com.sun.star.text.TextDocument")) {
            
            return new Writer(this.getCurrentComponent());
            
        } else if (xServiceInfo.supportsService("com.sun.star.presentation.PresentationDocument")) {
         
            return new Impress(this.getCurrentComponent());
            
        }
        
        else if (xServiceInfo.supportsService("com.sun.star.drawing.DrawingDocument")) {
            
            return null;
        }
        
        return null;
    }
    
    public IVisibleNotice getProgramWrapper() {
        return this.getProgramWrapper(this.getCurrentComponent());
    }
    
    public XMultiServiceFactory getMSFactory() {
        return (XMultiServiceFactory)UnoRuntime.queryInterface(
                XMultiServiceFactory.class, mxRemoteServiceManager);
        //return this.mxFactory;
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
        if ( aURL.Protocol.compareTo("org.creativecommons.openoffice.ccooo:") == 0 ) {
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
            this.AddOnLoadDocumentListener();
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
            com.sun.star.beans.PropertyValue[] aArguments ) {
        if ( aURL.Protocol.compareTo("org.creativecommons.openoffice.ccooo:") == 0 ) {
            
            this.updateCurrentComponent();
            
            if ( aURL.Path.compareTo("SelectLicense") == 0 ) {
                selectLicense();
            } // if select license
            else if ( aURL.Path.compareTo("InsertStatement") == 0 ) {
                insertStatement();
            } // if insert statement
        } // if CcOOoAddin protocol
    } // dispatch
    
    public void selectLicense() {
        
        try {
            
            if (mxRemoteServiceManager == null) {
                System.out.println("not available");
                return;
            }
            
            this.updateCurrentComponent();
            
            // Create the dialog for license selection
            ChooserDialog dialog = new ChooserDialog(this, this.m_xContext);
            dialog.showDialog();
            
            if (!dialog.isCancelled()) {
                // retrieve the selected License
                License selected = dialog.getSelectedLicense();
                IVisibleNotice document = this.getProgramWrapper();

                // store the license information in the document
                document.setDocumentLicense(selected);
                
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    } // selectLicense
    
    public void insertStatement() {
        
        if (this.getProgramWrapper().getDocumentLicense() == null) {
            this.selectLicense();
        }
        
        this.getProgramWrapper(this.getCurrentComponent()).insertVisibleNotice();
        
    } // insertVisibleNotice
    
    
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
    public void updateCurrentComponent(){
        
        
        XComponent ret = null;
        Object desktop;
        try {
            desktop = mxRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop", mxComponentContext);
            XDesktop xDesktop = (XDesktop)UnoRuntime.queryInterface(XDesktop.class, desktop);
            ret = xDesktop.getCurrentComponent();
            
            this.xMultiComponentFactory = this.m_xContext.getServiceManager();
            this.mxFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, this.xCurrentComponent);
            
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }
        this.xCurrentComponent = ret;
        
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
                        /*
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
                            
                            // createInfoBox("Creative Commons Licensed Document",body);
                        }
                        */
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
    
}
