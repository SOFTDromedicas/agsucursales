package com.dromedicas.dao;
// Generated 5/04/2017 03:19:04 PM by Hibernate Tools 5.1.2.Final

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.dromedicas.dto.Tipoincidente;
import com.dromedicas.dto.Tiponotificacion;

/**
 * Home object for domain model class Tiponotificacion.
 * @see com.dromedicas.dto.Tiponotificacion
 * @author Hibernate Tools
 */
public class TiponotificacionHome extends BaseHibernateDAO{

	private static final Log log = LogFactory.getLog(TiponotificacionHome.class);

	private final Session sessionFactory = super.getSession();

	protected Session getSessionFactory() {
		try {
			return this.sessionFactory;
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Tiponotificacion transientInstance) {
		log.debug("persisting Tiponotificacion instance");
		try {
			getSessionFactory().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Tiponotificacion instance) {
		log.debug("attaching dirty Tiponotificacion instance");
		try {
			getSessionFactory().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Tiponotificacion instance) {
		log.debug("attaching clean Tiponotificacion instance");
		try {
			getSessionFactory().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Tiponotificacion persistentInstance) {
		log.debug("deleting Tiponotificacion instance");
		try {
			getSessionFactory().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Tiponotificacion merge(Tiponotificacion detachedInstance) {
		log.debug("merging Tiponotificacion instance");
		try {
			Tiponotificacion result = (Tiponotificacion) getSessionFactory().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Tiponotificacion findById(java.lang.Integer id) {
		log.debug("getting Tiponotificacion instance with id: " + id);
		try {
			Tiponotificacion instance = (Tiponotificacion) getSessionFactory()
					.get("com.dromedicas.dto.Tiponotificacion", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Tiponotificacion instance) {
		log.debug("finding Tiponotificacion instance by example");
		try {
			List results = getSessionFactory().createCriteria("com.dromedicas.dto.Tiponotificacion")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	
	public Tiponotificacion obtenerTipoNotificacion(String tipoNoti){		
		Session session = null;
		Transaction txt = null;
		Tiponotificacion tipoIncidenteDTO = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			String queryString = "from Tiponotificacion t where t.descripcion = '" + tipoNoti + "'";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			tipoIncidenteDTO = (Tiponotificacion) queryObject.uniqueResult();
			txt.commit();
		} catch (HibernateException e) {
			txt.rollback();
			throw e;
		} finally {
			//session.close();
		}
		return tipoIncidenteDTO;
	}
}
