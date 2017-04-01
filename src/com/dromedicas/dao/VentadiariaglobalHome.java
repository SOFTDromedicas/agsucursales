package com.dromedicas.dao;
// Generated 28/03/2017 05:43:09 PM by Hibernate Tools 5.1.2.Final

import java.util.Date;
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

import com.dromedicas.dto.Ventadiariaglobal;

/**
 * Home object for domain model class Ventadiariaglobal.
 * @see com.dromedicas.dto.Ventadiariaglobal
 * @author Hibernate Tools
 */
public class VentadiariaglobalHome extends BaseHibernateDAO {

	private static final Log log = LogFactory.getLog(VentadiariaglobalHome.class);

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

	public void persist(Ventadiariaglobal transientInstance) {
		log.debug("persisting Ventadiariaglobal instance");
		try {
			this.getSessionFactory().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Ventadiariaglobal instance) {
		log.debug("attaching dirty Ventadiariaglobal instance");
		try {
			this.getSessionFactory().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Ventadiariaglobal instance) {
		log.debug("attaching clean Ventadiariaglobal instance");
		try {
			this.getSessionFactory().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Ventadiariaglobal persistentInstance) {
		log.debug("deleting Ventadiariaglobal instance");
		try {
			this.getSessionFactory().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Ventadiariaglobal merge(Ventadiariaglobal detachedInstance) {
		log.debug("merging Ventadiariaglobal instance");
		try {
			Ventadiariaglobal result = (Ventadiariaglobal) this.getSessionFactory().merge(detachedInstance);
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
			Ventadiariaglobal instance = (Ventadiariaglobal) this.getSessionFactory()
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
			List results = this.getSessionFactory().createCriteria("com.dromedicas.dto.Ventadiariaglobal")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	public List findAll() {
		log.debug("finding all Ventadiariaglobal instances");
		try {			
			String queryString = "from Ventadiariaglobal";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public Ventadiariaglobal getVentasDiaActual( String codSucursal , String diaActual, String vendedor) {
		log.debug("finding all Sucursales instances");
		try {			
			String queryString = "from Ventadiariaglobal v where v.codsucursal = '"+ 
		codSucursal + "' and v.diaoperativo = '"+ diaActual +"' and v.vendedor ='"+ vendedor + "'";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			return (Ventadiariaglobal) queryObject.uniqueResult();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * Metodo transaccional de servicio que guarda una instancia de 
	 * <code>Ventadiariaglobal</code> recibido como parametro.
	 * @param instance
	 * @return
	 */
	public boolean guardarVentaDiaraGlobal(Ventadiariaglobal instance) {		
		
		Session session = null;
		Transaction txt = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();
			this.persist(instance);
			txt.commit();
		} catch (HibernateException e) {
			txt.rollback();
			throw e;
		}
		finally{
			//session.close();
		}
		return true;// si nada fallo, regresamos verdadero
	}
	
	
	/**
	 * Metodo transaccional de servicio que elimina una instancia de 
	 * <code>Ventadiariaglobal</code> recibido como parametro.
	 * @param instance
	 * @return
	 */
	public boolean eliminarVentaDiaraGlobal(Ventadiariaglobal instance) {
		Session session = null;
		Transaction txt = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			//Eliminamos el registro de venta global
			this.delete(instance);
			txt.commit();
		} catch (HibernateException e) {
			txt.rollback();
			throw e;
		} finally {
			//session.close();
		}
		return true;// si nada falla regresamos verdadero
	}
	
		
	/**
	 * Devuelve la ultima actualizacion de venta diaria global.
	 * recibe como parametro un objeto <code>String</code> codigo
	 * de la sucursal.
	 * @param codSucursal
	 * @return
	 */
	public Ventadiariaglobal ultimaActualizacion(String codSucursal){		
		Session session = null;
		Transaction txt = null;
		Ventadiariaglobal ventaDto = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			String queryString = "from Ventadiariaglobal v where v.id in" +
						"( select max(v_2.id) from Ventadiariaglobal v_2 "+
						"where v_2.codsucursal = '"+ codSucursal +"')";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			ventaDto = (Ventadiariaglobal) queryObject.uniqueResult();
			txt.commit();
		} catch (HibernateException e) {
			txt.rollback();
			throw e;
		} finally {
			//session.close();
		}
		return ventaDto;
	}
}
