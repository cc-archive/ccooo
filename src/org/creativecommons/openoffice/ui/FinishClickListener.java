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
import org.creativecommons.openoffice.Calc;
import org.creativecommons.openoffice.CcOOoAddin;
import org.creativecommons.openoffice.Impress;
import org.creativecommons.openoffice.Writer;



class FinishClickListener implements XActionListener {

    private ChooserDialog chooserDialog;
    private CcOOoAddin addin;
    
    public FinishClickListener(ChooserDialog chooserDialog, CcOOoAddin addin){

        this.chooserDialog = chooserDialog;
        this.addin = addin;
        
    } // OnFinishClick - public constructor
        
    public void actionPerformed(ActionEvent a) {
        
        // retrieve the selected License
        License selected = this.chooserDialog.getSelectedLicense();
        
        if (this.addin.getServiceType().equalsIgnoreCase("spreadsheet")) {
            
            Calc.embedGraphic(addin.getCurrentComponent(), selected.getImageUrl());
            Calc.insertLicenseText(addin.getCurrentComponent(), selected.getName());
            
        }  else if (this.addin.getServiceType().equalsIgnoreCase("text")) {
            
            Writer.createLicenseTextField(addin.getCurrentComponent(),
                    selected.getName(),selected.getLicenseUri(),selected.getImageUrl());
            
        }  else if (this.addin.getServiceType().equalsIgnoreCase("presentation")) {
            
            Impress.embedGraphic(addin.getCurrentComponent(), selected.getImageUrl());
            Impress.insertLicenseText(addin.getCurrentComponent(), selected.getName());
            
        }  else if (this.addin.getServiceType().equalsIgnoreCase("drawing")) {
            
        }

        this.addin.insertLicenseMetadata(selected.getName(), selected.getLicenseUri());
                    
        this.chooserDialog.xDialog.endExecute();
        
    } // actionPerformed
    
    public void disposing(EventObject e) {
    }
} // OnFinishClick