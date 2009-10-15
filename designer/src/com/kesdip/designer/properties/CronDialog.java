package com.kesdip.designer.properties;

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class CronDialog extends Dialog implements ISelectionChangedListener {
	
	private CronModel cronModel = new CronModel();
	
	private Group cronGroup;
	private TableViewer minuteTableViewer;
	private MinuteTableHelper minuteTableHelper;
	private TableViewer hourTableViewer;
	private Button addHourButton;
	private Button removeHourButton;
	private Button addMinuteButton;
	private Button removeMinuteButton;
	private Button cronCheckbox;
	private Text cronText;
	
	private Group monthGroup;
	private Group dayGroup;
	
	protected CronDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		cronGroup = new Group(container, SWT.NONE);
		cronGroup.setText("Cron Editor");
		cronGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		cronGroup.setLayout(gridLayout);
		
		Label label = new Label(cronGroup, SWT.FILL);
		label.setText("hh/mm");
		
		minuteTableViewer = new TableViewer(cronGroup,
				SWT.FULL_SELECTION | SWT.SINGLE);
		
		minuteTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				removeMinuteButton.setEnabled(
						minuteTableViewer.isCellEditorActive() 
						&& (selection.getFirstElement() instanceof Set));
			}
		});
		
		new Label(cronGroup, SWT.NONE);
		//SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		
		hourTableViewer = new TableViewer(cronGroup,
				SWT.FULL_SELECTION | SWT.SINGLE | SWT.RESIZE );
		hourTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				removeHourButton.setEnabled(!event.getSelection().isEmpty());
			}
		});
		GridData hourGridData = new GridData(SWT.LEFT, SWT.TOP, false, true);
		final Table hourTable = hourTableViewer.getTable();
		hourGridData.heightHint = 800;
		hourGridData.widthHint = 30;
		hourTable.setLayoutData(hourGridData);
		
		final ValidatingTextCellEditor hourTextCellEditor = new ValidatingTextCellEditor(hourTableViewer.getTable(), SWT.NONE);
		
		//final TableViewer timeTableViewer = new TableViewer(cronGroup, SWT.FILL);
		
		minuteTableHelper = new MinuteTableHelper(minuteTableViewer, cronModel);
		minuteTableHelper.refresh();
		
		int buttonWidth = 100;
		GridData buttonGridData = new GridData(buttonWidth, SWT.DEFAULT);
		
		Composite buttonContainer = new Composite(cronGroup, SWT.FILL);
		GridLayout buttonContainerLayout = new GridLayout();
		buttonContainerLayout.numColumns = 1;
		buttonContainer.setLayout(buttonContainerLayout);
		
		addHourButton = new Button(buttonContainer, SWT.PUSH);
		addHourButton.setText("Add Hour");
		addHourButton.setLayoutData(buttonGridData);
		addHourButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				hourTableViewer.add("new");
				hourTableViewer.editElement("new", 0);
			}
		});
		
		removeHourButton = new Button(buttonContainer, SWT.PUSH);
		removeHourButton.setText("Remove Hour");
		removeHourButton.setLayoutData(buttonGridData);
		removeHourButton.setEnabled(false);
		removeHourButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cronModel.getCronHour().remove(Integer.parseInt(hourTable.getSelection()[0].getData().toString()));
				hourTableViewer.refresh(true);
				minuteTableHelper.refresh();
			}
		});
		
		addMinuteButton = new Button(buttonContainer, SWT.PUSH);
		addMinuteButton.setText("Add Minute");
		addMinuteButton.setLayoutData(buttonGridData);
		addMinuteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minuteTableHelper.addMinute();
			}
		});
		
		removeMinuteButton = new Button(buttonContainer, SWT.PUSH);
		removeMinuteButton.setText("Remove Minute");
		removeMinuteButton.setLayoutData(buttonGridData);
		removeMinuteButton.setEnabled(false);
		removeMinuteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				minuteTableHelper.removeSelectedMinute();
				removeMinuteButton.setEnabled(false);
			}
		});
		
		final Table minuteTable = minuteTableViewer.getTable();
		GridData minuteGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		minuteTable.setLayoutData(minuteGridData);
		
		TableColumn hourColumn = new TableColumn(hourTable, SWT.CENTER);
		hourColumn.setWidth(40);
		
		hourTableViewer.setContentProvider(new CronDatumContentProvider());
		hourTableViewer.setInput(cronModel.getCronHour());
		
		hourTableViewer.setColumnProperties(new String[]{"hour"});
		hourTextCellEditor.setValidator(new CronRangeValidator(0, 23));
		hourTableViewer.setCellEditors(new CellEditor[]{hourTextCellEditor});
		
		dayGroup = new Group(cronGroup, SWT.FILL);
		dayGroup.setText("Day of week");
		dayGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
		GridLayout dayGroupLayout = new GridLayout(7, true);
		dayGroup.setLayout(dayGroupLayout);
		createCheckboxes(dayGroup, cronModel.getCronDayOfWeek(), 0, "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
		new Label(cronGroup, SWT.NONE);
		
		monthGroup = new Group(cronGroup, SWT.FILL);
		monthGroup.setText("Month");
		monthGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
		GridLayout monthGroupLayout = new GridLayout(7, true);
		monthGroup.setLayout(monthGroupLayout);
		createCheckboxes(monthGroup, cronModel.getCronMonth(), 1, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
		new Label(cronGroup, SWT.NONE);
		
		Group resultGroup = new Group(container, SWT.FILL);
		resultGroup.setText("Cron Expression");
		resultGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
		GridLayout resultGroupLayout = new GridLayout(2, false);
		resultGroup.setLayout(resultGroupLayout);
		
		cronCheckbox = new Button(resultGroup, SWT.CHECK);
		GridData checkGridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		cronCheckbox.setLayoutData(checkGridData);
		
		GridData textGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		cronText = new Text(resultGroup, SWT.BORDER);
		cronText.setLayoutData(textGridData);
		
		hourTableViewer.setCellModifier(new CronTableCellModifier(new GenericCallback() {
			@Override
			public Object doCallback(Object... args) {
				Integer oldValue = (Integer) args[0];
				Integer newValue = (Integer) args[1];
				if (oldValue >= 0)
					cronModel.getCronHour().remove(oldValue);
				else
					hourTableViewer.remove("new");
				if (newValue >= 0)
					cronModel.getCronHour().add(newValue);
				hourTableViewer.refresh(true);
				minuteTableViewer.refresh();
				return null;
			}
		}));
		
		cronCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//cronText.setEditable(cronCheckbox.getSelection());
				if (!cronCheckbox.getSelection())
					setCronExpression(cronText.getText());
				enableControls(!cronCheckbox.getSelection());
				cronText.setText(cronModel.getCron());
			}
		});
		
		setCronExpression(cron);
		return container;
	}
	
	private void createCheckboxes(Composite parent, final CronDatum cronDatum, int startFrom, String... labels) {
		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button checkbox = (Button) e.widget;
				int i = (Integer) checkbox.getData();
				if (checkbox.getSelection())
					cronDatum.add(i);
				else
					cronDatum.remove(i);
			}
		};
		for (int i = startFrom ; i < labels.length ; i++) {
			Button button = new Button(parent, SWT.CHECK);
			button.setText(labels[i]);
			button.setData(i);
			button.setSelection(cronDatum.getCronData().contains(i));
			button.addSelectionListener(selectionListener);
		}
	}
	
	private String cron;
	
	public void setCron(String cron) {
		this.cron = cron;
	}
	
	private void setCronExpression(String cron) {
		try {
			cronModel.setCron(cron);
			if (cronModel.getCronDayOfMonth().getCronData().size() > 0) {
				throw new RuntimeException("dayOfMonth not supported");
			}
			hourTableViewer.refresh(true);
			minuteTableHelper.refresh();
			for (int i = 0 ; i < dayGroup.getChildren().length ; i++) {
				Button chk = (Button) dayGroup.getChildren()[i];
				chk.setSelection(cronModel.getCronDayOfWeek().getCronData().contains(i));
			}
			for (int i = 0 ; i < monthGroup.getChildren().length ; i++) {
				Button chk = (Button) monthGroup.getChildren()[i];
				chk.setSelection(cronModel.getCronMonth().getCronData().contains(i+1));
			}
			enableControls(true);
			cronText.setEnabled(false);
		} catch (Exception ex) {
			//MessageDialog.openError(cronGroup.getShell(),
					//"Error", "The cron expression is too complex to edit in the editor");
			enableControls(false);
			cronCheckbox.setSelection(true);
			cronText.setEnabled(true);
			cronText.setText(cron);
			//throw new RuntimeException(ex);
		}
	}
	
	private String getCronExpression() {
		if (cronCheckbox.getSelection()) {
			return cronText.getText();
		} else {
			return cronModel.getCron();
		}
	}
	
	public String getCron() {
		return cron;
	}
	
	@Override
	protected void okPressed() {
		cron = getCronExpression();
		super.okPressed();
	}
	
	private void enableControls(boolean enable) {
		minuteTableViewer.getTable().setEnabled(enable);
		hourTableViewer.getTable().setEnabled(enable);
		cronText.setEnabled(!enable);
		for (int i = 0 ; i < monthGroup.getChildren().length ; i++) {
			monthGroup.getChildren()[i].setEnabled(enable);
		}
		for (int i = 0 ; i < dayGroup.getChildren().length ; i++) {
			dayGroup.getChildren()[i].setEnabled(enable);
		}
		if (enable) {
			addHourButton.setEnabled(true);
			addMinuteButton.setEnabled(true);
			removeHourButton.setEnabled(false);
			removeMinuteButton.setEnabled(false);
		} else {
			addHourButton.setEnabled(false);
			addMinuteButton.setEnabled(false);
			removeHourButton.setEnabled(false);
			removeMinuteButton.setEnabled(false);
		}
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(500, 600);
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent arg0) {
		// empty method body
	}
	
	

}
