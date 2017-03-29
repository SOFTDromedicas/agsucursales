package com.dromedicas.dao;
// Generated 28/03/2017 05:43:09 PM by Hibernate Tools 5.1.2.Final

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

import com.dromedicas.dto.Sucursales;

/**
 * Home object for domain model class Sucursales.
 * @see com.dromedicas.dto.Sucursales
 * @author Hibernate Tools
 */
public class SucursalesHome extends BaseHibernateDAO {

	private static final Log log = LogFactory.getLog(SucursalesHome.class);

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

	public void persist(Sucursales transientInstance) {
		log.debug("persisting Sucursales instance");
		try {
			this.getSessionFactory().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Sucursales instance) {
		log.debug("attaching dirty Sucursales instance");
		try {
			this.getSessionFactory().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Sucursales instance) {
		log.debug("attaching clean Sucursales instance");
		try {
			this.getSessionFactory().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Sucursales persistentInstance) {
		log.debug("deleting Sucursales instance");
		try {
			this.getSessionFactory().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Sucursales merge(Sucursales detachedInstance) {
		log.debug("merging Sucursales instance");
		try {
			Sucursales result = (Sucursales) this.getSessionFactory().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Sucursales findById(java.lang.Integer id) {
		log.debug("getting Sucursales instance with id: " + id);
		try {
			Sucursales instance = (Sucursales) this.getSessionFactory().get("com.dromedicas.dto.Sucursales",
					id);
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

	public List findByExample(Sucursales instance) {
		log.debug("finding Sucursales instance by example");
		try {
			List results = this.getSessionFactory().createCriteria("com.dromedicas.dto.Sucursales")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	public List findAll() {
		log.debug("finding all Sucursales instances");
		try {			
			String queryString = "from Sucursales s where s.esdrogueria = 'CHECKED'";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
}
