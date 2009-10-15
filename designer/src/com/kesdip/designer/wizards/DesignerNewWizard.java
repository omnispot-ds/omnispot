package com.kesdip.designer.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import com.kesdip.designer.handler.DeploymentEditorInput;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;

public class DesignerNewWizard extends Wizard implements INewWizard {
	private DesignerNewWizardPage page;

	public DesignerNewWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		page = new DesignerNewWizardPage();
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String fileName = page.getFileName();
		final String width = page.getWidth();
		final String height = page.getHeight();
		final String bitDepth = page.getBitDepth();
		final int layoutNumber = Integer.parseInt(page.getLayoutNumber());
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(fileName, width, height, bitDepth, layoutNumber, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(String fileName, String width, String height,
			String bitDepth, int layoutNumber, IProgressMonitor monitor)
			throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		final File f = new File(fileName);
		try {
			InputStream stream = openContentStream(width, height, bitDepth, layoutNumber);
			OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
			int count;
			byte[] buffer = new byte[10 * 1024];
			while ((count = stream.read(buffer)) != -1) {
				os.write(buffer, 0, count);
			}
			stream.close();
			os.close();
		} catch (IOException e) {
			throwCoreException(e.getMessage());
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					Deployment input = loadInputFromFile(f);
					DeploymentEditorInput dei = new DeploymentEditorInput(
							input, f.getAbsolutePath());
					
					IDE.openEditor(PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getActivePage(), dei,
							"com.kesdip.designer.DeploymentEditor");
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	private Deployment loadInputFromFile(File f) {
		Deployment retVal;
		try {
			retVal = new Deployment();
			retVal.deserialize(new FileInputStream(f));
		} catch (Exception e) { 
			DesignerLog.logError("Unable to load model.", e);
			retVal = new Deployment();
		}
		
		return retVal;
	}

	/**
	 * We will initialize file contents with a sample text.
	 */
	private InputStream openContentStream(String width, String height,
			String bitDepth, int layoutNumber) {
		String emptyLayout =
		    "        <bean class=\"com.kesdip.player.DeploymentLayout\">\n" +
		    "          <property name=\"name\" value=\"Layout\"/>\n" +
		    "          <property name=\"showGrid\" value=\"false\"/>\n" +
		    "          <property name=\"snapToGeometry\" value=\"false\"/>\n" +
		    "          <property name=\"contentRoots\">\n" +
		    "            <list>\n" +
		    "            </list>\n" +
		    "          </property>\n" +
		    "        </bean>\n";
		StringBuilder layouts = new StringBuilder();
		for (int i = 0; i < layoutNumber; i++) {
			layouts.append(emptyLayout);
		}
		String contents =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd\">\n" +
			"  <bean class=\"com.kesdip.player.DeploymentSettings\" id=\"deploymentSettings\">\n" +
			"    <property name=\"width\" value=\"" + width + "\"/>\n" +
			"    <property name=\"height\" value=\"" + height + "\"/>\n" +
			"    <property name=\"bitDepth\" value=\"" + bitDepth + "\"/>\n" +
			"    <property name=\"id\" value=\"" + UUID.randomUUID() + "\"/>\n" +
			"    <property name=\"startTime\">\n" +
			"      <bean class=\"java.util.Date\">\n" +
			"        <constructor-arg type=\"long\" value=\"1236192600000\"/>\n" +
			"      </bean>\n" +
			"    </property>\n" +
			"  </bean>\n" +
			"  <bean class=\"com.kesdip.player.DeploymentContents\" id=\"deploymentContents\">\n" +
		    "    <property name=\"layouts\">\n" +
		    "      <list>\n" +
		    layouts.toString() +
		    "      </list>\n" +
		    "    </property>\n" +
		    "  </bean>\n" +
			"</beans>";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "com.kesdip.designer", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}