package com.kesdip.business.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kesdip.business.domain.generated.Action;
import com.kesdip.business.domain.generated.Parameter;

public class ActionSerializationHandler {

	private final static String action="[ACTION]";
	private final static String endOfAction="[ACTION_END]";
	private final static String actionId="ACTION_ID:";
	private final static String dateAdded="DATE_ADDED:";
	private final static String message="MESSAGE:";
	private final static String status="STATUS:";
	private final static String type="TYPE:";
	private final static String parameterName="NAME:";
	private final static String parameterValue="VALUE:";
	private final static String newLine = System.getProperty("line.separator");

	public String serialize(Action[] actions){
		StringBuilder sb = new StringBuilder();
		for (Action action:actions) {
			sb.append(ActionSerializationHandler.action).append(newLine);
			sb.append(actionId).append(action.getActionId()).append(newLine);
			sb.append(dateAdded).append(action.getDateAdded().getTime()).append(newLine);
			sb.append(message).append(action.getMessage()).append(newLine);
			sb.append(status).append(action.getStatus()).append(newLine);
			sb.append(type).append(action.getType()).append(newLine);
			for (Parameter param:action.getParameters()){
				sb.append(parameterName).append(param.getName()).append(newLine);
				sb.append(parameterValue).append(param.getValue()).append(newLine);
			}
			sb.append(endOfAction).append(newLine);
		}
		return sb.toString();
	}

	public Action[] deserialize(String serializedActions) throws IOException{

		if (serializedActions == null || serializedActions.equals("") || serializedActions.equals("NO_ACTIONS"))
			return new Action[0];

		BufferedReader reader = new BufferedReader(new StringReader(serializedActions));
		String line;
		List<Action> actions = new ArrayList<Action>();
		Action action = null;
		Parameter parameter = null;
		
		while ((line = reader.readLine()) != null) {
			
			if (line.equals(ActionSerializationHandler.action)) {
				action = new Action();
				continue;
			}
				
			if (line.equals(endOfAction)) {
				actions.add(action);
				continue;
			}
				
			if (line.startsWith(actionId))  
				action.setActionId(line.substring(actionId.length()));
			else if (line.startsWith(dateAdded)) 
				action.setDateAdded(new Date(Long.parseLong(line.substring(dateAdded.length()))));
			else if (line.startsWith(message)) 
				action.setMessage(line.substring(message.length()));
			else if (line.startsWith(status)) 
				action.setStatus(Short.parseShort(line.substring(status.length())));
			else if (line.startsWith(type)) 
				action.setType(Short.parseShort(line.substring(type.length())));
			else if (line.startsWith(parameterName)) {
				parameter = new Parameter();
				parameter.setName(line.substring(parameterName.length()));
			} else if (line.startsWith(parameterValue)) {
				parameter.setValue(line.substring(parameterValue.length()));
				action.getParameters().add(parameter);
			}
		}
		return actions.toArray(new Action[0]); 
	}
}
