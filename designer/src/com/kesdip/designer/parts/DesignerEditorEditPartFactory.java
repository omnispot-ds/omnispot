package com.kesdip.designer.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Region;

public class DesignerEditorEditPartFactory implements EditPartFactory {

	@Override
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object modelElement) {
		// get EditPart for model element
		EditPart part = getPartForElement(modelElement);
		// store model element in EditPart
		part.setModel(modelElement);
		return part;
	}

	/**
	 * Maps an object to an EditPart. 
	 * @throws RuntimeException if no match was found (programming error)
	 */
	private EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof Layout) {
			return new LayoutEditPart();
		} else if (modelElement instanceof Region) {
			return new RegionEditPart();
		} else if (modelElement instanceof ComponentModelElement) {
			return new ComponentEditPart();
		}
		throw new RuntimeException(
				"Can't create part for model element: "
				+ ((modelElement != null) ? modelElement.getClass().getName() : "null"));
	}

}
