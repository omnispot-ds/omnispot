package com.kesdip.designer.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Root;
import com.kesdip.designer.model.Region;

public class OutlinePartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof Root) {
			return new OutlineRootPart((Root) model);
		} else if (model instanceof Deployment) {
			return new OutlineDeploymentPart((Deployment) model);
		} else if (model instanceof Layout) {
			return new OutlineLayoutPart((Layout) model);
		} else if (model instanceof Region) {
			return new OutlineRegionPart((Region) model);
		} else if (model instanceof ComponentModelElement) {
			return new OutlineComponentPart((ComponentModelElement) model);
		}
		return null; // will not show an entry for the corresponding model instance
	}

}
