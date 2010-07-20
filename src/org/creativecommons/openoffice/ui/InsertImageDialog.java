/*
 * ChooserDialog.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.ui;

import java.awt.Rectangle;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XPointer;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XPopupMenu;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.ArrayList;
import java.util.Collection;
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
public abstract class InsertImageDialog {

    private int btnPrvNextX;
    protected XMultiServiceFactory xMultiServiceFactory = null;
    protected XComponentContext m_xContext = null;
    protected XMultiComponentFactory xMultiComponentFactory = null;
    protected XNameContainer xNameCont = null;
    protected XControlContainer xControlCont = null;
    protected XDialog xDialog = null;
    protected XControl xControl;
    protected XWindowPeer xWindowPeer;
    protected CcOOoAddin addin = null;
    protected int currentPositionInList = 0;
    protected ArrayList<Image> currentList = null;
    protected Image selectedImage = null;
    protected Collection flickrLicenses = null;
    protected String savedTags;
    protected int currentPage = 0;
    protected boolean isLoadable = false;
    protected Image loadingImage = null;
    protected SearchClickListener searchClickListener = null;
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


    public int positionWidthHeight;//50
    public int locationMagesy;//100

    /**
     * Creates a new instance of ChooserDialog
     */
    public InsertImageDialog(CcOOoAddin addin, XComponentContext m_xContext,
            int positionWidthHeight, int locationMagesy) {
        this.positionWidthHeight=positionWidthHeight;
        this.locationMagesy=locationMagesy;
        this.addin = addin;
        this.m_xContext = m_xContext;
        this.loadingImage = new Image("Loading...", null, null, null, null, null,
                null, null, null, null);
    }

    /**
     * Method for creating a dialog at runtime
     *
     */
    public abstract void showDialog(boolean defaultSearch) throws com.sun.star.uno.Exception;

    protected XPropertySet createAWTControl(Object objControl, String ctrlName,
            String ctrlCaption, Rectangle posSize) throws Exception {

        XPropertySet xpsProperties = (XPropertySet) UnoRuntime.queryInterface(
                XPropertySet.class, objControl);

        xpsProperties.setPropertyValue("PositionX", new Integer(posSize.x));
        xpsProperties.setPropertyValue("PositionY", new Integer(posSize.y));
        xpsProperties.setPropertyValue("Width", new Integer(posSize.width));
        xpsProperties.setPropertyValue("Height", new Integer(posSize.height));
        xpsProperties.setPropertyValue("Name", ctrlName);
        if (ctrlCaption == null ? "" != null : !ctrlCaption.equals("")) {
            xpsProperties.setPropertyValue("Label", ctrlCaption);
        }

        if ((getNameContainer() != null) && (!getNameContainer().hasByName(ctrlName))) {
            getNameContainer().insertByName(ctrlName, objControl);
        }

        return xpsProperties;
    }

    protected void showNextPage(int progressValue) {
        if (currentList == null) {
            return;
        }

        enableControl(BTN_PREVIOUS, false);

        double rateProgress = (double) (95 - progressValue) / currentList.size();
        double currentProgress = progressValue;
        int currentY = locationMagesy - positionWidthHeight - 5;
        int currentX = 15 - positionWidthHeight - 10;

        for (int i = 0; i < SHOWRESULTSPERROW; i++) {

            currentY += positionWidthHeight + 20;
            currentX = 15 - positionWidthHeight - 10;

            for (int j = 0; j < SHOWRESULTSPERCOLUMN; j++) {

                currentX += positionWidthHeight + 10;

                if (currentList.size() > currentPositionInList) {
                    createImageControl(currentList.get(currentPositionInList),
                            new Rectangle(currentX,currentY, positionWidthHeight,
                            positionWidthHeight),String.valueOf(currentPositionInList));
                } else {
                    createImageControl(null, new Rectangle(currentX, currentY,
                            positionWidthHeight,positionWidthHeight),
                            String.valueOf(currentPositionInList));
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
                button = xMultiServiceFactory.createInstance(
                        "com.sun.star.awt.UnoControlButtonModel");
                isNewCreated = true;
            }

            createAWTControl(button, BTN_NEXT, BTN_NEXTLABEL, new Rectangle(150, 330, 40, 15));//395

            if (isNewCreated) {
                XButton xNextButton = (XButton) UnoRuntime.queryInterface(XButton.class,
                        xControlCont.getControl(BTN_NEXT));
                if (xNextButton != null) {
                    xNextButton.addActionListener(searchClickListener);
                    xNextButton.setActionCommand(BTN_NEXT);
                }
            }
            
            isNewCreated = false;
            if (getNameContainer().hasByName(BTN_PREVIOUS)) {
                button = getNameContainer().getByName(BTN_PREVIOUS);
            } else {
                button = xMultiServiceFactory.createInstance(
                        "com.sun.star.awt.UnoControlButtonModel");
                isNewCreated = true;
            }

            createAWTControl(button, BTN_PREVIOUS, BTN_PREVIOUSLABEL, new Rectangle(50, 330, 40, 15)); //395

            if (isNewCreated) {
                XButton xPrevButton = (XButton) UnoRuntime.queryInterface(XButton.class,
                        xControlCont.getControl(BTN_PREVIOUS));
                if (xPrevButton != null) {
                    xPrevButton.addActionListener(searchClickListener);
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

    protected abstract void createImageControl(Image img, Rectangle rect, String pos);

    protected void createImageLoad(Rectangle rect, String pos, String loadingImageFile) {

        if (!isLoadable) {

            return;
        }

        try {
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

            XGraphic xGraphic = null;

            if (loadingImage.getGraphic() != null) {
                xGraphic = loadingImage.getGraphic();
            } else {
                xGraphic = getGraphic(loadingImageFile);
                loadingImage.setGraphic(xGraphic);
            }

            XPropertySet xpsImageControl = (XPropertySet)
                    UnoRuntime.queryInterface(XPropertySet.class, oICModel);

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
                lblUser = xMultiServiceFactory.createInstance(
                        "com.sun.star.awt.UnoControlFixedHyperlinkModel");
            }

            String userName = "";

            XPropertySet xpsProperties = createAWTControl(lblUser, "ImageLabelUser" + pos, userName,
                    new Rectangle(rect.x, rect.y + rect.height + 3, positionWidthHeight, 15)); //50
            xpsProperties.setPropertyValue("Label", userName);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

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

    public void enableControl(String controlName, boolean enable) {
        try {
            if (getNameContainer().hasByName(controlName)) {
                Object oControl = getNameContainer().getByName(controlName);
                XPropertySet xModelPSet = (XPropertySet)
                        UnoRuntime.queryInterface(XPropertySet.class, oControl);

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
            XPropertySet xPBModelPSet = (XPropertySet)
                    UnoRuntime.queryInterface(XPropertySet.class, oPBar);

            xPBModelPSet.setPropertyValue("ProgressValue", new Integer(step));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //TO DO
    public abstract XPopupMenu executePopupMenu(Image img, Integer positionX, Integer positionY,
            XWindowPeer xImagePeer);

    // creates a UNO graphic object that can be used to be assigned
    // to the property "Graphic" of a controlmodel
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

    public abstract String getLicense();

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

    public abstract boolean IsInputValid();

    public abstract void saveSearch();

    public abstract void startSavedSearch();

    public void setLoadable(boolean val) {
        this.isLoadable = val;
    }
} // InsertImageDialog

