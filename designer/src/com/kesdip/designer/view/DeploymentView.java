package com.kesdip.designer.view;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import com.kesdip.designer.action.CreateLayoutAction;
import com.kesdip.designer.action.DeleteLayoutAction;
import com.kesdip.designer.action.EditLayoutAction;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class DeploymentView extends ViewPart implements PropertyChangeListener {
	
	private TableViewer viewer;
	private boolean isDirty;
	private Deployment deployment;
	private String deploymentPath;
	private List<Layout> layouts;
	
	private void unregisterFromLayouts() {
		if (layouts != null) {
			for (Layout l : layouts) {
				l.removePropertyChangeListener(this);
			}
		}
	}
	
	private void registerToLayouts() {
		layouts = new ArrayList<Layout>();
		for (Layout l : deployment.getLayouts()) {
			layouts.add(l);
			l.addPropertyChangeListener(this);
		}
	}

	public void setDeployment(Deployment deployment, String path) {
		if (this.deployment != null) {
			this.deployment.removePropertyChangeListener(this);
			unregisterFromLayouts();
		}
		this.deployment = deployment;
		this.deploymentPath = path;
		this.deployment.addPropertyChangeListener(this);
		registerToLayouts();
		viewer.setInput(deployment);
		isDirty = false;
	}
	
	public void setDirty() {
		isDirty = true;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public Deployment getDeployment() {
		return deployment;
	}
	
	public String getDeploymentPath() {
		return deploymentPath;
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setContentProvider(new DeploymentContentProvider());
		viewer.setLabelProvider(new DeploymentLabelProvider());
		
		createContextMenu();
		
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		unregisterFromLayouts();
		viewer.refresh();
		isDirty = true;
		registerToLayouts();
	}
	
	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}
	
	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager menuMgr) {
				DeploymentView.this.fillContextMenu(menuMgr);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	private void fillContextMenu(IMenuManager menuMgr) {
		if (deployment != null) {
			menuMgr.add(new CreateLayoutAction(deployment));
			if (viewer.getSelection() != null && !getSelection().isEmpty()) {
				menuMgr.add(new DeleteLayoutAction(deployment, getSelection()));
				if (getSelection().size() == 1) {
					menuMgr.add(new EditLayoutAction(getSelection()));
				}
			}
		}
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
