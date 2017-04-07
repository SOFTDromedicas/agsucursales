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

import com.dromedicas.dto.Incidente;
import com.dromedicas.dto.Ventadiariaglobal;

/**
 * Home object for domain model class Incidente.
 * @see com.dromedicas.dto.Incidente
 * @author Hibernate Tools
 */
public class IncidenteHome extends BaseHibernateDAO{

	private static final Log log = LogFactory.getLog(IncidenteHome.class);

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

	public void persist(Incidente transientInstance) {
		log.debug("persisting Incidente instance");
		try {
			this.getSessionFactory().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Incidente instance) {
		log.debug("attaching dirty Incidente instance");
		try {
			this.getSessionFactory().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Incidente instance) {
		log.debug("attaching clean Incidente instance");
		try {
			this.getSessionFactory().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Incidente persistentInstance) {
		log.debug("deleting Incidente instance");
		try {
			this.getSessionFactory().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Incidente merge(Incidente detachedInstance) {
		log.debug("merging Incidente instance");
		try {
			Incidente result = (Incidente) this.getSessionFactory().merge(detachedInstance);
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
			Incidente instance = (Incidente) this.getSessionFactory().get("com.dromedicas.dto.Incidente", id);
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
			List results = this.getSessionFactory().createCriteria("com.dromedicas.dto.Incidente")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	
	/**
	 * Metodo de servicio transaccional que busca incidentes abiertos
	 * para el cliente y tipo de incidente recibidos como parametros
	 * @param cliente
	 * @param incidente
	 * @return
	 */
	public Incidente  buscarIncidenteAbierto(String cliente, String incidente){		
		Session session = null;
		Transaction txt = null;
		Incidente incidenteDTO = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			String queryString = "from Incidente i where i.tipoincidente.nombreincidente = '"+ incidente
			+"' and i.cliente = '"+ cliente +"' and i.cierre is null ";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			incidenteDTO = (Incidente) queryObject.uniqueResult();
			txt.commit();
		} catch (HibernateException e) {
			txt.rollback();
			throw e;
		} finally {
			//session.close();
		}
		return incidenteDTO;
	}
	
	/**
	 * Metodo transaccional que persiste una nueva instancia
	 * de un objeto <code>Incidente</code>.
	 * @param instance
	 */
	public void guardarIncidente(Incidente instance){
		Session session = null;
		Transaction txt = null;		
		try {
			System.out.println("Instancia recibida a persistir: " + instance.getCliente());
			System.out.println("Incieente Recibido a persistir: " + instance.getTipoincidente().getNombreincidente());
			
			session = this.getSession();
			txt = session.beginTransaction();	
			System.out.println("----Incidente: " + instance.toString());
			//this.persist(instance);	
			this.getSessionFactory().save(instance);
			txt.commit();		
			
		} catch (HibernateException e) {
			txt.rollback();
			this.sessionFactory.flush();
			//this.sessionFactory.flush();
			e.printStackTrace();;
		} finally {
			//session.close();
		}		
	}
	
	
	public void actualizarIncidente(Incidente instance){
		Session session = null;
		Transaction txt = null;
		Incidente incidenteDTO = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			this.merge(instance);
			txt.commit();
		} catch (HibernateException e) {
			txt.rollback();
			throw e;
		} finally {
			//session.close();
		}		
	}
	
	
}
