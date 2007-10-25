/*
 * Ressources.java
 *
 * copyright 2007, Creative Commons
 * licensed under the MIT License; see docs/LICENSE for details.
 *
 * Created on Jul 7, 2007
 *
 */

package org.creativecommons.openoffice.util;

import java.io.File;

public class Ressources {
	private static String extensionPath = null;
	
	public static void getextensionPath() {
		if (extensionPath == null) {
			String classPath = System.getProperty("java.class.path");
			String[] tab = classPath.split(";");
			for (String s : tab) {
                            System.err.println("S: "+s);
				int pos;
				if ((pos = s.indexOf("ccooo.oxt")) != -1 ) {
					s = s.substring(0, pos + 19/* 9*/);// 19 ???
					File path = new File(s);
					extensionPath = path.getPath();
					break;
				}
			}
		}
	}
	
	public static String getImage(String name) {
		getextensionPath();
		return extensionPath + File.separator + "images" + File.separator + name;
	}
	
	public static String getFile(String name) {
		getextensionPath();
		return extensionPath + File.separator + name;
	}
}
