/*
 * StoreThread.Java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
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
