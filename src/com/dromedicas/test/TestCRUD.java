package com.dromedicas.test;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dromedicas.dao.ExistenciasHome;
import com.dromedicas.dao.SucursalesHome;
import com.dromedicas.dao.VentadiariaglobalHome;
import com.dromedicas.dto.Existencias;
import com.dromedicas.dto.Sucursales;
import com.dromedicas.dto.Ventadiariaglobal;

public class TestCRUD {

	
	private static final Log log = LogFactory.getLog(SucursalesHome.class);
	
	public static void main(String[] args) {
		
		
		ExistenciasHome daoSuc = new ExistenciasHome();	
		 List<Existencias> list = daoSuc.findAll();
		 
		 for( int i = 0; i < list.size(); i++){
			 Existencias e = list.get(i);
			 System.out.println(e.getId().getBodegaid() +" - "+ e.getCantidad());
		 }		 
		

	}

}
