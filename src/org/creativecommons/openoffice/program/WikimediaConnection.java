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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author akila
 */
public class WikimediaConnection {

    public final static WikimediaConnection instance = new WikimediaConnection();
    public final static ArrayList<Image> imgList = new ArrayList<Image>();

    protected WikimediaConnection() {
    }

    public WikimediaConnection getInstance() {
        return instance;
    }

    public ArrayList<Image> searchPhotos(String[] tags, int currentPage) {

        BufferedReader in = null;
        imgList.removeAll(imgList);
        String tagLine = "";
        for (int i = 0; i < tags.length; i++) {
            tagLine += "+" + tags[i];
        }
        tagLine.replaceFirst("\\+", "");
        try {
            URL url = new URL("http://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=" + tagLine
                    + "&gsrnamespace=6&gsrlimit=50&gsrprop=timestamp&prop=imageinfo|categories&iiprop=url|size&clcategories=Category:CC-BY&format=xml");
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine = in.readLine();
            System.out.println(inputLine);
            Pattern pattern = Pattern.compile("title=\"File:[\\w\\s\\-\\.\\#\\@\\&]+\\.[a-zA-Z]+\""), widthPattern,heightPattern;
            Matcher matcher = pattern.matcher(inputLine), widthMatcher,heightMatcher;
            int index = 0;
            String title, imgUrl, imgUrlMainPage;
            while (matcher.find()) {
                title = matcher.group().replace("title=\"File:", "").replaceAll("\"", "");
                Image img = new Image(title, null, null, null, null, null, null, null, title, null);
                imgList.add(img);
            }
            index = 0;
            pattern = Pattern.compile(" url=\"[\\w\\s\\-\\.\\#\\@\\&/\\:]+\"");
            matcher = pattern.matcher(inputLine);

            widthPattern = Pattern.compile("width=\"\\d+\"");
            widthMatcher = widthPattern.matcher(inputLine);

            heightPattern = Pattern.compile("height=\"\\d+\"");
            heightMatcher = heightPattern.matcher(inputLine);
            int width, height;
            while (matcher.find()) {
                imgUrl = matcher.group().replace(" url=\"", "").replaceAll("\"", "");
                imgList.get(index).setSelectedImageURL(imgUrl);
                imgUrl = imgUrl.replace("/commons/", "/commons/thumb/");
                widthMatcher.find();
                System.out.println(widthMatcher.group());
                width = Integer.parseInt(widthMatcher.group().replaceAll("width=\"", "").replaceAll("\"","" ));

                heightMatcher.find();
                height = Integer.parseInt(heightMatcher.group().replaceAll("height=\"", "").replaceAll("\"","" ));
                
//                if (width < height) {
//                    imgUrl = imgUrl.concat("/" + String.valueOf(120 * width / height) + "px-" + imgList.get(index).getTitle().replaceAll("\\s", "_"));
//                } else {
                    imgUrl = imgUrl.concat("/120px-" + imgList.get(index).getTitle().replaceAll("\\s", "_"));
//                }
                imgList.get(index).setImgURL(imgUrl);
                index++;

            }
            index = 0;
            pattern = Pattern.compile("descriptionurl=\"[\\w\\s\\-\\.\\#\\@\\&/\\:]+\"");
            matcher = pattern.matcher(inputLine);
            while (matcher.find()) {
                imgUrlMainPage = matcher.group().replace("descriptionurl=\"", "").replaceAll("\"", "");
                imgList.get(index).setImgUrlMainPage(imgUrlMainPage);
                imgList.get(index).setLicenseCode("License info not supported yet");
                imgList.get(index).setLicenseNumber("License info not supported yet");
                imgList.get(index).setLicenseURL("License info not supported yet");
                index++;
            }

        } catch (IOException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return imgList;
    }
}
