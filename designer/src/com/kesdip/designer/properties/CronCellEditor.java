package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CronCellEditor extends DialogCellEditor {
	private Composite parent;
	private CronDialog dialog;
	
	public CronCellEditor(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
	}

	private CLabel label;

	@Override
	protected Control createContents(Composite cell) {
		label = new CLabel(cell, SWT.CENTER);
		return label;
	}
	
	@Override
	protected void updateContents(Object value) {
		label.setText(value != null ? value.toString() : "");
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		dialog = new CronDialog(parent.getShell());
		String val = (String) getValue();
		dialog.setCron(val == null || val.equals("") ? "* * * * *" : val);
		int retVal = dialog.open();
		return retVal == Window.OK ? dialog.getCron() : null;
	}

}
