/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sn.tab28.dao.controllers;

import java.io.Serializable;

import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import sn.tab28.dao.controllers.exceptions.NonexistentEntityException;
import sn.tab28.dao.entities.UserDetails;
import sn.tab28.dao.entities.UserPhoto;

/**
 *
 * @author awaconsulting
 */
public class UserPhotoJpaController implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserPhotoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserPhoto userPhoto) {
        if (userPhoto.getUserDetailsList() == null) {
            userPhoto.setUserDetailsList(new ArrayList<UserDetails>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<UserDetails> attachedUserDetailsList = new ArrayList<UserDetails>();
            for (UserDetails userDetailsListUserDetailsToAttach : userPhoto.getUserDetailsList()) {
                userDetailsListUserDetailsToAttach = em.getReference(userDetailsListUserDetailsToAttach.getClass(), userDetailsListUserDetailsToAttach.getId());
                attachedUserDetailsList.add(userDetailsListUserDetailsToAttach);
            }
            userPhoto.setUserDetailsList(attachedUserDetailsList);
            em.persist(userPhoto);
            for (UserDetails userDetailsListUserDetails : userPhoto.getUserDetailsList()) {
                UserPhoto oldPhotoIdOfUserDetailsListUserDetails = userDetailsListUserDetails.getPhotoId();
                userDetailsListUserDetails.setPhotoId(userPhoto);
                userDetailsListUserDetails = em.merge(userDetailsListUserDetails);
                if (oldPhotoIdOfUserDetailsListUserDetails != null) {
                    oldPhotoIdOfUserDetailsListUserDetails.getUserDetailsList().remove(userDetailsListUserDetails);
                    oldPhotoIdOfUserDetailsListUserDetails = em.merge(oldPhotoIdOfUserDetailsListUserDetails);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserPhoto userPhoto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserPhoto persistentUserPhoto = em.find(UserPhoto.class, userPhoto.getPhotoId());
            List<UserDetails> userDetailsListOld = persistentUserPhoto.getUserDetailsList();
            List<UserDetails> userDetailsListNew = userPhoto.getUserDetailsList();
            List<UserDetails> attachedUserDetailsListNew = new ArrayList<UserDetails>();
            for (UserDetails userDetailsListNewUserDetailsToAttach : userDetailsListNew) {
                userDetailsListNewUserDetailsToAttach = em.getReference(userDetailsListNewUserDetailsToAttach.getClass(), userDetailsListNewUserDetailsToAttach.getId());
                attachedUserDetailsListNew.add(userDetailsListNewUserDetailsToAttach);
            }
            userDetailsListNew = attachedUserDetailsListNew;
            userPhoto.setUserDetailsList(userDetailsListNew);
            userPhoto = em.merge(userPhoto);
            for (UserDetails userDetailsListOldUserDetails : userDetailsListOld) {
                if (!userDetailsListNew.contains(userDetailsListOldUserDetails)) {
                    userDetailsListOldUserDetails.setPhotoId(null);
                    userDetailsListOldUserDetails = em.merge(userDetailsListOldUserDetails);
                }
            }
            for (UserDetails userDetailsListNewUserDetails : userDetailsListNew) {
                if (!userDetailsListOld.contains(userDetailsListNewUserDetails)) {
                    UserPhoto oldPhotoIdOfUserDetailsListNewUserDetails = userDetailsListNewUserDetails.getPhotoId();
                    userDetailsListNewUserDetails.setPhotoId(userPhoto);
                    userDetailsListNewUserDetails = em.merge(userDetailsListNewUserDetails);
                    if (oldPhotoIdOfUserDetailsListNewUserDetails != null && !oldPhotoIdOfUserDetailsListNewUserDetails.equals(userPhoto)) {
                        oldPhotoIdOfUserDetailsListNewUserDetails.getUserDetailsList().remove(userDetailsListNewUserDetails);
                        oldPhotoIdOfUserDetailsListNewUserDetails = em.merge(oldPhotoIdOfUserDetailsListNewUserDetails);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userPhoto.getPhotoId();
                if (findUserPhoto(id) == null) {
                    throw new NonexistentEntityException("The userPhoto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserPhoto userPhoto;
            try {
                userPhoto = em.getReference(UserPhoto.class, id);
                userPhoto.getPhotoId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userPhoto with id " + id + " no longer exists.", enfe);
            }
            List<UserDetails> userDetailsList = userPhoto.getUserDetailsList();
            for (UserDetails userDetailsListUserDetails : userDetailsList) {
                userDetailsListUserDetails.setPhotoId(null);
                userDetailsListUserDetails = em.merge(userDetailsListUserDetails);
            }
            em.remove(userPhoto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserPhoto> findUserPhotoEntities() {
        return findUserPhotoEntities(true, -1, -1);
    }

    public List<UserPhoto> findUserPhotoEntities(int maxResults, int firstResult) {
        return findUserPhotoEntities(false, maxResults, firstResult);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private List<UserPhoto> findUserPhotoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserPhoto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public UserPhoto findUserPhoto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserPhoto.class, id);
        } finally {
            em.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int getUserPhotoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserPhoto> rt = cq.from(UserPhoto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
