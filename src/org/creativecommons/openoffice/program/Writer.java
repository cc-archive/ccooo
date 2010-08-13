/*
 * Writer.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */
package org.creativecommons.openoffice.program;

import com.sun.star.awt.Size;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.ControlCharacter;
import com.sun.star.text.XDependentTextField;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextField;
import com.sun.star.text.XTextFieldsSupplier;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XRefreshable;
import com.sun.star.util.XUpdatable;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.util.PageHelper;

/**
 *
 * @author Cassio
 */
public class Writer extends OOoProgram {

    public Writer(XComponent component, XComponentContext m_xContext) {
        super(component, m_xContext);
    }

    public void insertPicture(Image img) {

        XTextDocument mxDoc = (XTextDocument) UnoRuntime.queryInterface(
                XTextDocument.class, this.getComponent());

        XTextCursor docCursor = ((XTextViewCursorSupplier) UnoRuntime.queryInterface(
                XTextViewCursorSupplier.class, mxDoc.getCurrentController())).getViewCursor();

        XMultiServiceFactory mxDocFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, mxDoc);

        XNameContainer xBitmapContainer = null;
        XTextContent xImage = null;
        String internalURL = null;

        try {

            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, mxDocFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));
            xImage = (XTextContent) UnoRuntime.queryInterface(
                    XTextContent.class, mxDocFactory.createInstance(
                    "com.sun.star.text.TextGraphicObject"));
            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xImage);

            // helper-stuff to let OOo create an internal name of the graphic
            // that can be used later (internal name consists of various checksums)
            System.out.println(img.getSelectedImageURL());
            String sName = PageHelper.createUniqueName(xBitmapContainer, img.getPhotoID());
            xBitmapContainer.insertByName(sName, img.getSelectedImageURL());

            Object obj = xBitmapContainer.getByName(sName);
            internalURL = AnyConverter.toString(obj);

            xProps.setPropertyValue("AnchorType",
                    com.sun.star.text.TextContentAnchorType.AS_CHARACTER);
            xProps.setPropertyValue("GraphicURL", internalURL);

            // insert the graphic at the cursor position
            docCursor.getText().insertTextContent(docCursor, xImage, false);

            Size size = (Size) xProps.getPropertyValue("ActualSize");
            if (size.Width != 0) {
                xProps.setPropertyValue("Width", size.Width);
            } else {
                xProps.setPropertyValue("Width", img.getSelectedImageWidth());
            }
            if (size.Height != 0) {
                xProps.setPropertyValue("Height", size.Height);
            } else {
                xProps.setPropertyValue("Height", img.getSelectedImageHeigth());
            }

            // remove the helper-entry
            xBitmapContainer.removeByName(sName);

            String byCaption = "";
            if (img.getLicenseCode().equals("by")) {

                byCaption = "CC BY ";
            } else {
                byCaption = img.getLicenseCode().toUpperCase() + " ";
            }

            docCursor.getText().insertControlCharacter(docCursor,
                    ControlCharacter.PARAGRAPH_BREAK, false);
            String caption = img.getTitle() + " ( " + img.getImgUrlMainPage()
                    + " ) / " + byCaption + img.getLicenseNumber()
                    + " ( " + img.getLicenseURL() + " )";
            docCursor.getText().insertString(docCursor, caption, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Embeds the license "button" into a Textdocument at the given cursor position
     *
     * @param xMSF    the factory to create services from
     * @param xCursor the cursor where to insert the graphic
     * @param imgURL  URL of the license button
     *
     */
    private void embedGraphic(XMultiServiceFactory mxDocFactory,
            XTextCursor xCursor, String imgURL) {

        XNameContainer xBitmapContainer = null;
        XTextContent xImage = null;
        String internalURL = null;

        try {

            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, mxDocFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));
            xImage = (XTextContent) UnoRuntime.queryInterface(
                    XTextContent.class, mxDocFactory.createInstance(
                    "com.sun.star.text.TextGraphicObject"));
            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xImage);

            // helper-stuff to let OOo create an internal name of the graphic
            // that can be used later (internal name consists of various checksums)
            xBitmapContainer.insertByName("imgID", imgURL);

            Object obj = xBitmapContainer.getByName("imgID");
            internalURL = AnyConverter.toString(obj);

            xProps.setPropertyValue("AnchorType",
                    com.sun.star.text.TextContentAnchorType.AS_CHARACTER);
            xProps.setPropertyValue("GraphicURL", internalURL);
            xProps.setPropertyValue("Width", 3104); // original: 88 px
            xProps.setPropertyValue("Height", 1093); // original: 31 px

            // insert the graphic at the cursor position
            xCursor.getText().insertTextContent(xCursor, xImage, false);

            // remove the helper-entry
            xBitmapContainer.removeByName("imgID");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasVisibleNotice() {

        XTextDocument mxDoc = (XTextDocument) UnoRuntime.queryInterface(
                XTextDocument.class, this.getComponent());

        XMultiServiceFactory mxDocFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, mxDoc);

        XTextFieldsSupplier mxTextFields = (XTextFieldsSupplier) UnoRuntime.queryInterface(
                XTextFieldsSupplier.class, mxDoc);

        try {

            XPropertySet licenseNameMaster = getMasterField("License Name",
                    mxTextFields, mxDocFactory);
            return (((Object[]) licenseNameMaster.getPropertyValue(
                    "DependentTextFields")).length != 0);

        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public void insertVisibleNotice() {

        License license = this.getDocumentLicense();

        if (license == null) {
            return;
        }

        try {

            XTextDocument mxDoc = (XTextDocument) UnoRuntime.queryInterface(
                    XTextDocument.class, this.getComponent());

            XText mxDocText = mxDoc.getText();

            XTextCursor docCursor = ((XTextViewCursorSupplier) UnoRuntime.queryInterface(
                    XTextViewCursorSupplier.class, mxDoc.getCurrentController())).getViewCursor();

            XMultiServiceFactory mxDocFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, mxDoc);

            XTextFieldsSupplier mxTextFields = (XTextFieldsSupplier) UnoRuntime.queryInterface(
                    XTextFieldsSupplier.class, mxDoc);

            //XPropertySet licenseNameMaster = updateMasterField("License Name", license.getName(), mxTextFields, mxDocFactory);
            //XPropertySet licenseURLMaster = updateMasterField("License URL", license.getLicenseUri(), mxTextFields, mxDocFactory);

            XDependentTextField licenseNameField = createUserTextField(mxDocFactory,
                    mxTextFields, "License Name", license.getName());
            XDependentTextField licenseURLField = createUserTextField(mxDocFactory,
                    mxTextFields, "License URL", license.getLicenseUri());


            // insert the license graphic if available
            if (license.getImageUrl() != null) {
                embedGraphic(mxDocFactory, docCursor, license.getImageUrl());
            }
            // insert the licensing statement
            if (license.getName().equals("CC0 1.0 Universal")) {
                mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false);
                mxDocText.insertString(docCursor, "To the extent possible under law, the person who associated ", false);
                mxDocText.insertTextContent(docCursor, licenseNameField, false);
                mxDocText.insertString(docCursor, " with this work has waived all "
                        + "copyright and related or neighboring rights to this work. "
                        + "The summary of the Legal Code is available at ", false);
                mxDocText.insertTextContent(docCursor, licenseURLField, false);
                mxDocText.insertString(docCursor, ".", false);
                if (license.getTerritory() != null) {
                    System.out.println("writer");
                    XDependentTextField territory = createUserTextField(mxDocFactory,
                            mxTextFields, "Territory", license.getTerritory());
                    mxDocText.insertString(docCursor, "This work is published from ", false);
                    mxDocText.insertTextContent(docCursor, territory, false);
                }
                mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false);

            } else if (license.getName().equals("Public Domain")) {
                mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false);
                mxDocText.insertString(docCursor, "This document in the ", false);
                mxDocText.insertTextContent(docCursor, licenseNameField, false);
                mxDocText.insertString(docCursor, ". The summary of the Legal Code is available at ", false);
                mxDocText.insertTextContent(docCursor, licenseURLField, false);
                mxDocText.insertString(docCursor, ".", false);
                mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false);

            } else {
                mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false);
                mxDocText.insertString(docCursor, "This document is licensed under the ", false);
                mxDocText.insertTextContent(docCursor, licenseNameField, false);
                mxDocText.insertString(docCursor, " license, available at ", false);
                mxDocText.insertTextContent(docCursor, licenseURLField, false);
                mxDocText.insertString(docCursor, ".", false);
                mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false);
            }

            // Refresh the fields
            ((XRefreshable) UnoRuntime.queryInterface(
                    XRefreshable.class, mxTextFields.getTextFields())).refresh();

        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }

    } // insertVisibleNotice

    protected XDependentTextField createUserTextField(
            final XMultiServiceFactory mxDocFactory,
            final XTextFieldsSupplier mxTextFields,
            final String field_name,
            final String field_value)
            throws WrappedTargetException,
            com.sun.star.uno.Exception,
            NoSuchElementException,
            UnknownPropertyException,
            PropertyVetoException,
            com.sun.star.lang.IllegalArgumentException {

        XPropertySet xMasterPropSet = updateMasterField(field_name, field_value,
                mxTextFields, mxDocFactory);

        // Use the text document's factory to create a user text field,
        // and access it's XDependentTextField interface
        XDependentTextField xUserField =
                (XDependentTextField) UnoRuntime.queryInterface(
                XDependentTextField.class, mxDocFactory.createInstance(
                "com.sun.star.text.TextField.User"));

        // Attach the field master to the user field
        xUserField.attachTextFieldMaster(xMasterPropSet);
        return xUserField;
    }

    protected XPropertySet updateMasterField(final String field_name,
            final String field_value, final XTextFieldsSupplier mxTextFields,
            final XMultiServiceFactory mxDocFactory)
            throws WrappedTargetException, com.sun.star.uno.Exception,
            NoSuchElementException, com.sun.star.lang.IllegalArgumentException,
            UnknownPropertyException, PropertyVetoException {

        // get or create the master field
        XPropertySet xMasterPropSet = getMasterField(field_name, mxTextFields, mxDocFactory);

        // Set the value of the FieldMaster
        xMasterPropSet.setPropertyValue("Content", field_value);

        // update any dependent text fields in the document
        XTextField[] fields = ((XTextField[]) xMasterPropSet.getPropertyValue("DependentTextFields"));

        for (int i = 0; i < fields.length; i++) {

            ((XUpdatable) UnoRuntime.queryInterface(
                    XUpdatable.class, fields[i])).update();
        }

        return xMasterPropSet;
    }

    protected XPropertySet getMasterField(final String field_name,
            final XTextFieldsSupplier mxTextFields,
            final XMultiServiceFactory mxDocFactory)
            throws com.sun.star.lang.IllegalArgumentException,
            PropertyVetoException, UnknownPropertyException,
            NoSuchElementException, WrappedTargetException, com.sun.star.uno.Exception {

        // property set for the user text field
        XPropertySet xMasterPropSet = null;

        // determine the name for the master field
        String masterFieldName = "com.sun.star.text.FieldMaster.User." + field_name;

        // see if the user field already exists
        if (mxTextFields.getTextFieldMasters().hasByName(masterFieldName)) {

            xMasterPropSet = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, mxTextFields.getTextFieldMasters().getByName(masterFieldName));
        } else {

            // Create a fieldmaster for our newly created User Text field, and
            // access it's XPropertySet interface
            xMasterPropSet = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, mxDocFactory.createInstance(
                    "com.sun.star.text.FieldMaster.User"));

            xMasterPropSet.setPropertyValue("Name", field_name);
        }
        return xMasterPropSet;
    }

    @Override
    public void setDocumentLicense(License license) {
        super.setDocumentLicense(license);

        // create/update the user fields for the license name and URL
        XTextDocument mxDoc = (XTextDocument) UnoRuntime.queryInterface(
                XTextDocument.class, this.getComponent());

        XMultiServiceFactory mxDocFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, mxDoc);

        XTextFieldsSupplier mxTextFields = (XTextFieldsSupplier) UnoRuntime.queryInterface(
                XTextFieldsSupplier.class, mxDoc);
        try {

            updateMasterField("License Name", license.getName(), mxTextFields, mxDocFactory);
            updateMasterField("License URL", license.getLicenseUri(), mxTextFields, mxDocFactory);
            if (license.getTerritory() != null) {
                updateMasterField("Territory", license.getTerritory(), mxTextFields, mxDocFactory);
            } else {
            }
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateVisibleNotice() {
        //TODO: method to change the visible notice
    }
} // Writer

