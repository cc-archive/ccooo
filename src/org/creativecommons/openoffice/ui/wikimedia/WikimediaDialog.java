/*
 * WikimediaDialog.java
 *
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.wikimedia;

import com.sun.star.awt.XPopupMenu;
import com.sun.star.awt.XWindowPeer;
import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
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
import java.util.Date;
import org.creativecommons.openoffice.ui.InsertImageDialog;
import org.creativecommons.openoffice.ui.SavedSearchThread;

/**
 *
 * @author akila
 */
public class WikimediaDialog extends InsertImageDialog {

    /**
     * Creates a new instance of ChooserDialog
     */
    public WikimediaDialog(CcOOoAddin addin, XComponentContext m_xContext) {
        super(addin, m_xContext, 45, 63, 347);
        searchClickListener = new WikimediaSearchClickListener(this, addin);
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
                    "", new Rectangle(DIALOGX, DIALOGY, DIALOGWIDTH, DIALOGHEIHT));//360
            xPSetDialog.setPropertyValue("Title", new String("Insert Picture from Wikimedia Commons"));
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

            Object chkCommercial = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlCheckBoxModel");
            XPropertySet xpsCHKProperties = createAWTControl(chkCommercial, CHK_COMMERCIALNAME, CHK_COMMERCIALLABEL,
                    new Rectangle(10, 27, 150, 12));

            xpsCHKProperties.setPropertyValue("TriState", Boolean.FALSE);
            xpsCHKProperties.setPropertyValue("State", new Short((short) 1));

            Object chkUpdate = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlCheckBoxModel");
            xpsCHKProperties = createAWTControl(chkUpdate, CHK_UPDATENAME, CHK_UPDATELABEL,
                    new Rectangle(10, 39, 150, 12));
            xpsCHKProperties.setPropertyValue("TriState", Boolean.FALSE);
            xpsCHKProperties.setPropertyValue("State", new Short((short) 1));

            Object chkShareAlike = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlCheckBoxModel");
            xpsCHKProperties = createAWTControl(chkShareAlike, CHK_SHAREALKENAME, CHK_SHAREALKELABEL,
                    new Rectangle(10, 51, 150, 12)); //(50, 66, 150, 12));
            xpsCHKProperties.setPropertyValue("TriState", Boolean.FALSE);
            xpsCHKProperties.setPropertyValue("State", new Short((short) 0));

            Object searchButton = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlButtonModel");
            XPropertySet xPSetFinishButton = createAWTControl(searchButton,
                    BTN_SEARCH, searchButtonLabel, new Rectangle(190, 10, 40, 15)); //(140, 85, 40, 15));
            xPSetFinishButton.setPropertyValue("DefaultButton", new Boolean("true"));

            // create the dialog control and set the model
            Object dialog = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.UnoControlDialog", m_xContext); //esse
            xControl = (XControl) UnoRuntime.queryInterface(XControl.class, dialog);
            XControlModel xControlModel = (XControlModel)
                    UnoRuntime.queryInterface(XControlModel.class, dlgLicenseSelector);
            xControl.setModel(xControlModel);

            xControlCont = (XControlContainer) UnoRuntime.queryInterface(
                    XControlContainer.class, dialog);

            Object objSearchButton = xControlCont.getControl(BTN_SEARCH);
            XButton xFinishButton = (XButton) UnoRuntime.queryInterface(
                    XButton.class, objSearchButton);
            xFinishButton.addActionListener(new WikimediaSearchClickListener(this, this.addin));
            xFinishButton.setActionCommand(BTN_SEARCH);

            Object oGBResults = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlGroupBoxModel");
            createAWTControl(oGBResults, GB_RESULTS, "Results",
                    new Rectangle(10, locationMagesy, 220, 280));//315

            Object oPBar = msfLicenseSelector.createInstance(
                    "com.sun.star.awt.UnoControlProgressBarModel");
            XMultiPropertySet xPBModelMPSet = (XMultiPropertySet)
                    UnoRuntime.queryInterface(XMultiPropertySet.class, oPBar);
            // Set the properties at the model - keep in mind to pass the
            // property names in alphabetical order!
            xPBModelMPSet.setPropertyValues(
                    new String[]{"Height", "Name", "PositionX", "PositionY", "Width"},
                    new Object[]{new Integer(8), PB_NAME, new Integer(10),
                        new Integer(DIALOGHEIHT - 8)/*418*/, new Integer(220)});

            // The controlmodel is not really available until inserted to the Dialog container
            getNameContainer().insertByName(PB_NAME, oPBar);
            XPropertySet xPBModelPSet = (XPropertySet)
                    UnoRuntime.queryInterface(XPropertySet.class, oPBar);

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
            XComponent xComponent = (XComponent)
                    UnoRuntime.queryInterface(XComponent.class, dialog);
            xComponent.dispose();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void createImageControl(Image img, Rectangle rect, String pos) {

        if (!isLoadable) {

            return;
        }
        createImageLoad(rect, pos, "file://" + this.getClass().
                getProtectionDomain().getCodeSource().
                getLocation().getPath().
                replaceFirst("ccooo.jar", "images/wikimedia_commons_loading.png"));

        try {
            String userName = "";
            if (img != null) {

                userName = img.getTitle();
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
            XPropertySet xpsImageControl = (XPropertySet)
                    UnoRuntime.queryInterface(XPropertySet.class, oICModel);

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
                xWindow.addMouseListener(new WikimediaImageButtonListener(this, this.addin, img));
            }

            xpsImageControl.setPropertyValue("Graphic", xGraphic);

            Object lblUser = null;
            if (getNameContainer().hasByName("ImageLabelUser" + pos)) {
                lblUser = getNameContainer().getByName("ImageLabelUser" + pos);
            } else {
                lblUser = xMultiServiceFactory.createInstance(
                        "com.sun.star.awt.UnoControlFixedHyperlinkModel");
            }

            XPropertySet xpsProperties = createAWTControl(lblUser, "ImageLabelUser" + pos,
                    userName, new Rectangle(rect.x, rect.y + rect.height + 3, positionWidthHeight, 15)); //50
            if (img != null) {
                xpsProperties.setPropertyValue("URL", img.getImgUrlMainPage());
            } else {
                xpsProperties.setPropertyValue("URL", "");
            }
            xpsProperties.setPropertyValue("Label", userName);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String[] getLicenses() {

        boolean commercial = getCheckBoxStatus(CHK_COMMERCIALNAME);
        boolean update = getCheckBoxStatus(CHK_UPDATENAME);
        boolean shareAlike = getCheckBoxStatus(CHK_SHAREALKENAME);

        if (commercial && update && shareAlike) {
            return new String[]{"CC BY", "PD", "CC BY SA", "GFDL", "CC0"};//"4,5"
        } else if (commercial && update) {
            return new String[]{"CC BY", "PD", "CC0"};//"4"
        } else if (update && shareAlike) {
            return new String[]{"CC BY", "PD", "CC BY SA", "CC BY NC SA", "CC BY NC", "GFDL", "CC0"};//"1,2,4,5"
        } else if (commercial) {
            return new String[]{"CC BY", "PD", "CC BY SA", "CC BY ND", "GFDL", "CC0"};//"4,5,6"
        } else if (update) {
            return new String[]{"CC BY", "PD", "CC BY NC", "CC0"};//"2,4"
        }
        //default atribution license
        return new String[]{"CC BY", "PD", "CC0"};//"4";
    }

    @Override
    public XPopupMenu executePopupMenu(Image img, Integer positionX, Integer positionY, XWindowPeer xImagePeer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLicense() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
} // WikimediaDialog

