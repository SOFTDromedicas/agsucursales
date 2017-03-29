package com.dromedicas.dao;
// Generated 28/03/2017 05:43:09 PM by Hibernate Tools 5.1.2.Final

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.dromedicas.dto.Existencias;
import com.dromedicas.dto.ExistenciasId;

/**
 * Home object for domain model class Existencias.
 * @see com.dromedicas.dto.Existencias
 * @author Hibernate Tools
 */
public class ExistenciasHome {

	private static final Log log = LogFactory.getLog(ExistenciasHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Existencias transientInstance) {
		log.debug("persisting Existencias instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Existencias instance) {
		log.debug("attaching dirty Existencias instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Existencias instance) {
		log.debug("attaching clean Existencias instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Existencias persistentInstance) {
		log.debug("deleting Existencias instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Existencias merge(Existencias detachedInstance) {
		log.debug("merging Existencias instance");
		try {
			Existencias result = (Existencias) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Existencias findById(com.dromedicas.dto.ExistenciasId id) {
		log.debug("getting Existencias instance with id: " + id);
		try {
			Existencias instance = (Existencias) sessionFactory.getCurrentSession()
					.get("com.dromedicas.dto.Existencias", id);
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

	public List findByExample(Existencias instance) {
		log.debug("finding Existencias instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria("com.dromedicas.dto.Existencias")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
