/*
 * AcceptListener.java
 *
 * Copyright 2010, Creative Commons
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
import com.sun.star.lang.EventObject;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;

/**
 * Enable OK button after accepting the deed.
 * @author akila
 */
public class AcceptListener implements XItemListener {

    private LicenseChooserDialog dialog;

    public AcceptListener(LicenseChooserDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void itemStateChanged(ItemEvent event) {

        XCheckBox accept = ((XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, event.Source));
        try {

            //enable disable dialog controls accoring to the state
            if (accept.getState() == (short) 0) {
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(
                        LicenseChooserDialog.BTN_OK))).setPropertyValue("Enabled", Boolean.FALSE);
            } else {
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(
                        LicenseChooserDialog.BTN_OK))).setPropertyValue("Enabled", Boolean.TRUE);
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
    }

    public void disposing(EventObject arg0) {
    }
}
