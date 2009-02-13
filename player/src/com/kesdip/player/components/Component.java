/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.util.Set;

import com.kesdip.player.DeploymentLayout;
import com.kesdip.player.TimingMonitor;

/**
 * The interface that all components in the KESDIP player should implement.
 * 
 * The component life-cycle is as follows:
 * <pre>
 * repeat { // layout loop
 *     component.init();
 *     for each child {
 *         component.add(child); // Only for container components
 *     }
 *     repeat { // render loop
 *         component.isComplete();
 *         component.repaint();
 *     }
 *     component.releaseResources();
 * }
 * </pre>
 * 
 * @author Pafsanias Ftakas
 */
public interface Component {
	/**
	 * Initialize the component. Implementations should do things, like create
	 * the swing resources necessary for display (JPanels, etc), and register
	 * themselves with the parent component through the add interface.
	 * @param parent The parent component that this component is to be added to.
	 * @param timingMonitor The timing monitor to schedule jobs with.
	 * @throws ComponentException Iff something goes wrong.
	 */
	void init(Component parent, TimingMonitor timingMonitor) throws ComponentException;
	
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
	
	/**
	 * Components for which it makes sense to note them as completed, should
	 * implement this class. This is intended to be used for components such as
	 * the video component that plays a list of videos, and some action should
	 * be taken when that list has finished playing.
	 * @return One of the different states that determine if this component
	 * has completed, or if it does not care about completion (most components
	 * will fit this later category).
	 */
	DeploymentLayout.CompletionStatus isComplete();
	
	/**
	 * Components that want to release resources must implement this method.
	 * Please see the component life-cycle to understand more about when this
	 * function gets called in the player execution.
	 */
	void releaseResources();
	
	/**
	 * Since components can be containers and can also refer to various
	 * resources, we want to have a means of gathering all the resources that
	 * a component hierarchy is using. This method caters for this use case.
	 * @return The set of resources contained in the hierarchy that is rooted
	 * in this component.
	 */
	Set<Resource> gatherResources();
	
	/**
	 * When resource have cron expression associated with them, the components should
	 * honor this interface by stopping running whatever it is they are showing right
	 * now in order to show the resource specified by this interface.
	 * 
	 * This only makes sense for the non-container components. The abstract component
	 * has a void implementation of this interface.
	 * @param resource The resource to run.
	 */
	void runResource(Resource resource);
}
