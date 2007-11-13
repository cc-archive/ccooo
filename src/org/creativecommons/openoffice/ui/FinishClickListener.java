package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;
import org.creativecommons.license.Chooser;
import org.creativecommons.license.License;
import org.creativecommons.openoffice.program.Calc;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.program.Impress;
import org.creativecommons.openoffice.program.Writer;



class FinishClickListener implements XActionListener {

    private ChooserDialog chooserDialog;
    private CcOOoAddin addin;
    
    public FinishClickListener(ChooserDialog chooserDialog, CcOOoAddin addin){

        this.chooserDialog = chooserDialog;
        this.addin = addin;
        
    } // OnFinishClick - public constructor
        
    public void actionPerformed(ActionEvent a) {
        
        this.chooserDialog.close();
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
} // OnFinishClick