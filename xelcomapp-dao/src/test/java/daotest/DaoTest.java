package daotest;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

import sn.tab28.dao.controllers.UserDetailsJpaController;
import sn.tab28.dao.controllers.UserPhotoJpaController;

public class DaoTest {
	
	@Test
	public void test(){
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("xelcomapp-dao");
		UserDetailsJpaController jpaUC = new UserDetailsJpaController(factory);
		UserPhotoJpaController jpaPC = new UserPhotoJpaController(factory);
		System.out.println("Le nombre d'utilisateur est: "+jpaUC.findUserDetailsEntities().size());
		System.out.println("Le nombre d'utilisateur est: "+jpaPC.findUserPhotoEntities().size());
	}

}
