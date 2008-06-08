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
        if (tags.length>0)
            sp.setTags(tags);
      
        if (license.length()>0)
            sp.setLicense(license);
       
        PhotosInterface pInterf = flickr.getPhotosInterface();
        PhotoList list=null;
        try
        {
           list = pInterf.search(sp, 1000, 1);  
        }
        catch(com.aetrion.flickr.FlickrException ex){
        ex.printStackTrace(); 
        } catch(java.io.IOException ex){
        ex.printStackTrace();
        } catch(org.xml.sax.SAXException ex){
        ex.printStackTrace(); 
        }
    
       ArrayList<Image> imgList = new ArrayList<Image>();
       for (Object p : list.toArray())
       {          
           Photo ph = ((Photo)p);           
           
           User user = ph.getOwner();
         String aaaa =  ph.getLargeUrl();
           String bbb = ph.getMediumUrl();
          
           String ddd  =ph.getSmallSquareUrl();
           String eee = ph.getSmallUrl();
           String fff = ph.getThumbnailUrl();
           
           Image img = new Image(ph.getTitle(), user.getUsername(), ph.getDateTaken(),
                   ph.getDateAdded(), ph.getSmallUrl(), "", ph.getTags());
           imgList.add(img);
       }
       
       return imgList;
    }
    
}
