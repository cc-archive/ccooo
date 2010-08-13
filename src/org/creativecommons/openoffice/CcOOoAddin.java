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
 *- Put "more info" option like ms office addin on the dialog
 *- CC option embeded in the ooo "File" menu instead?
 *- Exception handling (including timeout)
 *- Set the tab index of the dialog components
 *- Internationalization
 *- put the addin version in the metadata
 */
package org.creativecommons.openoffice;

import com.sun.star.awt.MessageBoxButtons;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XMessageBox;
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
import com.sun.star.beans.NamedValue;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XController;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.uno.AnyConverter;
import java.util.Locale;
import org.creativecommons.license.License;
import org.creativecommons.license.StoreThread;
import org.creativecommons.openoffice.program.Calc;
import org.creativecommons.openoffice.program.Draw;
import org.creativecommons.openoffice.program.IVisibleNotice;
import org.creativecommons.openoffice.program.Writer;
import org.creativecommons.openoffice.ui.license.ChooserDialog;
import org.creativecommons.openoffice.ui.flickr.FlickrDialog;
import org.creativecommons.openoffice.ui.openclipart.OpenClipArtDialog;
import org.creativecommons.openoffice.ui.picasa.PicasaDialog;
import org.creativecommons.openoffice.ui.wikimedia.WikimediaDialog;
import static org.creativecommons.openoffice.util.Util.setLocale;

/**
 *  The Creative Commons OpenOffice.org AddIn core class.
 *
 * @author Cassio A. Melo
 * @author Akila Wajirasena
 * @author Creative Commons
 * @version 0.7.0
 */
public final class CcOOoAddin extends WeakBase
        implements com.sun.star.lang.XServiceInfo,
        com.sun.star.frame.XDispatchProvider,
        com.sun.star.lang.XInitialization,
        com.sun.star.frame.XDispatch,
        com.sun.star.task.XJob {

    private final XComponentContext m_xContext;
    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = CcOOoAddin.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.frame.ProtocolHandler"};
    private XComponentContext mxComponentContext = null;
    private XTextDocument mxTextDoc = null;
    private XMultiServiceFactory mxDocFactory = null;
    private XMultiServiceFactory mxFactory = null;
    private XText mxDocText = null;
    private XTextCursor mxDocCursor = null;
    private XComponent xCurrentComponent = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    protected XMultiComponentFactory mxRemoteServiceManager = null;
    private FlickrDialog pictureFlickrDialog = null;
    private OpenClipArtDialog openClipArtDialog = null;
    private WikimediaDialog wikimediaDialog = null;
    private PicasaDialog picasaDialog = null;

    /**
     * Constructs a new instance
     *
     * @param context the XComponentContext
     */
    public CcOOoAddin(XComponentContext context) {

        m_xContext = context;
        try {
            mxRemoteServiceManager = this.getRemoteServiceManager();
            // get the service manager from the component context
            this.xMultiComponentFactory = this.m_xContext.getServiceManager();

            //set the locale for UI

            Object oProvider =
                    xMultiComponentFactory.createInstanceWithContext("com.sun.star.configuration.ConfigurationProvider", m_xContext);
            XMultiServiceFactory xConfigurationServiceFactory =
                    (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, oProvider);

            PropertyValue[] lArgs = new PropertyValue[1];
            lArgs[0] = new PropertyValue();
            lArgs[0].Name = "nodepath";
            lArgs[0].Value = "/org.openoffice.Setup/L10N";

            Object configAccess = xConfigurationServiceFactory.createInstanceWithArguments(
                    "com.sun.star.configuration.ConfigurationAccess", lArgs);

            XNameAccess xNameAccess = (XNameAccess) UnoRuntime.queryInterface(XNameAccess.class, configAccess);
            setLocale(new Locale(xNameAccess.getByName("ooLocale").toString()));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Generated method stubs
    public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
        XSingleComponentFactory xFactory = null;

        if (sImplementationName.equals(m_implementationName)) {
            xFactory = Factory.createComponentFactory(CcOOoAddin.class, m_serviceNames);
        }
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                m_serviceNames,
                xRegistryKey);
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
        return m_implementationName;
    }

    public XComponent getCurrentComponent() {
        return this.xCurrentComponent;
    }

    protected IVisibleNotice getProgramWrapper(XComponent component) {

        XServiceInfo xServiceInfo = (XServiceInfo) UnoRuntime.queryInterface(
                XServiceInfo.class, this.getCurrentComponent());

        if (xServiceInfo.supportsService("com.sun.star.sheet.SpreadsheetDocument")) {
            return new Calc(this.getCurrentComponent(), m_xContext);

        } else if (xServiceInfo.supportsService("com.sun.star.text.TextDocument")) {
            return new Writer(this.getCurrentComponent(), m_xContext);

        } else if (xServiceInfo.supportsService("com.sun.star.presentation.PresentationDocument")) {
            return new Draw(this.getCurrentComponent(), m_xContext);

        } else if (xServiceInfo.supportsService("com.sun.star.drawing.DrawingDocument")) {
            return new Draw(this.getCurrentComponent(), m_xContext);
        }

        return null;
    }

    public IVisibleNotice getProgramWrapper() {
        return this.getProgramWrapper(this.getCurrentComponent());
    }

    public XMultiServiceFactory getMSFactory() {
        return (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, mxRemoteServiceManager);
        //return this.mxFactory;
    }

    public boolean supportsService(String sService) {
        int len = m_serviceNames.length;

        for (int i = 0; i < len; i++) {
            if (sService.equals(m_serviceNames[i])) {
                return true;
            }
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch queryDispatch(com.sun.star.util.URL aURL,
            String sTargetFrameName,
            int iSearchFlags) {
        if (aURL.Protocol.compareTo("org.creativecommons.openoffice.ccooo:") == 0) {
            return this;
        }
        return null;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
            com.sun.star.frame.DispatchDescriptor[] seqDescriptors) {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher =
                new com.sun.star.frame.XDispatch[seqDescriptors.length];

        for (int i = 0; i < nCount; ++i) {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                    seqDescriptors[i].FrameName,
                    seqDescriptors[i].SearchFlags);
        }
        return seqDispatcher;
    }

    // com.sun.star.lang.XInitialization:
    public void initialize(Object[] object)
            throws com.sun.star.uno.Exception {
        if (object.length > 0) {
            m_xFrame = (com.sun.star.frame.XFrame) UnoRuntime.queryInterface(
                    com.sun.star.frame.XFrame.class, object[0]);
        }
    }

    public void addStatusListener(com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL) {
        // add your own code here
    }

    public void removeStatusListener(com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL) {
        // add your own code here
    }

    // End of generated method stubs
    // com.sun.star.frame.XDispatch:
    public void dispatch(com.sun.star.util.URL aURL,
            com.sun.star.beans.PropertyValue[] aArguments) {
        if (aURL.Protocol.compareTo("org.creativecommons.openoffice.ccooo:") == 0) {

            this.updateCurrentComponent();

            if (aURL.Path.compareTo("SelectLicense") == 0) {
                selectLicense();
            } // if select license
            else if (aURL.Path.compareTo("InsertStatement") == 0) {
                insertStatement();
            } // if insert statement
            else if (aURL.Path.compareTo("InsertPictureFlickr") == 0) {
                insertFlickrImage();
            } else if (aURL.Path.compareTo("InsertOpenClipArt") == 0) {
                insertOpenClipArt();
            } else if (aURL.Path.compareTo("InsertWikimediaCommons") == 0) {
                insertWikimediaImage();
            } else if (aURL.Path.compareTo("InsertPicasa") == 0) {
                insertPicasaImage();
            }
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
                this.getProgramWrapper(this.getCurrentComponent()).updateVisibleNotice();
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

    public void insertFlickrImage() {

        try {

            if (mxRemoteServiceManager == null) {
                System.out.println("not available");
                return;
            }

            this.updateCurrentComponent();

            if (pictureFlickrDialog == null) {
                pictureFlickrDialog = new FlickrDialog(this, this.m_xContext);
                pictureFlickrDialog.setLoadable(true);
                pictureFlickrDialog.showDialog(false);
            } else {
                pictureFlickrDialog.setLoadable(true);
                pictureFlickrDialog.showDialog(true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            pictureFlickrDialog.setLoadable(false);
        }
    } // insertFlickrImage

    public void insertOpenClipArt() {

        try {

            if (mxRemoteServiceManager == null) {
                System.out.println("not available");
                return;
            }

            this.updateCurrentComponent();

            if (openClipArtDialog == null) {
                openClipArtDialog = new OpenClipArtDialog(this, this.m_xContext);
                openClipArtDialog.setLoadable(true);
                openClipArtDialog.showDialog(false);
            } else {
                openClipArtDialog.setLoadable(true);
                openClipArtDialog.showDialog(true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            openClipArtDialog.setLoadable(false);
        }


    } // insertOpenClipArt

    public void insertWikimediaImage() {

        try {

            if (mxRemoteServiceManager == null) {
                System.out.println("not available");
                return;
            }

            this.updateCurrentComponent();

            if (wikimediaDialog == null) {
                wikimediaDialog = new WikimediaDialog(this, this.m_xContext);
                wikimediaDialog.setLoadable(true);
                wikimediaDialog.showDialog(false);
            } else {
                wikimediaDialog.setLoadable(true);
                wikimediaDialog.showDialog(true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            wikimediaDialog.setLoadable(false);
        }
    } // insertWikimediaImage

    public void insertPicasaImage() {

        try {

            if (mxRemoteServiceManager == null) {
                System.out.println("not available");
                return;
            }

            this.updateCurrentComponent();

            if (picasaDialog == null) {
                picasaDialog = new PicasaDialog(this, this.m_xContext);
                picasaDialog.setLoadable(true);
                picasaDialog.showDialog(false);
            } else {
                picasaDialog.setLoadable(true);
                picasaDialog.showDialog(true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            picasaDialog.setLoadable(false);
        }
    } // insertPicasaImage

    /**
     * Creates an infobox with the title and text given
     *
     * @param title The title of the dialog.
     * @param msg The text of the dialog.
     *
     */
    private void createInfoBox(String title, String msg) {
        XMessageBoxFactory factory;
        try {
            factory = (XMessageBoxFactory) UnoRuntime.queryInterface(
                    XMessageBoxFactory.class,
                    this.xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.Toolkit", m_xContext));

            Rectangle ret = new Rectangle();
            WindowDescriptor wd;

            XWindowPeer parent = (XWindowPeer) UnoRuntime.queryInterface(
                    XWindowPeer.class, m_xFrame.getContainerWindow());
            //This document is already licensed.\n\nWould you like do proceed anyway?"
            XMessageBox box = factory.createMessageBox(parent, ret, "infobox",
                    MessageBoxButtons.BUTTONS_OK, title, msg);

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
    private short createQueryBox(String title, String msg) {
        XMessageBoxFactory factory;
        try {
            factory = (XMessageBoxFactory) UnoRuntime.queryInterface(
                    XMessageBoxFactory.class,
                    this.xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.Toolkit", m_xContext));

            Rectangle ret = new Rectangle();
            WindowDescriptor wd;

            XWindowPeer parent = (XWindowPeer) UnoRuntime.queryInterface(
                    XWindowPeer.class, m_xFrame.getContainerWindow());
            // TODO put listeners to the OK and Cancel buttons!
            XMessageBox box = factory.createMessageBox(parent, ret, "querybox",
                    MessageBoxButtons.BUTTONS_OK_CANCEL, title, msg);

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
    public void updateCurrentComponent() {


        XComponent ret = null;
        Object desktop;
        try {
            desktop = mxRemoteServiceManager.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", mxComponentContext);
            XDesktop xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, desktop);
            ret = xDesktop.getCurrentComponent();

            this.xMultiComponentFactory = this.m_xContext.getServiceManager();
            this.mxFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, this.xCurrentComponent);

        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }
        this.xCurrentComponent = ret;

    }

    public Object execute(NamedValue[] args) throws IllegalArgumentException,
            com.sun.star.uno.Exception {
        NamedValue[] lEnvironment = null;
        for (int i = 0; i < args.length; i++) {

            if (args[i].Name.equals("Environment")) {
                lEnvironment = (NamedValue[]) AnyConverter.toArray(args[i].Value);
                break;
            }
        }

        if (lEnvironment == null) {
            throw new com.sun.star.lang.IllegalArgumentException("no environment");
        }

        String sEnvType = null;
        String sEventName = null;
        XController m_xController = null;
        XModel m_xModel = null;
        for (int i = 0; i < lEnvironment.length; i++) {
            if (lEnvironment[i].Name.equals("EnvType")) {
                sEnvType = AnyConverter.toString(lEnvironment[i].Value);
            } else if (lEnvironment[i].Name.equals("EventName")) {
                sEventName = AnyConverter.toString(lEnvironment[i].Value);
            } else if (lEnvironment[i].Name.equals("Frame")) {
                m_xFrame = (XFrame) AnyConverter.toObject(
                        new com.sun.star.uno.Type(XFrame.class),
                        lEnvironment[i].Value);
                if (m_xFrame != null) {
                    m_xController = m_xFrame.getController();
                    m_xModel = m_xController.getModel();
                }
            } else if (lEnvironment[i].Name.equals("Model")) {
                m_xModel = (XModel) AnyConverter.toObject(
                        new com.sun.star.uno.Type(XModel.class),
                        lEnvironment[i].Value);
                if (m_xModel != null) {
                    m_xController = m_xModel.getCurrentController();
                    if (m_xController != null) {
                        m_xFrame = m_xController.getFrame();
                    }
                }
            }
        }
        if ((sEnvType == null) || !sEnvType.equals("DOCUMENTEVENT")) {
            throw new com.sun.star.lang.IllegalArgumentException("\"" + sEnvType + "\" isn't a valid value for EnvType");
        }
        //where to start the thread??
        //running as a therad will stop the
        //unresponsive 2 second period when loading document
        StoreThread th = new StoreThread();
        th.setPriority(Thread.MIN_PRIORITY);
        th.start();

        //Display license information when opening CC licensed documents
        try {
            if (sEventName != null && sEventName.equalsIgnoreCase("onload")) {
                this.updateCurrentComponent();
                XDocumentInfoSupplier xDocumentInfoSupplier = (XDocumentInfoSupplier) UnoRuntime.queryInterface(
                        XDocumentInfoSupplier.class, this.getCurrentComponent());

                XDocumentInfo docInfo = xDocumentInfoSupplier.getDocumentInfo();
                XPropertySet docProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, docInfo);

                if (docProperties.getPropertySetInfo().hasPropertyByName(Constants.LICENSE_URI)) {
                    String message = "This work is licensed under a "
                            + docProperties.getPropertyValue(Constants.LICENSE_NAME).toString()
                            + " License \navailable at "
                            + docProperties.getPropertyValue(Constants.LICENSE_URI).toString();
                    createInfoBox("Creative Commons Licensed Document", message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return com.sun.star.uno.Any.complete(new NamedValue[0]);
    }
}
