/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.license;

/**
 *
 * @author akila
 */
public class StoreThread extends Thread{
    @Override
    public void run() {
     Store.get();
    }
}
