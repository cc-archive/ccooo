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
        this.chooserDialog.setCancelled(true);
        this.chooserDialog.close();
    }
    
    
    public void disposing(EventObject e) {
    }
}