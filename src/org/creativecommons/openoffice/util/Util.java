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
 * Get localized strings from the resource bundle.
 * @author akila
 */
public class Util {

    private static Locale locale;

    public static void setLocale(Locale locale) {
        Util.locale = locale;
    }
    
    /**
     * Get the localized string. If the string is not available returen the key.
     * @param key Key of for the loacalized string
     * @return Localized string
     */
    public static String _(String key) {
        try {
            return ResourceBundle.getBundle("Messages", locale).getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
