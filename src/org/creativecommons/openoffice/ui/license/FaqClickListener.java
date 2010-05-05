/*
 * FinishClickListener.java
 *
 * Copyright 2007, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 *
 */

package org.creativecommons.openoffice.ui.license;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.system.XSystemShellExecute;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;



class FaqClickListener implements XActionListener {

    private XComponentContext m_xContext = null;

    public FaqClickListener(ChooserDialog chooserDialog, XComponentContext m_xContext){
        this.m_xContext=m_xContext;

    } // OnFinishClick - public constructor

    public void actionPerformed(ActionEvent a) {

        try {
                final XMultiComponentFactory xFact = m_xContext.getServiceManager();
                final Object xObject = xFact.createInstanceWithContext(
                        "com.sun.star.system.SystemShellExecute", m_xContext);
                final XSystemShellExecute xSystemShellExecute = (XSystemShellExecute)
                        UnoRuntime.queryInterface(XSystemShellExecute.class, xObject);
                final String aURLString = "http://wiki.creativecommons.org/Frequently_Asked_Questions";
                xSystemShellExecute.execute( aURLString, "",
                        com.sun.star.system.SystemShellExecuteFlags.DEFAULTS );
            } catch( com.sun.star.uno.Exception ex) {
                ex.printStackTrace();
            }

    } // actionPerformed

    public void disposing(EventObject e) {
    }
} // OnFinishClick