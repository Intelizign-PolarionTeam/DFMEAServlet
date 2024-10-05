package com.intelizign.report.fmea.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.intelizign.report.fmea.impl.CreateFmeaWorkItem;
import com.intelizign.report.fmea.service.FMEAService;
import com.polarion.core.util.logging.Logger;


public class FMEAServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(FMEAServlet.class);
	private FMEAService dfmeaService;
	

	public void init() throws ServletException {
		super.init();
		this.dfmeaService = new CreateFmeaWorkItem();	
	}
	
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
	
	/*
	 * Capture Request From the Polarion FMEA Report Page
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String action = req.getParameter("action");	
			System.out.print("action"+action+"\n");
			if (action != null) {
				switch (action) {
				case "createWorkItem":
					dfmeaService.createWorkItem(req, resp);
					break;
				case "createLink":
					dfmeaService.createLink(req, resp);
					break;
				default:
					throw new IllegalArgumentException("Invalid action specified");
				}
				}else {
					System.out.println("Passing action Not Matched");
				}
			}catch(Exception e) {
				System.out.println("Error Message is"+e.getMessage()+"\n");
			}
	}

}
