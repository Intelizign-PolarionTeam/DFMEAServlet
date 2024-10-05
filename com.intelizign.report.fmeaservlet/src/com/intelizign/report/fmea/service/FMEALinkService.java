package com.intelizign.report.fmea.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelizign.report.fmea.model.CreateLinkPojo;
import com.polarion.alm.tracker.model.IWorkItem;

import java.util.*;

public interface FMEALinkService {
	
	public void createLink(HttpServletRequest req, HttpServletResponse resp) throws Exception;
	
	public void createResultMapObj(List<CreateLinkPojo> createLinkPojo, ObjectMapper objectMapper);
	
	public IWorkItem linkWorkItem();

}
