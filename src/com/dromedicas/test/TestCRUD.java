package com.dromedicas.test;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dto.Sucursales;

public class TestCRUD {

	
	private static final Log log = LogFactory.getLog(SucursalesHome.class);
	
	public static void main(String[] args) {
		
		
		SucursalesHome daoSuc = new SucursalesHome();	
		 List<Sucursales> list = daoSuc.findAll();
		 
		 for( int i = 0; i < list.size(); i++){
			 Sucursales e = list.get(i);
			 System.out.println(i + " Sucursal: " +  e.getDescripcion()  + " Es 24 Horas: " + e.getEs24horas());
		 }		 
		

	}

}
