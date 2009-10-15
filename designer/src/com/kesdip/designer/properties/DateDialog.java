package com.kesdip.designer.properties;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

public class DateDialog extends Dialog {
	private Date date;
	private DateTime calendar;
	private DateTime time;

	protected DateDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getDate() {
		return date;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		getShell().setText("Pick a date:");
		RowLayout layout = new RowLayout();
		layout.justify = true;
		layout.center = true;
		container.setLayout(layout);
		
		calendar = new DateTime(container, SWT.CALENDAR);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		calendar.setDate(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DATE));
		time = new DateTime(container, SWT.TIME);
		time.setTime(cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		SelectionListener sl = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.set(calendar.getYear(), calendar.getMonth(), calendar.getDay(),
						time.getHours(), time.getMinutes(), time.getSeconds());
				date = cal.getTime();
			}
		};
		calendar.addSelectionListener(sl);
		time.addSelectionListener(sl);
		
		return container;
	}
	
	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(250, 250);
	}

}
