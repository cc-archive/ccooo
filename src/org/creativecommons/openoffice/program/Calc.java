/*
 * Calc.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */

package org.creativecommons.openoffice.program;

import com.sun.star.awt.Point;
import com.sun.star.awt.Size;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.drawing.FillStyle;
import com.sun.star.drawing.LineStyle;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPageSupplier;
import com.sun.star.drawing.XShape;
import com.sun.star.drawing.XShapes;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.style.LineSpacing;
import com.sun.star.style.LineSpacingMode;
import com.sun.star.table.XCell;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.util.ShapeHelper;

/**
 *
 * @author Cassio
 */
public class Calc extends OOoProgram {
    
    public Calc(XComponent component) {
        super(component);
    }

    public boolean hasVisibleNotice() {
        // XXX need to actually detect if the notice exists
        return false;
    }

    public void insertVisibleNotice() {
        
        XDrawPage xPage;
        XSpreadsheet xSpreadsheet = null;
        License license = this.getDocumentLicense();
        
        try {
            //XDrawPage xPage = PageHelper.getDrawPageByIndex( xDrawDoc, 0 );
            // xPage = PageHelper.getMasterPageByIndex(xDrawDoc, 0);
            XSpreadsheetDocument xSheetDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(
                    XSpreadsheetDocument.class,
                    this.getComponent()); // <== tem que ver se o xcomponent o documento ou componente
            
            xSpreadsheet = (XSpreadsheet) UnoRuntime.queryInterface(
                    XSpreadsheet.class,
                    xSheetDoc.getSheets()
                    .getByName(xSheetDoc.getSheets()
                    .getElementNames()[0]));
            
            XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier)UnoRuntime.queryInterface(XDrawPageSupplier.class, xSpreadsheet);
            xPage = xDrawPageSupplier.getDrawPage();
            
            XShapes xShapes = (XShapes)
            UnoRuntime.queryInterface( XShapes.class, xPage );
            
            
            XShape xRectangle;
            XPropertySet xTextPropSet, xShapePropSet;
            LineSpacing  aLineSpacing = new LineSpacing();
            aLineSpacing.Mode = LineSpacingMode.PROP;
            
            
            
            // first shape
            xRectangle = ShapeHelper.createShape( this.getComponent(),
                    new Point(0, 1600 ),
                    new Size( 15000, 1500 ),
                    "com.sun.star.drawing.RectangleShape" );
            xShapes.add( xRectangle );
            xShapePropSet = (XPropertySet)
            UnoRuntime.queryInterface( XPropertySet.class, xRectangle );
            
            
            xShapePropSet.setPropertyValue("TextAutoGrowHeight", true);
            xShapePropSet.setPropertyValue("TextAutoGrowWidth", true);
            xShapePropSet.setPropertyValue("LineStyle", LineStyle.NONE);
            xShapePropSet.setPropertyValue("FillStyle", FillStyle.NONE);
            
            // first paragraph
            xTextPropSet =
                    ShapeHelper.addPortion( xRectangle, license.getName(), false );
            xTextPropSet.setPropertyValue( "CharColor", new Integer( 0x000000 ) );
            
            // insert the graphic
            this.embedGraphic(license.getImageUrl());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void embedGraphic(String imgURL) {
        XDrawPage xPage = null;
        
        XNameContainer xBitmapContainer = null;
        
        String internalURL = null;
        
        XMultiServiceFactory xSpreadsheetFactory = null;
        
        XSpreadsheet xSpreadsheet = null;
        
        try {
            XSpreadsheetDocument xSheetDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(
                    XSpreadsheetDocument.class,
                    this.getComponent()); // <== tem que ver se o xcomponent o documento ou componente
            
            xSpreadsheet = (XSpreadsheet) UnoRuntime.queryInterface(
                    XSpreadsheet.class,
                    xSheetDoc.getSheets()
                    .getByName(xSheetDoc.getSheets()
                    .getElementNames()[0]));
            
            xSpreadsheetFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, xSheetDoc);
            
            
            XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier)UnoRuntime.queryInterface(XDrawPageSupplier.class, xSpreadsheet);
            xPage = xDrawPageSupplier.getDrawPage();
            
            
            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, xSpreadsheetFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));
            
            Object graphicObject = xSpreadsheetFactory.createInstance("com.sun.star.drawing.GraphicObjectShape");
            XShape xGraphicShape = (XShape)UnoRuntime.queryInterface( XShape.class, graphicObject );

            xGraphicShape.setSize(new Size(3104, 1093));
            
            xGraphicShape.setPosition(getAbsoluteCellPosition(xSpreadsheet, new Integer(0), new Integer(0)));
            
            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xGraphicShape);
            
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
            /*xProps.setPropertyValue("HoriOrient", 5000);
            xProps.setPropertyValue("VertOrient", 6000); */
            
            // inser the graphic at the cursor position
            
            xPage.add(xGraphicShape);
            // remove the helper-entry
            xBitmapContainer.removeByName("imgID");
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
    private XSpreadsheet getSheet(XComponent xDoc) {
        XSpreadsheetDocument xSheetDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(
                XSpreadsheetDocument.class,
                xDoc);
        XSpreadsheet xSheet = null;
        
        try {
            xSheet = (XSpreadsheet) UnoRuntime.queryInterface(
                    XSpreadsheet.class,
                    xSheetDoc.getSheets()
                    .getByName(xSheetDoc.getSheets()
                    .getElementNames()[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return xSheet;
    }
    
    
    public static Point getAbsoluteCellPosition(XSpreadsheet spreadsheet, int x, int y) throws RuntimeException {
        Point p = null;
        try {
            XCell xCell = spreadsheet.getCellByPosition(x, y);
            
            XPropertySet xPropSet = null;
            xPropSet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xCell);
            p = (Point)xPropSet.getPropertyValue("Position");
            System.out.println("X: "+p.X);
            System.out.println("Y: "+p.Y);
        } catch (com.sun.star.uno.Exception e) {
            e.printStackTrace(System.out);
        }
        return p;
    }

    
}
