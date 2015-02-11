package sn.tab28.web.bean;

import java.io.ByteArrayInputStream;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import sn.tab28.dao.controllers.UserPhotoJpaController;


@ManagedBean(name = "imgBean")
@ApplicationScoped
public class ImageBean {

	private static UserPhotoJpaController userPhotoJpaController;

	static {
		EntityManagerFactory factory = Persistence
				.createEntityManagerFactory("xelcomapp-dao");

		userPhotoJpaController = new UserPhotoJpaController(factory);
	}

	public StreamedContent getImage()  {
		StreamedContent valStream = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		}

		String id = fc.getExternalContext().getRequestParameterMap()
				.get("photo_id");
		byte[] photoData = null;
		try {
			photoData = userPhotoJpaController.findUserPhoto(
					Integer.parseInt(id)).getPhotoData();
		} catch (NumberFormatException e) {
			System.out.println("ID Vide ou Null");
		} catch (Exception e) {
		}
		try {
			valStream = new DefaultStreamedContent(new ByteArrayInputStream(
					photoData));
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
		return valStream;
	}
}
