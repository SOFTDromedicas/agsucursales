package com.dromedicas.dao;
// Generated 5/04/2017 03:19:04 PM by Hibernate Tools 5.1.2.Final

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.dromedicas.dto.Incidente;

/**
 * Home object for domain model class Incidente.
 * @see com.dromedicas.dto.Incidente
 * @author Hibernate Tools
 */
public class IncidenteHome {

	private static final Log log = LogFactory.getLog(IncidenteHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Incidente transientInstance) {
		log.debug("persisting Incidente instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Incidente instance) {
		log.debug("attaching dirty Incidente instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Incidente instance) {
		log.debug("attaching clean Incidente instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Incidente persistentInstance) {
		log.debug("deleting Incidente instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Incidente merge(Incidente detachedInstance) {
		log.debug("merging Incidente instance");
		try {
			Incidente result = (Incidente) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Incidente findById(java.lang.Integer id) {
		log.debug("getting Incidente instance with id: " + id);
		try {
			Incidente instance = (Incidente) sessionFactory.getCurrentSession().get("com.dromedicas.dto.Incidente", id);
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

	public List findByExample(Incidente instance) {
		log.debug("finding Incidente instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria("com.dromedicas.dto.Incidente")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
