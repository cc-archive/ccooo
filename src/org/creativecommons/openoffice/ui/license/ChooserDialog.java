/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.FontDescriptor;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XRadioButton;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.graphic.XGraphic;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XEventListener;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.awt.Rectangle;
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
import org.creativecommons.openoffice.util.ReadFile;
import static org.creativecommons.openoffice.util.Util._;

/**
 *  The Creative Commons OpenOffice.org AddIn GUI class.
 *
 * @author Cassio A. Melo
 * @author akila
 * @author Creative Commons
 * @version 0.7
 */
public class ChooserDialog {

    private String selectedTerritory;
    private String[] trritories;
    private XMultiServiceFactory xMultiServiceFactory = null;
    protected XComponentContext m_xContext = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    private XNameContainer xNameCont = null;
    private XControlContainer xControlCont = null;
    private XDialog xDialog = null;
    private XPropertySet xPSetDialog, xPSetFinishButton;
    private XListBox cmbTList;
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
    public static final String BTN_CC = "btnCC";
    public static final String BTN_CC0 = "btnCC0";
    public static final String BTN_PUBLICDOMAIN = "btnPublicdomain";
    public static final String CHK_WAIVE = "chkWaive";
    public static final String CHK_YES_CC0 = "chkYesCC0";
    public static final String CHK_YES_PD = "chkYesPD";
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
    public static final String LBL_INSTRUCTIONS_CC = "lblInstructionsCC";
    public static final String LBL_INSTRUCTIONS_CC0 = "lblInstructionsCC0";
    public static final String CMB_JURISDICTION = "cmbJurisdiction";
    public static final String CMB_TERRITORY = "cmbTerritory";
    public static final String TXT_LEGAL_CODE_CC0 = "txtLegalCodeCC0";
    public static final String TXT_LEGAL_CODE_PD = "txtLegalCodePD";

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

        xPSetDialog = createAWTControl(dlgLicenseSelector, "cc",
                null, new Rectangle(100, 80, 210, 275), 1);
        xPSetDialog.setPropertyValue("Title", new String("Sharing & Reuse Permissions"));

        // get the name container for the dialog for inserting other elements
        this.xNameCont = (XNameContainer) UnoRuntime.queryInterface(
                XNameContainer.class, dlgLicenseSelector);

        // get the service manager from the dialog model
        this.xMultiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dlgLicenseSelector);

        ///////////////////////////////////////tabs
        Object ccButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetCCButton = createAWTControl(ccButton, BTN_CC,
                null, new Rectangle(4, 3, 70, 12), 0);
        xPSetCCButton.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetCCButton.setPropertyValue("Label", _("util.Creative_Commons"));//Creative Commons
        xPSetCCButton.setPropertyValue("Toggle", true);
        FontDescriptor fontDes = (FontDescriptor) xPSetCCButton.getPropertyValue("FontDescriptor");
        fontDes.Weight = 150;
        xPSetCCButton.setPropertyValue("FontDescriptor", fontDes);
        xPSetCCButton.setPropertyValue("State", (short) 1);

        Object cc0Button = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetCC0Button = createAWTControl(cc0Button, BTN_CC0,
                null, new Rectangle(73, 3, 20, 12), 0);
        xPSetCC0Button.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetCC0Button.setPropertyValue("Label", "CC0");
        xPSetCC0Button.setPropertyValue("Toggle", true);
        fontDes = (FontDescriptor) xPSetCC0Button.getPropertyValue("FontDescriptor");
        fontDes.Weight = 75;
        xPSetCC0Button.setPropertyValue("FontDescriptor", fontDes);
        xPSetCC0Button.setPropertyValue("State", (short) 0);

        Object pdButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetPDButton = createAWTControl(pdButton, BTN_PUBLICDOMAIN,
                null, new Rectangle(92, 3, 60, 12), 0);
        xPSetPDButton.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetPDButton.setPropertyValue("Label", _("util.Public_Domain"));//"Public Domain"
        xPSetPDButton.setPropertyValue("Toggle", true);
        fontDes = (FontDescriptor) xPSetPDButton.getPropertyValue("FontDescriptor");
        fontDes.Weight = 75;
        xPSetPDButton.setPropertyValue("FontDescriptor", fontDes);
        xPSetPDButton.setPropertyValue("State", (short) 0);

        Object oGBResults = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlGroupBoxModel");
        XPropertySet xpsBox = createAWTControl(
                oGBResults, "box", null, new Rectangle(2, 15, 206, 243), 0);
        crateCC0LicenseTab();
        createCCLicenseTab();
        cratePDLicenseTab();

        // create the button model - FAQ and set the properties
        Object faqButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetFaqButton = createAWTControl(faqButton, BTN_FAQ,
                null, new Rectangle(70, 260, 40, 14), 0);
        xPSetFaqButton.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetFaqButton.setPropertyValue("Label", faqButtonLabel);

        // create the button model - OK and set the properties
        Object finishButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        xPSetFinishButton = createAWTControl(finishButton, BTN_OK,
                null, new Rectangle(115, 260, 40, 14), 0);
        xPSetFinishButton.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetFinishButton.setPropertyValue("Label", finishButtonLabel);

        // create the button model - Cancel and set the properties
        Object cancelButton = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlButtonModel");
        XPropertySet xPSetCancelButton = createAWTControl(cancelButton, BTN_CANCEL,
                null, new Rectangle(160, 260, 40, 14), 0);
        xPSetCancelButton.setPropertyValue("Name", BTN_CANCEL);
        xPSetCancelButton.setPropertyValue("Label", cancelButtonLabel);

        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext(
                "com.sun.star.awt.UnoControlDialog", m_xContext); //esse
        XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, dialog);
        XControlModel xControlModel = (XControlModel) UnoRuntime.queryInterface(
                XControlModel.class, dlgLicenseSelector);
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
        cmbJList.addItem(_("util.Unported"), count++);//"Unported"

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
        addListners(XRadioButton.class, RDO_ALLOW_COMERCIAL_YES, new UpdateLicenseListener(this));
        addListners(XRadioButton.class, RDO_ALLOW_COMERCIAL_NO, new UpdateLicenseListener(this));
        addListners(XRadioButton.class, RDO_ALLOW_MODIFICATIONS_YES, new UpdateLicenseListener(this));
        addListners(XRadioButton.class, RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE, new UpdateLicenseListener(this));
        addListners(XRadioButton.class, RDO_ALLOW_MODIFICATIONS_NO, new UpdateLicenseListener(this));
        cmbJList.addItemListener(new JurisdictionSelectListener(this));

        addListners(XCheckBox.class, CHK_WAIVE, new AcceptWaiveListener(this));
        addListners(XCheckBox.class, CHK_YES_CC0, new AcceptListener(this));
        addListners(XCheckBox.class, CHK_YES_PD, new AcceptListener(this));

        // add an action listener to the Faq buttons
        addListners(XButton.class, BTN_FAQ, new FaqClickListener(this, this.m_xContext));
        addListners(XButton.class, BTN_OK, new FinishClickListener(this, this.addin));
        addListners(XButton.class, BTN_CANCEL, new CancelClickListener(this));
        addListners(XButton.class, BTN_CC, new CCClickListener(this));
        addListners(XButton.class, BTN_CC0, new CC0ClickListener(this));
        addListners(XButton.class, BTN_PUBLICDOMAIN, new PDClickListener(this));

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

        setInfoImage(new Rectangle(55, 58, 9, 10), RDO_ALLOW_COMERCIAL_YES,_("license.help.commercial"),1);
//                "The licensor permits others to copy, distribute,"
//                + "\ndisplay and perform the work,"
//                + "\nas well as make derivative works based on it.", 1);

        setInfoImage(new Rectangle(55, 73, 9, 10), RDO_ALLOW_COMERCIAL_NO,_("license.help.noncommercial"),1);
//                "The licensor permits others to copy, "
//                + "\ndistribute, display, and perform the work "
//                + "\nfor non-commercial purposes only", 1);

        setInfoImage(new Rectangle(55, 103, 9, 10), RDO_ALLOW_MODIFICATIONS_YES,_("license.help.derivatives"),1);
//                "The licensor permits others to copy, "
//                + "\ndistribute, display and perform the work, "
//                + "\nas well as make derivative works based on it.", 1);

        setInfoImage(new Rectangle(125, 118, 9, 10), RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE,
                "The licensor permits others to distribute derivative works "
                + "\nonly under the same license or one compatible "
                + "\nwith the one that governs the licensor's work.", 1);

        setInfoImage(new Rectangle(55, 133, 9, 10), RDO_ALLOW_MODIFICATIONS_NO, _("char.nd_description"),1);
//                "The licensor permits others to copy, "
//                + "\ndistribute and transmit only unaltered copies of the "
//                + "\nwork — not derivative works based on it.", 1);

        setInfoImage(new Rectangle(155, 148, 9, 10), CMB_JURISDICTION,
                "Use the option \"Unported\" if you desire a license using "
                + "\nlanguage and terminology from international treaties. ", 1);

        trritories = ReadFile.read(getClass().getResourceAsStream(
                "/org/creativecommons/license/rdf/territory")).split("\\n");
        cmbTList = (XListBox) UnoRuntime.queryInterface(
                XListBox.class, xControlCont.getControl(CMB_TERRITORY));

        cmbTList.addItem("", (short) 0);
        cmbTList.addItems(trritories, (short) 1);
        cmbTList.selectItemPos((short) 0, true);
        cmbTList.makeVisible((short) 0);
        cmbTList.addItemListener(new TerritorySelectListener(this));
        // execute the dialog
        this.xDialog = (XDialog) UnoRuntime.queryInterface(XDialog.class, dialog);
        this.xDialog.execute();

        // dispose the dialog
        XComponent xComponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, dialog);
        xComponent.dispose();
    }

    private void createCCLicenseTab() throws Exception {

        ///////////////////////////////////// create the current license information
        Object lblSelectedLicenseLabel = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblSelectedLicenseLabel, LBL_SELECTED_LICENSE_LABEL,
                "Selected License:", new Rectangle(10, 20, 50, 15), 1);

        Object lblSelectedLicense = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsSelectedLicense = createAWTControl(lblSelectedLicense, LBL_SELECTED_LICENSE,
                null, new Rectangle(60, 20, 145, 30), 1);
        xpsSelectedLicense.setPropertyValue("MultiLine", true);

        /////////////////////////////////////Allow commercial uses of your work?
        Object lblAllowCommercialUse = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblAllowCommercialUse, LBL_ALLOW_COMERCIAL_USE,
                _("api.commercial"), new Rectangle(15, 45, 100, 12), 1);//"Allow commercial uses of your work?"

        Object radioCommercialYes = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioCommercialYes = createAWTControl(
                radioCommercialYes, RDO_ALLOW_COMERCIAL_YES,
                _("util.Yes"), new Rectangle(20, 60, 30, 12), 1);
        xpsRadioCommercialYes.setPropertyValue("State", new Short((short) 1));

        Object radioCommercialNo = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioCommercialNo = createAWTControl(
                radioCommercialNo, RDO_ALLOW_COMERCIAL_NO,
                _("util.No"), new Rectangle(20, 75, 30, 12), 1);
        xpsRadioCommercialNo.setPropertyValue("State", new Short((short) 0));

        ///////////////////////////////////Allow modifications of your work?

        Object lblAllowModifications = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        createAWTControl(lblAllowModifications, LBL_ALLOW_MODIFICATIONS,
                _("api.derivatives"), new Rectangle(15, 90, 100, 12), 1);//"Allow modifications of your work?"
        Object radioModificationsYes = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioModificationYes = createAWTControl(
                radioModificationsYes, RDO_ALLOW_MODIFICATIONS_YES,
                _("util.Yes"), new Rectangle(20, 105, 30, 12), 1);
        xpsRadioModificationYes.setPropertyValue("State", new Short((short) 1));

        Object radioModificationsShareAlike = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioModificationsShareAlike = createAWTControl(
                radioModificationsShareAlike, RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE,
                _("Yes, as long as others share alike"), new Rectangle(20, 120, 100, 12), 1);
        xpsRadioModificationsShareAlike.setPropertyValue("State", new Short((short) 0));

        Object radioModificationsNo = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlRadioButtonModel");
        XPropertySet xpsRadioModificationsNo = createAWTControl(
                radioModificationsNo, RDO_ALLOW_MODIFICATIONS_NO,
                _("util.No"), new Rectangle(20, 135, 30, 12), 1);
        xpsRadioModificationsNo.setPropertyValue("State", new Short((short) 0));

        // create the jurisdiction drop-down list
        Object lblJurisdictionList = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsLblJurisdictionList =
                createAWTControl(lblJurisdictionList, LBL_JURISDICTION_LIST,
                _("license.jurisdiction_question"), new Rectangle(15, 150, 75, 15), 1);//"Jurisdiction of your license"

        Object cmbJurisdictionList = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlListBoxModel");
        XPropertySet xPSetList = createAWTControl(cmbJurisdictionList, CMB_JURISDICTION,
                null, new Rectangle(90, 150, 60, 12), 1);
        xPSetList.setPropertyValue("Dropdown", new Boolean("true"));
        xPSetList.setPropertyValue("MultiSelection", new Boolean("false"));

        Object hrLine = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedLineModel");
        XPropertySet xpshrLine=createAWTControl(hrLine, "hrLine",
                null, new Rectangle(5, 165, 200, 5), 1);
        xpshrLine.setPropertyValue("Orientation", 0);

        Object lblInstructions = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsLblInstructions=createAWTControl(lblInstructions, LBL_INSTRUCTIONS_CC,
                "With a Creative Commons license, you keep your copyright but allow " +
                "people to copy and distribute your work provided they give you credit  " +
                "— and only on the conditions you specify here. " +
                "\n\nIf you want to offer your work with no conditions or you" +
                " want to certify a work as public domain, choose one of the " +
                "public domain tools.(CC0 & Public Domain)", new Rectangle(10, 175, 195, 80), 1);
        xpsLblInstructions.setPropertyValue("MultiLine", true);
        FontDescriptor fontDes = (FontDescriptor) xpsLblInstructions.getPropertyValue("FontDescriptor");
        fontDes.Weight = 75;
        xpsLblInstructions.setPropertyValue("FontDescriptor", fontDes);
    }

    private void crateCC0LicenseTab() throws Exception {

        ///////////////////////////////////Public domian waring
        Object lblWarning = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsLblWarning = createAWTControl(lblWarning, LBL_INSTRUCTIONS_CC0,
//                "Are you certain you wish to waive all rights to your work? "
//                + "Once these rights are waived, you cannot reclaim them."
                _("license.zero.confirm_waiver_1")
                + "\n\nIn particular, if you are an artist or author who depends "
                + "upon copyright for your income, "
                + "Creative Commons does not recommend that you use this tool."
                + "\n\nIf you don't own the rights to this work, then do not use CC0. "
                + "\nIf you believe that nobody owns rights to the work, then the "
                + "Public Domain Certification may be what you're looking for.",
                new Rectangle(10, 25, 195, 80), 2);
        xpsLblWarning.setPropertyValue("MultiLine", true);
        FontDescriptor fontDes = (FontDescriptor) xpsLblWarning.getPropertyValue("FontDescriptor");
        fontDes.Weight = 150;
        xpsLblWarning.setPropertyValue("FontDescriptor", fontDes);

        Object chkWaive = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlCheckBoxModel");
        XPropertySet xpsChkWaive = createAWTControl(chkWaive, CHK_WAIVE,
//                "I hereby waive all copyright and related or neighboring rights "
//                + "together with all associated claims and causes of action with "
//                + "respect to this work to the extent possible under the law.",
                _("license.zero.confirm_waiver"),
                new Rectangle(10, 110, 190, 30), 2);
        xpsChkWaive.setPropertyValue("MultiLine", true);

        String cc0LegalCode = ReadFile.read(getClass().getResourceAsStream(
                "/org/creativecommons/license/legalcodes/cc0"));
        Object txtDeed = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlEditModel");
        XPropertySet xpsTxtDeed = createAWTControl(txtDeed, TXT_LEGAL_CODE_CC0, null,
                new Rectangle(10, 145, 190, 60), 2);
        xpsTxtDeed.setPropertyValue("MultiLine", true);
        xpsTxtDeed.setPropertyValue("ReadOnly", true);
        xpsTxtDeed.setPropertyValue("VScroll", true);
        xpsTxtDeed.setPropertyValue("Text", cc0LegalCode);

        Object chkYes = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlCheckBoxModel");
        XPropertySet xpsChkYes = createAWTControl(chkYes, CHK_YES_CC0,
                "I have read and understand the terms and intended legal effect of CC0, "
                + "and hereby voluntarily elect to apply it to this work.",
                new Rectangle(10, 210, 190, 20), 2);
        xpsChkYes.setPropertyValue("MultiLine", true);

        Object lblJurisdictionList = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsLblJurisdictionList =
                createAWTControl(lblJurisdictionList, "lbltrritory",
                "Territory", new Rectangle(10, 230, 45, 15), 2);

        Object cmbTerritoryList = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlListBoxModel");
        XPropertySet xPSetList = createAWTControl(cmbTerritoryList, CMB_TERRITORY,
                null, new Rectangle(55, 230, 120, 12), 2);
        xPSetList.setPropertyValue("Dropdown", new Boolean("true"));
        xPSetList.setPropertyValue("MultiSelection", new Boolean("false"));
    }

    private void cratePDLicenseTab() throws Exception {

        ///////////////////////////////////Public domian waring
        Object lblWarning = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsLblWarning = createAWTControl(lblWarning, "pdwarning",
                "You have selected the Public Domain Certification. "
                + "The Public Domain Certification should only be used to certify "
                + "a work that is already in the public domain. "
                + "\n\nCreative Commons does not recommend you use the "
                + "Public Domain Certification for dedicating a work still "
                + "protected by copyright to the public domain. "
                + "To dedicate a work to the public domain, consider using CC0. "
                + "\n\nPlease note that if you use the Public Domain Certification to "
                + "dedicate a work to the public domain, it may not be valid outside "
                + "of the United States.",
                new Rectangle(10, 25, 190, 100), 3);
        xpsLblWarning.setPropertyValue("MultiLine", true);
        FontDescriptor fontDes = (FontDescriptor) xpsLblWarning.getPropertyValue("FontDescriptor");
        fontDes.Weight = 150;
        xpsLblWarning.setPropertyValue("FontDescriptor", fontDes);
        String pdLegalCode = ReadFile.read(getClass().getResourceAsStream(
                "/org/creativecommons/license/legalcodes/pd"));

        Object txtDeed = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlEditModel");
        XPropertySet xpsTxtDeed = createAWTControl(txtDeed, TXT_LEGAL_CODE_PD, null,
                new Rectangle(10, 130, 190, 75), 3);
        xpsTxtDeed.setPropertyValue("MultiLine", true);
        xpsTxtDeed.setPropertyValue("ReadOnly", true);
        xpsTxtDeed.setPropertyValue("VScroll", true);
        xpsTxtDeed.setPropertyValue("Text", pdLegalCode);

        Object chkYes = xMultiServiceFactory.createInstance(
                "com.sun.star.awt.UnoControlCheckBoxModel");
        XPropertySet xpsChkYes = createAWTControl(chkYes, CHK_YES_PD,
//                "I have read and understand the terms and intended legal effect of "
//                + "this tool, and hereby voluntarily elect to apply it to this work.",
                _("license.zero.confirm_reading"),
                new Rectangle(10, 210, 190, 30), 3);
        xpsChkYes.setPropertyValue("MultiLine", true);

    }

    private void setInfoImage(Rectangle rect, String pos, String title, int step) {
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
                    replaceFirst("ccooo.jar", "images/information.png");
            xGraphic = getGraphic(file);

            XPropertySet xpsImageControl = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, oICModel);
            xpsImageControl.setPropertyValue("Border", (short) 0);

            xpsImageControl.setPropertyValue("Height", new Integer(rect.height));
            xpsImageControl.setPropertyValue("Name", "ImageControl" + pos);
            xpsImageControl.setPropertyValue("PositionX", new Integer(rect.x));
            xpsImageControl.setPropertyValue("PositionY", new Integer(rect.y));
            xpsImageControl.setPropertyValue("Width", new Integer(rect.width));
            xpsImageControl.setPropertyValue("Step", step);

            getNameContainer().insertByName("ImageControl" + pos, oICModel);
            xpsImageControl.setPropertyValue("HelpText", title);
            xpsImageControl.setPropertyValue("Graphic", xGraphic);

        } catch (Exception ex) {
            Logger.getLogger(ChooserDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addListners(Class type, String controlName, XEventListener listener) {
        if (type == XButton.class) {
            Object objectButton = xControlCont.getControl(controlName);
            XButton xFinishButton = (XButton) UnoRuntime.queryInterface(XButton.class, objectButton);
            xFinishButton.addActionListener((XActionListener) listener);
        } else if (type == XRadioButton.class) {
            ((XRadioButton) UnoRuntime.queryInterface(XRadioButton.class,
                    xControlCont.getControl(controlName))).addItemListener((XItemListener) listener);
        } else if (type == XCheckBox.class) {
            ((XCheckBox) UnoRuntime.queryInterface(XCheckBox.class,
                    xControlCont.getControl(controlName))).addItemListener((XItemListener) listener);
        }
    }

    private XPropertySet createAWTControl(Object objControl, String ctrlName,
            String ctrlCaption, Rectangle posSize, int step) throws Exception {

        XPropertySet xpsProperties = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, objControl);

        xpsProperties.setPropertyValue("PositionX", new Integer(posSize.x));
        xpsProperties.setPropertyValue("PositionY", new Integer(posSize.y));
        xpsProperties.setPropertyValue("Width", new Integer(posSize.width));
        xpsProperties.setPropertyValue("Height", new Integer(posSize.height));
        xpsProperties.setPropertyValue("Name", ctrlName);
        xpsProperties.setPropertyValue("Step", step);
        if (ctrlCaption != null) {
            xpsProperties.setPropertyValue("Label", ctrlCaption);
        }

        if ((getNameContainer() != null) && (!getNameContainer().hasByName(ctrlName))) {
            getNameContainer().insertByName(ctrlName, objControl);
        }
        return xpsProperties;
    }

    private XGraphic getGraphic(String _sImageUrl) {

        XGraphic xGraphic = null;
        try {
            // create a GraphicProvider at the global service manager...
            Object oGraphicProvider = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.graphic.GraphicProvider", m_xContext);
            XGraphicProvider xGraphicProvider = (XGraphicProvider) UnoRuntime.queryInterface(XGraphicProvider.class, oGraphicProvider);
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

    private void setCRadioButtonValue(String controlName, Boolean b) {

        try {
            XPropertySet xPSetList = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(controlName));
            xPSetList.setPropertyValue("State", (b ? new Short((short) 1) : new Short((short) 0))); // b.booleanValue());
        }  catch (Exception ex) {
            Logger.getLogger(ChooserDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Boolean getRadioButtonValue(String rdoName) {
        try {
            XPropertySet xPSetList = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(rdoName));
            return (((Short) xPSetList.getPropertyValue("State")).intValue() == 1);

        }  catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    } // getRadioButtonValue

    public void setLicenseType(int type) {
        String[] btnArray = new String[]{BTN_CC, BTN_CC0, BTN_PUBLICDOMAIN};
        try {
            for (int i = 0; i < btnArray.length; i++) {
                XPropertySet xPSetLicenseButton = ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        getNameContainer().getByName(btnArray[i])));
                FontDescriptor fontDes = (FontDescriptor) xPSetLicenseButton.getPropertyValue("FontDescriptor");
                if (i + 1 == type) {
                    fontDes.Weight = 150;
                    xPSetLicenseButton.setPropertyValue("State", (short) 1);
                } else {
                    fontDes.Weight = 50;
                    xPSetLicenseButton.setPropertyValue("State", (short) 0);
                }
                xPSetLicenseButton.setPropertyValue("FontDescriptor", fontDes);
            }
            if (type != 1) {
                xPSetFinishButton.setPropertyValue("Enabled", false);
            } else {
                xPSetFinishButton.setPropertyValue("Enabled", true);
            }
            ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    getNameContainer().getByName(CHK_YES_CC0))).setPropertyValue("Enabled", Boolean.FALSE);
            ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    getNameContainer().getByName(TXT_LEGAL_CODE_CC0))).setPropertyValue("Enabled", Boolean.FALSE);
            ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    getNameContainer().getByName(CHK_WAIVE))).setPropertyValue("State", (short) 0);
            ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    getNameContainer().getByName(CHK_YES_CC0))).setPropertyValue("State", (short) 0);
            ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    getNameContainer().getByName(CMB_TERRITORY))).setPropertyValue("Enabled", Boolean.FALSE);
            cmbTList.selectItemPos((short) 0, true);
            ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    getNameContainer().getByName(CHK_YES_PD))).setPropertyValue("State", (short) 0);

            xPSetDialog.setPropertyValue("Step", type);
        }  catch (Exception ex) {
            Logger.getLogger(ChooserDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public IJurisdiction getSelectedJurisdiction() {
        return selectedJurisdiction;
    }

    public void setSelectedJurisdiction(IJurisdiction selected) {
        this.selectedJurisdiction = selected;
    }

    public void setSelectedTerritory(int selecion) {
        if(selecion>0){
            selectedTerritory=trritories[selecion-1];
            System.out.println(selectedTerritory);
        }else{
            selectedTerritory=null;
        }
    }

    public List getJurisdictionList() {
        return jurisdictionList;
    }

    public void setJurisdictionList(List<Jurisdiction> jurisdictionList) {
        this.jurisdictionList = jurisdictionList;
    }

    public License getSelectedLicense() {
        try {
            // retrieve the Document for the issued license
            Chooser licenseChooser = new Chooser();
            int type = (Integer) xPSetDialog.getPropertyValue("Step");
            if (type == 2) {
                return licenseChooser.selectPDTools(selectedTerritory, 2);
            } else if (type == 3) {
                return licenseChooser.selectPDTools(null, 3);
            } else {
                return licenseChooser.selectLicense(this.getRadioButtonValue(
                        ChooserDialog.RDO_ALLOW_MODIFICATIONS_YES).booleanValue()
                        || this.getRadioButtonValue(
                        ChooserDialog.RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE).booleanValue(),
                        this.getRadioButtonValue(
                        ChooserDialog.RDO_ALLOW_COMERCIAL_NO).booleanValue(),
                        this.getRadioButtonValue(
                        ChooserDialog.RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE).booleanValue(),
                        this.getSelectedJurisdiction());
            }
        } catch (Exception ex) {
            Logger.getLogger(ChooserDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    } // getSelectedLicense

    public void setSelectedLicense(License selected) {
        // update the user interface to match this selection
        this.setCRadioButtonValue(ChooserDialog.RDO_ALLOW_COMERCIAL_YES,
                !selected.prohibitCommercial());
        this.setCRadioButtonValue(ChooserDialog.RDO_ALLOW_COMERCIAL_NO,
                selected.prohibitCommercial());
        this.setCRadioButtonValue(ChooserDialog.RDO_ALLOW_MODIFICATIONS_YES,
                selected.allowRemix() && !selected.requireShareAlike());
        this.setCRadioButtonValue(ChooserDialog.RDO_ALLOW_MODIFICATIONS_SHARE_ALIKE,
                selected.requireShareAlike());
        this.setCRadioButtonValue(ChooserDialog.RDO_ALLOW_MODIFICATIONS_NO,
                !selected.allowRemix());

        this.setSelectedJurisdiction(selected.getJurisdiction());

        this.updateSelectedLicense();

    } // setSelectedLicense

    public void updateSelectedLicense() {
        try {
            XPropertySet xpsSelectedLicense = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(LBL_SELECTED_LICENSE));
            xpsSelectedLicense.setPropertyValue("Label", this.getSelectedLicense().getName());
        } catch (Exception ex) {
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

