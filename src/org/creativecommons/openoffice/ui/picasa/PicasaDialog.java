/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.picasa;

import com.sun.star.awt.SystemPointer;
import com.sun.star.awt.XPopupMenu;
import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XRadioButton;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XMultiPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.graphic.XGraphic;
import org.creativecommons.openoffice.*;
import org.creativecommons.openoffice.program.Image;
import com.sun.star.awt.XWindowPeer;
import java.util.Date;
import org.creativecommons.openoffice.ui.InsertImageDialog;
import org.creativecommons.openoffice.ui.SavedSearchThread;

/**
 *
 * @author Husleag Mihai
 */
public class PicasaDialog extends InsertImageDialog{

    public static final String RDO_COMMERCIALNAME = "rdoCommercial";
    public static final String RDO_COMMERCIALLABEL = "Search for works I can use for commercial purposes";
    public static final String RDO_UPDATENAME = "chkUpdate";
    public static final String RDO_UPDATELABEL = "Search for works I can modify, adapt, or build upon";
    public static final String RDO_CCNAME = "rdoCC";
    public static final String RDO_CCLABEL = "Images with Creative Commons Licenses (Including above two)";

    private short savedCommercialStatus;
    private short savedUpdateStatus;
    private short savedCCStatus;

    /**
     * Creates a new instance of ChooserDialog
     */
    public PicasaDialog(CcOOoAddin addin, XComponentContext m_xContext) {
        super(addin, m_xContext, 45, 80, 370);
        searchClickListener = new PicasaSearchClickListener(this, addin);
    }

    /**
     * Method for creating a dialog at runtime
     *
     */
    public void showDialog(boolean defaultSearch) throws com.sun.star.uno.Exception {

        try {
            long time = new Date().getTime();
            // get the service manager from the component context
            this.xMultiComponentFactory = this.m_xContext.getServiceManager();

            // create the dialog model and set the properties
            Object dlgLicenseSelector = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.UnoControlDialogModel", m_xContext);
            XMultiServiceFactory msfLicenseSelector = (XMultiServiceFactory)
                    UnoRuntime.queryInterface(XMultiServiceFactory.class, dlgLicenseSelector);

            XPropertySet xPSetDialog = createAWTControl(dlgLicenseSelector, "dlgMainForm",
                    "", new Rectangle(100, 100, 240, 400));//360
            xPSetDialog.setPropertyValue("Title", new String("Insert Picture from Picasa"));
            xPSetDialog.setPropertyValue("Step", (short) 1);

            // get the name container for the dialog for inserting other elements
            this.xNameCont = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, dlgLicenseSelector);

            // get the service manager from the dialog model
            this.xMultiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, dlgLicenseSelector);

            Object lblTags = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlFixedTextModel");
            createAWTControl(lblTags, LBL_TAGS, "Tags", new Rectangle(10, 10, 50, 12));

            Object txtTags = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlEditModel");
            createAWTControl(txtTags, TXT_TAGS, "", new Rectangle(30, 10, 150, 12));

            Object rdoCommercial = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlRadioButtonModel");
            XPropertySet xpsRDOProperties = createAWTControl(rdoCommercial, RDO_COMMERCIALNAME, RDO_COMMERCIALLABEL,
                    new Rectangle(10, 32, 150, 12));
            xpsRDOProperties.setPropertyValue("State", new Short((short) 1));

            Object rdoUpdate = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlRadioButtonModel");
            xpsRDOProperties = createAWTControl(rdoUpdate, RDO_UPDATENAME, RDO_UPDATELABEL,
                    new Rectangle(10, 49, 150, 12));
            xpsRDOProperties.setPropertyValue("State", new Short((short) 0));

            Object chkCC = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlRadioButtonModel");
            xpsRDOProperties = createAWTControl(chkCC, RDO_CCNAME, RDO_CCLABEL,
                    new Rectangle(10, 66, 150, 12)); //(50, 66, 150, 12));
            xpsRDOProperties.setPropertyValue("State", new Short((short) 0));

            Object searchButton = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlButtonModel");
            XPropertySet xPSetFinishButton = createAWTControl(searchButton,
                    BTN_SEARCH, searchButtonLabel,
                    new Rectangle(190, 10, 40, 15)); //(140, 85, 40, 15));
            xPSetFinishButton.setPropertyValue("DefaultButton", new Boolean("true"));

            // create the dialog control and set the model
            Object dialog = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.UnoControlDialog", m_xContext); //esse
            xControl = (XControl) UnoRuntime.queryInterface(XControl.class, dialog);
            XControlModel xControlModel = (XControlModel) UnoRuntime.queryInterface(
                    XControlModel.class, dlgLicenseSelector);
            xControl.setModel(xControlModel);

            xControlCont = (XControlContainer) UnoRuntime.queryInterface(
                    XControlContainer.class, dialog);

            Object objSearchButton = xControlCont.getControl(BTN_SEARCH);
            XButton xFinishButton = (XButton) UnoRuntime.queryInterface(XButton.class, objSearchButton);
            xFinishButton.addActionListener(new PicasaSearchClickListener(this, this.addin));
            xFinishButton.setActionCommand(BTN_SEARCH);

            Object oGBResults = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlGroupBoxModel");
            createAWTControl(oGBResults, GB_RESULTS, "Results",
                    new Rectangle(10, locationMagesy, 220, 280));//315

            Object oPBar = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlProgressBarModel");
            XMultiPropertySet xPBModelMPSet = (XMultiPropertySet)
                    UnoRuntime.queryInterface(XMultiPropertySet.class, oPBar);
            // Set the properties at the model - keep in mind to pass the property names in alphabetical order!
            xPBModelMPSet.setPropertyValues(
                    new String[]{"Height", "Name", "PositionX", "PositionY", "Width"},
                    new Object[]{new Integer(8), PB_NAME, new Integer(10), new Integer(390)/*418*/, new Integer(220)});

            // The controlmodel is not really available until inserted to the Dialog container
            getNameContainer().insertByName(PB_NAME, oPBar);
            XPropertySet xPBModelPSet = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, oPBar);

            xPBModelPSet.setPropertyValue("ProgressValueMin", new Integer(0));
            xPBModelPSet.setPropertyValue("ProgressValueMax", new Integer(100));

            // create a peer
            Object toolkit = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.Toolkit", m_xContext);
            XToolkit xToolkit = (XToolkit) UnoRuntime.queryInterface(XToolkit.class, toolkit);
            XWindow xWindow = (XWindow) UnoRuntime.queryInterface(XWindow.class, xControl);
            xWindow.setVisible(false);
            xControl.createPeer(xToolkit, null);
            xWindowPeer = xControl.getPeer();
            System.out.println("Flicker Dialog Showing All " + (new Date().getTime() - time));
            if (defaultSearch) {
                SavedSearchThread thread = new SavedSearchThread(this);
                thread.start();
            }

            // execute the dialog
            this.xDialog = (XDialog) UnoRuntime.queryInterface(XDialog.class, dialog);
            this.xDialog.execute();

            // dispose the dialog
            XComponent xComponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, dialog);
            xComponent.dispose();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void createImageControl(Image img, Rectangle rect, String pos) {

        if (!isLoadable) {

            return;
        }
        createImageLoad(rect, pos,"file://" + this.getClass().
                        getProtectionDomain().getCodeSource().
                        getLocation().getPath().
                        replaceFirst("ccooo.jar", "images/Picasa_Loading.png"));

        try {
            String userName = "";
            if (img != null) {

                userName = "From " + img.getUserName();
            }

            XGraphic xGraphic = null;
            if (img != null) {

                if (img.getGraphic() != null) {
                    xGraphic = img.getGraphic();
                } else {
                    xGraphic = getGraphic(img.getImgURL());

                    img.setGraphic(xGraphic);
                }
            }

            Object oICModel = null;
            if (getNameContainer().hasByName("ImageControl" + pos)) {
                XControl xImageControl = xControlCont.getControl("ImageControl" + pos);
                if (xImageControl != null) {
                    xImageControl.dispose();
                }
                getNameContainer().removeByName("ImageControl" + pos);
            }

            oICModel = xMultiServiceFactory.createInstance(
                    "com.sun.star.awt.UnoControlImageControlModel");
            XPropertySet xpsImageControl = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, oICModel);

            xpsImageControl.setPropertyValue("Border", (short) 0);
            xpsImageControl.setPropertyValue("Height", new Integer(rect.height));
            xpsImageControl.setPropertyValue("Name", "ImageControl" + pos);
            xpsImageControl.setPropertyValue("PositionX", new Integer(rect.x));
            xpsImageControl.setPropertyValue("PositionY", new Integer(rect.y));
            xpsImageControl.setPropertyValue("Width", new Integer(rect.width));

            String title = "";
            if (img != null) {
                title = img.getTitle();
            } else {
                title = "";
            }

            xpsImageControl.setPropertyValue("HelpText", title);

            getNameContainer().insertByName("ImageControl" + pos, oICModel);

            XControl xImageControl = xControlCont.getControl("ImageControl" + pos);
            XWindow xWindow = (XWindow) UnoRuntime.queryInterface(XWindow.class, xImageControl);
            if (xWindow != null) {
                xWindow.addMouseListener(new PicasaImageButtonListener(this, this.addin, img));
            }

            xpsImageControl.setPropertyValue("Graphic", xGraphic);

            Object lblUser = null;
            if (getNameContainer().hasByName("ImageLabelUser" + pos)) {
                lblUser = getNameContainer().getByName("ImageLabelUser" + pos);
            } else {
                lblUser = xMultiServiceFactory.createInstance(
                        "com.sun.star.awt.UnoControlFixedHyperlinkModel");
            }

            XPropertySet xpsProperties = createAWTControl(lblUser, "ImageLabelUser"
                    + pos, userName,
                    new Rectangle(rect.x, rect.y + rect.height + 3, positionWidthHeight, 15)); //50
            if (img != null) {
                xpsProperties.setPropertyValue("URL", img.getProfile());
            } else {
                xpsProperties.setPropertyValue("URL", "");
            }
            xpsProperties.setPropertyValue("Label", userName);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void saveSearch() {

        try {

            Object oTags = xControlCont.getControl(TXT_TAGS);
            XControl txtTags = (XControl) UnoRuntime.queryInterface(XControl.class, oTags);
            XControlModel xControlModel = txtTags.getModel();
            XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xControlModel);
            String selTags = (String) xPSet.getPropertyValue("Text");
            this.savedTags = selTags.trim();

            if (getRadioButtonStatus(RDO_COMMERCIALNAME)) {
                savedCommercialStatus = 1;
            } else {
                savedCommercialStatus = 0;
            }

            if (getRadioButtonStatus(RDO_UPDATENAME)) {
                savedUpdateStatus = 1;
            } else {
                savedUpdateStatus = 0;
            }

            if (getRadioButtonStatus(RDO_CCNAME)) {
                savedCCStatus = 1;
            } else {
                savedCCStatus = 0;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startSavedSearch() {

        setMousePointer(SystemPointer.WAIT);
        enableControl(PicasaDialog.BTN_SEARCH, false);
        enableControl(PicasaDialog.BTN_NEXT, false);
        currentPositionInList = 0;

        try {
            Object oTags = xControlCont.getControl(TXT_TAGS);
            XControl txtTags = (XControl) UnoRuntime.queryInterface(XControl.class, oTags);
            XControlModel xControlModel = txtTags.getModel();
            XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xControlModel);
            xPSet.setPropertyValue("Text", this.savedTags);

            Object oLicense = xControlCont.getControl(RDO_COMMERCIALNAME);
            XCheckBox checkBox = (XCheckBox) UnoRuntime.queryInterface(
                    XCheckBox.class, oLicense);
            checkBox.setState(savedCommercialStatus);

            oLicense = xControlCont.getControl(RDO_UPDATENAME);
            checkBox = (XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, oLicense);
            checkBox.setState(savedUpdateStatus);

            oLicense = xControlCont.getControl(RDO_CCNAME);
            checkBox = (XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, oLicense);
            checkBox.setState(savedCCStatus);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        showResults(currentList, 0);
        setProgressValue(100);
        enableControl(PicasaDialog.BTN_SEARCH, true);
        // enableControl(PicasaDialog.BTN_NEXT, true);
        setMousePointer(SystemPointer.ARROW);
    }

    public boolean getRadioButtonStatus(String ctrlName) {

        Object oLicense = xControlCont.getControl(ctrlName);
        XRadioButton radioButton = (XRadioButton) UnoRuntime.queryInterface(XRadioButton.class, oLicense);

        Object value = radioButton.getState();
        if (value != null) {
                return (Boolean)value;
        }
        return false;
    }

    public String getLicense() {

        boolean commercial = getRadioButtonStatus(RDO_COMMERCIALNAME);
        boolean update = getRadioButtonStatus(RDO_UPDATENAME);
        boolean cc = getRadioButtonStatus(RDO_CCNAME);

        if (commercial) {
            return "commercial";
        } else if (update) {
            return "remix";
        } else if (cc) {
            return "creative_commons";
        }
        return null;
    }

    public boolean IsInputValid() {

        if (this.GetTags().length == 0||this.getLicense()==null) {
            return false;
        }

        return true;
    }

    @Override
    public XPopupMenu executePopupMenu(Image img, Integer positionX, Integer positionY, XWindowPeer xImagePeer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

} // PicasaDialog

