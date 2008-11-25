package com.kesdip.player.components;

/**
 * The interface that all components in the KESDIP player should implement.
 * 
 * @author Pafsanias Ftakas
 */
public interface Component {
	/**
	 * Initialize the component. Implementations should do things, like create
	 * the swing resources necessary for display (JPanels, etc), and register
	 * themselves with the parent component through the add interface.
	 * @param parent The parent component that this component is to be added to.
	 * @throws ComponentException Iff something goes wrong.
	 */
	void init(Component parent) throws ComponentException;
	
	/**
	 * A interface to build the component hierarchy. Some components are not
	 * containers. These have to advertise this fact in their documentation,
	 * and throw a runtime exception should this interface be called.
	 * @param component The component to add to this container.
	 * @throws ComponentException Iff something goes wrong.
	 */
	void add(Component component) throws ComponentException;
	
	/**
	 * If the component is to be backed by a window component (a JPanel for
	 * example), then this interface will be used to extract this component
	 * from the KESDIP component.
	 * @return The corresponding window component, or null if not applicable.
	 */
	java.awt.Component getWindowComponent();
	
	/**
	 * Repaint.
	 * @throws ComponentException Iff something goes wrong.
	 */
	void repaint() throws ComponentException;
}
