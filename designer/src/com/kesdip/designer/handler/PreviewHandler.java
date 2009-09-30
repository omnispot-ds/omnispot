package com.kesdip.designer.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kesdip.designer.Activator;
import com.kesdip.designer.editor.DeploymentEditor;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.preferences.PreferenceConstants;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.player.preview.PlayerPreview;

public class PreviewHandler extends AbstractHandler {
	
	private static boolean launchStandalone = true;

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
			
			String mPlayerFile = Activator.getDefault().getPreferenceStore().getString(
					PreferenceConstants.P_MPLAYER_FILE);

			if (reason == null) {
				String deploymentLocation = dei.getPath();
				File tempDeploymentFile = null;
				int displayWidth = Display.getDefault().getBounds().width -
					Display.getDefault().getBounds().x;
				int displayHeight = Display.getDefault().getBounds().height -
					Display.getDefault().getBounds().y;
				try {
					if (de.getModel().getSize().width != displayWidth ||
						de.getModel().getSize().height != displayHeight) {
						// We must scale in order to cover the whole screen
						Deployment d = (Deployment) de.getModel().deepCopy();
						double xFactor = ((double) displayWidth) /
								((double) de.getModel().getSize().width);
						double yFactor = ((double) displayHeight) /
								((double) de.getModel().getSize().height);
						d.resizeBy(xFactor, yFactor);
						tempDeploymentFile = File.createTempFile("deployment", ".des.xml");
						OutputStream os = new BufferedOutputStream(
								new FileOutputStream(tempDeploymentFile));
						d.serialize(os, false);
						os.close();
						deploymentLocation = tempDeploymentFile.getAbsolutePath();
					}
					if (launchStandalone) {
						PreviewLauncher.launchPreview(deploymentLocation, vlcPath, mPlayerFile);
					} else {
						PlayerPreview.previewPlayer(deploymentLocation, vlcPath, mPlayerFile);
					}
				} finally {
					// TODO These had to be commented out for standalone player to work
					//if (tempDeploymentFile != null)
						//tempDeploymentFile.delete();
				}
			} else {
				MessageDialog.openError(HandlerUtil.getActiveShell(event),
						"Unable to preview", reason);
			}
		} catch (Exception e) {
			DesignerLog.logError("Unable to start deployment preview", e);
		}
		return null;
	}

}
