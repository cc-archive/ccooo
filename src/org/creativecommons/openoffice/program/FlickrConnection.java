/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.program;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.people.User;
import java.util.ArrayList;
import org.creativecommons.license.License;
import com.aetrion.flickr.people.PeopleInterface;
import java.io.IOException;
import org.xml.sax.SAXException;

/**
 *
 * @author Administrator
 */
public class FlickrConnection {

    public  String  apiKEY= "c5e0a253b7f94e354d62c9b64d589f73";
    public final static FlickrConnection instance = new FlickrConnection();
    private Flickr flickr = new Flickr(apiKEY);
    
    protected FlickrConnection() {        
    }
    
    public FlickrConnection getInstance()
    {
        return instance;
    }
    
    public ArrayList<Image> searchPhotos(String[] tags, String license)
    {
        SearchParameters sp = new SearchParameters();       
        sp.setSort(SearchParameters.RELEVANCE);
        if (tags.length>0)
            sp.setTags(tags);
      
        if (license.length()>0)
            sp.setLicense(license);
       
        PhotosInterface pInterf = flickr.getPhotosInterface();
        PhotoList list=null;
        try
        {          
           list = pInterf.search(sp, 20, 3);  
        }
        catch(com.aetrion.flickr.FlickrException ex){
        ex.printStackTrace(); 
        } catch(java.io.IOException ex){
        ex.printStackTrace();
        } catch(org.xml.sax.SAXException ex){
        ex.printStackTrace(); 
        }
    
       ArrayList<Image> imgList = new ArrayList<Image>();
       
       int count = 0;
       
       for (Object p : list.toArray())
       {                     
           Photo ph = ((Photo)p);                      
           User user = ph.getOwner();           
             
           count++;
           if (count>20)
           {
               break;
           }               

          
         String profile = ph.getUrl();
         profile = profile.substring(0, profile.lastIndexOf("/"));
         Image img = new Image(ph.getTitle(), ph.getDateTaken(), ph.getDateAdded(), 
                 ph.getSmallUrl(), profile, ph.getTags(), ph.getUrl(), user.getId());
           imgList.add(img);      
           
       }
       
       return imgList;
    }
    
    public String GetUserName(String userID)
    {
        User userInfo = null; 
        PeopleInterface people = new PeopleInterface(apiKEY, flickr.getTransport());
           try
           {
                userInfo = people.getInfo(userID);
                return userInfo.getUsername();
           }
           catch(com.aetrion.flickr.FlickrException ex){
        ex.printStackTrace(); 
        } catch(java.io.IOException ex){
        ex.printStackTrace();
        } catch(org.xml.sax.SAXException ex){
        ex.printStackTrace(); 
        }
        
        return "";
    }
}
