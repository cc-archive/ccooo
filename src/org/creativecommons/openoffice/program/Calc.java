/*
 * Calc.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */

package org.creativecommons.openoffice.program;

import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.creativecommons.openoffice.util.PageHelper;
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
import com.sun.star.frame.XController;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.sheet.XCellRangeAddressable;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.style.LineSpacing;
import com.sun.star.style.LineSpacingMode;
import com.sun.star.table.CellRangeAddress;
import com.sun.star.table.XCell;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.ArrayList;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.util.ShapeHelper;

/**
 *
 * @author Cassio
 */
public class Calc extends OOoProgram {

    public Calc(XComponent component,XComponentContext m_xContext) {
        super(component,m_xContext);
    }

    public void insertPicture(Image img) {

        XDrawPage xPage = null;
        XNameContainer xBitmapContainer = null;
        String internalURL = null;
        XMultiServiceFactory xSpreadsheetFactory = null;
        XSpreadsheet xSpreadsheet = null;

        try {
            XSpreadsheetDocument xSheetDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(
                    XSpreadsheetDocument.class,
                    this.getComponent());
            
            XModel xDocModel = (XModel) UnoRuntime.queryInterface(XModel.class, this.getComponent());
            XController xController = xDocModel.getCurrentController();
            XSpreadsheetView view = (XSpreadsheetView) UnoRuntime.queryInterface(
                    XSpreadsheetView.class, xController);
            xSpreadsheet = view.getActiveSheet();

            xSpreadsheetFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, xSheetDoc);

            XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier)
                    UnoRuntime.queryInterface(XDrawPageSupplier.class, xSpreadsheet);
            xPage = xDrawPageSupplier.getDrawPage();

            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, xSpreadsheetFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));

            Object graphicObject = xSpreadsheetFactory.createInstance(
                    "com.sun.star.drawing.GraphicObjectShape");
            XShape xGraphicShape = (XShape)UnoRuntime.queryInterface( XShape.class, graphicObject );

            xGraphicShape.setPosition(getAbsoluteCellPosition(
                    xSpreadsheet, getActiveCellsRange(component).StartColumn,
                    getActiveCellsRange(component).StartRow ));         //add to current cell

            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xGraphicShape);

            // helper-stuff to let OOo create an internal name of the graphic
            // that can be used later (internal name consists of various checksums)
            String sName = PageHelper.createUniqueName(xBitmapContainer, img.getPhotoID());
            xBitmapContainer.insertByName(sName, img.getSelectedImageURL());

            Object obj = xBitmapContainer.getByName(sName);
            internalURL = AnyConverter.toString(obj);

            xProps.setPropertyValue("AnchorType",
                    com.sun.star.text.TextContentAnchorType.AS_CHARACTER);
            xProps.setPropertyValue("GraphicURL", internalURL);
            xProps.setPropertyValue("Name", "ccoo:picture");

            // insert the graphic at the cursor position
            xPage.add(xGraphicShape);

            Object xGraphicObject = xProps.getPropertyValue( "Graphic" );
            XPropertySet xGraphicPropsGOSX = ( XPropertySet )
                    UnoRuntime.queryInterface( XPropertySet.class,
                xGraphicObject );
            Object sizePixelObject = xGraphicPropsGOSX.getPropertyValue( "Size100thMM" );
            Size actualSize = ( Size ) AnyConverter.toObject(Size.class, sizePixelObject );

            if (actualSize.Width != 0||actualSize.Height != 0) {
                xGraphicShape.setSize(actualSize);
            }else{
                sizePixelObject = xGraphicPropsGOSX.getPropertyValue("SizePixel");
                actualSize = (Size) AnyConverter.toObject(Size.class, sizePixelObject);
                xGraphicShape.setSize(new Size((int) (actualSize.Width * 26.4),
                        (int)(actualSize.Height*26.4))); //convert pixels to 100th of mm
            }

            // remove the helper-entry
            xBitmapContainer.removeByName(sName);

            XShape xRectangle;
            XPropertySet xTextPropSet, xShapePropSet;
            LineSpacing  aLineSpacing = new LineSpacing();
            aLineSpacing.Mode = LineSpacingMode.PROP;

            String byCaption = "";
            if (img.getLicenseCode().equals("by")) {

                  byCaption = "CC BY ";
            }
            else {
                  byCaption = img.getLicenseCode().toUpperCase()+ " ";
              }

            // first shape
            String caption = byCaption + img.getLicenseNumber() + " ( " + img.getLicenseURL() + " )";
            xRectangle = ShapeHelper.createShape( this.getComponent(),
                    new Point(xGraphicShape.getPosition().X ,
                    xGraphicShape.getPosition().Y + xGraphicShape.getSize().Height ),
                    new Size( caption.length()*176, 1500 ),
                    "com.sun.star.drawing.RectangleShape" );
            xPage.add( xRectangle );
            xShapePropSet = (XPropertySet) UnoRuntime.queryInterface( XPropertySet.class, xRectangle );

            xShapePropSet.setPropertyValue("TextLeftDistance", new Long(0));
            xShapePropSet.setPropertyValue("LineStyle", LineStyle.NONE);
            xShapePropSet.setPropertyValue("FillStyle", FillStyle.NONE);
            xProps.setPropertyValue("Name", "ccoo:picture");

            // first paragraph
            xTextPropSet = ShapeHelper.addPortion( xRectangle, caption, false );
            xTextPropSet.setPropertyValue( "CharColor", new Integer( 0x000000 ) );

            // first shape
            caption = img.getTitle() + " ( " + img.getImgUrlMainPage() + " )";
            xRectangle = ShapeHelper.createShape(this.getComponent(),
                    new Point(xGraphicShape.getPosition().X,
                    xGraphicShape.getPosition().Y + xGraphicShape.getSize().Height + 600),
                    new Size(caption.length() * 190, 1500),
                    "com.sun.star.drawing.RectangleShape");
            xPage.add(xRectangle);
            xShapePropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xRectangle);

            xShapePropSet.setPropertyValue("TextLeftDistance", new Long(0));
            xShapePropSet.setPropertyValue("LineStyle", LineStyle.NONE);
            xShapePropSet.setPropertyValue("FillStyle", FillStyle.NONE);

            //second one
            xTextPropSet = ShapeHelper.addPortion( xRectangle, caption, false );
            xTextPropSet.setPropertyValue( "CharColor", new Integer( 0x000000 ) );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasVisibleNotice() {
        // XXX need to actually detect if the notice exists
        return false;
    }

    public void insertVisibleNotice(){
        XSpreadsheet xSpreadsheet=null;
        XModel xDocModel = (XModel) UnoRuntime.queryInterface(XModel.class, this.getComponent());
        XController xController = xDocModel.getCurrentController();
        XSpreadsheetView view = (XSpreadsheetView) UnoRuntime.queryInterface(
                XSpreadsheetView.class, xController);
        xSpreadsheet = view.getActiveSheet();
        insertVisibleNotice(xSpreadsheet);
    }
    public void insertVisibleNotice(XSpreadsheet xSpreadsheet) {

        XDrawPage xPage;
        License license = this.getDocumentLicense();

        try {
            XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier)
                    UnoRuntime.queryInterface(XDrawPageSupplier.class, xSpreadsheet);
            xPage = xDrawPageSupplier.getDrawPage();

            XShapes xShapes = (XShapes)
            UnoRuntime.queryInterface( XShapes.class, xPage );

            XShape xRectangle;
            XPropertySet xTextPropSet, xShapePropSet;
            LineSpacing  aLineSpacing = new LineSpacing();
            aLineSpacing.Mode = LineSpacingMode.PROP;

            // first shape
            xRectangle = ShapeHelper.createShape( this.getComponent(),
                    getAbsoluteCellPosition(
                    xSpreadsheet, getActiveCellsRange(component).StartColumn,
                    getActiveCellsRange(component).StartRow +3 ),
                    new Size( 15000, 1500 ),
                    "com.sun.star.drawing.RectangleShape" );
            xShapes.add( xRectangle );
            xShapePropSet = (XPropertySet)
            UnoRuntime.queryInterface( XPropertySet.class, xRectangle );


            xShapePropSet.setPropertyValue("TextAutoGrowHeight", true);
            xShapePropSet.setPropertyValue("TextAutoGrowWidth", true);
            xShapePropSet.setPropertyValue("LineStyle", LineStyle.NONE);
            xShapePropSet.setPropertyValue("FillStyle", FillStyle.NONE);
            xShapePropSet.setPropertyValue("Name", "ccoo:licenseText");

            // first paragraph
            xTextPropSet =
                    ShapeHelper.addPortion( xRectangle, license.getName(), false );
            xTextPropSet.setPropertyValue( "CharColor", new Integer( 0x000000 ) );

            // insert the graphic
            this.embedGraphic(license.getImageUrl(),xSpreadsheet);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void embedGraphic(String imgURL, XSpreadsheet xSpreadsheet) {
        XDrawPage xPage = null;

        XNameContainer xBitmapContainer = null;

        String internalURL = null;

        XMultiServiceFactory xSpreadsheetFactory = null;

        try {
            XSpreadsheetDocument xSheetDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(
                    XSpreadsheetDocument.class,
                    this.getComponent()); // <== tem que ver se o xcomponent o documento ou componente            

            xSpreadsheetFactory = (XMultiServiceFactory)
                    UnoRuntime.queryInterface(XMultiServiceFactory.class, xSheetDoc);


            XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier)
                    UnoRuntime.queryInterface(XDrawPageSupplier.class, xSpreadsheet);
            xPage = xDrawPageSupplier.getDrawPage();


            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, xSpreadsheetFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));

            Object graphicObject = xSpreadsheetFactory.createInstance(
                    "com.sun.star.drawing.GraphicObjectShape");
            XShape xGraphicShape = (XShape)
                    UnoRuntime.queryInterface( XShape.class, graphicObject );

            xGraphicShape.setSize(new Size(3104, 1093));

            xGraphicShape.setPosition(getAbsoluteCellPosition(
                    xSpreadsheet, getActiveCellsRange(component).StartColumn,
                    getActiveCellsRange(component).StartRow ));         //add to current cell

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
            xProps.setPropertyValue("Width", 4000); // original: 88 px
            xProps.setPropertyValue("Height", 1550); // original: 31 px
            /*xProps.setPropertyValue("HoriOrient", 5000);
            xProps.setPropertyValue("VertOrient", 6000); */
            xProps.setPropertyValue("Name", "ccoo:licenseImage");

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


    public static Point getAbsoluteCellPosition(XSpreadsheet spreadsheet, int x, int y)
            throws RuntimeException {
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

    private CellRangeAddress getActiveCellsRange(XComponent xComponent) {

        XModel xDocModel = (XModel) UnoRuntime.queryInterface(XModel.class, xComponent);
        XCellRangeAddressable xSheetCellAddressable =
                (XCellRangeAddressable) UnoRuntime.queryInterface(
                XCellRangeAddressable.class, xDocModel.getCurrentSelection());

        return xSheetCellAddressable.getRangeAddress();
    }

    public void updateVisibleNotice() {
        ArrayList<XSpreadsheet> drawPages = new ArrayList<XSpreadsheet>();
        ArrayList<XShape> shapes = new ArrayList<XShape>();

        XSpreadsheetDocument xSheetDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(
                XSpreadsheetDocument.class,
                this.getComponent());
        String[] sheetNames = xSheetDoc.getSheets().getElementNames();
        try {
            for (int i = 0; i < sheetNames.length; i++) {

                XSpreadsheet xSpreadsheet = (XSpreadsheet) UnoRuntime.queryInterface(
                        XSpreadsheet.class,
                        xSheetDoc.getSheets().getByName(sheetNames[i]));
                XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier)
                        UnoRuntime.queryInterface(XDrawPageSupplier.class, xSpreadsheet);
                XDrawPage xPage = xDrawPageSupplier.getDrawPage();
                XShapes xShapes = (XShapes) UnoRuntime.queryInterface(XShapes.class, xPage);
                for (int j = 0; j < xShapes.getCount(); j++) {
                    XShape xShape = (XShape) UnoRuntime.queryInterface(
                            XShape.class, xShapes.getByIndex(j));
                    XPropertySet xShapePropSet = (XPropertySet) UnoRuntime.queryInterface(
                            XPropertySet.class, xShape);
                    String name = xShapePropSet.getPropertyValue("Name").toString();
                    if (name.equalsIgnoreCase("ccoo:licenseImage")) {
                        shapes.add(xShape);
                        drawPages.add(xSpreadsheet);
                    } else if (name.equalsIgnoreCase("ccoo:licenseText")) {
                        shapes.add(xShape);
                    }
                }
                for (int j = 0; j < shapes.size(); j++) {
                    xShapes.remove(shapes.get(j));
                }
                shapes.clear();

            }
        } catch (UnknownPropertyException ex) {
            Logger.getLogger(Calc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WrappedTargetException ex) {
            Logger.getLogger(Calc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IndexOutOfBoundsException ex) {
            Logger.getLogger(Calc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchElementException ex) {
            Logger.getLogger(Calc.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < drawPages.size(); i++) {
            insertVisibleNotice(drawPages.get(i));
        }
    }
}
