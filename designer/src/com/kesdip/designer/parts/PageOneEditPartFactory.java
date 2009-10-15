package com.kesdip.designer.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Root;

public class PageOneEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof Root) {
			return new PageOneRootPart((Root) model);
		} else if (model instanceof Deployment) {
			return new PageOneDeploymentPart((Deployment) model);
		} else if (model instanceof Layout) {
			return new PageOneLayoutPart((Layout) model);
		}
		return null;
	}

}
