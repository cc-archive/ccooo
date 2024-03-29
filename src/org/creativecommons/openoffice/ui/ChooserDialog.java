/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */

package org.creativecommons.openoffice.ui;

import com.sun.star.awt.XButton;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.Iterator;
import java.util.List;
import org.creativecommons.license.Chooser;
import org.creativecommons.license.IJurisdiction;
import org.creativecommons.license.Jurisdiction;
import org.creativecommons.license.License;
import org.creativecommons.license.Store;
import org.creativecommons.openoffice.*;

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
public class ChooserDialog {
    private XMultiServiceFactory xMultiServiceFactory = null;
    protected XComponentContext m_xContext = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    private XNameContainer xNameCont = null;
    private XControlContainer xControlCont = null;
    
    private XDialog xDialog = null;
    private CcOOoAddin addin = null;
    
    private List jurisdictionList = null;
    private IJurisdiction selectedJurisdiction = null;
    
    private boolean cancelled = true;
    
    // TODO put these labels in a properties file
    public static final String BTN_OK = "finishbt";
    public static final String finishButtonLabel = "OK";
    public static final String BTN_CANCEL = "cancelbt";
    public static final String cancelButtonLabel = "Cancel";
    
    public static final String CHK_ALLOW_REMIX = "chkAllowRemix";
    public static final String CHK_PROHIBIT_COMMERCIAL = "chkProhibitCommercial";
    public static final String CHK_REQUIRE_SHAREALIKE = "chkRequireShareAlike";
    public static final String CMB_JURISDICTION = "cmbJurisdiction";
    
    public static final String LBL_SELECTED_LICENSE = "lblSelectedLicense";
    public static final String LBL_SELECTED_LICENSE_LABEL = "lblSelectedLicense_lbl";
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
        Object dlgLicenseSelector = xMultiComponentFactory.createInstanceWithContext
                ("com.sun.star.awt.UnoControlDialogModel", m_xContext);
        XMultiServiceFactory msfLicenseSelector = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dlgLicenseSelector);
        
        XPropertySet xPSetDialog = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, dlgLicenseSelector);
        xPSetDialog.setPropertyValue("PositionX", new Integer(100));
        xPSetDialog.setPropertyValue("PositionY", new Integer(100));
        xPSetDialog.setPropertyValue("Width", new Integer(210));//470
        xPSetDialog.setPropertyValue("Height", new Integer(125));//360
        xPSetDialog.setPropertyValue("Title", new String("Select a License"));
        xPSetDialog.setPropertyValue("Name", new String("cc"));
        xPSetDialog.setPropertyValue("Step", (short)1 );
        
        // get the name container for the dialog for inserting other elements
        this.xNameCont = (XNameContainer)UnoRuntime.queryInterface(
                XNameContainer.class, dlgLicenseSelector);
        
        // get the service manager from the dialog model
        this.xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dlgLicenseSelector);
        
        // create the current license information
        Object lblSelectedLicenseLabel = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsSelectedLicenseLbl = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, lblSelectedLicenseLabel);
        
        xpsSelectedLicenseLbl.setPropertyValue("PositionX", new Integer(10));
        xpsSelectedLicenseLbl.setPropertyValue("PositionY", new Integer(10));
        xpsSelectedLicenseLbl.setPropertyValue("Width", new Integer(50));
        xpsSelectedLicenseLbl.setPropertyValue("Height", new Integer(15));
        xpsSelectedLicenseLbl.setPropertyValue("Name", LBL_SELECTED_LICENSE_LABEL);
        xpsSelectedLicenseLbl.setPropertyValue("Label", "Selected License:");
        
        getNameContainer().insertByName(LBL_SELECTED_LICENSE_LABEL, lblSelectedLicenseLabel);
        
        Object lblSelectedLicense = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsSelectedLicense = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, lblSelectedLicense);
        
        xpsSelectedLicense.setPropertyValue("PositionX", new Integer(60));
        xpsSelectedLicense.setPropertyValue("PositionY", new Integer(10));
        xpsSelectedLicense.setPropertyValue("Width", new Integer(200));
        xpsSelectedLicense.setPropertyValue("Height", new Integer(15));
        xpsSelectedLicense.setPropertyValue("Name", LBL_SELECTED_LICENSE);
        // xpsSelectedLicense.setPropertyValue("Label", current_license);
        
        getNameContainer().insertByName(LBL_SELECTED_LICENSE, lblSelectedLicense);
        
        // create the boolean selection fields
        Object chkAllowRemixing = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlCheckBoxModel");
        XPropertySet xpsAllowRemixing = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, chkAllowRemixing);
        
        xpsAllowRemixing.setPropertyValue("PositionX", new Integer(15));
        xpsAllowRemixing.setPropertyValue("PositionY", new Integer(30));
        xpsAllowRemixing.setPropertyValue("Width", new Integer(250));
        xpsAllowRemixing.setPropertyValue("Height", new Integer(12));
        xpsAllowRemixing.setPropertyValue("Name", CHK_ALLOW_REMIX);
        xpsAllowRemixing.setPropertyValue("Label", "Allow Remixing?");
        
        xpsAllowRemixing.setPropertyValue("TriState", Boolean.FALSE);
        xpsAllowRemixing.setPropertyValue("State", new Short((short) 1));
        
        getNameContainer().insertByName(CHK_ALLOW_REMIX, chkAllowRemixing);
        
        Object chkProhibitCommercial = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlCheckBoxModel");
        XPropertySet xpsProhibitCommercial = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, chkProhibitCommercial);
        
        xpsProhibitCommercial.setPropertyValue("PositionX", new Integer(15));
        xpsProhibitCommercial.setPropertyValue("PositionY", new Integer(40));
        xpsProhibitCommercial.setPropertyValue("Width", new Integer(250));
        xpsProhibitCommercial.setPropertyValue("Height", new Integer(12));
        xpsProhibitCommercial.setPropertyValue("Name", CHK_PROHIBIT_COMMERCIAL);
        xpsProhibitCommercial.setPropertyValue("Label", "Prohibit Commercial Use?");
        
        xpsProhibitCommercial.setPropertyValue("TriState", Boolean.FALSE);
        xpsProhibitCommercial.setPropertyValue("State", new Short((short) 0));
        
        getNameContainer().insertByName(CHK_PROHIBIT_COMMERCIAL, chkProhibitCommercial);
        
        Object chkRequireShareAlike = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlCheckBoxModel");
        XPropertySet xpsRequireShareAlike = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, chkRequireShareAlike);
        
        xpsRequireShareAlike.setPropertyValue("PositionX", new Integer(15));
        xpsRequireShareAlike.setPropertyValue("PositionY", new Integer(50));
        xpsRequireShareAlike.setPropertyValue("Width", new Integer(250));
        xpsRequireShareAlike.setPropertyValue("Height", new Integer(12));
        xpsRequireShareAlike.setPropertyValue("Name", CHK_REQUIRE_SHAREALIKE);
        xpsRequireShareAlike.setPropertyValue("Label", "Require Share-Alike?");
        
        xpsRequireShareAlike.setPropertyValue("TriState", Boolean.FALSE);
        xpsRequireShareAlike.setPropertyValue("State", new Short((short) 0));
        
        getNameContainer().insertByName(CHK_REQUIRE_SHAREALIKE, chkRequireShareAlike);
        
        // create the jurisdiction drop-down list
        Object lblJurisdictionList = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedTextModel" );
        XPropertySet xpsLblJurisdictionList = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, lblJurisdictionList);
        
        xpsLblJurisdictionList.setPropertyValue("PositionX", new Integer(15));
        xpsLblJurisdictionList.setPropertyValue("PositionY", new Integer(72));
        xpsLblJurisdictionList.setPropertyValue("Width", new Integer(30));
        xpsLblJurisdictionList.setPropertyValue("Height", new Integer(15));
        xpsLblJurisdictionList.setPropertyValue("Name", LBL_JURISDICTION_LIST);
        xpsLblJurisdictionList.setPropertyValue("Label", "Jurisdiction");
        
        getNameContainer().insertByName(LBL_JURISDICTION_LIST, xpsLblJurisdictionList);
        
        Object cmbJurisdictionList = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlListBoxModel" );
        
        XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, cmbJurisdictionList);
        xPSetList.setPropertyValue("PositionX", new Integer(45));
        xPSetList.setPropertyValue("PositionY", new Integer(70));
        xPSetList.setPropertyValue("Width", new Integer(80));
        xPSetList.setPropertyValue("Height", new Integer(12));
        xPSetList.setPropertyValue("Name", CMB_JURISDICTION);
        xPSetList.setPropertyValue("Dropdown", new Boolean("true"));
        xPSetList.setPropertyValue("MultiSelection", new Boolean("false"));
        xPSetList.setPropertyValue("Step", new Short((short)1));
        
        getNameContainer().insertByName(CMB_JURISDICTION, cmbJurisdictionList);
        
        // create the button model - OK and set the properties
        Object finishButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetFinishButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, finishButton);
        xPSetFinishButton.setPropertyValue("PositionX", new Integer(115));
        xPSetFinishButton.setPropertyValue("PositionY", new Integer(100));
        xPSetFinishButton.setPropertyValue("Width", new Integer(40));
        xPSetFinishButton.setPropertyValue("Height", new Integer(14));
        xPSetFinishButton.setPropertyValue("Name", BTN_OK);
        xPSetFinishButton.setPropertyValue("DefaultButton", new Boolean("true"));
        xPSetFinishButton.setPropertyValue("Label", finishButtonLabel);
        
        getNameContainer().insertByName(BTN_OK, finishButton);
        
        // create the button model - Cancel and set the properties
        Object cancelButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetCancelButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, cancelButton);
        xPSetCancelButton.setPropertyValue("PositionX", new Integer(160));
        xPSetCancelButton.setPropertyValue("PositionY", new Integer(100));
        xPSetCancelButton.setPropertyValue("Width", new Integer(40));
        xPSetCancelButton.setPropertyValue("Height", new Integer(14));
        xPSetCancelButton.setPropertyValue("Name", BTN_CANCEL);
        xPSetCancelButton.setPropertyValue("Label", cancelButtonLabel);
        
        getNameContainer().insertByName(BTN_CANCEL, cancelButton);
        
        
        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialog", m_xContext); //esse
        XControl xControl = (XControl)UnoRuntime.queryInterface(XControl.class, dialog );
        XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(XControlModel.class, dlgLicenseSelector);
        xControl.setModel(xControlModel);
        
        // add an action listener to the Previous button control
        xControlCont = (XControlContainer)UnoRuntime.queryInterface(
                XControlContainer.class, dialog);
        
        XListBox cmbJList = (XListBox)UnoRuntime.queryInterface(XListBox.class, xControlCont.getControl(CMB_JURISDICTION));
        this.setJurisdictionList(Store.get().jurisdictions());
        
        Iterator it;
        it = this.getJurisdictionList().iterator();
        short count = 0;
        
        // add Unported, which isn't actually a jurisdiction'
        cmbJList.addItem("Unported", count++);
        
        while (it.hasNext()) {
            Jurisdiction j = (Jurisdiction)it.next();
            cmbJList.addItem(j.getTitle(), count++);
            
        }
        
        // add a bogus place-holder for Unported in the JurisdictionList to
        // ensure indices match up when determining the item selectedJurisdiction
        this.getJurisdictionList().add(0, null);
        
        // Pre-select Unported
        cmbJList.selectItemPos((short)0, true);
        cmbJList.makeVisible((short)0);
        
        // listen for license selection changes
        ((XCheckBox)UnoRuntime.queryInterface(XCheckBox.class, xControlCont.getControl(CHK_ALLOW_REMIX))).addItemListener(
                new AllowRemixListener(this));
        ((XCheckBox)UnoRuntime.queryInterface(XCheckBox.class, xControlCont.getControl(CHK_PROHIBIT_COMMERCIAL))).addItemListener(
                new UpdateLicenseListener(this));
        ((XCheckBox)UnoRuntime.queryInterface(XCheckBox.class, xControlCont.getControl(CHK_REQUIRE_SHAREALIKE))).addItemListener(
                new UpdateLicenseListener(this));
        
        cmbJList.addItemListener(new JurisdictionSelectListener(this));
        
        // add an action listener to the Finish button control
        Object objectButton3 = xControlCont.getControl(BTN_OK);
        XButton xFinishButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton3);
        xFinishButton.addActionListener(new FinishClickListener(this, this.addin));
        
        // add an action listener to the Cancel button control
        Object objectButton4 = xControlCont.getControl(BTN_CANCEL);
        XButton xCancelButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton4);
        xCancelButton.addActionListener(new CancelClickListener(this));
        
        if (this.addin.getProgramWrapper().getDocumentLicense() != null) {
            this.setSelectedLicense( this.addin.getProgramWrapper().getDocumentLicense() );
        } else {
            this.setSelectedLicense(new License("http://creativecommons.org/licenses/by/3.0/"));
        }
        
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
        
    }
    
    protected void setCheckboxValue(String controlName, Boolean b) {
        
        try {
            XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(controlName));
            
            xPSetList.setPropertyValue("State", (b?new Short((short)1):new Short((short)0)) ); // b.booleanValue());
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
    
    protected Boolean getCheckboxValue(String chkName) {
        try {
            
            XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class,
                    this.getNameContainer().getByName(chkName));
            
            return (((Short) xPSetList.getPropertyValue("State")).intValue()  == 1);
            
        }  catch (UnknownPropertyException ex) {
            ex.printStackTrace();
            return null;
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
            return null;
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
            return null;
        }
        
    } // getCheckboxValue
    
    public IJurisdiction getSelectedJurisdiction() {
        return selectedJurisdiction;
    }
    
    public void setSelectedJurisdiction(IJurisdiction selected) {
        this.selectedJurisdiction = selected;
    }
    
    public List getJurisdictionList() {
        return jurisdictionList;
    }
    
    protected void setJurisdictionList(List jurisdictionList) {
        this.jurisdictionList = jurisdictionList;
    }
    
    public License getSelectedLicense() {
        
        // retrieve the Document for the issued license
        Chooser licenseChooser = new Chooser();
        
        return licenseChooser.selectLicense(
                this.getCheckboxValue(this.CHK_ALLOW_REMIX).booleanValue(),
                this.getCheckboxValue(this.CHK_PROHIBIT_COMMERCIAL).booleanValue(),
                this.getCheckboxValue(this.CHK_REQUIRE_SHAREALIKE).booleanValue(),
                this.getSelectedJurisdiction());
        
    } // getSelectedLicense
    
    public void setSelectedLicense(License selected) {
        // update the user interface to match this selection
        
        this.setCheckboxValue(this.CHK_ALLOW_REMIX, selected.allowRemix());
        this.setCheckboxValue(this.CHK_PROHIBIT_COMMERCIAL, selected.prohibitCommercial());
        this.setCheckboxValue(this.CHK_REQUIRE_SHAREALIKE, selected.requireShareAlike());
        
        this.setSelectedJurisdiction(selected.getJurisdiction());
        
        this.updateSelectedLicense();
        
    } // setSelectedLicense
    
    void updateSelectedLicense() {
        try {
            
            XPropertySet xpsSelectedLicense = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
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

