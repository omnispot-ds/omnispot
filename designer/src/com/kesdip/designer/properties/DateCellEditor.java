package com.kesdip.designer.properties;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class DateCellEditor extends DialogCellEditor {
	private static final SimpleDateFormat sdf =
		new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private Composite parent;
	
	public DateCellEditor(Composite parent, int style) {
		super(parent, style);
		
		this.parent = parent;
	}

	private CLabel label;

	@Override
	protected Control createContents(Composite cell) {
		label = new CLabel(cell, SWT.CENTER);
		label.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (getErrorMessage() != null) {
					MessageDialog.openError(parent.getShell(),
							"Invalid date", getErrorMessage());
				}
				super.focusLost(e);
			}
		});
		return label;
	}
	
	@Override
	protected void updateContents(Object value) {
		label.setText(value != null ? sdf.format((Date) value) : "");
	}
	
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		DateDialog dialog = new DateDialog(parent.getShell());
		dialog.setDate((Date) getValue());
		int retVal = dialog.open();
		return retVal == Window.OK ? dialog.getDate() : null;
	}

}
