package com.intelizign.report.fmea.impl;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelizign.report.fmea.model.CreateWorkItemPojo;
import com.intelizign.report.fmea.model.FieldData;
import com.intelizign.report.fmea.model.LinkedWorkItems;
import com.intelizign.report.fmea.service.FMEAService;
import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.ILinkRoleOpt;
import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.logging.Logger;
import com.polarion.core.util.types.Text;
import com.polarion.platform.ITransactionService;
import com.polarion.platform.core.PlatformContext;
import com.polarion.subterra.base.location.ILocation;
import com.polarion.subterra.base.location.Location;

public class CreateFmeaWorkItem implements FMEAService {
	private ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
	private ITransactionService transactionservice = (ITransactionService) PlatformContext.getPlatform()
			.lookupService(ITransactionService.class);
	private static final Logger log = Logger.getLogger(CreateFmeaWorkItem.class);

	// End Point Triggered By DFMEA Report
	@SuppressWarnings("unchecked")
	@Override
	public void createWorkItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		System.out.print("Create CreateWorkItemPojo Method is Executed!!!!");
		StringBuilder jsonString = new StringBuilder();
		String line;

		try (BufferedReader reader = req.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
		}

		// Use Jackson to parse the JSON into a list of CreateWorkItemPojo
		ObjectMapper objectMapper = new ObjectMapper();
		List<CreateWorkItemPojo> createWorkItemPojos;
		createWorkItemPojos = objectMapper.readValue(jsonString.toString(), new TypeReference<List<CreateWorkItemPojo>>() {
		});
	
		
		try {
		transactionservice.beginTx();
		Map<String, Object> resultMap = createObj(createWorkItemPojos, objectMapper);
		IWorkItem workItem = createWi(resultMap);
		workItem.save();
		System.out.println("WorkIten Created Sucessfully"+workItem.getId()+"\n");
		transactionservice.endTx(false);
		}catch(Exception e) {
		System.out.println("Exception is"+e.getMessage());
		e.printStackTrace();
		}
		
		
		// Send success response with status code 200
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write("{\"status\":\"success\", \"message\":\"Work items processed successfully - newWiId\"}");

		
		/***-- Accessing Request Object From Frontend for getting backlinedWorkItem Json**/
		/*List<CreateWorkItemPojo> createWorkItemPojoList = createWorkItemPojos;
		for (CreateWorkItemPojo createWiPojo : createWorkItemPojoList) {
			System.out.println("wiType is"+createWiPojo.getWiType()+"\n");
			System.out.println("projectId is"+createWiPojo.getProjectId()+"\n");
		    List<FieldData> fieldDataList = createWiPojo.getFieldData();
		    for (FieldData fieldData : fieldDataList) {
		        if (fieldData.getField().equals("linkedWorkItems")) {
		            // Check if the value is a LinkedHashMap (which indicates a single item or a list of maps)
		            if (fieldData.getValue() instanceof List) {
		                List<?> rawLinkedItems = (List<?>) fieldData.getValue();
		                List<LinkedWorkItems> linkedItems = new ArrayList<>();
		                
		                // Iterate over the raw linked items and convert each one to LinkedWorkItems
		                for (Object rawItem : rawLinkedItems) {
		                    // Check if rawItem is a LinkedHashMap
		                    if (rawItem instanceof LinkedHashMap) {
		                        // Convert LinkedHashMap to LinkedWorkItems
		                        LinkedWorkItems item = objectMapper.convertValue(rawItem, LinkedWorkItems.class);
		                        linkedItems.add(item);
		                    }
		                }

		                // Now linkedItems is a list of LinkedWorkItems objects
		                for (LinkedWorkItems item : linkedItems) {
		                    System.out.println("Work Item to Link: " + item.getWiToLink());
		                    System.out.println("Link Role: " + item.getLinkrole());
		                    System.out.println("Suspect: " + item.isSuspect());
		                    System.out.println("Direction: " + item.getDirection());
		                }
		            }
		        } else {
		            // Handle other fields like title, description, etc.
		            List<String> values = (List<String>) fieldData.getValue();
		            for (String value : values) {
		                System.out.println("Field: " + fieldData.getField() + ", Value: " + value);
		            }
		        }
		    }
		}
		
        List<ILinkRoleOpt> LinkRoles = trackerService.getTrackerProject(projectId).getWorkItemLinkRoleEnum().getAllOptions();
        for(ILinkRoleOpt links: LinkRoles)
		{
			
			if(links.getId().equalsIgnoreCase(LinkRole)) {
				
			}
		}
        
        boolean b =  FromWorkItem.addLinkedItem(ToWorkItem, links, null, false);*/
		
	
		
		//for(W)
		
		
		/*IModule moduleObj = creatingWorkItemInModule(workItems);
		transactionservice.beginTx();
		try {
			if(moduleObj != null) {
			moduleObj.save();
			}
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

	// Linking CreateWorkItemPojo
	public void addLinkedWorkItem() {

	}

	// Inserting CreateWorkItemPojo in Selected Module
	public IModule creatingWorkItemInModule(List<CreateWorkItemPojo> workItems) {
		for (CreateWorkItemPojo item : workItems) {
			String wiType = item.getWiType();
			String projectId = item.getProjectId();
			String docPath = item.getDocPath();
			//String addAsChildNode = item.getAddAsChildNode();
			List<CreateWorkItemPojo.FieldData> fieldData = item.getFieldData();
			String spaceName = extractingSpaceName(docPath);
			String moduleName = extractingDocName(docPath);
			String appendModuleNameWithSpace = spaceName + "/" + moduleName;
			System.out.println("spaceName" + spaceName + "ModuleName" + moduleName + "\n");

			IProject projObj = trackerService.getProjectsService().getProject(projectId);
			ILocation location = Location.getLocation(appendModuleNameWithSpace);
			System.out.println("ILocation is" + location + "\n");
			IModule moduleObj = trackerService.getModuleManager().getModule(projObj, location);
			if(moduleObj != null) {
			System.out.println("Module obj is" + moduleObj);
			List<IWorkItem> moduleWi = moduleObj.getAllWorkItems();
			for (IWorkItem wi : moduleWi) {
				System.out.println("CreateWorkItemPojo Id is" + wi.getId() + "\n");
			}
			IWorkItem workItem = moduleObj.createWorkItem(wiType);
			settingValueInCreatedWorkItemObj(workItem, fieldData);
			}else {
			ITrackerProject trackerPro = trackerService.getTrackerProject(projectId);
		    IWorkItem workItem  = trackerPro.createWorkItem(wiType);
			settingValueInCreatedWorkItemObj(workItem, fieldData);
			}

			return moduleObj;
		}
		return null;
	}

	// setting values in Created CreateWorkItemPojo Object
	public void settingValueInCreatedWorkItemObj(IWorkItem workItem, List<CreateWorkItemPojo.FieldData> fieldData) {

		for (CreateWorkItemPojo.FieldData field : fieldData) {
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
		}*/

	}

	@Override
	public void createLink(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createObj(List<CreateWorkItemPojo> createWorkItemPojos, ObjectMapper objectMapper) {
		List<CreateWorkItemPojo> createWorkItemPojoList = createWorkItemPojos;
		Map<String, Object> resultMap = new HashMap<>();
		for (CreateWorkItemPojo createWiPojo : createWorkItemPojoList) {
			//System.out.println("wiType is"+createWiPojo.getWiType()+"\n");
		//	System.out.println("projectId is"+createWiPojo.getProjectId()+"\n");
			resultMap.put("projectId",createWiPojo.getProjectId());
			resultMap.put("wiType",createWiPojo.getWiType());
		    List<FieldData> fieldDataList = createWiPojo.getFieldData();
		    for (FieldData fieldData : fieldDataList) {
		        if (fieldData.getField().equals("linkedWorkItems")) {
		            // Check if the value is a LinkedHashMap (which indicates a single item or a list of maps)
		            if (fieldData.getValue() instanceof List) {
		                List<?> rawLinkedItems = (List<?>) fieldData.getValue();
		                List<LinkedWorkItems> linkedItems = new ArrayList<>();
		                
		                // Iterate over the raw linked items and convert each one to LinkedWorkItems
		                for (Object rawItem : rawLinkedItems) {
		                    // Check if rawItem is a LinkedHashMap
		                    if (rawItem instanceof LinkedHashMap) {
		                        // Convert LinkedHashMap to LinkedWorkItems
		                        LinkedWorkItems item = objectMapper.convertValue(rawItem, LinkedWorkItems.class);
		                        linkedItems.add(item);
		                    }
		                }

		                // Now linkedItems is a list of LinkedWorkItems objects
		                for (LinkedWorkItems item : linkedItems) {
		                	resultMap.put("wiLink",item.getWiToLink());
		        			resultMap.put("role",item.getLinkrole());
		        			resultMap.put("suspect",item.isSuspect());
		        			resultMap.put("direction",item.getDirection());
		                //    System.out.println("wiLink: " + item.getWiToLink());
		                //    System.out.println("Role: " + item.getLinkrole());
		                //    System.out.println("Suspect: " + item.isSuspect());
		                 //   System.out.println("Direction: " + item.getDirection());
		                }
		            }
		        } else {
		            // Handle other fields like title, description, etc.
		            List<String> values = (List<String>) fieldData.getValue();
		            for (String value : values) {
		                System.out.println("Field: " + fieldData.getField() + ", Value: " + value);
		                resultMap.put(fieldData.getField() , value);
		            }
		        }
		    }
		}
		
		return resultMap;
		
	}

	//Iterating Result Map Object
    @SuppressWarnings("unused")
	public IWorkItem createWi(Map<String, Object> resultMap) throws Exception {
    	System.out.println("ResultMap is"+resultMap+"\n");
    	String projectId = "";
    	String wiType = "";
    	String wiLink = "";
    	String role = "";
    	boolean suspect = false;
    	String direction = "";
    	String title = "";
    	String description = "";
    	String subType = "";
    	//boolean booleanValue = ((Boolean) value).booleanValue();
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            //System.out.println("Key: " + key + ", Value: " + value);
            if ("projectId".equals(key)) {
                projectId = String.valueOf(value);
            } else if ("wiType".equals(key)) {
                wiType = String.valueOf(value);
            } else if ("wiLink".equals(key)) {
                wiLink = String.valueOf(value);
            } else if ("role".equals(key)) {
                role = String.valueOf(value);
            } else if ("suspect".equals(key)) {
                suspect = (Boolean) value; // Directly cast to Boolean
            } else if ("direction".equals(key)) {
                direction = String.valueOf(value);
            } else if ("title".equals(key)) {
                title = String.valueOf(value);
            } else if ("description".equals(key)) {
                description = String.valueOf(value);
            }else if ("subType".equals(key)) {
                subType = String.valueOf(value);
            }
        }
        String localRole = role;
        Text descriptionTextObj = Text.html(description);
        ITrackerProject trackerPro =  trackerService.getTrackerProject(projectId);
        String wiLinkEx = wiLink;
        String exWiLink = wiLinkEx.substring(wiLinkEx.lastIndexOf("/") + 1);
        List<ILinkRoleOpt> LinkRoles = trackerPro.getWorkItemLinkRoleEnum().getAllOptions();
        IWorkItem wiLinkObj = trackerPro.getWorkItem(exWiLink);
        Optional<ILinkRoleOpt> matchingLink = LinkRoles.stream()
            .filter(links -> localRole != null && !localRole.isEmpty() && links.getId().equalsIgnoreCase(localRole))
            .findFirst();

        IWorkItem wi;
        //if linked workitem role has value goes to if condition otherwise else
        if (matchingLink.isPresent()) {
            // If matching link is found based on role
            ILinkRoleOpt links = matchingLink.get();
            wi = trackerPro.createWorkItem(wiType);
            wi.save();
            wi.setTitle(title);
            wi.setDescription(descriptionTextObj);
            wi.addLinkedItem(wiLinkObj, links, null, suspect); 
        } else {
            // If no matching link is found
            Object subtypeObject = (Object) subType;
            System.out.println("Its Working!!!");
            wi = trackerPro.createWorkItem(wiType);
            wi.setTitle(title);
            wi.setDescription(descriptionTextObj);
            wi.setEnumerationValue("subType", subType);
           // wi.setCustomField("subType", subtypeObject);
        }

        return wi; 
    }

	/*@SuppressWarnings("unused")
	@Override
	public void createLink(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		System.out.println("CreateLink CreateWorkItemPojo Method is Executing!!!");
		StringBuilder jsonString = new StringBuilder();
		String line;

		try (BufferedReader reader = req.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
		}

		// Use Jackson to parse the JSON into a list of CreateWorkItemPojo
		ObjectMapper objectMapper = new ObjectMapper();
		List<LinkWorkItem> LinkWorkItem;
		LinkWorkItem = objectMapper.readValue(jsonString.toString(), new TypeReference<List<LinkWorkItem>>() {
		});
		
		for(LinkWorkItem linked : LinkWorkItem) {
			String childWi = linked.getChild();
			String projectId  = linked.getProject();
			List<LinkedWi> parentWi = linked.getParents();
			for(LinkedWi link : parentWi) {
				String parentIWiId = link.getId();
				link.getLinkrole();
				link.getRevision();
				link.getSuspect();
			}
		}
	    System.out.println("CreateLinkPojo"+LinkWorkItem+"\n");
		
	}*/


}