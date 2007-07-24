/*
 * Calc.java
 *
 * Created on 24 de Julho de 2007, 19:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.openoffice;

/**
 *
 * @author Cassio
 */
public class Calc {
    
    /** Creates a new instance of Calc */
    public Calc() {
    }
    
    
     /*
      *
      *not working yet...
     
     public void writeImageintoOO(XSpreadsheet xSpreadsheet , int width, int height, String currentCol, String currentRow, String imageURL, HttpServletRequest request) {
      try {
         XMultiServiceFactory xServiceMan = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, xSpreadsheetDocument);
         // Create graphic object
         Object graphicObject = xServiceMan.createInstance("com.sun.star.drawing.GraphicObjectShape");
         
         XPropertySet xGraphicProps = (XPropertySet)UnoRuntime.queryInterface( XPropertySet.class, graphicObject);
         xGraphicProps.setPropertyValue("GraphicURL", imageURL);
         
         XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier)UnoRuntime.queryInterface(XDrawPageSupplier.class, xSpreadsheet);
            XDrawPage xDrawPage = xDrawPageSupplier.getDrawPage();
           
         XShape xGraphicShape = (XShape)UnoRuntime.queryInterface( XShape.class, graphicObject );
         
         xDrawPage.add(xGraphicShape);
         xGraphicShape.setSize(new Size(width,height));
         xGraphicShape.setPosition(getAbsoluteCellPosition(xSpreadsheet, new Integer(currentCol), new Integer(currentRow)));
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   public Point getAbsoluteCellPosition(XSpreadsheet spreadsheet, int x, int y) throws RuntimeException {
        Point p = null;
        try {
            XCell xCell = spreadsheet.getCellByPosition(x, y);
           
            XPropertySet xPropSet = null;
            xPropSet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, xCell);
            p = (Point)xPropSet.getPropertyValue("Position");   
            System.out.println("X: "+p.X);
            System.out.println("Y: "+p.Y);
        }
        catch (com.sun.star.uno.Exception e) {
            e.printStackTrace(System.out);
        }                 
        return p;                   
   } */
    
}
