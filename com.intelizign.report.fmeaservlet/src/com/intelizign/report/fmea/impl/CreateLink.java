package com.intelizign.report.fmea.impl;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelizign.report.fmea.model.CreateLinkPojo;
import com.intelizign.report.fmea.model.ParentWorkItem;
import com.intelizign.report.fmea.service.FMEALinkService;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.ILinkRoleOpt;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.logging.Logger;
import com.polarion.platform.ITransactionService;
import com.polarion.platform.core.PlatformContext;

public class CreateLink implements FMEALinkService {
	private ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
	private ITransactionService transactionservice = (ITransactionService) PlatformContext.getPlatform()
			.lookupService(ITransactionService.class);
	private static final Logger log = Logger.getLogger(CreateFmeaWorkItem.class);
	private Map<String, Object> resultMapObj = new HashMap<>();

	//Capturing Request From the Front End and Processing
	@Override
	public void createLink(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		StringBuilder jsonString = new StringBuilder();
		String line;

		try (BufferedReader reader = req.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
		}

		ObjectMapper objectMapper = new ObjectMapper();
		List<CreateLinkPojo> createLinkPojos;
		createLinkPojos = objectMapper.readValue(jsonString.toString(), new TypeReference<List<CreateLinkPojo>>() {
		});

		createResultMapObj(createLinkPojos, objectMapper);

		try {
			transactionservice.beginTx();
			IWorkItem wi = linkWorkItem();
			wi.save();
			System.out.println("WorkItem Linked Sucessfully" + wi.getId() + "\n");
			log.info("WorkItem Linked Sucessfully");
			transactionservice.endTx(false);
		} catch (Exception e) {
			System.out.println("Exception is" + e.getMessage());
			e.printStackTrace();
		}

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write("{\"status\":\"success\", \"message\":\"Work items Linked successfully - newWiId\"}");

	}

	//Adding Response to ResultMap Object
	@Override
	public void createResultMapObj(List<CreateLinkPojo> createLinkPojos, ObjectMapper objectMapper) {
		List<CreateLinkPojo> createLinkPojoList = createLinkPojos;
		for (CreateLinkPojo createLinkPojo : createLinkPojoList) {
			resultMapObj.put("childWi", createLinkPojo.getChild());
			resultMapObj.put("projectId", createLinkPojo.getProject());
			List<ParentWorkItem> parentWiList = createLinkPojo.getParents();
			for (ParentWorkItem parentWi : parentWiList) {
				resultMapObj.put("parentWiId", parentWi.getId());
				resultMapObj.put("linkRole", parentWi.getLinkrole());
				resultMapObj.put("revision", parentWi.getRevision());
				resultMapObj.put("suspect", parentWi.isSuspect());
			}

		}
	}

	//Linking WorkItem
	@Override
	public IWorkItem linkWorkItem() {
		String childWi = "";
		String projectId = "";
		String parentWiId = "";
		String linkRole = "";
		String revision = "";
		boolean suspect = false;
		for (Map.Entry<String, Object> entry : resultMapObj.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if ("childWi".equals(key)) {
				childWi = String.valueOf(value);
			} else if ("projectId".equals(key)) {
				projectId = String.valueOf(value);
			} else if ("parentWiId".equals(key)) {
				parentWiId = String.valueOf(value);
			} else if ("linkRole".equals(key)) {
				linkRole = String.valueOf(value);
			} else if ("suspect".equals(key)) {
				suspect = (Boolean) value;
			} else if ("revision".equals(key)) {
				revision = String.valueOf(value);
			}
		}
		String localRole = linkRole;
		String extractedChildWi = childWi.substring(childWi.lastIndexOf("/") + 1);
		String extractedParentWiId = parentWiId.substring(parentWiId.lastIndexOf("/") + 1);
		ITrackerProject trackerPro = trackerService.getTrackerProject(projectId);
		List<ILinkRoleOpt> LinkRoles = trackerPro.getWorkItemLinkRoleEnum().getAllOptions();
		IWorkItem childWiObj = trackerPro.getWorkItem(extractedChildWi);
		IWorkItem parentWiObj = trackerPro.getWorkItem(extractedParentWiId);
		Optional<ILinkRoleOpt> matchingLink = LinkRoles.stream()
				.filter(links -> localRole != null && !localRole.isEmpty() && links.getId().equalsIgnoreCase(localRole))
				.findFirst();
		boolean linked = false;
		if (matchingLink.isPresent()) {
			ILinkRoleOpt links = matchingLink.get();
			linked = parentWiObj.addLinkedItem(childWiObj, links, revision, suspect);
			if (linked == true) {
				return parentWiObj;
			} else {
				return null;
			}
		}else {
			System.out.println("No Matching Link Found");
		}

		return null;

	}

}
