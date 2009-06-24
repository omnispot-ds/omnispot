package com.kesdip.designer.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.quartz.CronExpression;

import com.kesdip.designer.constenum.ResourceListColumnNames;
import com.kesdip.designer.model.Resource;

public class ResourceListDialog extends Dialog implements
		ISelectionChangedListener {
	private List<Resource> tableContents;
	private TableViewer table;
	private Button addButton;
	private Button removeButton;
	private Button upButton;
	private Button downButton;

	protected ResourceListDialog(Shell parentShell) {
		super(parentShell);
	}

	public void setValues(List<Resource> resourceList) {
		// Make a deep copy of the resource list, so that we have our very own
		// copy.
		// We need this because the text editors make modifications directly on
		// the
		// resource instance that we hold, and if this is the same instance as
		// the
		// resource list held by our caller, we might effect changes that the
		// user
		// will later cancel.
		tableContents = new ArrayList<Resource>();
		for (Resource r : resourceList) {
			tableContents.add(Resource.deepCopy(r));
		}
	}

	public List<Resource> getValues() {
		return tableContents;
	}

	public void updateButtons() {
		IStructuredSelection selection = (IStructuredSelection) table
				.getSelection();
		Resource selectedResource = null;
		if (selection.size() != 0)
			selectedResource = (Resource) selection.getFirstElement();
		int selectedIndex = tableContents.indexOf(selectedResource);
		upButton.setEnabled(selectedResource != null && selectedIndex != 0);
		downButton.setEnabled(selectedResource != null
				&& selectedIndex != tableContents.size() - 1);

		removeButton.setEnabled(selectedResource != null);

		if (getButton(IDialogConstants.OK_ID) != null) {
			boolean allOK = true;
			for (Resource r : tableContents) {
				File f = new File(r.getResource());
				if (!f.exists()) {
					allOK = false;
					break;
				}
				if (r.getCronExpression() == null
						|| r.getCronExpression().length() == 0)
					continue;
				if (!CronExpression.isValidExpression(r.getCronExpression())) {
					allOK = false;
					break;
				}
			}
			getButton(IDialogConstants.OK_ID).setEnabled(allOK);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		final Group resourcleListGroup = new Group(container, SWT.NONE);
		resourcleListGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		resourcleListGroup.setText("Resourcle List");
		resourcleListGroup.setLayout(new GridLayout());

		table = createResourceTableViewer(resourcleListGroup, this, tableContents);

		final Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(100, SWT.DEFAULT));
		composite.setLayout(new GridLayout());

		addButton = new Button(composite, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Resource newResource = new Resource("", null);
				tableContents.add(newResource);
				table.refresh();
				updateButtons();
			}
		});
		addButton.setLayoutData(new GridData(90, SWT.DEFAULT));
		addButton.setText("Add");

		removeButton = new Button(composite, SWT.NONE);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) table
						.getSelection();
				Resource selectedResource = (Resource) selection
						.getFirstElement();
				tableContents.remove(selectedResource);
				table.refresh();
				updateButtons();
			}
		});
		final GridData gd_removeButton = new GridData(90, SWT.DEFAULT);
		removeButton.setLayoutData(gd_removeButton);
		removeButton.setText("Remove");

		@SuppressWarnings("unused")
		final Label label = new Label(composite, SWT.NONE);

		upButton = new Button(composite, SWT.NONE);
		upButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) table
						.getSelection();
				Resource selectedResource = (Resource) selection
						.getFirstElement();
				int oldIndex = tableContents.indexOf(selectedResource);
				tableContents.remove(selectedResource);
				tableContents.add(oldIndex - 1, selectedResource);
				table.refresh();
				updateButtons();
			}
		});
		upButton.setLayoutData(new GridData(90, SWT.DEFAULT));
		upButton.setText("Up");

		downButton = new Button(composite, SWT.NONE);
		downButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) table
						.getSelection();
				Resource selectedResource = (Resource) selection
						.getFirstElement();
				int oldIndex = tableContents.indexOf(selectedResource);
				tableContents.remove(selectedResource);
				tableContents.add(oldIndex + 1, selectedResource);
				table.refresh();
				updateButtons();
			}
		});
		final GridData gd_downButton = new GridData(90, SWT.DEFAULT);
		downButton.setLayoutData(gd_downButton);
		downButton.setText("Down");

		updateButtons();

		return container;
	}

	/**
	 * Creates a populated and initialized table instance for addition in the
	 * dialog. Template method for descendants to override if necessary.
	 * 
	 * @param resourceListGroup
	 *            the component group in which to add the table
	 * @param listener
	 *            the selection changed listener
	 * @param tableContents
	 *            the contents of the table
	 * @return Table the table instance
	 */
	protected TableViewer createResourceTableViewer(Group resourceListGroup,
			ISelectionChangedListener listener, List<Resource> tableContents) {
		TableViewer table = new TableViewer(resourceListGroup,
				SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL
						| SWT.H_SCROLL);
		Table t = table.getTable();
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		table.setContentProvider(new ResourceListContentProvider());
		table.setLabelProvider(new ResourceListLabelProvider());
		table
				.setColumnProperties(new String[] { ResourceListColumnNames.RESOURCE,
						ResourceListColumnNames.CRON_EXPRESSION });
		table.setCellModifier(new ResourceCellModifier(this));
		ResourceFileCellEditor resourceCellEditor = new ResourceFileCellEditor(
				t, SWT.NONE);
		resourceCellEditor.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				String v = (String) value;
				if (v == null || "".equals(v)) {
					return null;
				}
				try {
					File f = new File(v);
					if (f.exists()) {
						// valid resource
						return null; 
					}
					return "Unable to locate resource at: " + v;
				} catch (Exception e) {
					return "Error while trying to access resource at: " + v
							+ " [" + e.getMessage() + "]";
				}
			}
		});
		CronCellEditor cronExpressionCellEditor = new CronCellEditor(t,
				SWT.NONE);
		/*
		 * cronExpressionCellEditor.setValidator(new ICellEditorValidator() {
		 * 
		 * @Override public String isValid(Object value) { String v = (String)
		 * value; if (v == null || "".equals(v)) return null; if
		 * (CronExpression.isValidExpression(v)) return null; else return
		 * "Invalid CRON expression: '" + v + "'."; } });
		 */
		table.setCellEditors(new CellEditor[] { resourceCellEditor,
				cronExpressionCellEditor });
		String[] columnNames = new String[] { "Resoure", "Cron Expr." };
		int[] columnWidths = new int[] { 250, 100 };
		int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT };
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(t, columnAlignments[i]);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
		}
		t.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setInput(tableContents);
		table.addSelectionChangedListener(listener);

		return table;
	}

	/**
	 * Create contents of the button bar
	 * 
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
		return new Point(500, 375);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		updateButtons();
	}

}
