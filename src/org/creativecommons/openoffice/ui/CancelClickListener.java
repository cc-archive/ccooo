package org.creativecommons.openoffice.ui;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.EventObject;



class CancelClickListener implements XActionListener {
    private final ChooserDialog chooserDialog;

    public CancelClickListener(ChooserDialog chooserDialog){
        this.chooserDialog = chooserDialog;
    }
    
    public void actionPerformed(ActionEvent a) {
        this.chooserDialog.xDialog.endExecute();
    }
    
    
    public void disposing(EventObject e) {
    }
}