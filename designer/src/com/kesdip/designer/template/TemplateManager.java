package com.kesdip.designer.template;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import com.kesdip.designer.Activator;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Template;
import com.kesdip.designer.utils.DesignerLog;

public class TemplateManager {
	private static TemplateManager singleton;
	
	public static synchronized TemplateManager getManager() {
		if (singleton == null) {
			singleton = new TemplateManager();
			singleton.loadTemplates();
		}
		
		return singleton;
	}
	
	private List<Template> templateList;
	
	private TemplateManager() {
		templateList = new ArrayList<Template>();
	}
	
	public void addTemplate(String name, Layout l) {
		if (templateNameIsInUse(name))
			throw new RuntimeException("Unable to add template with name: " +
					name + ", because it is already in use.");
		Template t = new Template();
		t.setTemplateName(name);
		t.setLayout(l);
		saveTemplate(t);
		templateList.add(t);
		sortList();
	}
	
	public void removeTemplate(Template t) {
		File f = new File(t.getFilename());
		f.delete();
		templateList.remove(t);
		sortList();
	}
	
	public String[] getTemplateNames() {
		String[] retVal = new String[templateList.size()];
		for (int i = 0; i < templateList.size(); i++) {
			retVal[i] = templateList.get(i).getTemplateName();
		}
		return retVal;
	}
	
	private boolean templateNameIsInUse(String name) {
		for (Template t : templateList) {
			if (t.getTemplateName().equals(name))
				return true;
		}
		return false;
	}
	
	private void sortList() {
		Collections.sort(templateList, new Comparator<Template>() {
			@Override
			public int compare(Template o1, Template o2) {
				return o1.getTemplateName().compareTo(o2.getTemplateName());
			}
		});
	}
	
	private void saveTemplate(Template t) {
		File templateDir = Activator.getDefault().getStateLocation().
				append("templates").toFile();
		if (!templateDir.exists()) {
			if (!templateDir.mkdir()) {
				throw new RuntimeException("Unable to create template directory.");
			}
		}
		File file;
		do {
			file = new File(templateDir, UUID.randomUUID() + ".tmpl.xml");
			t.setFilename(file.getAbsolutePath());
		} while (file.exists());
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			XMLMemento w = XMLMemento.createWriteRoot(ModelElement.TAG_TEMPLATE);
			t.save(w);
			w.save(writer);
		} catch (IOException e) {
			DesignerLog.logError("Unable to save template to: " +
					file.getPath(), e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				DesignerLog.logError("Unable to close template writer", e);
			}
		}
	}
	
	private void loadTemplates() {
		File templateDir = Activator.getDefault().getStateLocation().
				append("templates").toFile();
		if (!templateDir.exists()) {
			if (!templateDir.mkdir()) {
				throw new RuntimeException("Unable to create template directory.");
			}
		}
		File[] files = templateDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".tmpl.xml");
			}
		});
		if (files == null)
			return;
		for (File f : files) {
			FileReader reader = null;
			try {
				Template t = new Template();
				t.setFilename(f.getAbsolutePath());
				reader = new FileReader(f);
				t.load(XMLMemento.createReadRoot(reader));
				templateList.add(t);
			} catch (FileNotFoundException e) {
				DesignerLog.logError("Unable to load template from: " +
						f.getPath(), e);
			} catch (WorkbenchException e) {
				DesignerLog.logError("Unable to load template from: " +
						f.getPath(), e);
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (IOException e) {
					DesignerLog.logError("Unable to close template reader", e);
				}
			}
		}
		sortList();
	}
}
