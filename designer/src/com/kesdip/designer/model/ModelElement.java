package com.kesdip.designer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.eclipse.swt.graphics.Image;
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

	/** An empty property descriptor. */
	private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

	/** Delegate used to implement property-change-support. */
	private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

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
	 * Children should override this. The default implementation returns null.
	 */
	@Override
	public Object getPropertyValue(Object id) {
		return null;
	}

	/**
	 * Children should override this. The default implementation returns false.
	 */
	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	/**
	 * Children should override this. The default implementation does nothing.
	 */
	@Override
	public void resetPropertyValue(Object id) {
		// Intentionally empty.
	}

	/**
	 * Children should override this. The default implementation does nothing.
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		// Intentionally empty.
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

}
