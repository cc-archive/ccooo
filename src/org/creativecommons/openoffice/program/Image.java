/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.program;

import java.util.Collection;
import java.util.Date;


/**
 *
 * @author Administrator
 */
public class Image {

    private String title;
    private String username;
    private String userID;
    private String photoID;
    private Date taken;
    private Date uploaded;
    private String imgUrl;
    private String profile;
    private String imgUrlMainPage;
    private Collection tags;
    
    public Image(String _title,Date _taken,Date _uploaded, String _imgUrl, String _profile, 
            Collection _tags, String _imgUrlMainPage, String _userID, String _photoID) {
            
        this.title = _title;
        this.taken = _taken;
        this.uploaded = _uploaded;
        this.imgUrl = _imgUrl;
        this.profile = _profile;
        this.tags = _tags;
        this.imgUrlMainPage = _imgUrlMainPage;
        this.userID = _userID;
        this.photoID = _photoID;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getUserName()
    {
        return username;
    }
    
    public void setUserName(String _userName)
    {
        username = _userName;
    }

    public String getImgUrlMainPage()
    {
        return imgUrlMainPage;
    }
    
    public Date getTaken()
    {
        return taken;
    }
    
    public Date getUploaded()
    {
        return uploaded;
    }
    
    public String getImgURL()
    {
        return imgUrl;
    }
    
    public String getProfile()
    {
        return profile;
    }
    
    public Collection getTags()
    {        
        return tags;
    }
    
    public String getUserID()
    {
        return userID;
    }
     
    public String getPhotoID()
    {
        return photoID;
    }
}

