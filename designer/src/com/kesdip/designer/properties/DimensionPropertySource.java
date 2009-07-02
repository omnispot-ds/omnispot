package com.kesdip.designer.properties;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Region;

public class DimensionPropertySource implements IPropertySource {
	private static final String HEIGHT_PROP = "dimension.height_prop";
	private static final String WIDTH_PROP = "dimension.width_prop";
	
	private Dimension dimension;
	private Object owner;
	
	public DimensionPropertySource(Dimension dimension, Object owner) {
		this.dimension = dimension;
		this.owner = owner;
	}
	
	@Override
	public Object getEditableValue() {
		return dimension;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(HEIGHT_PROP, "Height"),
				new TextPropertyDescriptor(WIDTH_PROP, "Width")
		};
		for (int i = 0; i < descriptors.length; i++) {
			if (HEIGHT_PROP.equals(descriptors[i].getId())) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					@Override
					public String isValid(Object value) {
						try {
							int newHeight = Integer.parseInt(value.toString());
							if (!(owner instanceof ModelElement))
								return null;
							ModelElement parent = ((ModelElement) owner).getParent();
							if (parent == null)
								return null;
							if (parent instanceof Layout && owner instanceof Region) {
								Deployment deployment = (Deployment)
									((Layout) parent).getParent();
								Region region = (Region) owner;
								if (deployment.getSize().height <
										region.getLocation().y + newHeight)
									return "Changing the height would take the " +
											"component outside the deployment area";
							}
							if (parent instanceof Region &&
									owner instanceof ComponentModelElement) {
								Region region = (Region) parent;
								ComponentModelElement element =
									(ComponentModelElement) owner;
								if (region.getSize().height <
										element.getLocation().y + newHeight) {
									return "Changing the height would take the " +
											"component outside the region area";
								}
							}
							return null;
						} catch (Exception e) {
							return "Unable to convert value: " +
								value.toString() + " to an integer.";
						}
					}
				});
			} else if (WIDTH_PROP.equals(descriptors[i].getId())) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					@Override
					public String isValid(Object value) {
						try {
							int newWidth = Integer.parseInt(value.toString());
							if (!(owner instanceof ModelElement))
								return null;
							ModelElement parent = ((ModelElement) owner).getParent();
							if (parent == null)
								return null;
							if (parent instanceof Layout && owner instanceof Region) {
								Deployment deployment = (Deployment)
									((Layout) parent).getParent();
								Region region = (Region) owner;
								if (deployment.getSize().width <
										region.getLocation().x + newWidth)
									return "Changing the width would take the " +
											"component outside the deployment area";
							}
							if (parent instanceof Region &&
									owner instanceof ComponentModelElement) {
								Region region = (Region) parent;
								ComponentModelElement element =
									(ComponentModelElement) owner;
								if (region.getSize().width <
										element.getLocation().x + newWidth) {
									return "Changing the width would take the " +
											"component outside the region area";
								}
							}
							return null;
						} catch (Exception e) {
							return "Unable to convert value: " +
								value.toString() + " to an integer.";
						}
					}
				});
			}
		}
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (HEIGHT_PROP.equals(id)) {
			return Integer.toString(dimension.height);
		} else if (WIDTH_PROP.equals(id)) {
			return Integer.toString(dimension.width);
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (HEIGHT_PROP.equals(id)) {
			return true;
		} else if (WIDTH_PROP.equals(id)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (HEIGHT_PROP.equals(id)) {
			dimension.height = 0;
		} else if (WIDTH_PROP.equals(id)) {
			dimension.width = 0;
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (HEIGHT_PROP.equals(id)) {
			dimension.height = Integer.parseInt((String) value);
		} else if (WIDTH_PROP.equals(id)) {
			dimension.width = Integer.parseInt((String) value);
		}
	}

}
