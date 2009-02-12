package com.kesdip.player.components;

import java.awt.Color;
import java.io.File;

import org.apache.log4j.Logger;

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

public class FlashComponent extends AbstractComponent {
	
	private final static Logger logger = Logger.getLogger(FlashComponent.class);
	
	protected OleContainer oleContainer = new OleContainer();

	protected IShockwaveFlash flash;
	
	protected String source;
	
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public void add(Component component) throws ComponentException {
		throw new RuntimeException("FlashComponent is not a container.");
	}

	@Override
	public java.awt.Component getWindowComponent() {
		logger.info("getWindowComponent called");
		oleContainer.setSize(width, height);
		return oleContainer;
	}

	@Override
	public void init(Component parent) throws ComponentException {
		OleFunctions.oleInitialize();
		oleContainer.setBackground(Color.blue);
		createOleObject();
		showOleObject();
		parent.add(this);
		doOpen(source);
	}
	
	private boolean showing;

	@Override
	public void repaint() throws ComponentException {
		if (!showing) {
			logger.info("doing OleVerb.SHOW for the first time");
			showOleObject();
			showing = true;
			oleContainer.getParent().requestFocus();
		}
	}
	
	@Override
	public void releaseResources() {
		super.releaseResources();
		logger.info("destroying OleObject");
		destroyOleObject();
	}
	
    private void createOleObject() {
    	IShockwaveFlash flash = ShockwaveFlash.create(ClsCtx.INPROC_SERVER);
    	IOleObject oleObject = new IOleObjectImpl(flash);
    	this.flash = flash;
    	oleContainer.insertObject(oleObject);
    }
    
    private void showOleObject() {
    	oleContainer.doVerb(OleVerbs.SHOW);
    }
    
    private void destroyOleObject() {
    	oleContainer.destroyObject();
    }
    
    protected void doInvoke(String methodName, Object... args) {
    	doInvoke(this, methodName, args);
    }
    
    /**
     * ActiveX calls should be made by the thread that created the control. OleMessageLoop serves this purpose
     * with its invokeMethod callback mechanism.
     * @param context
     * @param methodName
     * @param args
     */
    protected void doInvoke(Object context, String methodName, Object[] args) {
    	try {
        	OleMessageLoop.invokeMethod(context, methodName, args);
        } catch (Exception ex) {
        	throw new RuntimeException(ex);
        }
    }
    
    private void doOpen(String filename) {
       	doInvoke(this, "openFile", new Object[] { filename });
    }
    
    public void openFile(String filename) {
    	
    	File file = new File(filename);

        if (!file.exists()) {
        	throw new RuntimeException("Couldn't find file with movie: " + filename);
        }
        
        // load movie
        flash.setMovie(new BStr(filename));
        flash.play();

    }

	
}
