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
import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.MessageBoxButtons;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XComboBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.EventObject;

import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.creativecommons.api.LicenseClass;
import org.creativecommons.api.LicenseField;

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
    protected ccooo addin = null;
    
    String service = "";
    
    // TODO put these labels in a properties file
    protected String previousButtonName = "previousbt";
    protected String previousButtonLabel = "< Previous";
    protected String nextButtonName = "nextbt";
    protected String nextButtonLabel = "Next >";
    protected String finishButtonName = "finishbt";
    protected String finishButtonLabel = "Finish";
    protected String cancelButtonName = "cancelbt";
    protected String cancelButtonLabel = "Cancel";
    
    /**
     * Creates a new instance of AddInUI
     */
    public AddInUI(ccooo addin, XComponentContext m_xContext, String sv) {
        namesList = new Vector();
        this.addin = addin;
        this.m_xContext = m_xContext;
        this.service = sv;
    }
    
    
    
    /**
     * Method for creating a dialog at runtime
     *
     */
    public void createDialog() throws com.sun.star.uno.Exception {
        /*
          // checar se ja licenciado
                     Map prop = addin.retrieveLicenseMetadata();
         
                     //XMessageBoxFactory;
                     XMessageBoxFactory factory = (XMessageBoxFactory)
                     UnoRuntime.queryInterface( XMessageBoxFactory.class,
                             this.xMultiComponentFactory.createInstanceWithContext(
                             "com.sun.star.awt.Toolkit", m_xContext ) );
         
                     Rectangle ret = new Rectangle();
         
         
                     XMessageBox box = factory.createMessageBox(this,ret,"querybox",MessageBoxButtons.BUTTONS_YES_NO,"Title","Document already licensed.\n\nWould you like do proceed anyway?");
         
                     box.execute();*/
        
        
        // get the service manager from the component context
        this.xMultiComponentFactory = this.m_xContext.getServiceManager();
        
        // create the dialog model and set the properties
        Object dialogModel = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialogModel", m_xContext);
        
        XPropertySet xPSetDialog = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, dialogModel);
        xPSetDialog.setPropertyValue("PositionX", new Integer(100));
        xPSetDialog.setPropertyValue("PositionY", new Integer(100));
        xPSetDialog.setPropertyValue("Width", new Integer(240));//470
        xPSetDialog.setPropertyValue("Height", new Integer(200));//360
        xPSetDialog.setPropertyValue("Title", new String("Creative Commons Licensing"));
        xPSetDialog.setPropertyValue("Name", new String("cc"));
        xPSetDialog.setPropertyValue("Step", (short)1 );
        
        
        // get the service manager from the dialog model
        this.xMultiServiceFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                XMultiServiceFactory.class, dialogModel);
        
        // create the listbox model and set the properties
        String licenseListName = "Class";
        
        Object comboBoxModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlComboBoxModel" );
        XPropertySet xPSetComboBox = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, comboBoxModel);
        
        XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, comboBoxModel);
        xPSetList.setPropertyValue("PositionX", new Integer(80));
        xPSetList.setPropertyValue("PositionY", new Integer(35));
        xPSetList.setPropertyValue("Width", new Integer(85));
        xPSetList.setPropertyValue("Height", new Integer(12));
        xPSetList.setPropertyValue("Name", licenseListName);
        xPSetList.setPropertyValue("Dropdown", new Boolean("true"));
        
        //xPSetList.setPropertyValue("TabIndex", new Short((short)3));
        xPSetList.setPropertyValue("Step", new Short((short)1));
        
        
        // create the button model - Previous and set the properties
        Object previousButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetPreviousButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, previousButton);
        xPSetPreviousButton.setPropertyValue("PositionX", new Integer(20));
        xPSetPreviousButton.setPropertyValue("PositionY", new Integer(180));
        xPSetPreviousButton.setPropertyValue("Width", new Integer(50));
        xPSetPreviousButton.setPropertyValue("Height", new Integer(14));
        xPSetPreviousButton.setPropertyValue("Name", previousButtonName);
        xPSetPreviousButton.setPropertyValue("Label", previousButtonLabel);
        
        
        // create the button model - next and set the properties
        Object nextButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetNextButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, nextButton);
        xPSetNextButton.setPropertyValue("PositionX", new Integer(73));
        xPSetNextButton.setPropertyValue("PositionY", new Integer(180));
        xPSetNextButton.setPropertyValue("Width", new Integer(50));
        xPSetNextButton.setPropertyValue("Height", new Integer(14));
        xPSetNextButton.setPropertyValue("Name", nextButtonName);
        //  xPSetNextButton.setPropertyValue("TabIndex", new Short((short)0));
        xPSetNextButton.setPropertyValue("Label", nextButtonLabel);
        
        
        // create the button model - Finish and set the properties
        Object finishButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetFinishButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, finishButton);
        xPSetFinishButton.setPropertyValue("PositionX", new Integer(126));
        xPSetFinishButton.setPropertyValue("PositionY", new Integer(180));
        xPSetFinishButton.setPropertyValue("Width", new Integer(50));
        xPSetFinishButton.setPropertyValue("Height", new Integer(14));
        xPSetFinishButton.setPropertyValue("Name", finishButtonName);
        xPSetFinishButton.setPropertyValue("Label", finishButtonLabel);
        
        
        // create the button model - Cancel and set the properties
        Object cancelButton = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel" );
        XPropertySet xPSetCancelButton = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, cancelButton);
        xPSetCancelButton.setPropertyValue("PositionX", new Integer(179));
        xPSetCancelButton.setPropertyValue("PositionY", new Integer(180));
        xPSetCancelButton.setPropertyValue("Width", new Integer(50));
        xPSetCancelButton.setPropertyValue("Height", new Integer(14));
        xPSetCancelButton.setPropertyValue("Name", cancelButtonName);
        xPSetCancelButton.setPropertyValue("Label", cancelButtonLabel);
        
        
        // create the label model and set the properties
        Object labelModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedTextModel" );
        XPropertySet xPSetLabel = ( XPropertySet )UnoRuntime.queryInterface(XPropertySet.class, labelModel );
        xPSetLabel.setPropertyValue("PositionX", new Integer(80));
        xPSetLabel.setPropertyValue("PositionY", new Integer(15));
        xPSetLabel.setPropertyValue("Width", new Integer(100));
        xPSetLabel.setPropertyValue("Height", new Integer(14));
        xPSetLabel.setPropertyValue("Name", "nomeRotulo"/*_labelName*/);
        xPSetLabel.setPropertyValue("TabIndex", new Short((short)1));
        xPSetLabel.setPropertyValue("Label", "Chose the license class:"/*_labelPrefix*/);
        xPSetLabel.setPropertyValue("Step", new Short((short)1));
        
        
        // insert the control models into the dialog model
        this.xNameCont = (XNameContainer)UnoRuntime.queryInterface(
                XNameContainer.class, dialogModel);
        
        xNameCont.insertByName("nomeRotulo"/*_labelName*/, labelModel);
        xNameCont.insertByName(previousButtonName, previousButton);
        xNameCont.insertByName(nextButtonName, nextButton);
        xNameCont.insertByName(finishButtonName, finishButton);
        xNameCont.insertByName(cancelButtonName, cancelButton);
        xNameCont.insertByName(licenseListName, comboBoxModel);
        
        // create the dialog control and set the model
        Object dialog = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialog", m_xContext); //esse
        XControl xControl = (XControl)UnoRuntime.queryInterface(XControl.class, dialog );
        XControlModel xControlModel = (XControlModel)UnoRuntime.queryInterface(XControlModel.class, dialogModel);
        xControl.setModel(xControlModel);
        
        // add an action listener to the Previous button control
        xControlCont = (XControlContainer)UnoRuntime.queryInterface(
                XControlContainer.class, dialog);
        
        Object objectButton = xControlCont.getControl(previousButtonName);
        XButton xPreviousButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton);
        xPreviousButton.addActionListener(new OnPreviousClick(xControlCont,xPSetDialog, this));
        
        // add an action listener to the Next button control
        Object objectButton2 = xControlCont.getControl(nextButtonName);
        XButton xNextButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton2);
        xNextButton.addActionListener(new OnNextClick(xPSetDialog));
        
        // add an action listener to the Finish button control
        Object objectButton3 = xControlCont.getControl(finishButtonName);
        XButton xFinishButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton3);
        xFinishButton.addActionListener(new OnFinishClick(this.addin, this));
        
        // add an action listener to the Cancel button control
        Object objectButton4 = xControlCont.getControl(cancelButtonName);
        XButton xCancelButton = (XButton)UnoRuntime.queryInterface(XButton.class, objectButton4);
        xCancelButton.addActionListener(new OnCancelClick(xPSetDialog));
        
        // add Items and an action listener to the License ComboBox control
        XComboBox lb = (XComboBox)UnoRuntime.queryInterface(XComboBox.class, xControlCont.getControl(licenseListName));
        
        List classes = (List)ccooo.ccr.licenseClasses("en");
        
        // TODO timeout exception
        
        Iterator it;
        it = classes.iterator();
        short count = 0;
        while (it.hasNext()) {
            LicenseClass lc = (LicenseClass)it.next();
            lb.addItem(lc.getLabel(), count++);
            
        }
        
        
        lb.addItemListener(new OnSelectLicenceClass());
        
        // Disable previous, next and finish button at step 1
        ((XWindow)UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl(previousButtonName))).setEnable(false);
        ((XWindow)UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl(finishButtonName))).setEnable(false);
        ((XWindow)UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl(nextButtonName))).setEnable(false);
        
        
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
    
    /**
     * Creates a ComboBox in the dialog's interface
     *
     * @param posY The vertical position of the ComboBox
     * @param cbName The name of the ComboBox component
     * @param items  Map of items of the ComboBox
     * @param step The step in the sequence which this component should appear
     *
     */
    public void createCombo(int posY, String cbName, Map items, short step){
        
        try {
            if (xNameCont.hasByName(cbName)) {
                return; // it already exists. Let it be the sabe the user had chosen previously
            } else {
                
                Object comboBoxModel;
                comboBoxModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlComboBoxModel");
                
                XPropertySet xPSetComboBox = (XPropertySet)UnoRuntime.queryInterface(
                        XPropertySet.class, comboBoxModel);
                
                XPropertySet xPSetList = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, comboBoxModel);
                xPSetList.setPropertyValue("PositionX", new Integer(80));
                xPSetList.setPropertyValue("PositionY", new Integer(posY));
                xPSetList.setPropertyValue("Width", new Integer(85));
                xPSetList.setPropertyValue("Height", new Integer(12));
                xPSetList.setPropertyValue("Name", cbName);
                xPSetList.setPropertyValue("Dropdown", new Boolean("true"));
                //xPSetList.setPropertyValue("TabIndex", new Short((short)3));
                xPSetList.setPropertyValue("Step", new Short(step));
                
                Object [] values =  items.values().toArray();
                String [] temp = new String[values.length];
                
                for (int i = 0; i < values.length; i++) {
                    temp[i] = (String) values[i];
                }
                xPSetList.setPropertyValue("StringItemList", temp);
                
                xNameCont.insertByName(cbName, comboBoxModel);
                this.namesList.add(cbName); // Add the component name to the list so it can be removed after
            }
            
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Creates a Label in the dialog's interface
     *
     * @param posY The vertical position of the Label
     * @param lbName The name of the Label component
     * @param label The text of the Label
     * @param step The step in the sequence which this component should appear
     *
     */
    public void createLabel(int posY, String lbName, String label, short step){
        
        try {
            if (xNameCont.hasByName(lbName)) {
                return; // it already exists. Let it be the sabe the user had chosen previously
            } else {
                
                // create the label model and set the properties
                Object labelModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedTextModel" );
                XPropertySet xPSetLabel = ( XPropertySet )UnoRuntime.queryInterface(XPropertySet.class, labelModel );
                xPSetLabel.setPropertyValue("PositionX", new Integer(80));
                xPSetLabel.setPropertyValue("PositionY", new Integer(posY));
                xPSetLabel.setPropertyValue("Width", new Integer(100));
                xPSetLabel.setPropertyValue("Height", new Integer(10));
                xPSetLabel.setPropertyValue("Name", lbName);
                xPSetLabel.setPropertyValue("TabIndex", new Short((short)1));
                xPSetLabel.setPropertyValue("Label", label);
                xPSetLabel.setPropertyValue("Step", new Short(step));
                
                xNameCont.insertByName(lbName, labelModel);
                this.namesList.add(lbName); // Add the component name to the list so it can be removed after
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     *
     * Removes all Components in the dialog
     *
     */
    
    public void clearLabels() {
        // This is because XNameContainer doesn't have a method like "clearAll" or "removeAll" :P
        try {
            Iterator it = this.namesList.iterator();
            
            while (it.hasNext()) {
                String name = (String) it.next();
                
                if (this.xNameCont.hasByName(name)) {
                    this.xNameCont.removeByName(name);
                }
                
            }
            this.namesList.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Mounts the interface based on the selected license class
     *
     * @param id The license class id
     *
     */
    public void selectClass(String id) {
        this.answers = new HashMap();
        this.currentId = id;
        LicenseField current;
        
        
        
        // get the fields related to the class
        List fields = (List)ccooo.ccr.fields(id);
        
        if (fields.size() == 0) {
            ((XWindow)UnoRuntime.queryInterface(
                    XWindow.class, this.xControlCont.getControl(this.finishButtonName))).setEnable(true);
            
        }
        
        
        for (int i = 0; i < fields.size(); i ++) {
            current = (LicenseField)fields.get(i);
            
            // Add the field label
            String labelName = "label"+i;
            this.createLabel((i*50 + 30), labelName,current.getLabel(), (short)2 );
            
            
            // determine what type of widget needs to be added
            if (current.getType().equals("enum")) {
                // TODO choose between drop downs and combo-boxes based on the number of available options
                // This code only uses drop downs
                
                String comboName = "combo"+ i;
                this.createCombo((i*50+40),comboName,current.getEnum(),(short)2);
                
                // add an action listener to the combo box control
                XComboBox lb = (XComboBox)UnoRuntime.queryInterface(XComboBox.class, xControlCont.getControl(comboName));
                lb.addItemListener(new OnSelectAnswers(xControlCont,comboName,current.getId(),current.getEnum(),this.answers,fields.size(), this));
                
                
            } // if type == enum
            else {
                System.out.print(current.getType());
            }
            
        }
        
    } // end of method selectClass
//}// end of AddIn class
    
    /**
     * This is the listening class for
     */
    class OnSelectLicenceClass implements XItemListener {
        
        
        public  OnSelectLicenceClass(){
            
        }
        
        public void disposing(EventObject e) {
        }
        
        public void itemStateChanged(ItemEvent e) {
            
            
            
       /* next version!
        // get the fields related to the class
        List fields = (List)ccr.fields(id);
        
        if (fields.size() == 0) {
            ((XWindow)UnoRuntime.queryInterface(
                    XWindow.class, xControlCont.getControl(finishButtonName))).setEnable(true);
        
        } else {
             // Enable next button
                ((XWindow)UnoRuntime.queryInterface(
                XWindow.class, xControlCont.getControl(nextButtonName))).setEnable(true);
        }
        */
            
            
        /*
        System.out.println("selected id:" + e.Selected);
        if (e.Selected==1){//this.licenseClass.equalsIgnoreCase("Public Domain")) {
            ((XWindow)UnoRuntime.queryInterface(
                    XWindow.class, xControlCont.getControl(finishButtonName))).setEnable(true);
         
          //  currentId = "";
        } else {*/
            // Enable next button
            ((XWindow)UnoRuntime.queryInterface(
                    XWindow.class, xControlCont.getControl(nextButtonName))).setEnable(true);
            // }
            
            
            clearLabels();
            
        }
    }
    /**
     *Listening class
     */
    class OnSelectAnswers implements XItemListener {
        
        private XControlContainer _xControlCont;
        
        private Map answerContainer;
        private String id;
        private String comboName;
        private int number;
        private Map answerMap;
        private AddInUI ui;
        
        public  OnSelectAnswers(XControlContainer xControlCont, String comboName, String id, Map aMap, Map answers, int number, AddInUI ui){
            this._xControlCont = xControlCont;
            this.comboName = comboName;
            this.answerContainer = answers;
            this.id = id; // id da pergutna
            this.number = number;
            this.ui = ui;
            this.answerMap = new HashMap();
            
            Iterator keys = aMap.keySet().iterator();
            
            while (keys.hasNext()) {
                String current = (String)keys.next();
                
                this.answerMap.put(aMap.get(current), current);
                
            }
        }
        
        
        public void disposing(EventObject e) {
        }
        
        public void itemStateChanged(ItemEvent e) {
            
            XTextComponent t = (XTextComponent)UnoRuntime.queryInterface(
                    XTextComponent.class, _xControlCont.getControl(this.comboName));
            
            
            this.answerContainer.put(this.id, this.answerMap.get(t.getText()));
            
            // Number of answers must be the same of the number of questions to enable finish button
            if (this.answerContainer.size() == this.number) {
                
                // Enable "Finish" button
                ((XWindow)UnoRuntime.queryInterface(
                        XWindow.class, this._xControlCont.getControl(this.ui.finishButtonName))).setEnable(true);
            }
        }
    }
    
    
    
    class OnNextClick implements XActionListener {
        
        
        private XPropertySet _xPSetDialog;
        
        
        public  OnNextClick(XPropertySet xPSetDialog){
            
            this._xPSetDialog = xPSetDialog;
            
            
        }
        
        public void actionPerformed(ActionEvent a ) {
            
            try {
                int step = ((Integer) this._xPSetDialog.getPropertyValue("Step")).intValue();
                
                // current step
                if (step == 1) {
                    
                    XTextComponent t = (XTextComponent)UnoRuntime.queryInterface(
                            XTextComponent.class, xControlCont.getControl("Class"));
                    selectClass(ccooo.ccr.getLicenseId(t.getText()));
                    
                    this._xPSetDialog.setPropertyValue("Step", ++step);
                    
                    // Enable Previous Button
                    ((XWindow)UnoRuntime.queryInterface(
                            XWindow.class, xControlCont.getControl(previousButtonName))).setEnable(true);
                    
                    // Disable next button
                    ((XWindow)UnoRuntime.queryInterface(
                            XWindow.class, xControlCont.getControl(nextButtonName))).setEnable(false);
                    
                }
                
            } catch (com.sun.star.lang.IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (WrappedTargetException ex) {
                ex.printStackTrace();
            } catch (UnknownPropertyException ex) {
                ex.printStackTrace();
            } catch (PropertyVetoException ex) {
                ex.printStackTrace();
            }
            
        }
        
        public void disposing(EventObject e ) {
        }
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
    
    
    class OnPreviousClick implements XActionListener {
        
        private XControlContainer _xControlCont;
        private XPropertySet _xPSetDialog;
        private AddInUI ui;
        
        public  OnPreviousClick(XControlContainer xControlCont, XPropertySet xPSetDialog, AddInUI ui){
            this._xControlCont = xControlCont;
            this._xPSetDialog = xPSetDialog;
            this.ui = ui;
            
        }
        
        public void actionPerformed(ActionEvent a ) {
            int step;
            try {
                
                step = ((Integer) this._xPSetDialog.getPropertyValue("Step")).intValue();
                
                // current step
                if (step == 2) {
                    
                    this._xPSetDialog.setPropertyValue("Step", --step);
                    
                    System.out.println("STEP: "+step);
                    
                    // Disable Previous Button
                    ((XWindow)UnoRuntime.queryInterface(
                            XWindow.class, this._xControlCont.getControl(this.ui.previousButtonName))).setEnable(false);
                    
                    // Enable next button
                    ((XWindow)UnoRuntime.queryInterface(
                            XWindow.class, this._xControlCont.getControl(this.ui.nextButtonName))).setEnable(true);
                    
                    // Disable finish
                    ((XWindow)UnoRuntime.queryInterface(
                            XWindow.class, this._xControlCont.getControl(this.ui.finishButtonName))).setEnable(false);
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        public void disposing(EventObject e ) {
        }
    }
    
    
    class OnFinishClick implements XActionListener {
        
        
        private ccooo addin;
        private AddInUI ui;
        
        public  OnFinishClick(ccooo addin, AddInUI ui){
            
            
            this.addin = addin;
            this.ui = ui;
            
        }
        
        public void actionPerformed(ActionEvent a ) {
            try {
                
                // retrieve the Document for the issued license
                ccooo.ccr.issue(this.ui.currentId, this.ui.answers, "en");
                
                String licenseName = ccooo.ccr.getLicenseName();
                String licenseURL =  ccooo.ccr.getLicenseUrl();
                String licenseImageURL = ccooo.ccr.getLicenseImageURL();
                
                
                if (service.equalsIgnoreCase("spreadsheet")) {
                    Calc.embedGraphic(addin.getCurrentComponent(),licenseImageURL);
                    Calc.insertLicenseText(addin.getCurrentComponent(), licenseName);
                } else if (service.equalsIgnoreCase("text")) {
                    Writer.createAutoText(addin.getCurrentComponent(), addin.getMSFactory(),licenseName,licenseURL,licenseImageURL);
                } else if (service.equalsIgnoreCase("presentation")) {
                    Impress.embedGraphic(addin.getCurrentComponent(), licenseImageURL);
                    Impress.insertLicenseText(addin.getCurrentComponent(), licenseName);
                    
                } else if (service.equalsIgnoreCase("drawing")) {
                    
                }
                
                this.addin.insertLicenseMetadata(licenseName,licenseURL);
                
           /* this.addin.createAutoText(licenseName,licenseURL, licenseImageURL);
            
            */
                
                this.ui.xDialog.endExecute();
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        public void disposing(EventObject e ) {
        }
    }
    
    
}

