package com.kesdip.designer.properties;

import org.eclipse.draw2d.geometry.Point;
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

public class LocationPropertySource implements IPropertySource {
	private static final String X_PROP = "location.x_prop";
	private static final String Y_PROP = "location.y_prop";
	
	private Point location;
	private Object owner;
	
	public LocationPropertySource(Point location, Object owner) {
		this.location = location;
		this.owner = owner;
	}
	
	@Override
	public Object getEditableValue() {
		return location;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(X_PROP, "X"),
				new TextPropertyDescriptor(Y_PROP, "Y")
		};
		for (int i = 0; i < descriptors.length; i++) {
			if (Y_PROP.equals(descriptors[i].getId())) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					@Override
					public String isValid(Object value) {
						try {
							int newYLocation = Integer.parseInt(value.toString());
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
										newYLocation + region.getSize().height)
									return "Changing the y location would take the " +
											"component outside the deployment area";
							}
							if (parent instanceof Region &&
									owner instanceof ComponentModelElement) {
								Region region = (Region) parent;
								ComponentModelElement element =
									(ComponentModelElement) owner;
								if (region.getSize().height <
										newYLocation + element.getSize().height) {
									return "Changing the y location would take the " +
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
			} else if (X_PROP.equals(descriptors[i].getId())) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					@Override
					public String isValid(Object value) {
						try {
							int newXLocation = Integer.parseInt(value.toString());
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
										newXLocation + region.getSize().width)
									return "Changing the x location would take the " +
											"component outside the deployment area";
							}
							if (parent instanceof Region &&
									owner instanceof ComponentModelElement) {
								Region region = (Region) parent;
								ComponentModelElement element =
									(ComponentModelElement) owner;
								if (region.getSize().width <
										newXLocation + element.getSize().width) {
									return "Changing the x location would take the " +
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
		if (X_PROP.equals(id)) {
			return Integer.toString(location.x);
		} else if (Y_PROP.equals(id)) {
			return Integer.toString(location.y);
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (X_PROP.equals(id)) {
			return true;
		} else if (Y_PROP.equals(id)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (X_PROP.equals(id)) {
			location.x = 0;
		} else if (Y_PROP.equals(id)) {
			location.y = 0;
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (X_PROP.equals(id)) {
			location.x = Integer.parseInt((String) value);
		} else if (Y_PROP.equals(id)) {
			location.y = Integer.parseInt((String) value);
		}
	}

}
