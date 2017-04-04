package com.dromedicas.dao;
// Generated 28/03/2017 05:43:09 PM by Hibernate Tools 5.1.2.Final

import java.text.SimpleDateFormat;
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

import com.dromedicas.dto.Existencias;
import com.dromedicas.dto.ExistenciasId;
import com.dromedicas.dto.Ventadiariaglobal;

/**
 * Home object for domain model class Existencias.
 * @see com.dromedicas.dto.Existencias
 * @author Hibernate Tools
 */
public class ExistenciasHome extends BaseHibernateDAO {

	private static final Log log = LogFactory.getLog(ExistenciasHome.class);

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

	public void persist(Existencias transientInstance) {
		log.debug("persisting Existencias instance");
		try {
			this.getSessionFactory().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Existencias instance) {
		log.debug("attaching dirty Existencias instance");
		try {
			this.getSessionFactory().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Existencias instance) {
		log.debug("attaching clean Existencias instance");
		try {
			this.getSessionFactory().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Existencias persistentInstance) {
		log.debug("deleting Existencias instance");
		try {
			this.getSessionFactory().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Existencias merge(Existencias detachedInstance) {
		//log.debug("merging Existencias instance");
		try {
			Existencias result = (Existencias) this.getSessionFactory().merge(detachedInstance);
			//log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Existencias findById(com.dromedicas.dto.ExistenciasId id) {
		log.debug("getting Existencias instance with id: " + id);
		try {
			Existencias instance = (Existencias) this.getSessionFactory()
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
			List results = this.getSessionFactory().createCriteria("com.dromedicas.dto.Existencias")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: " + results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	public List findAll() {
		log.debug("finding all Existencias instances");
		try {			
			String queryString = "from Existencias";
			Query queryObject = this.sessionFactory.createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	
	/**
	 * Metodo transaccional de servicio que actualiza una instancia de
	 * <code>Existencias</code> recibido como parametro.
	 * @param instance
	 * @return
	 */
	public boolean actualizarExistneciaProducto(Existencias instance) {		
		//log.info("Actualizano producto id: " + instance.getId().getProductoid());
		Session session = null;
		Transaction txt = null;
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			//this.merge(instance); //--> No se usa este metodo para evitar la consulta redundante de la instancia
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String queryString = "update existencias2 set existencias2.cantidad = " + instance.getCantidad() + 
					", existencias2.ultcambio = '" + sdf.format(instance.getUltcambio()) + "' where " +
					" existencias2.bodegaid = " + instance.getId().getBodegaid() + " and existencias2.productoid = " +
					instance.getId().getProductoid() ;
			
			//System.out.println(queryString);
			Query queryObject = this.sessionFactory.createSQLQuery(queryString);
			queryObject.executeUpdate();
			
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
	 * Metodo transaccional de servicio que asigna la cantidad  de cero 
	 * para el objeto  <code>Existencias</code> recibido como parametro.
	 * @param instance
	 * @return
	 */
	public boolean existenciaBodegaoACero(Integer bodedgaId) {		
		
		Session session = null;
		Transaction txt = null;
		log.info("--Actualizando existencias a cero para la Bodega con id : " + bodedgaId);				
		try {
			session = this.getSession();
			txt = session.beginTransaction();			
			String queryString = 
					"update Existencias e set e.cantidad = 0 where e.id.bodegaid = " + bodedgaId;
			Query queryObject = this.sessionFactory.createQuery(queryString);
			//ventaDto = (Ventadiariaglobal) queryObject.uniqueResult();
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
}
