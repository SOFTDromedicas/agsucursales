package com.dromedicas.webcontroller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class WebController
 */
@WebServlet("/WebController")
public class WebController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(WebController.class);	
	private static  int c ;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.processRequest(request, response);
	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//params
		String email = request.getParameter("email");
		String nombre = request.getParameter("nombre");
		
		logger.info("Request: "+ c);
		logger.info("Parametros recibido");
		logger.info("twitter: " + nombre);
		logger.info("nombre: " + email);
		
		c++;
		
		logger.debug("------------ End Request Dromedicas------------");
		
		request.getRequestDispatcher("index.jsp").forward(request, response);		
	}

}
