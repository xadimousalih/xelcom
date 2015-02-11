/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sn.tab28.dao.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author awaconsulting
 */
@Entity
@Table(name = "user_photo")
@NamedQueries({
    @NamedQuery(name = "UserPhoto.findAll", query = "SELECT u FROM UserPhoto u"),
    @NamedQuery(name = "UserPhoto.findByPhotoId", query = "SELECT u FROM UserPhoto u WHERE u.photoId = :photoId"),
    @NamedQuery(name = "UserPhoto.findByPhotoName", query = "SELECT u FROM UserPhoto u WHERE u.photoName = :photoName")})
public class UserPhoto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "photo_id")
    private Integer photoId;
    @Column(name = "photo_name")
    private String photoName;
    @Lob
    @Column(name = "photo_data")
    private byte[] photoData;
    @OneToMany(mappedBy = "photoId", fetch = FetchType.LAZY)
    private List<UserDetails> userDetailsList;

    public UserPhoto() {
    }

    public UserPhoto(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public byte[] getPhotoData() {
        return photoData;
    }

    public void setPhotoData(byte[] photoData) {
        this.photoData = photoData;
    }

    public List<UserDetails> getUserDetailsList() {
        return userDetailsList;
    }

    public void setUserDetailsList(List<UserDetails> userDetailsList) {
        this.userDetailsList = userDetailsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (photoId != null ? photoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserPhoto)) {
            return false;
        }
        UserPhoto other = (UserPhoto) object;
        if ((this.photoId == null && other.photoId != null) || (this.photoId != null && !this.photoId.equals(other.photoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dbtest.UserPhoto[ photoId=" + photoId + " ]";
    }
    
}
