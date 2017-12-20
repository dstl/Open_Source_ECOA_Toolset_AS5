/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.gui.LCPButton;
import com.iawg.ecoa.gui.LCPPanel;
import com.iawg.ecoa.gui.PDLabel;
import com.iawg.ecoa.gui.PDPanel;
import com.iawg.ecoa.gui.ServiceAvailLabel;
import com.iawg.ecoa.gui.ServiceAvailPanel;
import com.iawg.ecoa.receive.Receiver;
import com.iawg.ecoa.systemmodel.SystemModel;

public class ECOA_Monitor implements ActionListener {
	private static final Logger LOGGER = LogManager.getLogger(ECOA_Monitor.class);
	private JFrame frame;
	private LCPPanel lcpPanel;
	private PDPanel pdPanel;
	private ServiceAvailPanel serviceAvailPanel;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem resetMenuItem;
	private JMenuItem exitMenuItem;

	private static ECOA_System_Model systemModel;

	/**
	 * This is the main function of the application.
	 * 
	 * @param args
	 *            Steps directory of ECOA XML.
	 */
	public static void main(String[] args) {
		Path projectLocation = null;
		boolean apiOnly = false;

		// The argument should be the root Steps/ directory
		if (args.length == 1) {
			projectLocation = Paths.get(args[0]);
		} else {
			LOGGER.info("ERROR - call should be \"CodeGenerator <project root directory>\"");
			
		}

		// Create an object to manage processing of the XML
		systemModel = new ECOA_System_Model(projectLocation, apiOnly);

		// Now create the GUI application...
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ECOA_Monitor window = new ECOA_Monitor();
					window.frame.setVisible(true);

					// Create the receiver thread.
					Thread t = new Thread(new Receiver(window));
					t.start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public ECOA_Monitor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 640);
		frame.setTitle("ECOA Monitor Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the layout of the top-level frame to a 1x3 grid.
		frame.setLayout(new BorderLayout());

		lcpPanel = new LCPPanel(this);
		pdPanel = new PDPanel(this);
		serviceAvailPanel = new ServiceAvailPanel(this);

		// Create the menu bar.
		menuBar = new JMenuBar();
		// Build the first menu.
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		// Add menu items for reset and exit.
		resetMenuItem = new JMenuItem("Reset");
		exitMenuItem = new JMenuItem("Exit");

		// Add event listeners for each of the menu items.
		resetMenuItem.addActionListener(this);
		exitMenuItem.addActionListener(this);

		fileMenu.add(resetMenuItem);
		fileMenu.add(exitMenuItem);

		JPanel containingPanel = new JPanel();
		containingPanel.setLayout(new GridLayout(1, 2));
		containingPanel.add(pdPanel);
		containingPanel.add(serviceAvailPanel);

		// Add the panels to the frame container.
		frame.add(lcpPanel, BorderLayout.PAGE_START);
		frame.add(containingPanel, BorderLayout.CENTER);

		frame.setJMenuBar(menuBar);
	}

	public LCPPanel getLCPPanel() {
		return lcpPanel;
	}

	public PDPanel getPDPanel() {
		return pdPanel;
	}

	public ServiceAvailPanel getServiceAvailPanel() {
		return serviceAvailPanel;
	}

	public SystemModel getSystemModel() {
		return systemModel.getSystemModel();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == resetMenuItem) {
			// Reset LCP states.
			for (Component swingComp : lcpPanel.getComponents()) {
				if (swingComp instanceof LCPButton) {
					((LCPButton) swingComp).setState("DOWN");

					// Reset PD states.
					for (PDLabel pdLabel : ((LCPButton) swingComp).getPDLabelList()) {
						pdLabel.setState("DOWN");
					}

					// Reset service availabilities
					for (ServiceAvailLabel serviceAvailLabel : ((LCPButton) swingComp).getServiceAvailabiltyLabelList()) {
						serviceAvailLabel.setAvailability("RESET");
					}
				}
			}
		} else if (ae.getSource() == exitMenuItem) {
			
		}

	}
}
