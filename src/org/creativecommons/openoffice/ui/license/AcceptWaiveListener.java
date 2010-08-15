/*
 * AcceptWaiveListener.java
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
 * Enable CC0 deed and territory etc. after accepting to waive.
 * @author akila
 */
public class AcceptWaiveListener implements XItemListener {

    private LicenseChooserDialog dialog;

    public AcceptWaiveListener(LicenseChooserDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void itemStateChanged(ItemEvent event) {

        XCheckBox waive = ((XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, event.Source));
        try {

            //enable disable dialog controls accoring to the state
            if (waive.getState() == (short) 0) {
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.CHK_YES_CC0))).setPropertyValue("Enabled", Boolean.FALSE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.CHK_YES_CC0))).setPropertyValue("State", (short) 0);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.TXT_LEGAL_CODE_CC0))).setPropertyValue("Enabled", Boolean.FALSE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.CMB_TERRITORY))).setPropertyValue("Enabled", Boolean.FALSE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.BTN_OK))).setPropertyValue("Enabled", Boolean.FALSE);
            } else {
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.CHK_YES_CC0))).setPropertyValue("Enabled", Boolean.TRUE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.TXT_LEGAL_CODE_CC0))).setPropertyValue("Enabled", Boolean.TRUE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(LicenseChooserDialog.CMB_TERRITORY))).setPropertyValue("Enabled", Boolean.TRUE);
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
