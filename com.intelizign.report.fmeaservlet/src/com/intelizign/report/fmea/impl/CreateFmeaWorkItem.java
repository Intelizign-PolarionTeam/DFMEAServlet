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
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.ILinkRoleOpt;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.types.Text;
import com.polarion.platform.ITransactionService;
import com.polarion.platform.core.PlatformContext;

public class CreateFmeaWorkItem implements FMEAService {
	private ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
	private ITransactionService transactionservice = (ITransactionService) PlatformContext.getPlatform()
			.lookupService(ITransactionService.class);
	//private static final Logger log = Logger.getLogger(CreateFmeaWorkItem.class);

	
	//Capturing Request From Frontend And Processing 
	@Override
	public void createWorkItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		StringBuilder jsonString = new StringBuilder();
		String line;

		try (BufferedReader reader = req.getReader()) {
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
		}
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

	}


	//Adding Retrieve Request to ResultMap Object
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createObj(List<CreateWorkItemPojo> createWorkItemPojos, ObjectMapper objectMapper) {
		List<CreateWorkItemPojo> createWorkItemPojoList = createWorkItemPojos;
		Map<String, Object> resultMap = new HashMap<>();
		for (CreateWorkItemPojo createWiPojo : createWorkItemPojoList) {
			resultMap.put("projectId",createWiPojo.getProjectId());
			resultMap.put("wiType",createWiPojo.getWiType());
		    List<FieldData> fieldDataList = createWiPojo.getFieldData();
		    for (FieldData fieldData : fieldDataList) {
		        if (fieldData.getField().equals("linkedWorkItems")) {
		            if (fieldData.getValue() instanceof List) {
		                List<?> rawLinkedItems = (List<?>) fieldData.getValue();
		                List<LinkedWorkItems> linkedItems = new ArrayList<>();
		                for (Object rawItem : rawLinkedItems) {
		                    if (rawItem instanceof LinkedHashMap) {
		                        LinkedWorkItems item = objectMapper.convertValue(rawItem, LinkedWorkItems.class);
		                        linkedItems.add(item);
		                    }
		                }
		                for (LinkedWorkItems item : linkedItems) {
		                	resultMap.put("wiLink",item.getWiToLink());
		        			resultMap.put("role",item.getLinkrole());
		        			resultMap.put("suspect",item.isSuspect());
		        			resultMap.put("direction",item.getDirection());
		                }
		            }
		        } else {
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

	//Iterating Result Map Object & Creating Wi Obj
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
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if ("projectId".equals(key)) {
                projectId = String.valueOf(value);
            } else if ("wiType".equals(key)) {
                wiType = String.valueOf(value);
            } else if ("wiLink".equals(key)) {
                wiLink = String.valueOf(value);
            } else if ("role".equals(key)) {
                role = String.valueOf(value);
            } else if ("suspect".equals(key)) {
                suspect = (Boolean) value; 
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
        String exWiLink = wiLink.substring(wiLink.lastIndexOf("/") + 1);
        List<ILinkRoleOpt> LinkRoles = trackerPro.getWorkItemLinkRoleEnum().getAllOptions();
        IWorkItem wiLinkObj = trackerPro.getWorkItem(exWiLink);
        Optional<ILinkRoleOpt> matchingLink = LinkRoles.stream()
            .filter(links -> localRole != null && !localRole.isEmpty() && links.getId().equalsIgnoreCase(localRole))
            .findFirst();

        IWorkItem wi;
        if (matchingLink.isPresent()) {
            ILinkRoleOpt links = matchingLink.get();
            wi = trackerPro.createWorkItem(wiType);
            wi.save();
            wi.setTitle(title);
            wi.setDescription(descriptionTextObj);
            wi.addLinkedItem(wiLinkObj, links, null, suspect); 
        } else {
            Object subtypeObject = (Object) subType;
            System.out.println("Its Working!!!");
            wi = trackerPro.createWorkItem(wiType);
            wi.setTitle(title);
            wi.setDescription(descriptionTextObj);
            wi.setEnumerationValue("subType", subType);
        }

        return wi; 
    }

}