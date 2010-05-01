/*
 * Draw.java
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
import com.sun.star.drawing.TextFitToSizeType;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XShape;
import com.sun.star.drawing.XShapes;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.style.LineSpacing;
import com.sun.star.style.LineSpacingMode;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import java.util.Date;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.util.PageHelper;
import org.creativecommons.openoffice.util.ShapeHelper;

/**
 *
 * @author Cassio
 */
public class Draw extends OOoProgram {

    private int pageWidth;
    private int pageHeight;
    private int pageBorderTop;
    private int pageBorderBottom;
    private int pageBorderLeft;
    private int pageBorderRight;

    public Draw(XComponent component) {
        super(component);
    }

    public void insertPictureFlickr(Image img) {

        XDrawPage xPage = null;
        XNameContainer xBitmapContainer = null;
        String internalURL = null;
        XMultiServiceFactory xPresentationFactory = null;

        try {

            xPresentationFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, this.getComponent());
            xPage = PageHelper.getDrawPageByIndex(this.getComponent(), 0);
            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, xPresentationFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));

            Object graphicObject = xPresentationFactory.createInstance("com.sun.star.drawing.GraphicObjectShape");
            XShape xGraphicShape = (XShape) UnoRuntime.queryInterface(XShape.class, graphicObject);

            xGraphicShape.setPosition(new Point(150, 150));

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
            xProps.setPropertyValue("MoveProtect", false);
            xProps.setPropertyValue("SizeProtect", false);

            xPage.add(xGraphicShape);

            Object xGraphicObject = xProps.getPropertyValue("Graphic");
            XPropertySet xGraphicPropsGOSX = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    xGraphicObject);
            Object sizePixelObject = xGraphicPropsGOSX.getPropertyValue("Size100thMM");
            Size actualSize = (Size) AnyConverter.toObject(Size.class, sizePixelObject);

            xGraphicShape.setSize(actualSize);

            // remove the helper-entry
            xBitmapContainer.removeByName(sName);

            XShape xRectangle;
            XPropertySet xTextPropSet, xShapePropSet;
            LineSpacing aLineSpacing = new LineSpacing();
            aLineSpacing.Mode = LineSpacingMode.PROP;

            String byCaption = "";
            if (img.getLicenseCode().equals("by")) {

                byCaption = "CC BY ";
            } else {
                byCaption = img.getLicenseCode().toUpperCase() + " ";
            }

            // first shape
            String caption = byCaption + img.getLicenseNumber() + " ( " + img.getLicenseURL() + " )";
            xRectangle = ShapeHelper.createShape(this.getComponent(),
                    new Point(0, xGraphicShape.getPosition().Y + xGraphicShape.getSize().Height),
                    new Size(caption.length() * 300, 1000),
                    "com.sun.star.drawing.RectangleShape");
            xPage.add(xRectangle);
            xShapePropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xRectangle);

            xShapePropSet.setPropertyValue("TextLeftDistance", new Long(0));
            xShapePropSet.setPropertyValue("LineStyle", LineStyle.NONE);
            xShapePropSet.setPropertyValue("FillStyle", FillStyle.NONE);

            // first paragraph
            xTextPropSet = ShapeHelper.addPortion(xRectangle, caption, false);
            xTextPropSet.setPropertyValue("CharColor", new Integer(0x000000));

            // first shape
            caption = img.getTitle() + " ( " + img.getImgUrlMainPage() + " )";
            xRectangle = ShapeHelper.createShape(this.getComponent(),
                    new Point(0, xGraphicShape.getPosition().Y + xGraphicShape.getSize().Height + 800),
                    new Size(caption.length() * 310, 1000),
                    "com.sun.star.drawing.RectangleShape");
            xPage.add(xRectangle);
            xShapePropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xRectangle);

            xShapePropSet.setPropertyValue("TextLeftDistance", new Long(0));
            xShapePropSet.setPropertyValue("LineStyle", LineStyle.NONE);
            xShapePropSet.setPropertyValue("FillStyle", FillStyle.NONE);
            xShapePropSet.setPropertyValue("MoveProtect", false);
            xShapePropSet.setPropertyValue("SizeProtect", false);

            //second one
            xTextPropSet = ShapeHelper.addPortion(xRectangle, caption, false);
            xTextPropSet.setPropertyValue("CharColor", new Integer(0x000000));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean hasVisibleNotice() {
        // XXX need to actually detect if the notice exists
        return false;
    }

    public void insertVisibleNotice() {

        XDrawPage xPage;
        License license = this.getDocumentLicense();

        try {
            //XDrawPage xPage = PageHelper.getDrawPageByIndex( xDrawDoc, 0 );
            // xPage = PageHelper.getMasterPageByIndex(xDrawDoc, 0);
            xPage = PageHelper.getMasterPageByIndex(this.getComponent(), 0);

            com.sun.star.beans.XPropertySet xPageProps = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, xPage);
            pageWidth = AnyConverter.toInt(xPageProps.getPropertyValue("Width"));
            pageHeight = AnyConverter.toInt(xPageProps.getPropertyValue("Height"));
            pageBorderTop = AnyConverter.toInt(xPageProps.getPropertyValue("BorderTop"));
            pageBorderBottom = AnyConverter.toInt(xPageProps.getPropertyValue("BorderBottom"));
            pageBorderLeft = AnyConverter.toInt(xPageProps.getPropertyValue("BorderLeft"));
            pageBorderRight = AnyConverter.toInt(xPageProps.getPropertyValue("BorderRight"));
            
            XShapes xShapes = (XShapes) UnoRuntime.queryInterface(XShapes.class, xPage);
           
            XShape xRectangle;
            XPropertySet xTextPropSet, xShapePropSet;
            LineSpacing aLineSpacing = new LineSpacing();
            aLineSpacing.Mode = LineSpacingMode.PROP;
            
            // first shape
            xRectangle = ShapeHelper.createShape(this.getComponent(),
                    new Point(pageWidth - license.getName().length() * pageWidth / 65 - pageBorderRight - 200, pageHeight - 2 * pageWidth / 50 - pageBorderBottom - 200),/*15500, 19600*/
                    new Size(license.getName().length() * pageWidth / 65, 2 * pageWidth / 50),/*15000, 1500*/
                    "com.sun.star.drawing.RectangleShape");

            xPage.add(xRectangle);
            //xShapes.add( xRectangle );
            xShapePropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xRectangle);

            xShapePropSet.setPropertyValue("TextAutoGrowHeight", true);
            xShapePropSet.setPropertyValue("TextAutoGrowWidth", true);
            xShapePropSet.setPropertyValue("LineStyle", LineStyle.NONE);
            xShapePropSet.setPropertyValue("FillStyle", FillStyle.NONE);
            xShapePropSet.setPropertyValue("TextFitToSize", TextFitToSizeType.PROPORTIONAL);

            // first paragraph
            xTextPropSet =
                    ShapeHelper.addPortion(xRectangle, license.getName(), false);
            xTextPropSet.setPropertyValue("CharColor", new Integer(0x000000));
            xTextPropSet.setPropertyValue("CharColor", new Integer(0x000000));
            //xTextPropSet.setPropertyValue( "CharWeight", new Float(com.sun.star.awt.FontWeight.BOLD) );

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

        XMultiServiceFactory xDrawingFactory = null;
        System.out.print(imgURL);
        
        try {

            xDrawingFactory = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, this.getComponent());

            xPage = PageHelper.getMasterPageByIndex(this.getComponent(), 0);

            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, xDrawingFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));

            Object graphicObject = xDrawingFactory.createInstance("com.sun.star.drawing.GraphicObjectShape");
            XShape xGraphicShape = (XShape) UnoRuntime.queryInterface(XShape.class, graphicObject);

            xGraphicShape.setSize(new Size(310 * pageWidth / 2000, 109 * pageWidth / 2000));//new Size(3104, 1093));

            xGraphicShape.setPosition(new Point(pageWidth - 310 * pageWidth / 2000 - pageBorderRight - 200, pageHeight - 2 * pageWidth / 50 - 109 * pageWidth / 1800 - pageBorderBottom - 200));//new Point(21000,18200));

            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xGraphicShape);

            // helper-stuff to let OOo create an internal name of the graphic
            // that can be used later (internal name consists of various checksums)
            long time = new Date().getTime();
            xBitmapContainer.insertByName("imgID", imgURL);
            //xBitmapContainer.insertByName("imgID", "file:/home/akila/Desktop/coordinates.jpg");
            System.out.println("\ninsertByName " + (new Date().getTime() - time));
            Object obj = xBitmapContainer.getByName("imgID");
            internalURL = AnyConverter.toString(obj);

            xProps.setPropertyValue("AnchorType",
                    com.sun.star.text.TextContentAnchorType.AS_CHARACTER);
            xProps.setPropertyValue("GraphicURL", internalURL);
            xProps.setPropertyValue("Width", 4000); // original: 88 px
            xProps.setPropertyValue("Height", 1550); // original: 31 px
            /*xProps.setPropertyValue("HoriOrient", 5000);
            xProps.setPropertyValue("VertOrient", 6000); */
            xProps.setPropertyValue("MoveProtect", false);
            xProps.setPropertyValue("SizeProtect", false);

            // inser the graphic at the cursor position
            xPage.add(xGraphicShape);
            // remove the helper-entry
            xBitmapContainer.removeByName("imgID");

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
