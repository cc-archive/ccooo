/*
 * AllowRemixListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */

package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ItemEvent;
import com.sun.star.awt.XCheckBox;
import com.sun.star.awt.XItemListener;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;

/**
 *
 * @author nathan
 */
public class AllowRemixListener extends UpdateLicenseListener
        implements XItemListener {

    public AllowRemixListener (ChooserDialog dialog) {
        super(dialog);
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        
        XCheckBox allow_Remixing = ((XCheckBox)UnoRuntime.queryInterface(XCheckBox.class, event.Source));
        try {
            
            if (allow_Remixing.getState() == (short)0) {
                // if remixing is not allowed, you can't require Share-Alike
                this.getDialog().setCheckboxValue(this.getDialog().CHK_REQUIRE_SHAREALIKE, Boolean.FALSE);
                                
                ((XPropertySet)UnoRuntime.queryInterface(XPropertySet.class,
                        this.getDialog().getNameContainer().getByName(this.getDialog().CHK_REQUIRE_SHAREALIKE))).
                        setPropertyValue("Enabled", Boolean.FALSE);
                
            } else {
                ((XPropertySet)UnoRuntime.queryInterface(XPropertySet.class,
                        this.getDialog().getNameContainer().getByName(this.getDialog().CHK_REQUIRE_SHAREALIKE))).
                        setPropertyValue("Enabled", Boolean.TRUE);
                
            }
            
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
        }
        
        super.itemStateChanged(event);
        
    }
    
    
}
