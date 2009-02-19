package com.kesdip.designer.properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ValidatingTextCellEditor extends TextCellEditor {
	private Composite ancestor;
	
	public ValidatingTextCellEditor() {
		super();
	}
	
	public ValidatingTextCellEditor(Composite parent) {
		super(parent);
	}
	
	public ValidatingTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Control createControl(Composite parent) {
		ancestor = parent;
		Control retVal = super.createControl(parent);
		retVal.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (getErrorMessage() != null) {
					MessageDialog.openError(ancestor.getShell(),
							"Invalid CRON expression", getErrorMessage());
				}
				super.focusLost(e);
			}
		});
		return retVal;
	}

	
}
