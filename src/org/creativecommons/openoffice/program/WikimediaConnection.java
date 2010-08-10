/*
 * WikimediaConnection.java
 * 
 * Copyright 2010, Creative Commons
 * licensed under the GNU LGPL License; see licenses/LICENSE for details
 * 
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

    public ArrayList<Image> searchPhotos(String[] tags, String[] licenses) {

        imgList.removeAll(imgList);
        String tagLine = "";
        for (int i = 0; i < tags.length; i++) {
            tagLine += "+" + tags[i];
        }
        tagLine = tagLine.replaceFirst("\\+", "");
        String title, imgUrl, imgUrlMainPage, imgUrlThumb, licenseNumber = " ", licenseURL = " ",
                licenseCode = "license info not available";
        int width, height;
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("http://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=" + tagLine
                    + "&gsrnamespace=6&gsrlimit=50&gsrprop=timestamp&prop=imageinfo|categories&iiprop=url|size&&clshow=hidden&cllimit=500&format=xml");//CC-BY

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfPages = doc.getElementsByTagName("page");

            for (int s = 0; s < listOfPages.getLength(); s++) {

                Node page = listOfPages.item(s);
                title = page.getAttributes().getNamedItem("title").getNodeValue().replace("File:", "");
                if (page.getNodeType() == Node.ELEMENT_NODE
                        && (!title.endsWith(".ogg"))) {

                    Node imageInfo = goToDepth(page);//page.getChildNodes().item(0).getChildNodes().item(0)
                    imgUrl = imageInfo.getAttributes().getNamedItem("url").getNodeValue();
                    imgUrlMainPage = imageInfo.getAttributes().getNamedItem("descriptionurl").getNodeValue();
                    width = Integer.parseInt(imageInfo.getAttributes().getNamedItem("width").getNodeValue());
                    height = Integer.parseInt(imageInfo.getAttributes().getNamedItem("height").getNodeValue());
                    imgUrlThumb = imgUrl.replace("/commons/", "/commons/thumb/").concat("/120px-"
                            + title.replaceAll("\\s", "_"));
                    if (title.contains(".svg")) {
                        imgUrlThumb = imgUrlThumb.concat(".png");
                        imgUrl = imgUrlThumb.replace("/120px-", "/400px-");
                        height = 400 * height / width;
                        width = 400;
                    } else if (width < 120 || height < 120) {
                        imgUrlThumb = imgUrl;
                    }

                    Image img = new Image(title, null, null, imgUrlThumb, null,
                            null, imgUrlMainPage, null, title, null);

                    NodeList listOfLicenses = page.getLastChild().getChildNodes();
                    for (int l = 0; l < listOfLicenses.getLength(); l++) {
                        String license = listOfLicenses.item(l).getAttributes().
                                getNamedItem("title").getNodeValue().replace("Category:", "");
                        if (license.startsWith("PD") || license.startsWith("Public domain")) {
                            licenseCode = "PD";
                            licenseURL = "http://creativecommons.org/licenses/publicdomain/";
                            licenseNumber = " ";
                            break;
                        } else if (license.startsWith("CC")) {
                            Pattern pattern = Pattern.compile("CC[\\-\\w\\.\\d\\s]+\\d");
                            Matcher matcher = pattern.matcher(license);
                            if (matcher.find()) {
                                licenseCode = matcher.group().replaceAll("\\s", "-");
                                pattern = Pattern.compile("\\d\\.\\d");
                                matcher = pattern.matcher(licenseCode);
                                if (matcher.find()) {
                                    licenseNumber = matcher.group();
                                    licenseURL = "http://creativecommons.org/licenses/"
                                            + licenseCode.toLowerCase().replace("cc-", "").replaceAll("\\-\\d\\.\\d", "/" + licenseNumber);
                                }
                                img.setLicenseURL(licenseURL);
                                licenseCode = licenseCode.replaceAll("-\\d\\.\\d", "").replace("-", " ");
                                img.setLicenseCode(licenseCode);
                            } else if (license.equalsIgnoreCase("CC-Zero")) {
                                licenseCode = "CC0";
                                licenseURL = "http://creativecommons.org/publicdomain/zero/1.0/";
                                licenseNumber = "1.0";
                            }

                        } else if (license.startsWith("GFDL") && (l == listOfLicenses.getLength() - 1 || licenseURL.equals(" "))) {
                            licenseCode = "GFDL";
                            licenseURL = "http://www.gnu.org/licenses/fdl.html";
                            licenseNumber = " ";
                        } else if (license.startsWith("LGPL") && (l == listOfLicenses.getLength() - 1 || licenseURL.equals(" "))) {
                            licenseCode = "LGPL";
                            licenseURL = "http://creativecommons.org/licenses/LGPL/2.1/";//http://www.gnu.org/licenses/lgpl.html
                            licenseNumber = " ";
                        } else if (license.startsWith("GPL") && (l == listOfLicenses.getLength() - 1 || licenseURL.equals(" "))) {
                            licenseCode = "GPL";
                            licenseURL = "http://creativecommons.org/licenses/GPL/2.0/";//http://www.gnu.org/licenses/gpl.html
                            licenseNumber = " ";
                        } else if (license.startsWith("Copyrighted free use") && (l == listOfLicenses.getLength() - 1 || licenseURL.equals(" "))) {
                            licenseCode = "Copyrighted free use";
                            licenseURL = "-";
                            licenseNumber = " ";
                        } else if (license.startsWith("FAL") && (l == listOfLicenses.getLength() - 1 || licenseURL.equals(" "))) {
                            licenseCode = "Free Art License";
                            licenseURL = "http://artlibre.org/licence/lal/en";
                            licenseNumber = " ";
                        }
                    }
                    img.setLicenseURL(licenseURL);
                    img.setLicenseCode(licenseCode);
                    img.setLicenseNumber(licenseNumber);
                    img.setSelectedImageURL(imgUrl);
                    img.setSelectedImageWidth(width);
                    img.setSelectedImageHeight(height);
                    for (int i = 0; i < licenses.length; i++) {
                        if (img.getLicenseCode().equals(licenses[i])) {
                            imgList.add(img);
                            System.out.println(imgUrlMainPage);
                            System.out.println(licenseCode);
                            System.out.println(licenseNumber);
                            break;
                        }
                    }
                }//end of if clause
            }//end of for loop with s var

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXParseException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(WikimediaConnection.class.getName()).log(Level.SEVERE, null, ex);
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
                    + image.getTitle() );
            //Document doc = docBuilder.parse("http://toolserver.org/~magnus/commonsapi.php?image=Book-cover1.jpg");
            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfLicenses = doc.getElementsByTagName("license");
            if (listOfLicenses != null && listOfLicenses.item(0) != null
                    && listOfLicenses.item(0).hasChildNodes()) {
                String license = goToDepth(listOfLicenses.item(0)).getNodeValue();
                if (license.startsWith("PD") || license.startsWith("Public domain")) {
                    licenseCode = "PD";
                    licenseURL = "http://creativecommons.org/licenses/publicdomain/";
                } else if (license.startsWith("CC")) {
                    Pattern pattern = Pattern.compile("CC[\\-\\w\\.\\d\\s]+\\d");
                    Matcher matcher = pattern.matcher(license);
                    if (matcher.find()) {
                        licenseCode = matcher.group();

                        pattern = Pattern.compile("\\d\\.\\d");
                        matcher = pattern.matcher(licenseCode);
                        if (matcher.find()) {
                            licenseNumber = matcher.group();
                            licenseURL = "http://creativecommons.org/licenses/"
                                    + licenseCode.toLowerCase().replace("cc-", "").replaceAll("\\-\\d\\.\\d", "/" + licenseNumber);
                        }
                        image.setLicenseURL(licenseURL);
                        licenseCode = licenseCode.replace("-", " ").replaceAll("\\d\\.\\d", "");
                        image.setLicenseCode(licenseCode);
                    } else if (license.equalsIgnoreCase("CC-Zero")) {
                        licenseCode = "CC0";
                        licenseURL = "http://creativecommons.org/publicdomain/zero/1.0/";
                        licenseNumber = "1.0";
                    }
                } else if (license.startsWith("GFDL")) {
                    licenseCode = "GFDL";
                    licenseURL = "http://www.gnu.org/licenses/fdl.html";
                } else if (license.startsWith("LGPL")) {
                    licenseCode = "LGPL";
                    licenseURL = "http://creativecommons.org/licenses/LGPL/2.1/";//http://www.gnu.org/licenses/lgpl.html
                } else if (license.startsWith("GPL")) {
                    licenseCode = "GPL";
                    licenseURL = "http://creativecommons.org/licenses/GPL/2.0/";//http://www.gnu.org/licenses/gpl.html
                } else if (license.startsWith("Copyrighted free use")) {
                    licenseCode = "Copyrighted free use";
                    licenseURL = "-";
                } else if (license.startsWith("FAL")) {
                    licenseCode = "Free Art License";
                    licenseURL = "http://artlibre.org/licence/lal/en";
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
                image.setUserName(goToDepth(listOfAuthors.item(0).getLastChild()).getNodeValue());
                System.out.println(goToDepth(listOfAuthors.item(0).getLastChild()));
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

    private Node goToDepth(Node node) {
        if (node.hasChildNodes()) {
            return goToDepth(node.getFirstChild());
        } else {
            return node;
        }
    }
}
