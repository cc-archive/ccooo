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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
        String title = null, imgUrl = null, imgUrlMainPage = null,
                imgUrlThumb = null, userID = null, userName=null,
                profile = null, licenseCode = null, licenseURL = null, licenseNumber = null;
        try {
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("http://picasaweb.google.com/data/feed/api/"
                    + "all?&kind=photo&access=public&filter=1&q="//,320,640,800
                    + tagLine + "&imglic=creative_commons&thumbsize=144&max-results=500" +
                    "&fields=entry["+licenseID+"](title," +
                    "link[@rel='alternate'](@href),author(uri),gphoto:width," +
                    "gphoto:height,gphoto:license(@url)," +
                    "media:group(media:credit,media:thumbnail,media:content(@url)))");
            //&imgmax=94, 110, 128, 200, 220, 288, 320, 400, 512, 576, 640, 720, 800, 912, 1024, 1152, 1280, 1440, 1600

            //normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfItems = doc.getElementsByTagName("entry");

            for (int s = 0; s < listOfItems.getLength(); s++) {

                for (int i = 0; i < listOfItems.item(s).getChildNodes().getLength(); i++) {
                    Node node = listOfItems.item(s).getChildNodes().item(i);
                    String value = goToDepth(node).getNodeValue();
                    String nodeName = node.getNodeName();
                    if (nodeName.equalsIgnoreCase("title")) {
                        title = value;
                    } else if (nodeName.equalsIgnoreCase("link")) {
                        imgUrlMainPage = node.getAttributes().getNamedItem("href").getNodeValue();
                    } else if (nodeName.equalsIgnoreCase("author")) {
                        userID = value.replace("http://picasaweb.google.com/", "");
                        userName=userID;
                        profile = value;
                    } else if (nodeName.equalsIgnoreCase("enclosure")) {
                        imgUrl = node.getAttributes().getNamedItem("url").getNodeValue();
                    } else if (nodeName.equalsIgnoreCase("media:group")) {
                        for (int j = 0;
                                j < node.getChildNodes().getLength(); j++) {

                            if (node.getChildNodes().item(j).getNodeName().
                                    equalsIgnoreCase("media:thumbnail")) {
                                imgUrlThumb = node.getChildNodes().item(j).getAttributes().
                                        getNamedItem("url").getNodeValue();
                            } else if (node.getChildNodes().item(j).getNodeName().
                                    equalsIgnoreCase("media:content")) {
                                imgUrl = node.getChildNodes().item(j).getAttributes().getNamedItem("url").getNodeValue();
                            } else if (node.getChildNodes().item(j).getNodeName().
                                    equalsIgnoreCase("media:credit")) {
                                userName = node.getChildNodes().item(j).getFirstChild().getNodeValue();
                            }
                        }
                    } else if (nodeName.equalsIgnoreCase("gphoto:license")) {
                        if (value.equals("ATTRIBUTION")) {
                            licenseCode = "cc by";
                        } else if (value.equals("ATTRIBUTION_SHARE_ALIKE")) {
                            licenseCode = "cc by sa";
                        } else if (value.equals("ATTRIBUTION_NO_DERIVATIVES")) {
                            licenseCode = "cc by nd";
                        } else if (value.equals("ATTRIBUTION_NON_COMMERCIAL")) {
                            licenseCode = "cc by nc";
                        } else if (value.equals("ATTRIBUTION_NON_COMMERCIAL_SHARE_ALIKE")) {
                            licenseCode = "cc by nc sa";
                        } else if (value.equals("ATTRIBUTION_NON_COMMERCIAL_NO_DERIVATIVES")) {
                            licenseCode = "cc by nc nd";
                        }
                        licenseURL = node.getAttributes().getNamedItem("url").getNodeValue();
                        licenseNumber = licenseURL.split("/")[licenseURL.split("/").length - 1];
                    }
                }

                if (title != null) {
                    Image img = new Image(title, null, null, imgUrlThumb, profile,
                            null, imgUrlMainPage, userID, title, null);
                    img.setSelectedImageURL(imgUrl);
                    img.setUserName(userName);
                    img.setLicenseCode(licenseCode);
                    img.setLicenseNumber(licenseNumber);
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
}
