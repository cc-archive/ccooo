/*
 * Writer.java
 *
 * Created on 21 de Julho de 2007, 18:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.program;

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
import com.sun.star.text.XTextFieldsSupplier;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XRefreshable;
import org.creativecommons.license.License;

/**
 *
 * @author Cassio
 */
public class Writer {
    
    /**
     * Embeds the license "button" into a Textdocument at the given cursor position
     *
     * @param xMSF    the factory to create services from
     * @param xCursor the cursor where to insert the graphic
     * @param imgURL  URL of the license button
     *
     */
    private static void embedGraphic(XMultiServiceFactory mxDocFactory, XTextCursor xCursor, String imgURL) {

        XNameContainer xBitmapContainer = null;
        XTextContent xImage = null;
        String internalURL = null;
        
        try {

            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, mxDocFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));
            xImage = (XTextContent) UnoRuntime.queryInterface(
                    XTextContent.class,     mxDocFactory.createInstance(
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
            xProps.setPropertyValue("Width", (int) 4000); // original: 88 px
            xProps.setPropertyValue("Height", (int) 1550); // original: 31 px
            
            // insert the graphic at the cursor position
            xCursor.getText().insertTextContent(xCursor, xImage, false);
            
            // remove the helper-entry
            xBitmapContainer.removeByName("imgID");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and insert an auto-text containing the license
     *
     * @param licenseName The License Name.
     * @param licenseURL The License URL.
     * @param licenseImgURL The license "button" URL.
     *
     */
    public static void insertStatement(XComponent xTextComponent,
            License license) {

        try {

            XTextDocument mxDoc = (XTextDocument)UnoRuntime.queryInterface(
                    XTextDocument.class, xTextComponent);
            
            XText mxDocText = mxDoc.getText();
            
            XTextCursor docCursor = ((XTextViewCursorSupplier)UnoRuntime.queryInterface(
                        XTextViewCursorSupplier.class, mxDoc.getCurrentController())).getViewCursor();
            
            XMultiServiceFactory mxDocFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, mxDoc );
                        
            XTextFieldsSupplier mxTextFields = (XTextFieldsSupplier)UnoRuntime.queryInterface(
                    XTextFieldsSupplier.class, mxDoc);
            XDependentTextField licenseNameField = createField(mxDocFactory, mxTextFields, "License Name", license.getName());
            XDependentTextField licenseURLField = createField(mxDocFactory, mxTextFields, "License URL", license.getLicenseUri());
            
            // insert the license graphic if available
            if (license.getImageUrl() != null)
                embedGraphic(mxDocFactory, docCursor, license.getImageUrl()); 

            // insert the licensing statement           
            mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false );
            mxDocText.insertString(docCursor, "This document is licensed under the ", false );
            mxDocText.insertTextContent(docCursor, licenseNameField, false );
            mxDocText.insertString(docCursor, " license, available at ", false );
            mxDocText.insertTextContent(docCursor, licenseURLField, false );
            mxDocText.insertString(docCursor, ".", false );
            mxDocText.insertControlCharacter(docCursor, ControlCharacter.PARAGRAPH_BREAK, false );
            
            // Refresh the fields
            ( (XRefreshable)UnoRuntime.queryInterface(
                    XRefreshable.class, mxTextFields.getTextFields())
                    ).refresh();
            
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }
        

    } // insertStatement

    private static XDependentTextField createField(
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
        
        // property set for the user text field
        XPropertySet xMasterPropSet = null;
        
        // determine the name for the master field
        String masterFieldName = "com.sun.star.text.FieldMaster.User." + field_name;
        
        // see if the user field already exists
        if (mxTextFields.getTextFieldMasters().hasByName(masterFieldName)) {
            
            xMasterPropSet = (XPropertySet)UnoRuntime.queryInterface(
                    XPropertySet.class, mxTextFields.getTextFieldMasters().getByName(masterFieldName));
        } else {
        
            // Create a fieldmaster for our newly created User Text field, and
            // access it's XPropertySet interface
            xMasterPropSet = (XPropertySet)UnoRuntime.queryInterface(
                XPropertySet.class, mxDocFactory.createInstance (
                    "com.sun.star.text.FieldMaster.User" ) );
            
            xMasterPropSet.setPropertyValue ( "Name", field_name );

        }
        
        // Set the name and value of the FieldMaster
        xMasterPropSet.setPropertyValue ( "Content", field_value );
        
        // Use the text document's factory to create a user text field, 
        // and access it's XDependentTextField interface
        XDependentTextField xUserField = 
            (XDependentTextField) UnoRuntime.queryInterface (
                XDependentTextField.class, mxDocFactory.createInstance (
                    "com.sun.star.text.TextField.User" ) );
        
        // Attach the field master to the user field
        xUserField.attachTextFieldMaster ( xMasterPropSet );
        return xUserField;
    }


} // Writer
