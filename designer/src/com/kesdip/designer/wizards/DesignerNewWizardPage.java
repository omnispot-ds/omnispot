package com.kesdip.designer.wizards;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (des.xml).
 */

public class DesignerNewWizardPage extends WizardPage {
	private Text fileText;
	private Combo width;
	private Combo height;
	private Combo bitDepth;
	private Combo layoutNumber;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public DesignerNewWizardPage() {
		super("wizardPage");
		setTitle("Designer Editor File");
		setDescription("This wizard creates a new file with *.des.xml extension" +
				"that can be opened by a designer editor.");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&File:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("&Width:");

		width = new Combo(container, SWT.BORDER | SWT.SINGLE);
		width.setItems(new String[] { "640", "800", "1024", "1280" });
		gd = new GridData();
		width.setLayoutData(gd);
		width.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		label = new Label(container, SWT.NULL);
		label = new Label(container, SWT.NULL);
		label.setText("&Height:");

		height = new Combo(container, SWT.BORDER | SWT.SINGLE);
		height.setItems(new String[] { "400", "600", "800" });
		gd = new GridData();
		height.setLayoutData(gd);
		height.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		label = new Label(container, SWT.NULL);
		label = new Label(container, SWT.NULL);
		label.setText("Bit &depth:");

		bitDepth = new Combo(container, SWT.BORDER | SWT.SINGLE);
		bitDepth.setItems(new String[] { "1", "8", "16", "24", "32" });
		gd = new GridData();
		bitDepth.setLayoutData(gd);
		bitDepth.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		label = new Label(container, SWT.NULL);
		label = new Label(container, SWT.NULL);
		label.setText("# of &layouts:");

		layoutNumber = new Combo(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		layoutNumber.setItems(new String[] { "1", "2", "3", "4", "5" });
		gd = new GridData();
		layoutNumber.setLayoutData(gd);
		layoutNumber.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		label = new Label(container, SWT.NULL);
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		fileText.setText("new_file.des.xml");
		width.setText("1280");
		height.setText("800");
		bitDepth.setText("24");
		layoutNumber.setText("1");
	}
	
	private void handleBrowse() {
		FileDialog dialog = new FileDialog(
				getShell(), SWT.SAVE | SWT.APPLICATION_MODAL);
		dialog.setText("Choose File");
		dialog.setFilterNames(new String[] { "Omni-Spot Designer Files", "All files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.des.xml", "*.*" });
		fileText.setText(dialog.open());
	}

	private void dialogChanged() {
		String fileName = getFileName();

		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		File f = new File(fileName);
		if (f.exists()) {
			updateWarning("File already exists. If you continue, the content of the " +
					"file will be overwritten with the output of this wizard.");
		} else {
			updateWarning(null);
		}
		if (!f.exists()) {
			try {
				if (f.createNewFile())
					f.delete();
			} catch (Exception e) {
				updateStatus("The path to the file must be valid");
				return;
			}
		}
		if (f.getParentFile() == null) {
			updateStatus("The full path to the file must be specified");
			return;
		}
		if (!f.getParentFile().isDirectory()) {
			updateStatus("The parent directory is not valid: " +
					f.getParentFile().getAbsolutePath());
			return;
		}
		if (!fileName.endsWith("des.xml")) {
			updateStatus("File extension must be \"des.xml\"");
			return;
		}
		if (!isPositiveInteger(width.getText())) {
			updateStatus("Width must be a positive integer value");
			return;
		}
		if (!isPositiveInteger(height.getText())) {
			updateStatus("Height must be a positive integer value");
			return;
		}
		if (!isPositiveInteger(bitDepth.getText())) {
			updateStatus("Bit depth must be a positive integer value");
			return;
		}
		updateStatus(null);
	}
	
	private boolean isPositiveInteger(String v) {
		try {
			int i = Integer.parseInt(v);
			return i > 0;
		} catch (Exception e) {
			return false;
		}
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	private void updateWarning(String warning) {
		setMessage(warning, WizardPage.WARNING);
	}

	public String getFileName() {
		return fileText.getText();
	}
	
	public String getWidth() {
		return width.getText();
	}
	
	public String getHeight() {
		return height.getText();
	}
	
	public String getBitDepth() {
		return bitDepth.getText();
	}
	
	public String getLayoutNumber() {
		return layoutNumber.getText();
	}
	
}