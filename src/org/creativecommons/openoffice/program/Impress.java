/*
 * Impress.java
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
import com.sun.star.drawing.XShape;
import com.sun.star.drawing.XShapes;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.style.LineSpacing;
import com.sun.star.style.LineSpacingMode;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.util.PageHelper;
import org.creativecommons.openoffice.util.ShapeHelper;

/**
 *
 * @author Cassio
 */
public class Impress extends OOoProgram {
     
    public Impress(XComponent component) {
        super(component);
    }
    
    public boolean hasVisibleNotice() {
        // XXX need to actually detect if the notice exists
        return false;
    }

    public void insertVisibleNotice() {
        
        License license = this.getDocumentLicense();
        XDrawPage xPage;
        
        try {
            //XDrawPage xPage = PageHelper.getDrawPageByIndex( xDrawDoc, 0 );
            xPage = PageHelper.getMasterPageByIndex(this.getComponent(), 0);
            
            
            XShapes xShapes = (XShapes)
            UnoRuntime.queryInterface( XShapes.class, xPage );
            
            
            XShape xRectangle;
            XPropertySet xTextPropSet, xShapePropSet;
            LineSpacing  aLineSpacing = new LineSpacing();
            aLineSpacing.Mode = LineSpacingMode.PROP;
            
            
            
            // first shape
            xRectangle = ShapeHelper.createShape( this.getComponent(),
                    new Point(15500, 19600 ),
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
            
            // add the graphic
            this.embedGraphic(license.getImageUrl());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void embedGraphic(String imgURL) {
        XDrawPage xPage = null;
        
        XNameContainer xBitmapContainer = null;
        
        String internalURL = null;
        
        XMultiServiceFactory xPresentationFactory = null;
        
        try {
            xPresentationFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(XMultiServiceFactory.class, this.getComponent());
            
            xPage = PageHelper.getMasterPageByIndex(this.getComponent(), 0);
            
            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, xPresentationFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));
            
           /* short hOrientation = HoriOrientation.RIGHT; // Choose among HoriOrientation elements
            short vOrientation = VertOrientation.BOTTOM; // Choose among VertOrientation elements */
            
            Object graphicObject = xPresentationFactory.createInstance("com.sun.star.drawing.GraphicObjectShape");
            XShape xGraphicShape = (XShape)UnoRuntime.queryInterface( XShape.class, graphicObject );
            xGraphicShape.setSize(new Size(4000,1550));
            xGraphicShape.setPosition(new Point(21000,18200));
            
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
            xProps.setPropertyValue("Width", (int) 3104); // original: 88 px
            xProps.setPropertyValue("Height", (int) 1093); // original: 31 px
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

}
