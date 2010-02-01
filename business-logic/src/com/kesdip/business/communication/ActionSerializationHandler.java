package com.kesdip.business.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kesdip.business.domain.admin.generated.Action;
import com.kesdip.business.domain.admin.generated.Parameter;

/**
 * Responsible for action serialization into the Kesdip message format.
 * 
 * @author gerogias
 */
public class ActionSerializationHandler {

	private static final String ACTION = "[ACTION]";

	private static final String END_OF_ACTION = "[ACTION_END]";

	private static final String ACTION_ID = "ACTION_ID:";

	private static final String DATE_ADDED = "DATE_ADDED:";

	private static final String MESSAGE = "MESSAGE:";

	private static final String STATUS = "STATUS:";

	private static final String TYPE = "TYPE:";

	private static final String PARAMETER_NAME = "NAME:";

	private static final String PARAMETER_VALUE = "VALUE:";

	/**
	 * A cross-platform new-line character.
	 */
	private static final String NEW_LINE = "\n";

	public String serialize(Action[] actions) {
		StringBuilder sb = new StringBuilder();
		for (Action currentAction : actions) {
			sb.append(ActionSerializationHandler.ACTION).append(NEW_LINE);
			sb.append(ACTION_ID).append(currentAction.getActionId()).append(NEW_LINE);
			sb.append(DATE_ADDED).append(currentAction.getDateAdded().getTime())
					.append(NEW_LINE);
			sb.append(MESSAGE).append(currentAction.getMessage()).append(NEW_LINE);
			sb.append(STATUS).append(currentAction.getStatus()).append(NEW_LINE);
			sb.append(TYPE).append(currentAction.getType()).append(NEW_LINE);
			for (Parameter param : currentAction.getParameters()) {
				sb.append(PARAMETER_NAME).append(param.getName())
						.append(NEW_LINE);
				sb.append(PARAMETER_VALUE).append(param.getValue()).append(
						NEW_LINE);
			}
			sb.append(END_OF_ACTION).append(NEW_LINE);
		}
		return sb.toString();
	}

	public Action[] deserialize(String serializedActions) throws IOException {

		if (serializedActions == null || serializedActions.equals("")
				|| serializedActions.equals("NO_ACTIONS")) {
			return new Action[0];
		}

		BufferedReader reader = new BufferedReader(new StringReader(
				serializedActions));
		String line = null;
		List<Action> actions = new ArrayList<Action>();
		Action action = null;
		Parameter parameter = null;

		while ((line = reader.readLine()) != null) {

			if (line.equals(ActionSerializationHandler.ACTION)) {
				action = new Action();
				continue;
			}

			if (line.equals(END_OF_ACTION)) {
				actions.add(action);
				continue;
			}

			if (line.startsWith(ACTION_ID)) {
				action.setActionId(line.substring(ACTION_ID.length()));
			} else if (line.startsWith(DATE_ADDED)) {
				action.setDateAdded(new Date(Long.parseLong(line
						.substring(DATE_ADDED.length()))));
			} else if (line.startsWith(MESSAGE)) {
				action.setMessage(line.substring(MESSAGE.length()));
			} else if (line.startsWith(STATUS)) {
				action.setStatus(Short.parseShort(line.substring(STATUS
						.length())));
			} else if (line.startsWith(TYPE)) {
				action.setType(Short.parseShort(line.substring(TYPE.length())));
			} else if (line.startsWith(PARAMETER_NAME)) {
				parameter = new Parameter();
				parameter.setName(line.substring(PARAMETER_NAME.length()));
			} else if (line.startsWith(PARAMETER_VALUE)) {
				parameter.setValue(line.substring(PARAMETER_VALUE.length()));
				action.getParameters().add(parameter);
			}
		}
		return actions.toArray(new Action[0]);
	}
}
