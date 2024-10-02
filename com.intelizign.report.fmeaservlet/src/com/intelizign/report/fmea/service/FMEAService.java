package com.intelizign.report.fmea.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FMEAService {

	public void createWorkItem(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
