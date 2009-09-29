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

import com.kesdip.designer.model.ClockComponent;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.FlashComponent;
import com.kesdip.designer.model.FlashWeatherComponent;
import com.kesdip.designer.model.ImageComponent;
import com.kesdip.designer.model.Region;
import com.kesdip.designer.model.TickerComponent;
import com.kesdip.designer.model.TunerVideoComponent;
import com.kesdip.designer.model.VideoComponent;

/**
 * Utility class that can create a GEF Palette.
 * @see #createPalette() 
 * @author Pafsanias Ftakas
 */
final class DesignerEditorPaletteFactory {
	/** Create the "Containers" drawer. */
	private static PaletteContainer createContainersDrawer() {
		PaletteDrawer containersDrawer = new PaletteDrawer("Containers");
		
		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
				"Region", 
				"Create a region", 
				Region.class,
				new SimpleFactory(Region.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/film_folder.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/film_folder_large.png"));
		containersDrawer.add(component);
		
		return containersDrawer;
	}
	
	/** Create the "Components" drawer. */
	private static PaletteContainer createComponentsDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Components");
	
		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
				"Image", 
				"Create an image", 
				ImageComponent.class,
				new SimpleFactory(ImageComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/camera.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/camera_large.png"));
		componentsDrawer.add(component);
		
		component = new CombinedTemplateCreationEntry(
				"Clock", 
				"Create a clock", 
				ClockComponent.class,
				new SimpleFactory(ClockComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/clock.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/clock_large.png"));
		componentsDrawer.add(component);
	
		component = new CombinedTemplateCreationEntry(
				"Ticker",
				"Create a ticker", 
				TickerComponent.class,
				new SimpleFactory(TickerComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/ticker.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/ticker_large.png"));
		componentsDrawer.add(component);
	
		component = new CombinedTemplateCreationEntry(
				"Video", 
				"Create a video", 
				VideoComponent.class,
				new SimpleFactory(VideoComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/clapperboard.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/clapperboard_large.png"));
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry(
				"TunerVideo", 
				"Create a tuner video", 
				TunerVideoComponent.class,
				new SimpleFactory(TunerVideoComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/wireless.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/wireless_large.png"));
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry(
				"FlashComponent",
				"Create a flash component", 
				FlashComponent.class,
				new SimpleFactory(FlashComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/flash_cs3.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/flash_cs3_large.png"));
		componentsDrawer.add(component);
	
		component = new CombinedTemplateCreationEntry(
				"FlashWeatherComponent",
				"Create a flash weather component", 
				FlashWeatherComponent.class,
				new SimpleFactory(FlashWeatherComponent.class), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/cloud_sun.png"), 
				ImageDescriptor.createFromFile(Deployment.class, "icons/cloud_sun_large.png"));
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
		palette.add(createContainersDrawer());
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
