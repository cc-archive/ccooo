/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.uno.Exception;
import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XRadioButton;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.graphic.XGraphic;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.creativecommons.license.Chooser;
import org.creativecommons.license.IJurisdiction;
import org.creativecommons.license.Jurisdiction;
import org.creativecommons.license.License;
import org.creativecommons.license.Store;
import org.creativecommons.openoffice.*;

/**
 *  The Creative Commons OpenOffice.org AddIn GUI class.
 *
 * @author Cassio A. Melo
 * @author akila
 * @author Creative Commons
 * @version 0.0.1
 */
public class ChooserDialog {

    private XMultiServiceFactory xMultiServiceFactory = null;
    protected XComponentContext m_xContext = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    private XNameContainer xNameCont = null;
    private XControlContainer xControlCont = null;
    private XDialog xDialog = null;
    private CcOOoAddin addin = null;
    private List<Jurisdiction> jurisdictionList = null;
    private IJurisdiction selectedJurisdiction = null;
    private boolean cancelled = true;
    // TODO put these labels in a properties file
    public static final String BTN_OK = "finishbt";
    public static final String finishButtonLabel = "OK";
    public static final String BTN_CANCEL = "cancelbt";
    public static final String cancelButtonLabel = "Cancel";
    public static final String BTN_FAQ = "faqbt";
    public static final String faqButtonLabel = "FAQ";

    public static final String CHK_ALLOW_REMIX = "chkAllowRemix";
    public static final String CHK_PROHIBIT_COMMERCIAL = "chkProhibitCommercial";
    public static final String CHK_REQUIRE_SHAREALIKE = "chkRequireShareAlike";
    public static final String CMB_JURISDICTION = "cmbJurisdiction";

    public static final String RDO_ALLOW_COMERCIAL_YES = "rdoAllowCommercial_Yes";
    public static final String RDO_ALLOW_COMERCIAL_NO = "rdoAllowCommercial_No";
    public static final String RDO_ALLOW_MODIFICATIONS_YES = "rdoAllowModifications_Yes";
    public static final String RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE = "rdoAllowModifications_ShareAlike";
    public static final String RDO_ALLOW_MODIFICATIONS_NO = "rdoAllowModifications_No";

    public static final String LBL_SELECTED_LICENSE = "lblSelectedLicense";
    public static final String LBL_SELECTED_LICENSE_LABEL = "lblSelectedLicense_lbl";
    public static final String LBL_SELECTED_LICENSE_INFO = "lblSelectedLicense_info";
    public static final String LBL_ALLOW_COMERCIAL_USE = "allowCommercialUse";
    public static final String LBL_ALLOW_MODIFICATIONS = "allowModifications";
    public static final String LBL_JURISDICTION_LIST = "lblJurisdictionList";

    /**
     * Creates a new instance of ChooserDialog
     */
    public ChooserDialog(CcOOoAddin addin, XComponentContext m_xContext) {
        this.addin = addin;
        this.m_xContext = m_xContext;
    }

    /**
     * Method for creating a dialog at runtime
     *
     */
    public void showDialog() throws com.sun.star.uno.Exception {

        // get the service manager from the component context
        this.xMultiComponentFactory = this.m_xContext.getServiceManager();

        // create the dialog model and set the properties
        Object dlgLicenseSelector = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.UnoControlDialogModel", m_xContext);
        XMultiServiceFactory msfLicenseSelector = (XMultiServiceFactory)
                UnoRuntime.queryInterface(XMultiServiceFactory.class, dlgLicenseSelector);

        XPropertySet xPSetDialog = createAWTControl(dlgLicenseSelector, "cc",
                null, new Rectangle(100, 100, 210, 190));
        xPSetDialog.setPropertyValue("Title", new String("Select a License"));
        xPSetDialog.setPropertyValue("Step", (short) 1);

        // get the name container for the dialog for inserting other elements
        this.xNameCont = (XNameContainer) UnoRuntime.queryInterface(
                XNameContainer.class, dlgLicenseSelector);

        // get the service manager from the dialog model
        this.xMultiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dlgLicenseSelector);

        msfLicenseSelector = xMultiServiceFactory;

///////////////////////////////////// create the current license information
        Object lblSelectedLicenseLabel = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblSelectedLicenseLabel, LBL_SELECTED_LICENSE_LABEL,
                "Selected License:", new Rectangle(10, 10, 50, 15));

        Object lblSelectedLicense = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblSelectedLicense, LBL_SELECTED_LICENSE,
                null, new Rectangle(60, 10, 200, 15));

/////////////////////////////////////Allow commercial uses of your work?
        Object lblAllowCommercialUse = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblAllowCommercialUse, LBL_ALLOW_COMERCIAL_USE,
                "Allow commercial uses of your work?", new Rectangle(15, 30, 100, 12));

        Object radioCommercialYes = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioCommercialYes = createAWTControl(
                radioCommercialYes, RDO_ALLOW_COMERCIAL_YES,
                "Yes", new Rectangle(20, 45, 30, 12));
        xpsRadioCommercialYes.setPropertyValue("State", new Short((short) 1));

        Object radioCommercialNo = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioCommercialNo = createAWTControl(
                radioCommercialNo, RDO_ALLOW_COMERCIAL_NO,
                "No", new Rectangle(20, 60, 30, 12));
        xpsRadioCommercialNo.setPropertyValue("State", new Short((short) 0));

///////////////////////////////////Allow modifications of your work?

        Object lblAllowModifications = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblAllowModifications, LBL_ALLOW_MODIFICATIONS,
                "Allow modifications of your work?", new Rectangle(15, 75, 100, 12));
        Object radioModificationsYes = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioModificationYes = createAWTControl(
                radioModificationsYes, RDO_ALLOW_MODIFICATIONS_YES,
                "Yes", new Rectangle(20, 90, 30, 12));
        xpsRadioModificationYes.setPropertyValue("State", new Short((short) 1));

        Object radioModificationsShareAlike = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioModificationsShareAlike = createAWTControl(
                radioModificationsShareAlike, RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE,
                "Yes, as long as others share alike", new Rectangle(20, 105, 100, 12));
        xpsRadioModificationsShareAlike.setPropertyValue("State", new Short((short) 0));

        Object radioModificationsNo = msfLicenseSelector.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioModificationsNo = createAWTControl(
                radioModificationsNo, RDO_ALLOW_MODIFICATIONS_NO,
                "No", new Rectangle(20, 120, 30, 12));
        xpsRadioModificationsNo.setPropertyValue("State", new Short((short) 0));

        // create the jurisdiction drop-down list
        Object lblJurisdictionList = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsLblJurisdictionList =
                createAWTControl(lblJurisdictionList, LBL_JURISDICTION_LIST,
                "Jurisdiction of your license", new Rectangle(15, 135, 75, 15));

        Object cmbJurisdictionList = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlListBoxModel");

        XPropertySet xPSetList = createAWTControl(cmbJurisdictionList, CMB_JURISDICTION,
                null, new Rectangle(90, 135, 60, 12));
        xPSetList.setPropertyValue("Dropdown", new Boolean("true"));
        xPSetList.setPropertyValue("MultiSelection", new Boolean("false"));
        xPSetList.setPropertyValue("Step", new Short((short) 1));

        // create the button model - FAQ and set the properties
        Object faqButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetFaqButton = createAWTControl(faqButton, BTN_FAQ,
                null, new Rectangle(70, 165, 40, 14));
        xPSetFaqButton.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetFaqButton.setPropertyValue("Label", faqButtonLabel);

        // create the button model - OK and set the properties
        Object finishButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetFinishButton = createAWTControl(finishButton, BTN_OK,
                null, new Rectangle(115, 165, 40, 14));
        xPSetFinishButton.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetFinishButton.setPropertyValue("Label", finishButtonLabel);

        // create the button model - Cancel and set the properties
        Object cancelButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetCancelButton = createAWTControl(cancelButton, BTN_CANCEL,
                null, new Rectangle(160, 165, 40, 14));
        xPSetCancelButton.setPropertyValue("Name", BTN_CANCEL);
        xPSetCancelButton.setPropertyValue("Label", cancelButtonLabel);

        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.UnoControlDialog", m_xContext); //esse
        XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, dialog);
        XControlModel xControlModel = (XControlModel)
                UnoRuntime.queryInterface(XControlModel.class, dlgLicenseSelector);
        xControl.setModel(xControlModel);

        // add an action listener to the Previous button control
        xControlCont = (XControlContainer) UnoRuntime.queryInterface(
                XControlContainer.class, dialog);

        XListBox cmbJList = (XListBox) UnoRuntime.queryInterface(
                XListBox.class, xControlCont.getControl(CMB_JURISDICTION));

        long time = new Date().getTime();
        this.setJurisdictionList(Store.get().jurisdictions());
        System.out.println("Dialog setJurisdictionList" + (new Date().getTime() - time));

        Iterator<Jurisdiction> it;
        it = this.getJurisdictionList().iterator();
        short count = 0;

        // add Unported, which isn't actually a jurisdiction'
        cmbJList.addItem("Unported", count++);

        while (it.hasNext()) {
            Jurisdiction j = it.next();
            cmbJList.addItem(j.getTitle(), count++);

        }

        // add a bogus place-holder for Unported in the JurisdictionList to
        // ensure indices match up when determining the item selectedJurisdiction
        this.getJurisdictionList().add(0, null);

        // Pre-select Unported
        cmbJList.selectItemPos((short) 0, true);
        cmbJList.makeVisible((short) 0);

        // listen for license selection changes

        ((XRadioButton) UnoRuntime.queryInterface(XRadioButton.class,
                xControlCont.getControl(RDO_ALLOW_COMERCIAL_YES))).addItemListener(
                new UpdateLicenseListener(this));
        ((XRadioButton) UnoRuntime.queryInterface(XRadioButton.class,
                xControlCont.getControl(RDO_ALLOW_COMERCIAL_NO))).addItemListener(
                new UpdateLicenseListener(this));
        ((XRadioButton) UnoRuntime.queryInterface(XRadioButton.class,
                xControlCont.getControl(RDO_ALLOW_MODIFICATIONS_YES))).addItemListener(
                new UpdateLicenseListener(this));
        ((XRadioButton) UnoRuntime.queryInterface(XRadioButton.class,
                xControlCont.getControl(RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE))).addItemListener(
                new UpdateLicenseListener(this));
        ((XRadioButton) UnoRuntime.queryInterface(XRadioButton.class,
                xControlCont.getControl(RDO_ALLOW_MODIFICATIONS_NO))).addItemListener(
                new UpdateLicenseListener(this));

        cmbJList.addItemListener(new JurisdictionSelectListener(this));

        // add an action listener to the Faq button control
        Object objectButton3 = xControlCont.getControl(BTN_FAQ);
        XButton xFaqButton = (XButton) UnoRuntime.queryInterface(XButton.class, objectButton3);
        xFaqButton.addActionListener(new FaqClickListener(this, this.m_xContext));

        // add an action listener to the Finish button control
        Object objectButton4 = xControlCont.getControl(BTN_OK);
        XButton xFinishButton = (XButton) UnoRuntime.queryInterface(XButton.class, objectButton4);
        xFinishButton.addActionListener(new FinishClickListener(this, this.addin));

        // add an action listener to the Cancel button control
        Object objectButton5 = xControlCont.getControl(BTN_CANCEL);
        XButton xCancelButton = (XButton) UnoRuntime.queryInterface(XButton.class, objectButton5);
        xCancelButton.addActionListener(new CancelClickListener(this));

        if (this.addin.getProgramWrapper().getDocumentLicense() != null) {
            this.setSelectedLicense(this.addin.getProgramWrapper().getDocumentLicense());
        } else {
            this.setSelectedLicense(new License("http://creativecommons.org/licenses/by/3.0/"));
        }

        // create a peer
        Object toolkit = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.Toolkit", m_xContext);
        XToolkit xToolkit = (XToolkit) UnoRuntime.queryInterface(XToolkit.class, toolkit);
        XWindow xWindow = (XWindow) UnoRuntime.queryInterface(XWindow.class, xControl);
        xWindow.setVisible(false);

        xControl.createPeer(xToolkit, null);

        setInfoImage(new Rectangle(55, 43, 9, 10), RDO_ALLOW_COMERCIAL_YES,
                "The licensor permits others to copy, distribute,"
                + "\ndisplay and perform the work,"
                + "\nas well as make derivative works based on it.");

        setInfoImage(new Rectangle(55, 58, 9, 10), RDO_ALLOW_COMERCIAL_NO,
                "The licensor permits others to copy, "
                + "\ndistribute, display, and perform the work "
                + "\nfor non-commercial purposes only");

        setInfoImage(new Rectangle(55, 88, 9, 10), RDO_ALLOW_MODIFICATIONS_YES,
                "The licensor permits others to copy, "
                + "\ndistribute, display and perform the work, "
                + "\nas well as make derivative works based on it.");

        setInfoImage(new Rectangle(125, 103, 9, 10), RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE,
                "The licensor permits others to distribute derivative works "
                + "\nonly under the same license or one compatible "
                + "\nwith the one that governs the licensor's work.");

        setInfoImage(new Rectangle(55, 118, 9, 10), RDO_ALLOW_MODIFICATIONS_NO,
                "The licensor permits others to copy, "
                + "\ndistribute and transmit only unaltered copies of the "
                + "\nwork — not derivative works based on it.");

        setInfoImage(new Rectangle(155, 133, 9, 10), CMB_JURISDICTION,
                "Use the option \"International\" if you desire a license using "
                + "\nlanguage and terminology from international treaties. ");

        // execute the dialog
        this.xDialog = (XDialog) UnoRuntime.queryInterface(XDialog.class, dialog);
        this.xDialog.execute();

        // dispose the dialog
        XComponent xComponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, dialog);
        xComponent.dispose();

    }

    private void setInfoImage(Rectangle rect, String pos, String title) {
        try {
            Object oICModel = null;
            if (getNameContainer().hasByName("ImageControl" + pos)) {
                try {
                    XControl xImageControl = xControlCont.getControl("ImageControl" + pos);
                    if (xImageControl != null) {
                        xImageControl.dispose();
                    }
                    getNameContainer().removeByName("ImageControl" + pos);
                } catch (NoSuchElementException ex) {
                    Logger.getLogger(ChooserDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (WrappedTargetException ex) {
                    Logger.getLogger(ChooserDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            oICModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlImageControlModel");
            XGraphic xGraphic = null;
            String file = "file://" + this.getClass().getProtectionDomain().
                    getCodeSource().getLocation().getPath().
                    replaceFirst("ccooo.jar", "images/information1.png");
            xGraphic = getGraphic(file);

            XPropertySet xpsImageControl = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, oICModel);
            xpsImageControl.setPropertyValue("Border", (short) 0);

            xpsImageControl.setPropertyValue("Height", new Integer(rect.height));
            xpsImageControl.setPropertyValue("Name", "ImageControl" + pos);
            xpsImageControl.setPropertyValue("PositionX", new Integer(rect.x));
            xpsImageControl.setPropertyValue("PositionY", new Integer(rect.y));
            xpsImageControl.setPropertyValue("Width", new Integer(rect.width));

            getNameContainer().insertByName("ImageControl" + pos, oICModel);
            xpsImageControl.setPropertyValue("HelpText", title);
            xpsImageControl.setPropertyValue("Graphic", xGraphic);

        } catch (Exception ex) {
            Logger.getLogger(ChooserDialog.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private XPropertySet createAWTControl(Object objControl, String ctrlName,
            String ctrlCaption, Rectangle posSize) throws Exception {

        XPropertySet xpsProperties = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, objControl);

        xpsProperties.setPropertyValue("PositionX", new Integer(posSize.x));
        xpsProperties.setPropertyValue("PositionY", new Integer(posSize.y));
        xpsProperties.setPropertyValue("Width", new Integer(posSize.width));
        xpsProperties.setPropertyValue("Height", new Integer(posSize.height));
        xpsProperties.setPropertyValue("Name", ctrlName);
        if (ctrlCaption != null) {
            xpsProperties.setPropertyValue("Label", ctrlCaption);
        }

        if ((getNameContainer() != null) && (!getNameContainer().hasByName(ctrlName))) {
            getNameContainer().insertByName(ctrlName, objControl);
        }

        return xpsProperties;
    }

    public XGraphic getGraphic(String _sImageUrl) {

        XGraphic xGraphic = null;
        try {

            // create a GraphicProvider at the global service manager...
            Object oGraphicProvider = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.graphic.GraphicProvider", m_xContext);
            XGraphicProvider xGraphicProvider = (XGraphicProvider)
                    UnoRuntime.queryInterface(XGraphicProvider.class, oGraphicProvider);
            // create the graphic object
            PropertyValue[] aPropertyValues = new PropertyValue[1];
            PropertyValue aPropertyValue = new PropertyValue();
            aPropertyValue.Name = "URL";
            aPropertyValue.Value = _sImageUrl;
            aPropertyValues[0] = aPropertyValue;
            xGraphic = xGraphicProvider.queryGraphic(aPropertyValues);
            return xGraphic;
        } catch (com.sun.star.uno.Exception ex) {
            throw new java.lang.RuntimeException("cannot happen...");
        }
    }

    protected void setCheckboxValue(String controlName, Boolean b) {

        try {
            XPropertySet xPSetList = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(controlName));

            xPSetList.setPropertyValue("State",
                    (b ? new Short((short) 1) : new Short((short) 0))); // b.booleanValue());
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
        }

    }

    protected Boolean getRadioButtonValue(String rdoName) {
        try {

            XPropertySet xPSetList = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(rdoName));

            return (((Short) xPSetList.getPropertyValue("State")).intValue() == 1);

        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
            return null;
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
            return null;
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
            return null;
        }

    } // getRadioButtonValue

    public IJurisdiction getSelectedJurisdiction() {
        return selectedJurisdiction;
    }

    public void setSelectedJurisdiction(IJurisdiction selected) {
        this.selectedJurisdiction = selected;
    }

    public List<Jurisdiction> getJurisdictionList() {
        return jurisdictionList;
    }

    protected void setJurisdictionList(List<Jurisdiction> jurisdictionList) {
        this.jurisdictionList = jurisdictionList;
    }

    public License getSelectedLicense() {

        // retrieve the Document for the issued license
        Chooser licenseChooser = new Chooser();
        return licenseChooser.selectLicense(
                this.getRadioButtonValue(
                ChooserDialog.RDO_ALLOW_MODIFICATIONS_YES).booleanValue()
                || this.getRadioButtonValue(
                ChooserDialog.RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE).booleanValue(),
                this.getRadioButtonValue(
                ChooserDialog.RDO_ALLOW_COMERCIAL_NO).booleanValue(),
                this.getRadioButtonValue(
                ChooserDialog.RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE).booleanValue(),
                this.getSelectedJurisdiction());

    } // getSelectedLicense

    public void setSelectedLicense(License selected) {
        // update the user interface to match this selection
        this.setCheckboxValue(ChooserDialog.RDO_ALLOW_COMERCIAL_YES,
                !selected.prohibitCommercial());
        this.setCheckboxValue(ChooserDialog.RDO_ALLOW_COMERCIAL_NO,
                selected.prohibitCommercial());
        this.setCheckboxValue(ChooserDialog.RDO_ALLOW_MODIFICATIONS_YES,
                selected.allowRemix()&&!selected.requireShareAlike());
        this.setCheckboxValue(ChooserDialog.RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE,
                selected.requireShareAlike());
        this.setCheckboxValue(ChooserDialog.RDO_ALLOW_MODIFICATIONS_NO,
                !selected.allowRemix());

        this.setSelectedJurisdiction(selected.getJurisdiction());

        this.updateSelectedLicense();

    } // setSelectedLicense

    void updateSelectedLicense() {
        try {

            XPropertySet xpsSelectedLicense = (XPropertySet)
                    UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(LBL_SELECTED_LICENSE));

            xpsSelectedLicense.setPropertyValue("Label", this.getSelectedLicense().getName());
        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
        }
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

