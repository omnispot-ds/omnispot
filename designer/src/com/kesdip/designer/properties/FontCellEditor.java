package com.kesdip.designer.properties;

import java.awt.Font;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;

import com.kesdip.designer.utils.FontKludge;

public class FontCellEditor extends DialogCellEditor {
	
	private Composite parent;
	
	public FontCellEditor(Composite parent, int style) {
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
		FontDialog dialog = new FontDialog(parent.getShell(), SWT.APPLICATION_MODAL);
		if (getValue() != null) {
			Font f = (Font) getValue();
			int s = SWT.NONE;
			if (f.isBold())
				s &= SWT.BOLD;
			if (f.isItalic())
				s &= SWT.ITALIC;
			FontData fontData = new FontData(f.getFamily(), s, f.getSize());
			dialog.setFontList(new FontData[] { fontData });
		}
		FontData fontData = dialog.open();
		if (fontData == null)
			return null;
		
		String name = fontData.getName();
		int style = Font.PLAIN;
		if ((fontData.getStyle() & SWT.BOLD) != 0)
			style |= Font.BOLD;
		if ((fontData.getStyle() & SWT.ITALIC) != 0)
			style |= Font.ITALIC;
		int size = fontData.getHeight();
		FontKludge f = new FontKludge(name, style, size);
		f.setFontData(fontData);
		return f;
	}

}
