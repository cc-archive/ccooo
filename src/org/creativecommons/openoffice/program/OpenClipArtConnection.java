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
    public final static ArrayList<Image> imgList = new ArrayList<Image>();

    protected OpenClipArtConnection() {
    }

    public ArrayList<Image> searchPhotos(String[] tags, int currentPage) {

        BufferedReader in = null;
        imgList.removeAll(imgList);
        String tagLine = "";
        for (int i = 0; i < tags.length; i++) {
            tagLine += "+" + tags[i];
        }
        tagLine = tagLine.replaceFirst("\\+", "");
        try {
            URL url = new URL("http://testvm.openclipart.org/cchost/api/query?limit=100&tags=" 
                    + tagLine + "&format=csv&t=links_by_dl_ul&lic=pd");
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine, title, imgUrl, profile, imgUrlMainPage, userID, photoID, userName;
            String[] list;
            int count = 0;
            while ((inputLine = in.readLine()) != null) {
                list = inputLine.split(",");
                title = list[2];
                profile = list[3].replaceFirst("testvm.", "").
                        replaceFirst("/cchost/people/", "/user-detail/");
                imgUrl = list[8].replaceFirst("/cchost/content/", "/people/").
                        replaceFirst("testvm.", "").replace(".svg", ".png");
                imgUrlMainPage = list[1];//the image is not shown in the current main page the url has to be cnaged
                userID = list[4];
                photoID = list[0];
                userName = list[6];
                count++;
                if (count > 100) {
                    break;
                }
                Image img = new Image(title, null, null, imgUrl, profile, null,
                        imgUrlMainPage, userID, photoID, "");
                img.setUserName(userName);
                img.setSelectedImageURL(imgUrl);
                img.setLicenseCode("PD");
                img.setLicenseNumber("");
                img.setLicenseURL("http://creativecommons.org/licenses/publicdomain/");
                imgList.add(img);

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
