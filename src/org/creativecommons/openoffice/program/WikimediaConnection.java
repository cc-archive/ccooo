/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.program;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class WikimediaConnection {

    public final static WikimediaConnection instance = new WikimediaConnection();
    public final static ArrayList<Image> imgList = new ArrayList<Image>();

    protected WikimediaConnection() {
    }

    public ArrayList<Image> searchPhotos(String[] tags, int currentPage) {

        imgList.removeAll(imgList);
        String tagLine = "";
        for (int i = 0; i < tags.length; i++) {
            tagLine += "+" + tags[i];
        }
        tagLine = tagLine.replaceFirst("\\+", "");
        String title, imgUrl, imgUrlMainPage, imgUrlThumb;
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("http://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=" + tagLine
                    + "&gsrnamespace=6&gsrlimit=50&gsrprop=timestamp&prop=imageinfo|categories&iiprop=url|size&clcategories=Category:CC&format=xml");//CC-BY

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfPages = doc.getElementsByTagName("page");

            for (int s = 0; s < listOfPages.getLength(); s++) {

                Node page = listOfPages.item(s);
                title = page.getAttributes().getNamedItem("title")
                        .getNodeValue().replace("File:", "");
                if (page.getNodeType() == Node.ELEMENT_NODE) {

                    Node imageInfo = page.getChildNodes().item(0).getChildNodes().item(0);
                    imgUrl = imageInfo.getAttributes().getNamedItem("url").getNodeValue();
                    imgUrlMainPage = imageInfo.getAttributes()
                            .getNamedItem("descriptionurl").getNodeValue();
                    imgUrlThumb = imgUrl.replace("/commons/", "/commons/thumb/")
                            .concat("/120px-" + title.replaceAll("\\s", "_"));
                    if (title.contains(".svg")) {
                        imgUrlThumb = imgUrlThumb.concat(".png");
                        imgUrl = imgUrlThumb.replace("/120px-", "/400px-");
                    }

                    Image img = new Image(title, null, null, imgUrlThumb, null,
                            null, imgUrlMainPage, null, title, null);

                    img.setSelectedImageURL(imgUrl);

                    img.setLicenseCode("License info not available");
                    img.setLicenseNumber("-");
                    img.setLicenseURL("-");
                    //setImageLisence(img);
                    imgList.add(img);

                    System.out.println(imageInfo.getAttributes().getNamedItem("width").getNodeValue());
                    System.out.println(imageInfo.getAttributes().getNamedItem("height").getNodeValue());
                }//end of if clause
            }//end of for loop with s var

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXParseException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();

        } catch (IOException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imgList;
    }

    public void setImageLisence(Image image) {
        try {
            String licenseNumber = " ", licenseURL = " ",
                    licenseCode = "license info not available";
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(
                    "http://commonstest.hostzi.com/CommonsAPI/commonsapi.php?image="
                    + image.getTitle());
            //Document doc = docBuilder.parse("http://toolserver.org/~magnus/commonsapi.php?image=Book-cover1.jpg");
            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfLicenses = doc.getElementsByTagName("license");
            if (listOfLicenses != null && listOfLicenses.item(0) != null
                    && listOfLicenses.item(0).hasChildNodes()) {
                String license = goToDepth(listOfLicenses.item(0).getFirstChild());
                if (license.startsWith("PD")) {
                    licenseCode = "PD";
                    licenseURL = "http://creativecommons.org/licenses/publicdomain/";
                }
                if (license.startsWith("CC")) {
                    Pattern pattern = Pattern.compile("CC[\\-\\w\\.\\d\\s]+\\d");
                    Matcher matcher = pattern.matcher(license);
                    if (matcher.find()) {
                        licenseCode = matcher.group();

                        pattern = Pattern.compile("\\d\\.\\d");
                        matcher = pattern.matcher(licenseCode);
                        if (matcher.find()) {
                            licenseNumber = matcher.group();
                            licenseURL = "http://creativecommons.org/licenses/" 
                                    + licenseCode.toLowerCase().replace("cc-", "")
                                    .replaceAll("\\-\\d\\.\\d", "/" + licenseNumber);
                        }
                        image.setLicenseURL(licenseURL);
                        licenseCode = licenseCode.replace("-", " ").replaceAll("\\d\\.\\d", "");
                        image.setLicenseCode(licenseCode);
                        System.out.println(licenseCode);
                    }
                }
                if (license.startsWith("GFDL")) {
                    licenseCode = "GFDL";
                    licenseURL = "http://www.gnu.org/licenses/fdl.html";
                }
                image.setLicenseURL(licenseURL);
                image.setLicenseCode(licenseCode);
                image.setLicenseNumber(licenseNumber);
                System.out.println(licenseCode);
                System.out.println(licenseNumber);
                System.out.println(licenseURL);
            }

            NodeList listOfAuthors = doc.getElementsByTagName("author");
            if (listOfAuthors != null && listOfAuthors.item(0) != null
                    && listOfAuthors.item(0).hasChildNodes()) {
                image.setUserName(goToDepth(listOfAuthors.item(0).getLastChild()));
            }

        } catch (IOException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXParseException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String goToDepth(Node node) {
        if (node.hasChildNodes()) {
            return goToDepth(node.getFirstChild());
        } else {
            return node.getNodeValue();
        }
    }
}
