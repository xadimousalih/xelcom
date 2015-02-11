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
public class UserDetailsJpaController implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserDetailsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserDetails userDetails) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserPhoto photoId = userDetails.getPhotoId();
            if (photoId != null) {
                photoId = em.getReference(photoId.getClass(), photoId.getPhotoId());
                userDetails.setPhotoId(photoId);
            }
            em.persist(userDetails);
            if (photoId != null) {
                photoId.getUserDetailsList().add(userDetails);
                photoId = em.merge(photoId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserDetails userDetails) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserDetails persistentUserDetails = em.find(UserDetails.class, userDetails.getId());
            UserPhoto photoIdOld = persistentUserDetails.getPhotoId();
            UserPhoto photoIdNew = userDetails.getPhotoId();
            if (photoIdNew != null) {
                photoIdNew = em.getReference(photoIdNew.getClass(), photoIdNew.getPhotoId());
                userDetails.setPhotoId(photoIdNew);
            }
            userDetails = em.merge(userDetails);
            if (photoIdOld != null && !photoIdOld.equals(photoIdNew)) {
                photoIdOld.getUserDetailsList().remove(userDetails);
                photoIdOld = em.merge(photoIdOld);
            }
            if (photoIdNew != null && !photoIdNew.equals(photoIdOld)) {
                photoIdNew.getUserDetailsList().add(userDetails);
                photoIdNew = em.merge(photoIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userDetails.getId();
                if (findUserDetails(id) == null) {
                    throw new NonexistentEntityException("The userDetails with id " + id + " no longer exists.");
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
            UserDetails userDetails;
            try {
                userDetails = em.getReference(UserDetails.class, id);
                userDetails.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userDetails with id " + id + " no longer exists.", enfe);
            }
            UserPhoto photoId = userDetails.getPhotoId();
            if (photoId != null) {
                photoId.getUserDetailsList().remove(userDetails);
                photoId = em.merge(photoId);
            }
            em.remove(userDetails);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserDetails> findUserDetailsEntities() {
        return findUserDetailsEntities(true, -1, -1);
    }

    public List<UserDetails> findUserDetailsEntities(int maxResults, int firstResult) {
        return findUserDetailsEntities(false, maxResults, firstResult);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private List<UserDetails> findUserDetailsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserDetails.class));
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

    public UserDetails findUserDetails(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserDetails.class, id);
        } finally {
            em.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int getUserDetailsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserDetails> rt = cq.from(UserDetails.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
