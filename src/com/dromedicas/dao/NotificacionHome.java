package com.dromedicas.dao;
// Generated 5/04/2017 03:19:04 PM by Hibernate Tools 5.1.2.Final

import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.dromedicas.dto.Incidente;
import com.dromedicas.dto.Notificacion;

/**
 * Home object for domain model class Notificacion.
 * @see com.dromedicas.dto.Notificacion
 * @author Hibernate Tools
 */
public class NotificacionHome extends BaseHibernateDAO{

	private static final Log log = LogFactory.getLog(NotificacionHome.class);

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

	public void persist(Notificacion transientInstance) {
		log.debug("persisting Notificacion instance");
		try {
			getSessionFactory().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Notificacion instance) {
		log.debug("attaching dirty Notificacion instance");
		try {
			getSessionFactory().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Notificacion instance) {
		log.debug("attaching clean Notificacion instance");
		try {
			getSessionFactory().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Notificacion persistentInstance) {
		log.debug("deleting Notificacion instance");
		try {
			getSessionFactory().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Notificacion merge(Notificacion detachedInstance) {
		log.debug("merging Notificacion instance");
		try {
			Notificacion result = (Notificacion) getSessionFactory().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Notificacion findById(java.lang.Integer id) {
		log.debug("getting Notificacion instance with id: " + id);
		try {
			Notificacion instance = (Notificacion) getSessionFactory()
					.get("com.dromedicas.dto.Notificacion", id);
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

	public List findByExample(Notificacion instance) {
		log.debug("finding Notificacion instance by example");
		try {
			List results = getSessionFactory().createCriteria("com.dromedicas.dto.Notificacion")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	/**
	 * Busca si exc
	 * @param incidente
	 * @return
	 */
	public List obtenerNotificacionPorIncidente(Incidente incidente){
		Session session = null;
		Transaction txt = null;
		List<Notificacion> notiDTO = new ArrayList<Notificacion>();
		try {
			session = this.getSession();
			txt = session.beginTransaction();
			String queryString ="from Notificacion n where n.incidente.idincidente  = " 
																+ incidente.getIdincidente();			
			Query queryObject = this.sessionFactory.createQuery(queryString);
			notiDTO = queryObject.list();
			txt.commit();
			
		} catch (Exception e) {
			txt.rollback();
			e.printStackTrace();
		}
		finally {
			//session.close();
		}
		return notiDTO;			
	}
	
	
	public void guardarNotificacion(Notificacion instance){
		Session session = null;
		Transaction txt = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();
			this.persist(instance);
			txt.commit();
			
		} catch (Exception e) {
			txt.rollback();
			e.printStackTrace();
		}
		finally {
			//session.close();
		}
	}
	
	
	public Notificacion obtenerUltimaNotiEmail(Incidente incidente){
		Session session = null;
		Transaction txt = null;
		Notificacion notiDTO = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();
			String queryString ="from Notificacion nu where nu.idnotificacion in( select max(n.idnotificacion) " + 
							"from Notificacion n where n.incidente.idincidente  = "+incidente .getIdincidente()+
							" and n.tiponotificacion.descripcion = 'Envio Email')";		
			
			Query queryObject = this.sessionFactory.createQuery(queryString);
			notiDTO = (Notificacion) queryObject.uniqueResult();
			txt.commit();
			
		} catch (Exception e) {
			txt.rollback();
			e.printStackTrace();
		}
		finally {
			//session.close();
		}
		return notiDTO;
	}
	
	public Notificacion obtenerUltimaNotiSMS(Incidente incidente){
		Session session = null;
		Transaction txt = null;
		Notificacion notiDTO = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();
			String queryString ="from Notificacion nu where nu.idnotificacion in( select max(n.idnotificacion) " + 
							"from Notificacion n where n.incidente.idincidente  = "+incidente .getIdincidente()+
							" and n.tiponotificacion.descripcion = 'Envio SMS')";		
			
			Query queryObject = this.sessionFactory.createQuery(queryString);
			System.out.println(queryObject.getQueryString());
			notiDTO = (Notificacion) queryObject.uniqueResult();
			txt.commit();
			
		} catch (Exception e) {
			txt.rollback();
			e.printStackTrace();
		}
		finally {
			//session.close();
		}
		return notiDTO;
	}
	
	
}
