/*
 * Writer.java
 *
 * Created on 21 de Julho de 2007, 18:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice;

import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XController;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.text.XAutoTextContainer;
import com.sun.star.text.XAutoTextEntry;
import com.sun.star.text.XAutoTextGroup;
import com.sun.star.text.XSimpleText;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;

/**
 *
 * @author Cassio
 */
public class Writer {
    
    /** Creates a new instance of Writer */
    public Writer() {
    }
    
    
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
    public static void createAutoText(XComponent xTextComponent, XMultiServiceFactory xRemoteServiceFactory, String licenseName, String licenseURL, String licenseImgURL){
        XTextDocument mxTextDoc = null;
        XMultiServiceFactory mxDocFactory = null;
        XText mxDocText = null;
        XTextCursor mxDocCursor = null;
        
        try {
            
            // query its XTextDocument interface to get the text
            mxTextDoc = (XTextDocument)UnoRuntime.queryInterface(
                    XTextDocument.class, xTextComponent);
            
            
            
            mxDocFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, mxTextDoc);
            
            
            // get a reference to the body text of the document
            mxDocText = mxTextDoc.getText();
            
            mxDocCursor = mxDocText.createTextCursor();
            
            // Create a new Auto-Text
            
            // Get an XNameAccess interface to all auto text groups from the
            // document factory
            XNameAccess xContainer = (XNameAccess) UnoRuntime.queryInterface(
                    XNameAccess.class, xRemoteServiceFactory.createInstance(
                    "com.sun.star.text.AutoTextContainer" ) );
            
            
            XAutoTextContainer container = (XAutoTextContainer) UnoRuntime.queryInterface(XAutoTextContainer.class, xContainer);
            
            if (xContainer.hasByName("Creative Commons")) {
                
                container.removeByName("Creative Commons");
            }
            
            
            XAutoTextGroup newgroup = container.insertNewByName("Creative Commons");
            XAutoTextEntry newentry = newgroup.insertNewByName("CC", "CCommons", mxDocCursor);
            
            
            // Get the XSimpleText and XText interfaces of the new autotext block
            
            XSimpleText xSimpleText = (XSimpleText) UnoRuntime.queryInterface(XSimpleText.class, newentry);
            XText xText = (XText) UnoRuntime.queryInterface(XText.class, newentry);
            
            
            // Insert the license image in the autotext
            embedGraphic(mxDocFactory,xSimpleText.createTextCursor(),licenseImgURL);
            
            // Insert a string at the beginning of the autotext block
            xSimpleText.insertString(xText.getEnd(), "\nThis work is licensed under a "+licenseName+" license.\n"+licenseURL+"\n", false);
            
            // Access the autotext group with this name
            XAutoTextGroup xGroup = (XAutoTextGroup)
            UnoRuntime.queryInterface(XAutoTextGroup.class,
                    xContainer.getByName("Creative Commons"));
            
            
            XAutoTextEntry xEntry = ( XAutoTextEntry )
            UnoRuntime.queryInterface(XAutoTextEntry.class, xGroup.getByName("CC"));
            
            
            // get the XModel interface from the component
            
            XModel xModel = (XModel)UnoRuntime.queryInterface(XModel.class, xTextComponent);
            
            // the model knows its controller
            XController xController = xModel.getCurrentController();
            
            // the controller gives us the TextViewCursor
            // query the viewcursor supplier interface
            XTextViewCursorSupplier xViewCursorSupplier =
                    (XTextViewCursorSupplier)UnoRuntime.queryInterface(XTextViewCursorSupplier.class, xController);
            
            // get the cursor
            XTextViewCursor xViewCursor = xViewCursorSupplier.getViewCursor();
            
            
            // Insert the autotext at the cursor
            xEntry.applyTo(xViewCursor);
            
            
            
            
            // To insert the auto-text at the header/footer of the document, uncomment the code below
            /*
            XText oObj = null;
            XPropertySet PropSet;
            XNameAccess PageStyles = null;
            XStyle StdStyle = null;
             
            XStyleFamiliesSupplier StyleFam = (XStyleFamiliesSupplier)
            UnoRuntime.queryInterface(XStyleFamiliesSupplier.class, mxTextDoc);
            XNameAccess StyleFamNames = StyleFam.getStyleFamilies();
             
            // obtains style 'Standard' from style family 'PageStyles'
             
            PageStyles = (XNameAccess) AnyConverter.toObject(
                    new Type(XNameAccess.class),StyleFamNames.getByName("PageStyles"));
            StdStyle = (XStyle) AnyConverter.toObject(
                    new Type(XStyle.class),PageStyles.getByName("Standard"));
             
            PropSet = (XPropertySet)
            UnoRuntime.queryInterface( XPropertySet.class, StdStyle);
             
            // Choose between header and footer (or both) here
            // PropSet.setPropertyValue("HeaderIsOn", new Boolean(true));
            PropSet.setPropertyValue("FooterIsOn", new Boolean(true));
             
            oObj = (XText) UnoRuntime.queryInterface(
                    XText.class, PropSet.getPropertyValue("FooterText"));
                 // ..or "HeaderText"
             
            xEntry.applyTo(oObj);
             */
            
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
}
