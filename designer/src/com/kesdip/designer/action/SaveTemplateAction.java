package com.kesdip.designer.action;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.kesdip.designer.model.Layout;
import com.kesdip.designer.template.TemplateManager;

public class SaveTemplateAction extends SelectionAction {
	public static final String ID = "com.kesdip.designer.action.SaveTemplateAction";
	
	public SaveTemplateAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		List objects = getSelectedObjects();
		Layout l = (Layout) ((EditPart) objects.get(0)).getModel();
		TemplateManager.getManager().addTemplate(l.getName(), l);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean calculateEnabled() {
		List objects = getSelectedObjects();
		if (objects == null || objects.size() != 1)
			return false;
		if (!(objects.get(0) instanceof EditPart))
			return false;
		if (!(((EditPart) objects.get(0)).getModel() instanceof Layout))
			return false;
		return true;
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setId(ID);
		setText("Save Template");
		setEnabled(false);
	}

}
