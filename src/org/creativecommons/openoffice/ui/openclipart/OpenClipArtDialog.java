/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui.openclipart;

import com.sun.star.awt.SystemPointer;
import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XPointer;
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
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.ArrayList;
import com.sun.star.graphic.XGraphic;
import org.creativecommons.openoffice.*;
import org.creativecommons.openoffice.program.Image;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.beans.PropertyValue;
import java.util.Date;

/**
 *
 * @author Husleag Mihai
 */
public class OpenClipArtDialog {

    private XMultiServiceFactory xMultiServiceFactory = null;
    protected XComponentContext m_xContext = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    private XNameContainer xNameCont = null;
    private XControlContainer xControlCont = null;
    private XDialog xDialog = null;
    private XControl xControl;
    private XWindowPeer xWindowPeer;
    private CcOOoAddin addin = null;
    private int currentPositionInList = 0;
    public ArrayList<Image> currentList = null;
    private Image selectedImage = null;
    private String savedTags;
    private int currentPage = 0;
    private boolean isLoadable = false;
    private Image loadingImage = null;
    public static final String LBL_TAGS = "lblTags";
    public static final String TXT_TAGS = "txtTags";
    //public static final String LBL_LICENSE = "lblLicense";
    //public static final String LISTBOX_LICENSE = "cmbLicense";    
    public static final String BTN_SEARCH = "btnSearch";
    public static final String searchButtonLabel = "Search";
    public static final String GB_RESULTS = "gbResults";
    public static final String BTN_NEXT = "btnNext";
    public static final String BTN_NEXTLABEL = "Next";
    public static final String BTN_PREVIOUS = "btnPrevious";
    public static final String BTN_PREVIOUSLABEL = "Previous";
    public static final String PB_NAME = "progressBar";
    public static final int SHOWRESULTSPERROW = 4;
    public static final int SHOWRESULTSPERCOLUMN = 4;
    public static final int POSITIONWIDTHHEIGHT = 45;//50
    public static final int LOCATIONIMAGESY = 40;//100

    /**
     * Creates a new instance of ChooserDialog
     */
    public OpenClipArtDialog(CcOOoAddin addin, XComponentContext m_xContext) {
        this.addin = addin;
        this.m_xContext = m_xContext;
        this.loadingImage = new Image("Loading...", null, null, null, null, null, null, null, null, null);
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
            Object dlgLicenseSelector = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialogModel", m_xContext);
            XMultiServiceFactory msfLicenseSelector = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, dlgLicenseSelector);

            XPropertySet xPSetDialog = createAWTControl(dlgLicenseSelector, "dlgMainForm",
                    "", new Rectangle(100, 100, 240, 360));//360
            xPSetDialog.setPropertyValue("Title", new String("Insert Clip Art from Open Clip Art"));
            xPSetDialog.setPropertyValue("Step", (short) 1);

            // get the name container for the dialog for inserting other elements
            this.xNameCont = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, dlgLicenseSelector);

            // get the service manager from the dialog model
            this.xMultiServiceFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, dlgLicenseSelector);

            Object lblTags = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
            createAWTControl(lblTags, LBL_TAGS, "Tags", new Rectangle(10, 10, 50, 12));

            Object txtTags = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlEditModel");
            createAWTControl(txtTags, TXT_TAGS, "", new Rectangle(30, 10, 150, 12));

            Object searchButton = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlButtonModel");
            XPropertySet xPSetFinishButton = createAWTControl(searchButton, BTN_SEARCH, searchButtonLabel,
                    new Rectangle(190, 10, 40, 15)); //(140, 85, 40, 15));
            xPSetFinishButton.setPropertyValue("DefaultButton", new Boolean("true"));

            // create the dialog control and set the model
            Object dialog = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialog", m_xContext); //esse
            xControl = (XControl) UnoRuntime.queryInterface(XControl.class, dialog);
            XControlModel xControlModel = (XControlModel) UnoRuntime.queryInterface(XControlModel.class, dlgLicenseSelector);
            xControl.setModel(xControlModel);

            xControlCont = (XControlContainer) UnoRuntime.queryInterface(
                    XControlContainer.class, dialog);

            Object objSearchButton = xControlCont.getControl(BTN_SEARCH);
            XButton xFinishButton = (XButton) UnoRuntime.queryInterface(XButton.class, objSearchButton);
            xFinishButton.addActionListener(new SearchClickListener(this, this.addin));
            xFinishButton.setActionCommand(BTN_SEARCH);

            Object oGBResults = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlGroupBoxModel");
            createAWTControl(oGBResults, GB_RESULTS, "Results", new Rectangle(10, LOCATIONIMAGESY, 220, 280));//315

            Object oPBar = msfLicenseSelector.createInstance("com.sun.star.awt.UnoControlProgressBarModel");
            XMultiPropertySet xPBModelMPSet = (XMultiPropertySet) UnoRuntime.queryInterface(XMultiPropertySet.class, oPBar);
            // Set the properties at the model - keep in mind to pass the property names in alphabetical order!
            xPBModelMPSet.setPropertyValues(
                    new String[]{"Height", "Name", "PositionX", "PositionY", "Width"},
                    new Object[]{new Integer(8), PB_NAME, new Integer(10), new Integer(350)/*418*/, new Integer(220)});

            // The controlmodel is not really available until inserted to the Dialog container
            getNameContainer().insertByName(PB_NAME, oPBar);
            XPropertySet xPBModelPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oPBar);

            xPBModelPSet.setPropertyValue("ProgressValueMin", new Integer(0));
            xPBModelPSet.setPropertyValue("ProgressValueMax", new Integer(100));

            // create a peer
            Object toolkit = xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
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

    private XPropertySet createAWTControl(Object objControl, String ctrlName,
            String ctrlCaption, Rectangle posSize) throws Exception {

        XPropertySet xpsProperties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, objControl);

        xpsProperties.setPropertyValue("PositionX", new Integer(posSize.x));
        xpsProperties.setPropertyValue("PositionY", new Integer(posSize.y));
        xpsProperties.setPropertyValue("Width", new Integer(posSize.width));
        xpsProperties.setPropertyValue("Height", new Integer(posSize.height));
        xpsProperties.setPropertyValue("Name", ctrlName);
        if (ctrlCaption != "") {
            xpsProperties.setPropertyValue("Label", ctrlCaption);
        }

        if ((getNameContainer() != null) && (!getNameContainer().hasByName(ctrlName))) {
            getNameContainer().insertByName(ctrlName, objControl);
        }

        return xpsProperties;
    }

    public String[] GetTags() {
        Object oTags = xControlCont.getControl(TXT_TAGS);
        XControl txtTags = (XControl) UnoRuntime.queryInterface(XControl.class, oTags);
        XControlModel xControlModel = txtTags.getModel();
        XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);

        try {

            String selTags = (String) xPSet.getPropertyValue("Text");
            if (selTags.trim().equalsIgnoreCase("")) {
                return new String[0];
            }

            return selTags.split(" ");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String[0];
    }

    public void showResults(ArrayList<Image> imgList, int progressValue) {

        this.currentList = imgList;
        this.currentPositionInList = 0;
        showNextPage(progressValue);
    }

    private void showNextPage(int progressValue) {
        if (currentList == null) {
            return;
        }

        enableControl(BTN_PREVIOUS, false);

        double rateProgress = (double) (95 - progressValue) / currentList.size();
        double currentProgress = progressValue;
        int currentY = LOCATIONIMAGESY - POSITIONWIDTHHEIGHT - 5;
        int currentX = 15 - POSITIONWIDTHHEIGHT - 10;

        for (int i = 0; i < SHOWRESULTSPERROW; i++) {

            currentY += POSITIONWIDTHHEIGHT + 20;
            currentX = 15 - POSITIONWIDTHHEIGHT - 10;

            for (int j = 0; j < SHOWRESULTSPERCOLUMN; j++) {

                currentX += POSITIONWIDTHHEIGHT + 10;

                if (currentList.size() > currentPositionInList) {
                    createImageControl(currentList.get(currentPositionInList), new Rectangle(currentX,
                            currentY, POSITIONWIDTHHEIGHT, POSITIONWIDTHHEIGHT), String.valueOf(currentPositionInList));
                } else {
                    createImageControl(null, new Rectangle(currentX, currentY, POSITIONWIDTHHEIGHT,
                            POSITIONWIDTHHEIGHT), String.valueOf(currentPositionInList));
                }

                currentPositionInList++;
                currentProgress += rateProgress;
                this.setProgressValue((int) currentProgress);
            }
        }

        try {
            Object button = null;
            boolean isNewCreated = false;
            if (getNameContainer().hasByName(BTN_NEXT)) {
                button = getNameContainer().getByName(BTN_NEXT);
            } else {
                button = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel");
                isNewCreated = true;
            }

            createAWTControl(button, BTN_NEXT, BTN_NEXTLABEL, new Rectangle(150, 330, 40, 15));//395

            if (isNewCreated) {
                XButton xNextButton = (XButton) UnoRuntime.queryInterface(XButton.class,
                        xControlCont.getControl(BTN_NEXT));
                if (xNextButton != null) {
                    xNextButton.addActionListener(new SearchClickListener(this, this.addin));
                    xNextButton.setActionCommand(BTN_NEXT);
                }
            }

            isNewCreated = false;
            if (getNameContainer().hasByName(BTN_PREVIOUS)) {
                button = getNameContainer().getByName(BTN_PREVIOUS);
            } else {
                button = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlButtonModel");
                isNewCreated = true;
            }

            createAWTControl(button, BTN_PREVIOUS, BTN_PREVIOUSLABEL, new Rectangle(50, 330, 40, 15)); //395

            if (isNewCreated) {
                XButton xPrevButton = (XButton) UnoRuntime.queryInterface(XButton.class,
                        xControlCont.getControl(BTN_PREVIOUS));
                if (xPrevButton != null) {
                    xPrevButton.addActionListener(new SearchClickListener(this, this.addin));
                    xPrevButton.setActionCommand(BTN_PREVIOUS);
                }
            }

            if (currentPage <= 1) {
                enableControl(BTN_PREVIOUS, false);
            } else {
                enableControl(BTN_PREVIOUS, true);
            }

            if (currentList.size() < SHOWRESULTSPERROW * SHOWRESULTSPERCOLUMN) {
                enableControl(BTN_NEXT, false);
            } else {
                enableControl(BTN_NEXT, true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createImageControl(Image img, Rectangle rect, String pos) {

        if (!isLoadable) {

            return;
        }
        createImageLoad(rect, pos);

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

            oICModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlImageControlModel");
            XPropertySet xpsImageControl = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oICModel);

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
                xWindow.addMouseListener(new ImageButtonListener(this, this.addin, img));
            }

            xpsImageControl.setPropertyValue("Graphic", xGraphic);

            Object lblUser = null;
            if (getNameContainer().hasByName("ImageLabelUser" + pos)) {
                lblUser = getNameContainer().getByName("ImageLabelUser" + pos);
            } else {
                lblUser = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedHyperlinkModel");
            }

            XPropertySet xpsProperties = createAWTControl(lblUser, "ImageLabelUser" + pos, userName,
                    new Rectangle(rect.x, rect.y + rect.height + 3, POSITIONWIDTHHEIGHT, 15)); //50
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

    private void createImageLoad(Rectangle rect, String pos) {
        try {
            Object oICModel = null;
            if (getNameContainer().hasByName("ImageControl" + pos)) {
                XControl xImageControl = xControlCont.getControl("ImageControl" + pos);
                if (xImageControl != null) {
                    xImageControl.dispose();
                }
                getNameContainer().removeByName("ImageControl" + pos);
            }

            oICModel = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlImageControlModel");

            XGraphic xGraphic = null;

            if (loadingImage.getGraphic() != null) {
                xGraphic = loadingImage.getGraphic();
            } else {
                xGraphic = getGraphic("file://" + this.getClass().
                        getProtectionDomain().getCodeSource().
                        getLocation().getPath().
                        replaceFirst("ccooo.jar", "images/openclipartloading.png"));
                loadingImage.setGraphic(xGraphic);
            }

            XPropertySet xpsImageControl = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oICModel);

            xpsImageControl.setPropertyValue("Border", (short) 0);
            xpsImageControl.setPropertyValue("Height", new Integer(rect.height));
            xpsImageControl.setPropertyValue("Name", "ImageControl" + pos);
            xpsImageControl.setPropertyValue("PositionX", new Integer(rect.x));
            xpsImageControl.setPropertyValue("PositionY", new Integer(rect.y));
            xpsImageControl.setPropertyValue("Width", new Integer(rect.width));

            String title = "Loading...";

            xpsImageControl.setPropertyValue("HelpText", title);

            getNameContainer().insertByName("ImageControl" + pos, oICModel);

            xpsImageControl.setPropertyValue("Graphic", xGraphic);

            Object lblUser = null;
            if (getNameContainer().hasByName("ImageLabelUser" + pos)) {
                lblUser = getNameContainer().getByName("ImageLabelUser" + pos);
            } else {
                lblUser = xMultiServiceFactory.createInstance("com.sun.star.awt.UnoControlFixedHyperlinkModel");
            }

            String userName = "";

            XPropertySet xpsProperties = createAWTControl(lblUser, "ImageLabelUser" + pos, userName,
                    new Rectangle(rect.x, rect.y + rect.height + 3, POSITIONWIDTHHEIGHT, 15)); //50
            xpsProperties.setPropertyValue("Label", userName);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void enableControl(String controlName, boolean enable) {
        try {
            if (getNameContainer().hasByName(controlName)) {
                Object oControl = getNameContainer().getByName(controlName);
                XPropertySet xModelPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oControl);

                xModelPSet.setPropertyValue("Enabled", enable);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setProgressValue(int step) {

        if (!getNameContainer().hasByName(PB_NAME)) {

            return;
        }

        try {
            Object oPBar = getNameContainer().getByName(PB_NAME);
            XPropertySet xPBModelPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, oPBar);

            xPBModelPSet.setPropertyValue("ProgressValue", new Integer(step));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // creates a UNO graphic object that can be used to be assigned
    // to the property "Graphic" of a controlmodel
    public XGraphic getGraphic(String _sImageUrl) {

        XGraphic xGraphic = null;
        try {

            // create a GraphicProvider at the global service manager...
            Object oGraphicProvider = xMultiComponentFactory.createInstanceWithContext("com.sun.star.graphic.GraphicProvider", m_xContext);
            XGraphicProvider xGraphicProvider = (XGraphicProvider) UnoRuntime.queryInterface(XGraphicProvider.class, oGraphicProvider);
            // create the graphic object
            PropertyValue[] aPropertyValues = new PropertyValue[1];
            PropertyValue aPropertyValue = new PropertyValue();
            aPropertyValue.Name = "URL";
            aPropertyValue.Value = _sImageUrl;
            aPropertyValues[0] = aPropertyValue;
            /**********************************************/
            long time = new Date().getTime();
            xGraphic = xGraphicProvider.queryGraphic(aPropertyValues); /////////// bottleneck 2
            System.out.println("Flicker adding thumbnails to dialog " + (new Date().getTime() - time) + _sImageUrl);
            /**********************************************/
            return xGraphic;
        } catch (com.sun.star.uno.Exception ex) {
            throw new java.lang.RuntimeException("cannot happen...");
        }
    }


    /**
     * Canges the mouse pointer.
     *
     * @param xContext
     * @param xWindowPeer
     * @param nSystemPointer
     */
    public void setMousePointer(int nSystemPointer) {
        if (xWindowPeer == null) {
            return;
        }

        try {
            Object oPointer = xMultiComponentFactory.createInstanceWithContext(
                    "com.sun.star.awt.Pointer", m_xContext);
            if (oPointer != null) {
                XPointer xPointer = (XPointer) UnoRuntime.queryInterface(
                        XPointer.class, oPointer);
                xPointer.setType(new Integer(nSystemPointer));
                xWindowPeer.setPointer(xPointer);
            }
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
        }
    }

    public XNameContainer getNameContainer() {
        return xNameCont;
    }

    public Image getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Image selImage) {

        this.selectedImage = selImage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int _currentPage) {
        this.currentPage = _currentPage;
    }

    public void close() {
        this.xDialog.endExecute();
    }

    public void saveSearch() {

        try {

            Object oTags = xControlCont.getControl(TXT_TAGS);
            XControl txtTags = (XControl) UnoRuntime.queryInterface(XControl.class, oTags);
            XControlModel xControlModel = txtTags.getModel();
            XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);
            String selTags = (String) xPSet.getPropertyValue("Text");
            this.savedTags = selTags.trim();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startSavedSearch() {

        setMousePointer(SystemPointer.WAIT);
        enableControl(OpenClipArtDialog.BTN_SEARCH, false);
        enableControl(OpenClipArtDialog.BTN_NEXT, false);
        currentPositionInList = 0;

        try {
            Object oTags = xControlCont.getControl(TXT_TAGS);
            XControl txtTags = (XControl) UnoRuntime.queryInterface(XControl.class, oTags);
            XControlModel xControlModel = txtTags.getModel();
            XPropertySet xPSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xControlModel);
            xPSet.setPropertyValue("Text", this.savedTags);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        showResults(currentList, 0);
        setProgressValue(100);
        enableControl(OpenClipArtDialog.BTN_SEARCH, true);
        // enableControl(OpenClipArtDialog.BTN_NEXT, true);
        setMousePointer(SystemPointer.ARROW);
    }

    public void setLoadable(boolean val) {

        this.isLoadable = val;
    }
} // OpenClipArtDialog

