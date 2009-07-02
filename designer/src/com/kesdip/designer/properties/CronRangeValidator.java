package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.ICellEditorValidator;

public class CronRangeValidator implements ICellEditorValidator {

	private int min;
	private int max;
	
	public CronRangeValidator(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public String isValid(Object value) {
		try {
			int val = Integer.parseInt(value.toString());
			if (!(val >= min && val <= max)) {
				return "Valid values should be between " + min + " and " + max;
			}
		} catch (NumberFormatException ex) {
			return value.toString() + " is not a number.";
		}
		return null;
	}
	
}
