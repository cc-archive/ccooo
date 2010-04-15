/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akila
 */
public class OpenClipArtConnection {

    public final static OpenClipArtConnection instance = new OpenClipArtConnection();

    protected OpenClipArtConnection() {
    }

    public OpenClipArtConnection getInstance() {
        return instance;
    }

    public ArrayList<Image> searchPhotos(String[] tags, int currentPage) {

        BufferedReader in = null;
        ArrayList<Image> imgList = new ArrayList<Image>();
        String tagLine = "";
        for (int i = 0; i < tags.length; i++) {
            tagLine += "+" + tags[i];
        }
        tagLine.replaceFirst("\\+", "");
        try {
            URL url = new URL("http://testvm.openclipart.org/cchost/api/query?limit=100&tags=" + tagLine
                    + "&format=csv&t=links_by_dl_ul&lic=pd");
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            String[] list;
            int count = 0;
            while ((inputLine = in.readLine()) != null) {
                list = inputLine.split(",");
//                System.out.println("_________________________________");
//                System.out.println("Image " + list[8].replaceFirst("/cchost/content/","/people/").replaceFirst("testvm.", ""));//replaceFirst("/cchost/content/[a-zA-z_]+/","/image/200px/svg_to_png/")
//                System.out.println("Image Name " + list[2]);
//                System.out.println("Image URL " + "http://www.openclipart.org/detail/"+list[0]);
//                System.out.println("User Name " + list[6]);
//                System.out.println("Real Name " + list[4]);
//                System.out.println("Real Name " + list[0]);
                list[3] = list[3].replaceFirst("testvm.", "").replaceFirst("/cchost/people/", "/user-detail/");
                list[8] = list[8].replaceFirst("/cchost/content/", "/people/").replaceFirst("testvm.", "");
                //list[1]="http://www.openclipart.org/detail/"+list[0];
                count++;
                if (count > 100) {
                    break;
                }
                if (/*list[8].endsWith(".png")*/count > (currentPage - 1) * 16) {
                    Image img = new Image(list[2], null, null, list[8], list[3], null, list[1], list[4], list[0], "");
                    img.setUserName(list[6]);
                    img.setSelectedImageURL(list[8]);
                    img.setLicenseCode("PD");
                    img.setLicenseNumber("");
                    img.setLicenseURL("http://creativecommons.org/licenses/publicdomain/");
                    imgList.add(img);
                }
            }
            imgList.remove(0);

        } catch (IOException ex) {
            Logger.getLogger(OpenClipArtConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(OpenClipArtConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return imgList;
    }
}
