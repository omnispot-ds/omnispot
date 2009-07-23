package com.kesdip.designer.properties;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;

import com.kesdip.designer.model.Resource;

public class SimpleResourceCellEditor extends DialogCellEditor {
	private Composite parent;
	
	public SimpleResourceCellEditor(Composite parent, int style) {
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
							"Invalid resource", getErrorMessage());
				}
				super.focusLost(e);
			}
		});
		return label;
	}
	
	@Override
	protected void updateContents(Object value) {
		label.setText(value != null ? value.toString() : "");
	}
	
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN | SWT.APPLICATION_MODAL);
		dialog.setFilterNames(new String[] { "All files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.*" });
		if (getValue() != null && ((String) getValue()).length() != 0)
			dialog.setFileName(new File((String) getValue()).getName());
		String path = dialog.open();
		return new Resource(path, "");
	}

}
