package com.dromedicas.dao;
// Generated 28/03/2017 05:43:09 PM by Hibernate Tools 5.1.2.Final

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.dromedicas.dto.Ventadiariaglobal;

/**
 * Home object for domain model class Ventadiariaglobal.
 * @see com.dromedicas.dto.Ventadiariaglobal
 * @author Hibernate Tools
 */
public class VentadiariaglobalHome {

	private static final Log log = LogFactory.getLog(VentadiariaglobalHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException("Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Ventadiariaglobal transientInstance) {
		log.debug("persisting Ventadiariaglobal instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Ventadiariaglobal instance) {
		log.debug("attaching dirty Ventadiariaglobal instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Ventadiariaglobal instance) {
		log.debug("attaching clean Ventadiariaglobal instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Ventadiariaglobal persistentInstance) {
		log.debug("deleting Ventadiariaglobal instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Ventadiariaglobal merge(Ventadiariaglobal detachedInstance) {
		log.debug("merging Ventadiariaglobal instance");
		try {
			Ventadiariaglobal result = (Ventadiariaglobal) sessionFactory.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Ventadiariaglobal findById(java.lang.Long id) {
		log.debug("getting Ventadiariaglobal instance with id: " + id);
		try {
			Ventadiariaglobal instance = (Ventadiariaglobal) sessionFactory.getCurrentSession()
					.get("com.dromedicas.dto.Ventadiariaglobal", id);
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

	public List findByExample(Ventadiariaglobal instance) {
		log.debug("finding Ventadiariaglobal instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria("com.dromedicas.dto.Ventadiariaglobal")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
