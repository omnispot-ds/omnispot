package com.kesdip.designer.handler;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kesdip.designer.editor.DesignerEditor;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.designer.view.DeploymentView;

public class SaveFileHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			DeploymentView dv = (DeploymentView) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().
				showView("com.kesdip.designer.DeploymentView");
			final Deployment deployment = dv.getDeployment();
			
			String path = dv.getDeploymentPath();
			if (path == null) {
				FileDialog dialog = new FileDialog(
						HandlerUtil.getActiveShell(event), SWT.SAVE | SWT.APPLICATION_MODAL);
				dialog.setFilterNames(new String[] { "Koutra Designer Files", "All files (*.*)" });
				dialog.setFilterExtensions(new String[] { "*.des.xml", "*.*" });
				path = dialog.open();

			}
			DesignerLog.logInfo("File being edited path: " + path);

			OutputStream os = new BufferedOutputStream(
					new FileOutputStream(path));
			deployment.serialize(os);
			os.close();

			IEditorReference[] editors = PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage().getEditorReferences();
			for (IEditorReference ref : editors) {
				IEditorPart ed = ref.getEditor(true);
				if (ed instanceof DesignerEditor && ed.isDirty()) {
					((DesignerEditor) ed).markSaveLocation();
				}
			}
		} catch (Exception e) {
			DesignerLog.logError("Unable to save file", e);
		}
		return null;
	}

}
