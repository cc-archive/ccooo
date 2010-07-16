/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author akila
 */
public class Util {

//    private static ResourceBundle catalog =
//            ResourceBundle.getBundle("Messages");
    private static Locale locale;
    public static void setLocale (Locale locale){
        Util.locale=locale;
    }

    public static String _(String s) {
        try {
            return ResourceBundle.getBundle("Messages", locale).getString(s);
            //return catalog.getString(s);
        } catch (MissingResourceException e) {
            return s;
        }
    }
}
