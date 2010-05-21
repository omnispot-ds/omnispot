package com.kesdip.player.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jniwrapper.win32.automation.Automation;
import com.jniwrapper.win32.automation.OleContainer;
import com.jniwrapper.win32.automation.OleMessageLoop;
import com.jniwrapper.win32.com.types.ClsCtx;
import com.jniwrapper.win32.ole.IOleObject;
import com.jniwrapper.win32.ole.OleFunctions;
import com.jniwrapper.win32.ole.impl.IOleObjectImpl;
import com.jniwrapper.win32.ole.types.OleVerbs;
import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.components.flash.shockwaveflashobjects.IShockwaveFlash;
import com.kesdip.player.components.flash.shockwaveflashobjects.ShockwaveFlash;
import com.kesdip.player.registry.ContentRegistry;

/**
 * 
 * <p>
 * The class runs an endless loop, polling the standard in, as long as the
 * {@link Player} itself is running. The class reads lines of the form
 * <code>expression=value</code> from the standard in and updates the currently
 * playing deployment.
 * </p>
 * 
 * @author gerogias
 */
public class FlashComponent extends AbstractComponent {

	private final static Logger logger = Logger.getLogger(FlashComponent.class);

	protected Resource source;

	protected OleContainer oleContainer;
	protected IShockwaveFlash flash;
	protected Automation automation;

	protected InvocationProxy invocationProxy;

	/**
	 * This is only to be used for testing purposes. Not intended to be part of
	 * the spring configuration, as we are supposed to use a resource flash
	 * object, so that we can control its deployment.
	 */
	protected String filename = null;
	private boolean showing;

	public FlashComponent() {
		super();
		initInvocationProxy();
	}

	public void setSource(Resource source) {
		this.source = source;
	}

	protected void afterInit() {
		if (filename == null) {
			ContentRegistry registry = ContentRegistry.getContentRegistry();
			filename = registry.getResourcePath(source, true);
		}
		openFile(filename);

	}

	protected void beforeInit() {
	}

	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.add(source);
		return retVal;
	}

	protected void initInvocationProxy() {
		invocationProxy = new InvocationProxy();
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public void add(Component component) throws ComponentException {
		throw new RuntimeException("FlashComponent is not a container.");
	}

	@Override
	public java.awt.Component getWindowComponent() {
		oleContainer.setLocation(new Point(x, y));
		return oleContainer;
	}

	@Override
	public synchronized void init(Component parent,
			TimingMonitor timingMonitor, Player player)
			throws ComponentException {
		setPlayer(player);
		beforeInit();
		OleFunctions.oleInitialize();
		createOleObject();
		oleContainer.setBackground(Color.blue);
		final Player cPlayer = this.player;
		oleContainer.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if (arg0.getKeyChar() == KeyEvent.VK_ESCAPE) {
					cPlayer.stopPlaying();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

		});

		showOleObject();
		// this check facilitates unit-testing
		if (parent != null) {
			parent.add(this);
		}
		afterInit();
	}

	@Override
	public void releaseResources() {
		logger.info("destroying OleObject");
		destroyOleObject();
		super.releaseResources();
	}
	
	@Override
	public synchronized void repaint() throws ComponentException {
		if (!showing) {
			logger.info("doing OleVerb.SHOW for the first time");
			showOleObject();
			showing = true;
		}
	}

	private synchronized void createOleObject() {
		oleContainer = new OleContainer();
		oleContainer.setSize(width, height);
		flash = ShockwaveFlash.create(ClsCtx.INPROC_SERVER);
		IOleObject oleObject = new IOleObjectImpl(flash);
		automation = new Automation(oleObject);
		oleContainer.insertObject(oleObject);
	}

	private synchronized void showOleObject() {
		oleContainer.doVerb(OleVerbs.SHOW);
	}

	// TODO Not sure why this throws NPE
	public void destroyOleObject() {
		try {
			oleContainer.destroyObject();
		} catch (Exception ex) {
			logger.warn(ex);
		}
	}

	protected void doInvoke(String methodName, Object... args) {
		doInvoke(invocationProxy, methodName, args);
	}

	/**
	 * ActiveX calls should be made by the thread that created the control.
	 * OleMessageLoop serves this purpose with its invokeMethod callback
	 * mechanism.
	 * 
	 * @param context
	 * @param methodName
	 * @param args
	 */
	protected void doInvoke(Object context, String methodName, Object[] args) {
		try {
			OleMessageLoop.invokeMethod(context, methodName, args);
		} catch (Exception ex) {
			logger.error("Error", ex);
			throw new RuntimeException(ex);
		}
	}

	private void openFile(String filename) {
		doInvoke("loadFile", filename, 0);
		// doInvoke("openFile", filename);
	}

	protected void openFilePrivate(String filename) {
		if (logger.isDebugEnabled()) {
			logger.debug("Proxy class: "
					+ invocationProxy.getClass().toString());
		}
		File file = new File(filename);

		if (!file.exists()) {
			throw new RuntimeException("Couldn't find file with movie: "
					+ filename);
		}

		automation.setProperty("Movie", filename);
	}

	protected void loadFilePrivate(String filename, int layer) {
		if (logger.isDebugEnabled()) {
			logger.debug("Proxy class: "
					+ invocationProxy.getClass().toString());
		}
		File file = new File(filename);

		if (!file.exists()) {
			throw new RuntimeException("Couldn't find file with movie: "
					+ filename);
		}

		automation.invoke("LoadMovie", new Object[] { filename, layer });
	}

	/**
	 * All ActiveX calls should be made through OleMessageLoop.invokeMethod().
	 * The context argument passed to this method however needs to expose the
	 * method to be called as public. This would mean that the interface of our
	 * class should contain two versions of the same method. One to pass the
	 * invocation to OleMessageLoop, and one to be invoked by OleMessageLoop and
	 * do the actual work. For the sake of a simpler class interface, the method
	 * that does the actual work is declared as private, and our InvocationProxy
	 * (to which this private method is accessible) is passed to OleMessageLoop
	 * as the context of the method call.
	 * 
	 * @author n.giamouris
	 * 
	 */
	protected class InvocationProxy {
		public void openFile(String filename) {
			FlashComponent.this.openFilePrivate(filename);
		}

		public void loadFile(String filename, Integer layer) {
			FlashComponent.this.loadFilePrivate(filename, layer);
		}
	}

}