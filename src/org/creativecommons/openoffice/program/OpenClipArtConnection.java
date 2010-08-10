/*
 * OpenClipArtConnection.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 */
package org.creativecommons.openoffice.program;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.*;

/**
 *
 * @author akila
 */
public class OpenClipArtConnection {

    public final static OpenClipArtConnection instance = new OpenClipArtConnection();
    public final static ArrayList<Image> imgList = new ArrayList<Image>();

    protected OpenClipArtConnection() {
    }

    public ArrayList<Image> searchPhotos(String[] tags) {

        imgList.removeAll(imgList);
        String tagLine = "";
        for (int i = 0; i < tags.length; i++) {
            tagLine += "," + tags[i];
        }
        tagLine = tagLine.replaceFirst(",", "");
        String title, imgUrl, imgUrlMainPage, imgUrlThumb, licenseURL, userID, profile;
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("http://www.openclipart.org/media/feed/rss/" + tagLine);//CC-BY

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfTitles = doc.getElementsByTagName("title");
            NodeList listOfDiscriptions = doc.getElementsByTagName("link");
            NodeList listOfURLs = doc.getElementsByTagName("enclosure");
            NodeList listOfThumbs = doc.getElementsByTagName("media:thumbnail");
            NodeList listOfLicenses = doc.getElementsByTagName("cc:license");
            NodeList listOfCreators = doc.getElementsByTagName("dc:creator");

            for (int s = 0; s < listOfURLs.getLength(); s++) {
                
                title = goToDepth(listOfTitles.item(s + 2)).getNodeValue();
                imgUrlMainPage = goToDepth(listOfDiscriptions.item(s + 2)).getNodeValue();
                imgUrl = listOfURLs.item(s).getAttributes().getNamedItem("url").getNodeValue();
                licenseURL = goToDepth(listOfLicenses.item(s)).getNodeValue();
                imgUrlThumb = listOfThumbs.item(s).getAttributes().getNamedItem("url").getNodeValue();
                if (imgUrl.contains(".svg")) {
                    imgUrl=imgUrlThumb.replace("/90px/", "/400px/");
                    imgUrlThumb = imgUrlThumb.replace("/90px/", "/120px/"); 
                }
                userID = goToDepth(listOfCreators.item(s)).getNodeValue();
                profile = "http://www.openclipart.org/user-detail/" + userID;
                Image img = new Image(title, null, null, imgUrlThumb, profile,
                        null, imgUrlMainPage, userID, title, null);

                img.setSelectedImageURL(imgUrl);
                img.setUserName(userID);
                img.setLicenseCode("PD");
                img.setLicenseNumber("-");
                img.setLicenseURL(licenseURL);
                imgList.add(img);

            }//end of for loop with s var

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OpenClipArtConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXParseException ex) {
            Logger.getLogger(OpenClipArtConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OpenClipArtConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenClipArtConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OpenClipArtConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imgList;
    }

    private Node goToDepth(Node node) {
        if (node.hasChildNodes()) {
            return goToDepth(node.getFirstChild());
        } else {
            return node;
        }
    }
}
