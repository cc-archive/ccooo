/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * TODO:
 * Support for selecting images based on license
 * Get different sizes of images
 *
 */
package org.creativecommons.openoffice.program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
public class PicasaConnection {

    public final static PicasaConnection instance = new PicasaConnection();
    public final static ArrayList<Image> imgList = new ArrayList<Image>();

    protected PicasaConnection() {
    }

    public ArrayList<Image> searchPhotos(String[] tags, String licenseID) {

        imgList.removeAll(imgList);
        String tagLine = "";
        for (int i = 0; i < tags.length; i++) {
            tagLine += "+" + tags[i];
        }
        tagLine = tagLine.replaceFirst("\\+", "");
        String title = null, imgUrl = null, imgUrlMainPage = null, imgUrlThumb = null, licenseURL = null, userID = null, profile = null;
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("http://picasaweb.google.com/data/feed/base/"
                    + "all?alt=rss&kind=photo&access=public&filter=1&q="
                    + tagLine + "&imglic="+licenseID+"&thumbsize=144");//CC-BY
            //&imgmax=94, 110, 128, 200, 220, 288, 320, 400, 512, 576, 640, 720, 800, 912, 1024, 1152, 1280, 1440, 1600

            //normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfItems = doc.getElementsByTagName("item");

            for (int s = 0; s < listOfItems.getLength(); s++) {

                System.out.println(s);
                for (int i = 0; i < listOfItems.item(s).getChildNodes().getLength(); i++) {
                    String value = goToDepth(listOfItems.item(s).getChildNodes().item(i)).getNodeValue();
                    String node = listOfItems.item(s).getChildNodes().item(i).getNodeName();
                    if (node.equalsIgnoreCase("title")) {
                        title = value;
                    } else if (node.equalsIgnoreCase("link")) {
                        imgUrlMainPage = value;
                    } else if (node.equalsIgnoreCase("author")) {
                        userID = value.split("\\(")[0].trim();
                        profile = "http://picasaweb.google.com/" + userID;
                    } else if (node.equalsIgnoreCase("enclosure")) {
                        imgUrl = listOfItems.item(s).getChildNodes().item(i).
                                getAttributes().getNamedItem("url").getNodeValue();
                    } else if (node.equalsIgnoreCase("media:group")) {
                        for (int j = 0;
                                j < listOfItems.item(s).getChildNodes().item(i).
                                getChildNodes().getLength(); j++) {

                            if (listOfItems.item(s).getChildNodes().item(i).
                                    getChildNodes().item(j).getNodeName().
                                    equalsIgnoreCase("media:thumbnail")) {

                                imgUrlThumb = listOfItems.item(s).getChildNodes().item(i).
                                        getChildNodes().item(j).getAttributes().
                                        getNamedItem("url").getNodeValue();
                            }
                        }
                    } else if (node.equalsIgnoreCase("guid")) {
                        licenseURL = value.split("\\?")[0];
                        System.out.println(licenseURL);
                    }
                }

                if (title != null) {
                    Image img = new Image(title, null, null, imgUrlThumb, profile,
                            null, imgUrlMainPage, userID, title, null);
                    img.setSelectedImageURL(imgUrl);
                    img.setUserName(userID);
                    img.setLicenseCode("-");
                    img.setLicenseNumber("-");
                    img.setLicenseURL(licenseURL);
                    imgList.add(img);
                }

            }//end of for loop with s var

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXParseException ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
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

    public void setImageLisence(Image image) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(image.getLicenseURL());
            doc.getDocumentElement().normalize();
            NodeList listOfLinks = doc.getElementsByTagName("link");
            for (int i = 0; i < listOfLinks.getLength(); i++) {
                if (listOfLinks.item(i).getAttributes().getNamedItem("rel").
                        getNodeValue().equalsIgnoreCase("http://schemas.google.com/photos/2007#canonical")) {
                    URL url = new URL(listOfLinks.item(i).getAttributes().
                            getNamedItem("href").getNodeValue());
                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(url.openStream()));
                    //System.out.println("Modi before reading " + (new Date().getTime() - time));
                    String inputLine = "";
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("ATTRIBUTION")) {
                            if (inputLine.contains("ATTRIBUTION_SHARE_ALIKE")) {
                                image.setLicenseCode("cc-by-sa");
                                image.setLicenseURL("http://creativecommons.org/licenses/by-sa/3.0/");

                            } else if (inputLine.contains("ATTRIBUTION_NO_DERIVATIVES")) {
                                image.setLicenseCode("cc-by-nd");
                                image.setLicenseURL("http://creativecommons.org/licenses/by-nd/3.0/");

                            } else if (inputLine.contains("ATTRIBUTION_NON_COMMERCIAL")) {
                                image.setLicenseCode("cc-by-nc");
                                image.setLicenseURL("http://creativecommons.org/licenses/by-nc/3.0/");

                            } else if (inputLine.contains("ATTRIBUTION_NON_COMMERCIAL_SHARE_ALIKE")) {
                                image.setLicenseCode("cc-by-nc-sa");
                                image.setLicenseURL("http://creativecommons.org/licenses/by-nc-sa/3.0/");

                            } else if (inputLine.contains("ATTRIBUTION_NON_COMMERCIAL_NO_DERIVATIVES")) {
                                image.setLicenseCode("cc-by-nc-nd");
                                image.setLicenseURL("http://creativecommons.org/licenses/by-nc-nd/3.0/");

                            } else {
                                image.setLicenseCode("cc-by");
                                image.setLicenseURL("http://creativecommons.org/licenses/by/3.0");
                            }
                            image.setLicenseNumber("3.0");
                        }
                    }
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PicasaConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
