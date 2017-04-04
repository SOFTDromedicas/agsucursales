package com.dromedicas.schedule;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.dromedicas.jaxrs.service.ClienteActualizarExistencia;
import com.dromedicas.jaxrs.service.ClienteVentasAlInstante;

public class QuartzListener implements ServletContextListener {

//	Scheduler scheduler = null;
	Scheduler schedulerExis = null;
	

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		System.out.println("Context Initialized");

		try {
			// Setup the Job class and the Job group
//			JobDetail job = newJob(ClienteVentasAlInstante.class).withIdentity("VentasAlInstante", "Group").build();
			JobDetail jobExistencias = newJob(ClienteActualizarExistencia.class).withIdentity("Existencias", "ExisGroup").build();

//			Trigger trigger = newTrigger().withIdentity("ActVentasAlInstante", "Group")
//					.withSchedule(CronScheduleBuilder.cronSchedule("0 0/4 * * * ?"))
//					.build();
			
			Trigger triggerExistencias = newTrigger().withIdentity("ActExistencia", "ExisGroup")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 0/15 * * * ?"))
					.build();
			
			// Setup the Job and Trigger with Scheduler & schedule jobs
//			scheduler = new StdSchedulerFactory().getScheduler();
//			scheduler.start();
//			scheduler.scheduleJob(job, trigger);
			
			schedulerExis = new StdSchedulerFactory().getScheduler();
			schedulerExis.start();
			schedulerExis.scheduleJob(jobExistencias, triggerExistencias);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		System.out.println("Context Destroyed");
		try {
//			scheduler.shutdown();
			schedulerExis.shutdown();

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
}//fin de la clase