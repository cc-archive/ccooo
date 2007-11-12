/*
 * AddInUI.java
 *
 * copyright 2007, Creative Commons
 * licensed under the MIT License; see docs/LICENSE for details.
 *
 * Created on Jun 20, 2007
 *
 */

package org.creativecommons.openoffice;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import org.creativecommons.license.Chooser;
import org.creativecommons.license.License;

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
public class AddInUI {
    private XMultiServiceFactory xMultiServiceFactory = null;
    protected XComponentContext m_xContext = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    private XNameContainer xNameCont = null;
    private XControlContainer xControlCont = null;
    
    protected Vector namesList = null;
    protected XDialog xDialog = null;
    protected Map answers = null;
    protected String currentId = "";
    protected CcOOoAddin addin = null;
    
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
     * Creates a new instance of AddInUI
     */
    public AddInUI(CcOOoAddin addin, XComponentContext m_xContext) {
        namesList = new Vector();
        this.addin = addin;
        this.m_xContext = m_xContext;
    }
    
    /**
     * Method for creating a dialog at runtime
     *
     */
    public void createDialog() throws com.sun.star.uno.Exception {
        
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
        xPSetDialog.setPropertyValue("Width", new Integer(140));//470
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

        xNameCont.insertByName(LBL_SELECTED_LICENSE_LABEL, lblSelectedLicenseLabel);

        Object lblSelectedLicense = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
        XPropertySet xpsSelectedLicense = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, lblSelectedLicense);
        
        xpsSelectedLicense.setPropertyValue("PositionX", new Integer(60));
        xpsSelectedLicense.setPropertyValue("PositionY", new Integer(10));
        xpsSelectedLicense.setPropertyValue("Width", new Integer(200));
        xpsSelectedLicense.setPropertyValue("Height", new Integer(15));
        xpsSelectedLicense.setPropertyValue("Name", LBL_SELECTED_LICENSE);
        xpsSelectedLicense.setPropertyValue("Label", "(none)");

        xNameCont.insertByName(LBL_SELECTED_LICENSE, lblSelectedLicense);

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

        xNameCont.insertByName(CHK_ALLOW_REMIX, chkAllowRemixing);

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

        xNameCont.insertByName(CHK_PROHIBIT_COMMERCIAL, chkProhibitCommercial);
        
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

        xNameCont.insertByName(CHK_REQUIRE_SHAREALIKE, chkRequireShareAlike);

        // create the jurisdiction drop-down list
        Object lblJurisdictionList = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedTextModel" );
        XPropertySet xpsLblJurisdictionList = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, lblJurisdictionList);
        
        xpsLblJurisdictionList.setPropertyValue("PositionX", new Integer(15));
        xpsLblJurisdictionList.setPropertyValue("PositionY", new Integer(72));
        xpsLblJurisdictionList.setPropertyValue("Width", new Integer(30));
        xpsLblJurisdictionList.setPropertyValue("Height", new Integer(15));
        xpsLblJurisdictionList.setPropertyValue("Name", LBL_JURISDICTION_LIST);
        xpsLblJurisdictionList.setPropertyValue("Label", "Jurisdiction");

        xNameCont.insertByName(LBL_JURISDICTION_LIST, xpsLblJurisdictionList);
        
        Object cmbJurisdictionList = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlComboBoxModel" );
        
        XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, cmbJurisdictionList);
        xPSetList.setPropertyValue("PositionX", new Integer(45));
        xPSetList.setPropertyValue("PositionY", new Integer(70));
        xPSetList.setPropertyValue("Width", new Integer(80));
        xPSetList.setPropertyValue("Height", new Integer(12));
        xPSetList.setPropertyValue("Name", CMB_JURISDICTION);
        xPSetList.setPropertyValue("Dropdown", new Boolean("true"));
        xPSetList.setPropertyValue("Step", new Short((short)1));

        xNameCont.insertByName(CMB_JURISDICTION, cmbJurisdictionList);
        
        // create the button model - OK and set the properties
        Object finishButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetFinishButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, finishButton);
        xPSetFinishButton.setPropertyValue("PositionX", new Integer(45));
        xPSetFinishButton.setPropertyValue("PositionY", new Integer(100));
        xPSetFinishButton.setPropertyValue("Width", new Integer(40));
        xPSetFinishButton.setPropertyValue("Height", new Integer(14));
        xPSetFinishButton.setPropertyValue("Name", BTN_OK);
        xPSetFinishButton.setPropertyValue("Label", finishButtonLabel);
        
        xNameCont.insertByName(BTN_OK, finishButton);
        
        // create the button model - Cancel and set the properties
        Object cancelButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetCancelButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, cancelButton);
        xPSetCancelButton.setPropertyValue("PositionX", new Integer(90));
        xPSetCancelButton.setPropertyValue("PositionY", new Integer(100));
        xPSetCancelButton.setPropertyValue("Width", new Integer(40));
        xPSetCancelButton.setPropertyValue("Height", new Integer(14));
        xPSetCancelButton.setPropertyValue("Name", BTN_CANCEL);
        xPSetCancelButton.setPropertyValue("Label", cancelButtonLabel);
                
        xNameCont.insertByName(BTN_CANCEL, cancelButton);
        
        
        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialog", m_xContext); //esse
        XControl xControl = (XControl)UnoRuntime.queryInterface(XControl.class, dialog );
        XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(XControlModel.class, dlgLicenseSelector);
        xControl.setModel(xControlModel);
        
        // add an action listener to the Previous button control
        xControlCont = (XControlContainer)UnoRuntime.queryInterface(
                XControlContainer.class, dialog);
        
        // add an action listener to the Finish button control
        Object objectButton3 = xControlCont.getControl(BTN_OK);
        XButton xFinishButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton3);
        xFinishButton.addActionListener(new OnFinishClick(this.addin, this));
        
        // add an action listener to the Cancel button control
        Object objectButton4 = xControlCont.getControl(BTN_CANCEL);
        XButton xCancelButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton4);
        xCancelButton.addActionListener(new OnCancelClick(xPSetDialog));
        
        // add Items and an action listener to the License ComboBox control
/*        XComboBox lb = (XComboBox)UnoRuntime.queryInterface(XComboBox.class, xControlCont.getControl(licenseListName));
 
        List classes = (List)CcOOoAddin.ccr.licenseClasses("en");
 
        // TODO timeout exception
 
        Iterator it;
        it = classes.iterator();
        short count = 0;
        while (it.hasNext()) {
            LicenseClass lc = (LicenseClass)it.next();
            lb.addItem(lc.getLabel(), count++);
 
        }
 
 
        lb.addItemListener(new OnSelectLicenceClass());
 */
        // Disable previous, next and finish button at step 1
/*        ((XWindow)UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl(previousButtonName))).setEnable(false);
        ((XWindow)UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl(BTN_OK))).setEnable(false);
        ((XWindow)UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl(nextButtonName))).setEnable(false);
 
 */
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
    
    
    class OnCancelClick implements XActionListener {
        
        
        private XPropertySet _xPSetDialog;
        
        
        public  OnCancelClick(XPropertySet xPSetDialog){
            
            this._xPSetDialog = xPSetDialog;
            
        }
        
        public void actionPerformed(ActionEvent a ) {
            Object dialog;
            xDialog.endExecute();
        }
        
        
        public void disposing(EventObject e ) {
        }
    }
    
    
    class OnFinishClick implements XActionListener {
        
        
        private CcOOoAddin addin;
        private AddInUI ui;
        
        public OnFinishClick(CcOOoAddin addin, AddInUI ui){
            
            this.addin = addin;
            this.ui = ui;
            
        } // OnFinishClick - public constructor
        
        private Boolean getCheckboxValue(String chkName) {
            try {
                
                XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class,
                        this.ui.xNameCont.getByName(chkName));
                
                System.out.println(
                        (((Short)xPSetList.getPropertyValue("State")).intValue() == 1));
                return (((Short)xPSetList.getPropertyValue("State")).intValue() == 1);
                
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
            
        } // getCheckboxValue
        
        public void actionPerformed(ActionEvent a ) {
            
            // retrieve the Document for the issued license
            Chooser licenseChooser = new Chooser();
            License selected = licenseChooser.selectLicense(
                    getCheckboxValue(CHK_ALLOW_REMIX).booleanValue(),
                    getCheckboxValue(CHK_PROHIBIT_COMMERCIAL).booleanValue(),
                    getCheckboxValue(CHK_REQUIRE_SHAREALIKE).booleanValue(),
                    null);
            
            if (this.addin.getServiceType().equalsIgnoreCase("spreadsheet")) {
                
                Calc.embedGraphic(addin.getCurrentComponent(), selected.getImageUrl());
                Calc.insertLicenseText(addin.getCurrentComponent(), selected.getName());
                
            } else if (this.addin.getServiceType().equalsIgnoreCase("text")) {
                
                Writer.createLicenseTextField(addin.getCurrentComponent(),
                        selected.getName(),selected.getLicenseUri(),selected.getImageUrl());
                
            } else if (this.addin.getServiceType().equalsIgnoreCase("presentation")) {
                
                Impress.embedGraphic(addin.getCurrentComponent(), selected.getImageUrl());
                Impress.insertLicenseText(addin.getCurrentComponent(), selected.getName());
                
            } else if (this.addin.getServiceType().equalsIgnoreCase("drawing")) {
                
            }
            
            this.addin.insertLicenseMetadata(selected.getName(), selected.getLicenseUri());
                        
            this.ui.xDialog.endExecute();
            
        } // actionPerformed
        
        public void disposing(EventObject e ) {
        }
    } // OnFinishClick
    
    
}

