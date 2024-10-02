package com.intelizign.report.fmea.impl;

import java.io.BufferedReader;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelizign.report.fmea.pojo.WorkItem;
import com.intelizign.report.fmea.service.FMEAService;
import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.logging.Logger;
import com.polarion.core.util.types.Text;
import com.polarion.platform.ITransactionService;
import com.polarion.platform.core.PlatformContext;
import com.polarion.subterra.base.location.ILocation;
import com.polarion.subterra.base.location.Location;

public class FMEAImpl implements FMEAService {
	private ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
	private ITransactionService transactionservice = (ITransactionService) PlatformContext.getPlatform()
			.lookupService(ITransactionService.class);
	private static final Logger log = Logger.getLogger(FMEAImpl.class);

	// End Point Triggered By DFMEA Report
	@Override
	public void createWorkItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		System.out.print("Create WorkItem Method is Executed!!!!");
		StringBuilder jsonString = new StringBuilder();
		String line;

		try (BufferedReader reader = req.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
		}

		// Use Jackson to parse the JSON into a list of WorkItem
		ObjectMapper objectMapper = new ObjectMapper();
		List<WorkItem> workItems;
		workItems = objectMapper.readValue(jsonString.toString(), new TypeReference<List<WorkItem>>() {
		});
		IModule moduleObj = creatingWorkItemInModule(workItems);
		transactionservice.beginTx();
		try {
			moduleObj.save();
			transactionservice.endTx(false);
		} catch (Exception e) {
			log.error("Exception is" + e.getMessage());
		}

		// Send success response with status code 200
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write("{\"status\":\"success\", \"message\":\"Work items processed successfully - newWiId\"}");

	}

	// Return Folder Name
	public String extractingSpaceName(String docPath) {
		docPath = docPath.replace("\\/", "/");
		String[] parts = docPath.split("/");
		String spaceName = parts[0];
		return spaceName;
	}

	// Return DocumentName
	public String extractingDocName(String docPath) {
		docPath = docPath.replace("\\/", "/");
		String[] parts = docPath.split("/");
		String moduleName = parts.length > 1 ? parts[1] : "";
		return moduleName;
	}

	// Linking WorkItem
	public void addLinkedWorkItem() {

	}

	// Inserting WorkItem in Selected Module
	public IModule creatingWorkItemInModule(List<WorkItem> workItems) {
		for (WorkItem item : workItems) {
			String wiType = item.getWiType();
			String projectId = item.getProjectId();
			String docPath = item.getDocPath();
			String addAsChildNode = item.getAddAsChildNode();
			List<WorkItem.FieldData> fieldData = item.getFieldData();
			String spaceName = extractingSpaceName(docPath);
			String moduleName = extractingDocName(docPath);
			String appendModuleNameWithSpace = spaceName + "/" + moduleName;
			System.out.println("spaceName" + spaceName + "ModuleName" + moduleName + "\n");

			IProject projObj = trackerService.getProjectsService().getProject(projectId);
			ILocation location = Location.getLocation(appendModuleNameWithSpace);
			System.out.println("ILocation is" + location + "\n");
			IModule moduleObj = trackerService.getModuleManager().getModule(projObj, location);
			System.out.println("Module obj is" + moduleObj);
			List<IWorkItem> moduleWi = moduleObj.getAllWorkItems();
			for (IWorkItem wi : moduleWi) {
				System.out.println("WorkItem Id is" + wi.getId() + "\n");
			}
			IWorkItem workItem = moduleObj.createWorkItem(wiType);
			settingValueInCreatedWorkItemObj(workItem, fieldData);

			return moduleObj;
		}
		return null;
	}

	// setting values in Created WorkItem Object
	public void settingValueInCreatedWorkItemObj(IWorkItem workItem, List<WorkItem.FieldData> fieldData) {

		for (WorkItem.FieldData field : fieldData) {
			String fieldName = field.getField();
			List<String> values = field.getValue();

			if (fieldName.equals("title")) {
				workItem.setTitle(values.get(0));
			} else if (fieldName.equals("description")) {
				Text description = Text.html(values.get(0));
				workItem.setDescription(description);
			} else if (fieldName.equals("subType")) {
				workItem.setCustomField("subType", values.get(0));
			} else if (fieldName.equals("linkedWorkItems")) {

			}
		}

	}

}
