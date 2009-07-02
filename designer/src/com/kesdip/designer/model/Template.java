package com.kesdip.designer.model;

import org.eclipse.ui.IMemento;

public class Template {
	private Layout layout;
	private String filename;
	private String templateName;
	
	public Template() {
		layout = null;
		filename = "";
		templateName = "";
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public void save(IMemento memento) {
		memento.putString(ModelElement.TAG_FILENAME, filename);
		memento.putString(ModelElement.TAG_NAME, templateName);
		IMemento child = memento.createChild(ModelElement.TAG_LAYOUT);
		layout.save(child);
	}
	
	public void load(IMemento memento) {
		// filename has been set up by our caller
		templateName = memento.getString(ModelElement.TAG_NAME);
		layout = new Layout();
		layout.load(memento.getChild(ModelElement.TAG_LAYOUT));
	}
}
