package com.kesdip.player.component.test;

import com.jniwrapper.win32.automation.OleContainer;
import com.jniwrapper.win32.automation.OleMessageLoop;
import com.jniwrapper.win32.automation.types.BStr;
import com.jniwrapper.win32.com.types.ClsCtx;
import com.jniwrapper.win32.ole.IOleObject;
import com.jniwrapper.win32.ole.OleFunctions;
import com.jniwrapper.win32.ole.impl.IOleObjectImpl;
import com.jniwrapper.win32.ole.types.OleVerbs;
import com.kesdip.player.components.flash.shockwaveflashobjects.IShockwaveFlash;
import com.kesdip.player.components.flash.shockwaveflashobjects.ShockwaveFlash;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class FlashComponent extends JFrame {
	private static final String WINDOW_TITLE = "JNIWrapper - Shockwave Flash Integration";

	private OleContainer _container = new OleContainer();

	private IShockwaveFlash _flash;

	public FlashComponent() {
		super(WINDOW_TITLE);

		init();
	}

	private void init() {
		setWindowsLookFeel();

		getContentPane().add(_container, BorderLayout.CENTER);

		createOleObject();

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				showOleObject();
			}

			public void windowClosing(WindowEvent e) {
				destroyOleObject();
			}
		});

		createMenu();
		setFrameProperties();
	}
	
	

	/**
	 * Creates embedded object.
	 */
	private void createOleObject() {
		IShockwaveFlash flash = ShockwaveFlash.create(ClsCtx.INPROC_SERVER);
		IOleObject oleObject = new IOleObjectImpl(flash);

		_flash = flash;

		_container.insertObject(oleObject);
	}

	/**
	 * Activates embedded object.
	 */
	private void showOleObject() {
		_container.doVerb(OleVerbs.SHOW);
	}

	/**
	 * Destroys embedded object.
	 */
	private void destroyOleObject() {
		_container.destroyObject();
	}

	private void doOpen(String filename) {
		try {
			OleMessageLoop.invokeMethod(this, "openFile",
					new Object[] { filename });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads file into embedded object.
	 * 
	 * @param filePath
	 *            absolute path of loaded file.
	 */
	public void openFile(String filePath) {
		File file = new File(filePath);

		if (!file.exists()) {
			throw new RuntimeException("Couldn't find file with movie: "
					+ filePath);
		}

		// load movie
		_flash.setMovie(new BStr(filePath));
		_flash.play();

		// get count of frames
		long framesCount = _flash.getTotalFrames().getValue();
		System.out.println("framesCount = " + framesCount);
	}

	public static void main(String[] args) {
		// initialize OLE
		OleFunctions.oleInitialize();

		FlashComponent sample = new FlashComponent();
		sample.setVisible(true);
	}

	// -------------------------------------------------------------------------------------------------
	// Creation of Swing GUI
	// -------------------------------------------------------------------------------------------------

	private void setWindowsLookFeel() {
		try {
			UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
		} catch (Exception e) {
		}
	}

	private void setFrameProperties() {
		setSize(640, 480);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createMenu() {
		// prevent hiding of popup menu under OleContaner component
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open...");
		JMenuItem exitItem = new JMenuItem("Exit");

		menu.add(openItem);
		menu.addSeparator();
		menu.add(exitItem);

		openItem.addActionListener(new FlashComponent.OpenFileActionListener());

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyOleObject();
				System.exit(0);
			}
		});

		menuBar.add(menu);

		setJMenuBar(menuBar);
	}

	private class OpenFileActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileFilter(new FlashFileFilter());

			if (chooser.showOpenDialog(FlashComponent.this) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file != null) {
					doOpen(file.getAbsolutePath());
				}
			}
		}
	}

	private static class FlashFileFilter extends FileFilter {
		private static final String FLASH_FILE_EXTENSION = "swf";
		private static final String DESCRIPTION = "Flash files (*.swf)";

		private static final String EXTENSION_SEPARATOR = ".";

		public boolean accept(File pathname) {
			if (pathname.isFile()) {
				String name = pathname.getName();
				int pos = name.lastIndexOf(EXTENSION_SEPARATOR);
				String extension = pos != -1 ? name.substring(pos + 1) : "";

				return FLASH_FILE_EXTENSION.equals(extension);
			}

			return true;
		}

		public String getDescription() {
			return DESCRIPTION;
		}
	}
}
