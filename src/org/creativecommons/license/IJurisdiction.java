/*
 * IJurisdiction.java
 *
 * Created on November 12, 2007, 7:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.creativecommons.license;

/**
 *
 * @author nathan
 */
public interface IJurisdiction extends Comparable {
    String getTitle();

    String getTitle(String lang);

    String toString();
    
}
