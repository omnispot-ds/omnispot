package com.kesdip.designer.editor;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.ImageComponent;
import com.kesdip.designer.model.Region;
import com.kesdip.designer.model.TickerComponent;
import com.kesdip.designer.model.VideoComponent;

/**
 * Utility class that can create a GEF Palette.
 * @see #createPalette() 
 * @author Pafsanias Ftakas
 */
final class DesignerEditorPaletteFactory {
	/** Create the "Components" drawer. */
	private static PaletteContainer createComponentsDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Components");
	
		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
				"Region", 
				"Create a region", 
				Region.class,
				new SimpleFactory(Region.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle16.gif"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle24.gif"));
		componentsDrawer.add(component);
	
		component = new CombinedTemplateCreationEntry(
				"Image", 
				"Create an image", 
				ImageComponent.class,
				new SimpleFactory(ImageComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle16.gif"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle24.gif"));
		componentsDrawer.add(component);
	
		component = new CombinedTemplateCreationEntry(
				"Ticker",
				"Create a ticker", 
				TickerComponent.class,
				new SimpleFactory(TickerComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle16.gif"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle24.gif"));
		componentsDrawer.add(component);
	
		component = new CombinedTemplateCreationEntry(
				"VLCVideo", 
				"Create a VLC video", 
				VideoComponent.class,
				new SimpleFactory(VideoComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle16.gif"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/rectangle24.gif"));
		componentsDrawer.add(component);

		return componentsDrawer;
	}
		
	/**
	 * Creates the PaletteRoot and adds all palette elements.
	 * Use this factory method to create a new palette for your graphical editor.
	 * @return a new PaletteRoot
	 */
	static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createComponentsDrawer());
		return palette;
	}
	
	/** Create the "Tools" group. */
	private static PaletteContainer createToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
	
		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);
		
		// Add a marquee tool to the group
		toolbar.add(new MarqueeToolEntry());
	
		return toolbar;
	}
	
	/** Utility class. */
	private DesignerEditorPaletteFactory() {
		// Utility class
	}

}
