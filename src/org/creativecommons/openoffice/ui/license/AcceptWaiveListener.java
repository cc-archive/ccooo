/*
 * AcceptWaiveListener.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author akila
 */
public class AcceptWaiveListener implements XItemListener {

    private ChooserDialog dialog;
    public AcceptWaiveListener(ChooserDialog dialog) {
        this.dialog=dialog;
    }

    @Override
    public void itemStateChanged(ItemEvent event) {

        XCheckBox allow_Remixing = ((XCheckBox) UnoRuntime.queryInterface(XCheckBox.class, event.Source));
        try {

            if (allow_Remixing.getState() == (short) 0) {
                // if remixing is not allowed, you can't require Share-Alike
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.CHK_YES_CC0))).setPropertyValue("Enabled", Boolean.FALSE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.CHK_YES_CC0))).setPropertyValue("State", (short) 0);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.TXT_LEGAL_CODE_CC0))).setPropertyValue("Enabled", Boolean.FALSE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.CMB_TERRITORY))).setPropertyValue("Enabled", Boolean.FALSE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.BTN_OK))).setPropertyValue("Enabled", Boolean.FALSE);
            } else {
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.CHK_YES_CC0))).setPropertyValue("Enabled", Boolean.TRUE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.TXT_LEGAL_CODE_CC0))).setPropertyValue("Enabled", Boolean.TRUE);
                ((XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                        this.dialog.getNameContainer().getByName(ChooserDialog.CMB_TERRITORY))).setPropertyValue("Enabled", Boolean.TRUE);
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
