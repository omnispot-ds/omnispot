package com.kesdip.designer.model;

import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.kesdip.designer.utils.DesignerLog;

/**
 * The base class of the AdDesigner model. Based on the GEF example by
 * Elias Volanakis.
 * @author Pafsanias Ftakas
 */
public abstract class ModelElement implements IPropertySource, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String PARENT_PROP = "ModelElement.Parent";
	public static final String CHILD_MOVE_UP = "ModelElement.ChildMoveUp";
	public static final String CHILD_MOVE_DOWN = "ModelElement.ChildMoveDown";

	/** An empty property descriptor. */
	private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

	/** Delegate used to implement property-change-support. */
	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);
	
	private ModelElement parent;
	
	public ModelElement() {
		setParent(null);
	}
	
	public void setParent(ModelElement parent) {
		setPropertyValue(PARENT_PROP, parent);
	}
	
	public ModelElement getParent() {
		return parent;
	}
	
	public abstract ModelElement deepCopy();
	public abstract void add(ModelElement child);
	public abstract void insertChildAt(int index, ModelElement child);
	public abstract List<ModelElement> getChildren();
	public abstract boolean removeChild(ModelElement child);
	public abstract boolean isFirstChild(ModelElement child);
	public abstract boolean isLastChild(ModelElement child);
	public abstract boolean moveChildUp(ModelElement child);
	public abstract boolean moveChildDown(ModelElement child);
	
	/**
	 * Returns a value for this property source that can be edited in a property sheet.
	 * <p>Rule of thumb of Mr. Volanakis:</p>
	 * <ul>
	 * <li>model elements should return themselves and</li> 
	 * <li>custom IPropertySource implementations (like DimensionPropertySource in the GEF-logic
	 * example) should return an editable value.</li>
	 * </ul>
	 * <p>Override only if necessary.</p>
	 * @return this instance
	 */
	@Override
	public Object getEditableValue() {
		return this;
	}

	/**
	 * Children should override this. The default implementation returns an empty array.
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return EMPTY_ARRAY;
	}

	/**
	 * Children should override this. Always call super.getPropertyValue() in subclasses.
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PARENT_PROP.equals(id))
			return getParent();
		return null;
	}

	/**
	 * Children should override this. Always call super.isPropertySet() in subclasses.
	 */
	@Override
	public boolean isPropertySet(Object id) {
		if (PARENT_PROP.equals(id))
			return true;
		return false;
	}

	/**
	 * Children should override this. Always call super.resetPropertyValue() in subclasses.
	 */
	@Override
	public void resetPropertyValue(Object id) {
		if (PARENT_PROP.equals(id))
			setParent(null);
	}

	/**
	 * Children should override this. Always call super.setPropertyValue() in subclasses.
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PARENT_PROP.equals(id)) {
			ModelElement oldValue = parent;
			parent = (ModelElement) value;
			firePropertyChange(PARENT_PROP, oldValue, value);
		}
	}

	/**
	 * Return an icon describing this model element.
	 * Children should override this method and return an appropriate Image.
	 * @return a 16x16 Image or null
	 */
	public abstract Image getIcon();

	protected static Image createImage(String name) {
		InputStream stream = ModelElement.class.getResourceAsStream(name);
		Image image = new Image(null, stream);
		try {
			stream.close();
		} catch (IOException ioe) {
			DesignerLog.logError("Unable to create image for: " + name, ioe);
		}
		return image;
	}

	/** 
	 * Attach a non-null PropertyChangeListener to this object.
	 * @param l a non-null PropertyChangeListener instance
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		pcsDelegate.addPropertyChangeListener(l);
	}

	/** 
	 * Remove a PropertyChangeListener from this component.
	 * @param l a PropertyChangeListener instance
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		if (l != null) {
			pcsDelegate.removePropertyChangeListener(l);
		}
	}

	/** 
	 * Report a property change to registered listeners (for example edit parts).
	 * @param property the programmatic name of the property that changed
	 * @param oldValue the old value of this property
	 * @param newValue the new value of this property
	 */
	protected void firePropertyChange(String property, Object oldValue, Object newValue) {
		if (pcsDelegate.hasListeners(property)) {
			pcsDelegate.firePropertyChange(property, oldValue, newValue);
		}
	}

	protected org.eclipse.swt.graphics.Font assureFontExists(Font sourceFont) {
		FontData[] fontDataArray = Display.getDefault().getFontList(
				sourceFont.getFamily(), true);
		if (fontDataArray.length == 0)
			return null;
		FontData retVal = fontDataArray[0];
		retVal.setName(sourceFont.getFamily());
		int style = Font.PLAIN;
		if ((sourceFont.getStyle() & SWT.BOLD) != 0)
			style |= Font.BOLD;
		if ((sourceFont.getStyle() & SWT.ITALIC) != 0)
			style |= Font.ITALIC;
		retVal.setStyle(style);
		retVal.setHeight(sourceFont.getSize());
		return new org.eclipse.swt.graphics.Font(Display.getCurrent(), retVal);
	}
}