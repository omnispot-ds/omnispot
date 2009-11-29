package com.kesdip.designer.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

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
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.preferences.PreferenceConstants;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.player.preview.PlayerPreview;

public class PreviewLayoutHandler extends AbstractHandler {

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
		if (de.getCurrentLayout() == null)
			return false;
		return true;
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
			Deployment d = de.getModel();
			Layout l = de.getCurrentLayout();
			Deployment tempDeployment = new Deployment();
			tempDeployment.setPropertyValue(Deployment.BIT_DEPTH_PROP,
					d.getPropertyValue(Deployment.BIT_DEPTH_PROP));
			tempDeployment.setPropertyValue(Deployment.SLEEP_INTERVAL_PROP,
					d.getPropertyValue(Deployment.SLEEP_INTERVAL_PROP));
			tempDeployment.setPropertyValue(Deployment.SIZE_PROP, d.getSize());
			tempDeployment.setPropertyValue(Deployment.ID_PROP,
					UUID.randomUUID().toString());
			tempDeployment.add(l.deepCopy());
			int displayWidth = Display.getDefault().getBounds().width -
				Display.getDefault().getBounds().x;
			int displayHeight = Display.getDefault().getBounds().height -
				Display.getDefault().getBounds().y;
			if (de.getModel().getSize().width != displayWidth ||
					de.getModel().getSize().height != displayHeight) {
				// We must scale in order to cover the whole screen
				double xFactor = ((double) displayWidth) /
						((double) tempDeployment.getSize().width);
				double yFactor = ((double) displayHeight) /
						((double) tempDeployment.getSize().height);
				tempDeployment.resizeBy(xFactor, yFactor);
			}
			File tempFile = File.createTempFile("layout", ".des.xml");
			try {
				OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));
				tempDeployment.serialize(os, false);
				os.close();
				
				String reason = PlayerPreview.canPreview(tempFile.getAbsolutePath());
				
				String vlcPath = Activator.getDefault().getPreferenceStore().getString(
						PreferenceConstants.P_VLC_PATH);
				
				String mPlayerPath = Activator.getDefault().getPreferenceStore().getString(
						PreferenceConstants.P_MPLAYER_FILE);

				if (reason == null) {
					String deploymentLocation = tempFile.getAbsolutePath();
					if (launchStandalone) {
						PreviewLauncher.launchPreview(deploymentLocation, vlcPath, mPlayerPath);
					} else {
						PlayerPreview.previewPlayer(deploymentLocation, vlcPath, mPlayerPath);
					}
				}
				else
					MessageDialog.openError(HandlerUtil.getActiveShell(event),
							"Unable to preview", reason);
			} finally {
				// TODO Commented out for standalone
				// tempFile.delete();
			}
		} catch (Exception e) {
			DesignerLog.logError("Unable to start deployment preview", e);
		}
		return null;
	}

}
