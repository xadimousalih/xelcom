package sn.tab28.web.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import sn.tab28.dao.controllers.UserDetailsJpaController;
import sn.tab28.dao.controllers.UserPhotoJpaController;
import sn.tab28.dao.controllers.exceptions.NonexistentEntityException;
import sn.tab28.dao.entities.UserDetails;
import sn.tab28.dao.entities.UserPhoto;


@ManagedBean(name = "rBean")
@ViewScoped
public class RegistrationMBean {

	private String name;
	private String address;
    private String nom;
    private String prenom;
    private String telephone;
    private String profession;
    private String kurel;
    private Date datenais;

	private UploadedFile file;
	private byte[] byteData;
	private UserDetailsJpaController userDetailsJpaController;
	private UserPhotoJpaController userPhotoJpaController;
	private List<UserDetails> users;
	private UserDetails selectedUser;

	private StreamedContent content;

	public RegistrationMBean() {
		EntityManagerFactory factory = Persistence
				.createEntityManagerFactory("xelcomapp-dao");
		userDetailsJpaController = new UserDetailsJpaController(factory);
		userPhotoJpaController = new UserPhotoJpaController(factory);
		users = userDetailsJpaController.findUserDetailsEntities();
	}

	public void handleFileUpload(FileUploadEvent event) {
		file = event.getFile();
		try {
			byteData = IOUtils.toByteArray(file.getInputstream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addUser(ActionEvent event) {
		// Add Photo
		try {
			UserPhoto photo = new UserPhoto();
			photo.setPhotoName(file.getFileName());
			photo.setPhotoData(byteData);
			userPhotoJpaController.create(photo);
			UserDetails detailed = new UserDetails();
			detailed.setUsername(name);
			detailed.setAddress(address);
			detailed.setNom(nom);
			detailed.setPrenom(prenom);
			detailed.setProfession(profession);
			detailed.setTelephone(telephone);
			detailed.setKurel(kurel);
			detailed.setDatenais(datenais);
			detailed.setPhotoId(photo);
			userDetailsJpaController.create(detailed);
			RequestContext.getCurrentInstance().showMessageInDialog(
					new FacesMessage("User Added"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//
	public void editUser(ActionEvent event) {
		// Edit User
		try {

			userDetailsJpaController.edit(selectedUser);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<UserDetails> getUsers() {
		return users;
	}

	public void setUsers(List<UserDetails> users) {
		this.users = users;
	}

	public UserDetails getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(UserDetails selectedUser) {
		this.selectedUser = selectedUser;
	}



	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getKurel() {
		return kurel;
	}

	public void setKurel(String kurel) {
		this.kurel = kurel;
	}

	public Date getDatenais() {
		return datenais;
	}

	public void setDatenais(Date datenais) {
		this.datenais = datenais;
	}

	public void onRowEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Car Edited", ""
				+ ((UserDetails) event.getObject()).getId());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onRowCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Edit Cancelled", ""
				+ ((UserDetails) event.getObject()).getId());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public StreamedContent getContent() {

		byte[] data = this.selectedUser.getPhotoId().getPhotoData();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				data);
		content = new DefaultStreamedContent(byteArrayInputStream, "image/jpg",
				this.selectedUser.getPhotoId().getPhotoName());
		return content;
	}

	public void setContent(StreamedContent content) {
		this.content = content;
	}

	public void deleteUser(int id) {

		try {
			int idPhoto = userDetailsJpaController.findUserDetails(id)
					.getPhotoId().getPhotoId();
			userDetailsJpaController.destroy(id);
			userPhotoJpaController.destroy(idPhoto);
			refresh();
		} catch (NonexistentEntityException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	public void refresh() {
		FacesContext context = FacesContext.getCurrentInstance();
		String viewId = context.getViewRoot().getViewId();
		ViewHandler handler = context.getApplication().getViewHandler();
		UIViewRoot root = handler.createView(context, viewId);
		root.setViewId(viewId);
		context.setViewRoot(root);
	}

}
