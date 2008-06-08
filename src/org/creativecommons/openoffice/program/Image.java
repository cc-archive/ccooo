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
    private Date taken;
    private Date uploaded;
    private String imgUrl;
    private String profile;
    private Collection tags;
    
    public Image(String _title,String _username,Date _taken,Date _uploaded,
            String _imgUrl, String _profile, Collection _tags) {
            
        this.title = _title;
        this.username = _username;
        this.taken = _taken;
        this.uploaded = _uploaded;
    //    _imgUrl="file:///F:/img_0023.jpg";
        this.imgUrl = _imgUrl;
        this.profile = _profile;
        this.tags = _tags;
    }
    
    public String Title()
    {
        return title;
    }
    
    public String UserName()
    {
        return username;
    }

    public Date Taken()
    {
        return taken;
    }
    
    public Date Uploaded()
    {
        return uploaded;
    }
    
    public String ImgURL()
    {
        return imgUrl;
    }
    
    public String Profile()
    {
        return profile;
    }
    
    public Collection Tags()
    {        
        return tags;
    }
}

