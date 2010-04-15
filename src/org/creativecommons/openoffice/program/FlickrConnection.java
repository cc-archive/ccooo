/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.creativecommons.openoffice.program;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photos.Size;
import com.aetrion.flickr.photos.licenses.LicensesInterface;
import com.aetrion.flickr.people.User;
import java.util.ArrayList;
import org.creativecommons.license.License;
import com.aetrion.flickr.people.PeopleInterface;
import java.io.IOException;
import java.util.Date;
import org.creativecommons.openoffice.ui.flickr.PictureFlickrDialog;
import org.xml.sax.SAXException;

/**
 *
 * @author Husleag Mihai
 */
public class FlickrConnection {

    public String apiKEY = "0f92f6ce471474b52886a4ce2a512e85"; // used to ID the application
    public String sharedSecret = "e2e9d20a7b2a1cf8"; 	// used for signed calls
    // The above key is a Non-Commercial API key for NiMaL13 flickr user
    public final static FlickrConnection instance = new FlickrConnection();
    private Flickr flickr = new Flickr(apiKEY);
    private SearchParameters currentSearch = null;

    protected FlickrConnection() {
    }

    public FlickrConnection getInstance() {
        return instance;
    }

    public ArrayList<Image> searchPhotos(String[] tags, String licenseId, int currentPage) {
        currentSearch = new SearchParameters();
        currentSearch.setSort(SearchParameters.INTERESTINGNESS_DESC);
        currentSearch.setTagMode("all");
        if (tags.length > 0) {
            currentSearch.setTags(tags);
        }

        if (licenseId.length() > 0) {
            currentSearch.setLicense(licenseId);
        }

        PhotosInterface pInterf = flickr.getPhotosInterface();
        PhotoList list = null;
        try {
            list = pInterf.search(currentSearch, PictureFlickrDialog.SHOWRESULTSPERROW
                    * PictureFlickrDialog.SHOWRESULTSPERCOLUMN, currentPage);
        } catch (com.aetrion.flickr.FlickrException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (org.xml.sax.SAXException ex) {
            ex.printStackTrace();
        }

        ArrayList<Image> imgList = new ArrayList<Image>();

        int count = 0;
        for (Object p : list.toArray()) {
            Photo ph = ((Photo) p);
            User user = ph.getOwner();

            count++;
            if (count > 100) {
                break;
            }

            String profile = ph.getUrl();
            profile = profile.substring(0, profile.lastIndexOf("/"));
            Image img = new Image(ph.getTitle(), ph.getDateTaken(), ph.getDateAdded(),
                    ph.getSmallSquareUrl(), profile, ph.getTags(), ph.getUrl(), user.getId(), ph.getId(), ph.getSecret());

            imgList.add(img);

        }

        return imgList;
    }

    public java.util.Collection getLicenses() {

        LicensesInterface lic = new LicensesInterface(apiKEY, sharedSecret, flickr.getTransport());

        try {
            return lic.getInfo();
        } catch (com.aetrion.flickr.FlickrException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (org.xml.sax.SAXException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getUserName(String userID) {
        User userInfo = null;

        PeopleInterface people = new PeopleInterface(apiKEY, sharedSecret, flickr.getTransport());

        try {
            /***************************/
            long time = new Date().getTime();
            userInfo = people.getInfo(userID); //////////bottleneck
            System.out.println("Flicker  getUserName" + (new Date().getTime() - time));
            /****************************/
            return userInfo.getUsername();
        } catch (com.aetrion.flickr.FlickrException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (org.xml.sax.SAXException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public java.util.Collection getPhotoSizes(String photoID) {
        PhotosInterface photos = flickr.getPhotosInterface();

        try {
            return photos.getSizes(photoID);
        } catch (com.aetrion.flickr.FlickrException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (org.xml.sax.SAXException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Photo getPhotoInfo(String photoID, String secret) {
        PhotosInterface photos = flickr.getPhotosInterface();

        try {
            return photos.getInfo(photoID, secret);
        } catch (com.aetrion.flickr.FlickrException ex) {
            ex.printStackTrace();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (org.xml.sax.SAXException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String getStringSize(int size) {
        switch (size) {
            case Size.THUMB:
                return "Thumbnail";
            case Size.SQUARE:
                return "Square";
            case Size.SMALL:
                return "Small";
            case Size.MEDIUM:
                return "Medium";
            case Size.LARGE:
                return "Large";
            case Size.ORIGINAL:
                return "Original";
            default:
                return "";
        }
    }

    public SearchParameters getSearch() {
        return this.currentSearch;
    }
}
