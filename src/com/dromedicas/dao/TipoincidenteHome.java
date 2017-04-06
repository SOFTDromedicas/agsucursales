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
import com.dromedicas.dto.Ventadiariaglobal;

/**
 * Home object for domain model class Tipoincidente.
 * @see com.dromedicas.dto.Tipoincidente
 * @author Hibernate Tools
 */
public class TipoincidenteHome extends BaseHibernateDAO {

	private static final Log log = LogFactory.getLog(TipoincidenteHome.class);

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

	public void persist(Tipoincidente transientInstance) {
		log.debug("persisting Tipoincidente instance");
		try {
			getSessionFactory().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Tipoincidente instance) {
		log.debug("attaching dirty Tipoincidente instance");
		try {
			getSessionFactory().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Tipoincidente instance) {
		log.debug("attaching clean Tipoincidente instance");
		try {
			getSessionFactory().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Tipoincidente persistentInstance) {
		log.debug("deleting Tipoincidente instance");
		try {
			getSessionFactory().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Tipoincidente merge(Tipoincidente detachedInstance) {
		log.debug("merging Tipoincidente instance");
		try {
			Tipoincidente result = (Tipoincidente) getSessionFactory().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Tipoincidente findById(java.lang.Integer id) {
		log.debug("getting Tipoincidente instance with id: " + id);
		try {
			Tipoincidente instance = (Tipoincidente) getSessionFactory()
					.get("com.dromedicas.dto.Tipoincidente", id);
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

	public List findByExample(Tipoincidente instance) {
		log.debug("finding Tipoincidente instance by example");
		try {
			List results = getSessionFactory().createCriteria("com.dromedicas.dto.Tipoincidente")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	public Tipoincidente obtenerTipoIncidente(String nombreIncidente){		
		Session session = null;
		Transaction txt = null;
		Tipoincidente tipoIncidenteDTO = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			String queryString = "from Tipoincidente t where t.nombreincidente = '" + nombreIncidente + "'";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			tipoIncidenteDTO = (Tipoincidente) queryObject.uniqueResult();
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
