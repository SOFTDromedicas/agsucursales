package com.dromedicas.dao;
// Generated 1/04/2017 11:59:13 AM by Hibernate Tools 5.1.2.Final

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.dromedicas.dto.Bodegas;

/**
 * Home object for domain model class Bodegas.
 * @see com.dromedicas.dto.Bodegas
 * @author Hibernate Tools
 */
public class BodegasHome {

	private static final Log log = LogFactory.getLog(BodegasHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Bodegas transientInstance) {
		log.debug("persisting Bodegas instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Bodegas instance) {
		log.debug("attaching dirty Bodegas instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Bodegas instance) {
		log.debug("attaching clean Bodegas instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Bodegas persistentInstance) {
		log.debug("deleting Bodegas instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Bodegas merge(Bodegas detachedInstance) {
		log.debug("merging Bodegas instance");
		try {
			Bodegas result = (Bodegas) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Bodegas findById(java.lang.Integer id) {
		log.debug("getting Bodegas instance with id: " + id);
		try {
			Bodegas instance = (Bodegas) sessionFactory.getCurrentSession().get("com.dromedicas.dto.Bodegas", id);
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

	public List findByExample(Bodegas instance) {
		log.debug("finding Bodegas instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria("com.dromedicas.dto.Bodegas")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
