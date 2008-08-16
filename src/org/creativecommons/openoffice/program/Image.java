/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.creativecommons.openoffice.program;

import com.sun.star.graphic.XGraphic;
import java.util.Collection;
import java.util.Date;
import com.aetrion.flickr.photos.Size;
import java.util.Collection;

/**
 *
 * @author Husleag Mihai
 */
public class Image {

    private String title;
    private String username = "";
    private String userID;
    private String photoID;
    private Date taken;
    private Date uploaded;
    private String imgUrl;
    private String profile;
    private String imgUrlMainPage;
    private Collection tags;    
    private String selectedImageURL;
    private Integer selectedImageWidth;
    private Integer selectedImageHeigth;    
    private Collection selectedImageSizes;
    private String licenseId;
    private String licenseURL;
    private String licenseNumber;
    private String licenseCode;
    private String secret;
    private XGraphic xGraphic;
    
    public Image(String _title,Date _taken,Date _uploaded, String _imgUrl, String _profile, 
            Collection _tags, String _imgUrlMainPage, String _userID, String _photoID, String _secret) {
            
        this.title = _title;
        this.taken = _taken;
        this.uploaded = _uploaded;
        this.imgUrl = _imgUrl;
        this.profile = _profile;
        this.tags = _tags;
        this.imgUrlMainPage = _imgUrlMainPage;
        this.userID = _userID;
        this.photoID = _photoID;
        this.secret = _secret;
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
    
    public String getSecret()
    {
        return secret;
    }
    
    public String getSelectedImageURL() {
        
        return selectedImageURL;
    }
    
    
    public Integer getSelectedImageWidth() {
        
        return selectedImageWidth;
    }
        
    public Integer getSelectedImageHeigth() {
        
        return selectedImageHeigth;
    }
    
    public void setSelectedImageSizes(Collection sizes) {
        
        this.selectedImageSizes = sizes;
    }
    
    public void setLicenseID(String licID) {
        
        this.licenseId  = licID;
    }
    
    public String getLicenseID() {
        
        return licenseId;
    }
    
    public void setLicenseURL(String licURL) {
        
        this.licenseURL  = licURL;
    }
    
    public String getLicenseURL() {
        
        return licenseURL;
    }
    
    public void setLicenseNumber(String licNumber) {
        
        this.licenseNumber  = licNumber;
    }
    
    public String getLicenseNumber() {
        
        return licenseNumber;
    }
    
    public void setLicenseCode(String licCode) {
        
        this.licenseCode = licCode;
    }
    
    public String getLicenseCode() {
        
        return licenseCode;
    }
    
    public void setGraphic(XGraphic _xGrapic) {
        
        this.xGraphic  = _xGrapic;
    }
    
    public XGraphic getGraphic() {
        
        return xGraphic;
    }
   
    public void RefreshSelectedSizeImageData(short selectedSize){
        
        for (Object p : selectedImageSizes.toArray())
        {              
             com.aetrion.flickr.photos.Size currentSize = ((com.aetrion.flickr.photos.Size)p);   
            
             if (currentSize.getLabel()  == (int)selectedSize) {
                 
                 this.selectedImageHeigth = currentSize.getHeight();
                 this.selectedImageWidth = currentSize.getWidth();
                 this.selectedImageURL = currentSize.getSource();
                 break;
             }
        }                        
    }
    
}

