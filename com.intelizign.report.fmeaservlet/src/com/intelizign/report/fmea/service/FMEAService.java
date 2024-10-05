package com.intelizign.report.fmea.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelizign.report.fmea.model.CreateWorkItemPojo;
import com.polarion.alm.tracker.model.IWorkItem;

public interface FMEAService {

	public void createWorkItem(HttpServletRequest req, HttpServletResponse resp) throws Exception;
	
	public Map<String, Object> createObj(List<CreateWorkItemPojo> createWorkItemPojos, ObjectMapper objectMapper);
	

}
