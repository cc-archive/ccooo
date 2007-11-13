/*
 * Writer.java
 *
 * Created on 21 de Julho de 2007, 18:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.program;

import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.ControlCharacter;
import com.sun.star.text.XAutoTextContainer;
import com.sun.star.text.XAutoTextEntry;
import com.sun.star.text.XAutoTextGroup;
import com.sun.star.text.XDependentTextField;
import com.sun.star.text.XParagraphCursor;
import com.sun.star.text.XSimpleText;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextFieldsSupplier;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XRefreshable;
import org.creativecommons.license.License;

/**
 *
 * @author Cassio
 */
public class Writer 
    implements IOOoProgramWrapper {
    
    /**
     * Embeds the license "button" into a Textdocument at the given cursor position
     *
     * @param xMSF    the factory to create services from
     * @param xCursor the cursor where to insert the graphic
     * @param imgURL  URL of the license button
     *
     */
    private static void embedGraphic(/*XComponent xTextComponent*/XMultiServiceFactory mxDocFactory, XTextCursor xCursor, String imgURL) {
       /* XMultiServiceFactory mxDocFactory = null;
        XTextDocument mxTextDoc = null;*/
        XNameContainer xBitmapContainer = null;
        XText xText = xCursor.getText();
        XTextContent xImage = null;
        String internalURL = null;
        
        try {
            
            // query its XTextDocument interface to get the text
           /* mxTextDoc = (XTextDocument)UnoRuntime.queryInterface(
                    XTextDocument.class, xTextComponent);
            
              mxDocFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, mxTextDoc);*/
            
            
            
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
            
            // inser the graphic at the cursor position
            xText.insertTextContent(xCursor, xImage, false);
            
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
    public static void createLicenseTextField(XComponent xTextComponent,
                String licenseName, String licenseURI, String licenseImageUri) {
        try
        {
            XTextDocument mxDoc = (XTextDocument)UnoRuntime.queryInterface(
                XTextDocument.class, xTextComponent);

            XText mxDocText = mxDoc.getText();
            
            XMultiServiceFactory mxDocFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                XMultiServiceFactory.class, mxDoc );

            XTextCursor mxDocCursor = mxDocText.createTextCursor();

            XTextFieldsSupplier mxTextFields = (XTextFieldsSupplier)UnoRuntime.queryInterface(
                    XTextFieldsSupplier.class, mxDoc);
            
            // property set for the user text field
            XPropertySet xMasterPropSet = null;
            
            // see if the user field already exists
            if (mxTextFields.getTextFieldMasters().hasByName("com.sun.star.text.FieldMaster.User.License Name")) {
                
                xMasterPropSet = (XPropertySet)UnoRuntime.queryInterface(
                        XPropertySet.class, mxTextFields.getTextFieldMasters().getByName("com.sun.star.text.FieldMaster.User.License Name"));
            } else {
            
                // Create a fieldmaster for our newly created User Text field, and
                // access it's XPropertySet interface
                xMasterPropSet = (XPropertySet)UnoRuntime.queryInterface(
                    XPropertySet.class, mxDocFactory.createInstance (
                        "com.sun.star.text.FieldMaster.User" ) );
                
                xMasterPropSet.setPropertyValue ( "Name", "License Name" );

            }
            
            // Set the name and value of the FieldMaster
            xMasterPropSet.setPropertyValue ( "Content", licenseName );
            
            // Use the text document's factory to create a user text field, 
            // and access it's XDependentTextField interface
            XDependentTextField xUserField = 
                (XDependentTextField) UnoRuntime.queryInterface (
                    XDependentTextField.class, mxDocFactory.createInstance (
                        "com.sun.star.text.TextField.User" ) );
            
            // Attach the field master to the user field
            xUserField.attachTextFieldMaster ( xMasterPropSet );
            
            // Move the cursor to the end of the document
            // mxDocCursor.gotoEnd( false );
            
            // insert a paragraph break using the XSimpleText interface
            mxDocText.insertControlCharacter ( 
                mxDocCursor, ControlCharacter.PARAGRAPH_BREAK, false );
            
            // Insert the user field at the end of the document
            mxDocText.insertTextContent ( mxDocText.getEnd(), xUserField, false );
            
            // YYY embedGraphic(mxDocFactory,xSimpleText.createTextCursor(),licenseImgURL);
            // Refresh the fields
            // YYY xMasterPropSet.addPropertyChangeListener()
            ( (XRefreshable)UnoRuntime.queryInterface(
                    XRefreshable.class, mxTextFields.getTextFields())
                    ).refresh();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
    } // createLicenseTextField


} // Writer
