package com.kesdip.designer.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kesdip.designer.constenum.IFileNames;
import com.kesdip.designer.editor.DeploymentEditor;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.player.preview.PlayerPreview;

public class PublishHandler extends AbstractHandler implements IHandler {

	@Override
	public boolean isEnabled() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null)
			return false;
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage() == null)
			return false;
		IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor == null)
			return false;
		if (!(editor instanceof DeploymentEditor))
			return false;
		DeploymentEditor de = (DeploymentEditor) editor;
		if (!(de.getEditorInput() instanceof DeploymentEditorInput))
			return false;
		DeploymentEditorInput dei = (DeploymentEditorInput) de.getEditorInput();
		if (dei.getPath() == null)
			return false;
		return !de.isDirty();
	}

	/**
	 * Publish a deployment into a ZIP archive along with all dependent
	 * resources.
	 * <p>
	 * The deployment is serialized as {@link IFileNames#DEPLOYMENT_XML} and all
	 * references to resources are changed to local path names.
	 * </p>
	 * 
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IEditorPart editor = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			if (editor == null)
				return false;
			if (!(editor instanceof DeploymentEditor))
				return false;
			DeploymentEditor de = (DeploymentEditor) editor;
			DeploymentEditorInput dei = (DeploymentEditorInput) de
					.getEditorInput();

			String reason = PlayerPreview.canPreview(dei.getPath());
			if (reason != null) {
				MessageDialog.openError(HandlerUtil.getActiveShell(event),
						"Unable to export deployment", reason);
				return null;
			}

			FileDialog dialog = new FileDialog(HandlerUtil
					.getActiveShell(event), SWT.SAVE | SWT.APPLICATION_MODAL);
			dialog.setText("Choose an export destination");
			dialog
					.setFilterNames(new String[] { "ZIP Files",
							"All files (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.zip", "*.*" });
			String path = dialog.open();
			DesignerLog.logInfo("User entered path: " + path);

			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(path)));
			// set the proper XML file name
			ZipEntry entry = new ZipEntry(IFileNames.DEPLOYMENT_XML);
			zos.putNextEntry(entry);
			dei.getDeployment().serialize(zos, true);
			Set<String> resourcePaths = PlayerPreview.getResourcePaths(dei
					.getPath());
			for (String resourcePath : resourcePaths) {
				File f = new File(resourcePath);
				entry = new ZipEntry(f.getName());
				zos.putNextEntry(entry);
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				byte[] buffer = new byte[8 * 1024];
				int count;
				while ((count = is.read(buffer)) != -1) {
					zos.write(buffer, 0, count);
				}
				is.close();
			}
			zos.close();
		} catch (Exception e) {
			DesignerLog.logError("Unable to export deployment for publishing",
					e);
		}
		return null;
	}

}
