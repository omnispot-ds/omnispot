package com.kesdip.player.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jniwrapper.win32.automation.OleContainer;
import com.jniwrapper.win32.ole.types.OleVerbs;
import com.kesdip.player.components.FlashComponent;

public class Test1 extends JFrame {
	
	private FlashComponent flashComp = new FlashComponent();
	
	public Test1() throws Exception {
		super();
		init();
	}
	
    private void init() throws Exception
    {
        //setWindowsLookFeel();
    	
    	setFrameProperties();

    	flashComp.setSource("C:/GETME_HOME/client/core/getme/bin-release2/shellV2.swf");
    	flashComp.init(null);
    	
    	final OleContainer _container = (OleContainer) flashComp.getWindowComponent();
        getContentPane().add(_container, BorderLayout.CENTER);

        //createOleObject();

        addWindowListener(new WindowAdapter()
        {
            public void windowOpened(WindowEvent e)
            {
            	_container.doVerb(OleVerbs.SHOW);
            }

            public void windowClosing(WindowEvent e)
            {
                flashComp.releaseResources();
            }
        });

        //createMenu();
        setFrameProperties();
    }
    
    private void setFrameProperties()
    {
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void main(String[] args) throws Exception {
    	Test1 test = new Test1();
    	test.setVisible(true);
    }

}
