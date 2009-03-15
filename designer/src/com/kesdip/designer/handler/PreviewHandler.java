package com.kesdip.designer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kesdip.designer.Activator;
import com.kesdip.designer.editor.DeploymentEditor;
import com.kesdip.designer.preferences.PreferenceConstants;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.player.preview.PlayerPreview;

public class PreviewHandler extends AbstractHandler implements IHandler {

	@Override
	public boolean isEnabled() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null)
			return false;
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null)
			return false;
		IEditorPart editor = PlatformUI.getWorkbench().
			getActiveWorkbenchWindow().getActivePage().getActiveEditor();
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

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IEditorPart editor = PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editor == null)
				return false;
			if (!(editor instanceof DeploymentEditor))
				return false;
			DeploymentEditor de = (DeploymentEditor) editor;
			DeploymentEditorInput dei = (DeploymentEditorInput) de.getEditorInput();
			String reason = PlayerPreview.canPreview(dei.getPath());
			
			String vlcPath = Activator.getDefault().getPreferenceStore().getString(
					PreferenceConstants.P_VLC_PATH);
			
			if (reason == null)
				PlayerPreview.previewPlayer(dei.getPath(), vlcPath);
			else
				MessageDialog.openError(HandlerUtil.getActiveShell(event),
						"Unable to preview", reason);
		} catch (Exception e) {
			DesignerLog.logError("Unable to start deployment preview", e);
		}
		return null;
	}

}
