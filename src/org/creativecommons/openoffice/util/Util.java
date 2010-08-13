/*
 * Util.java
 *
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 */
package org.creativecommons.openoffice.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author akila
 * Get strings from the resource bundle
 */
public class Util {

    private static Locale locale;

    public static void setLocale(Locale locale) {
        Util.locale = locale;
    }

    public static String _(String s) {
        try {
            return ResourceBundle.getBundle("Messages", locale).getString(s);
        } catch (MissingResourceException e) {
            return s;
        }
    }
}
