/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.receive;

import java.awt.Component;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.ECOA_Monitor;
import com.iawg.ecoa.gui.LCPButton;
import com.iawg.ecoa.gui.PDLabel;
import com.iawg.ecoa.gui.ServiceAvailLabel;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceInst;

public class Receiver implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(Receiver.class);
	private final static int PACKETSIZE = 1024;

	private ECOA_Monitor ecoaMonitor;

	public Receiver(ECOA_Monitor ecoaMonitor) {
		this.ecoaMonitor = ecoaMonitor;
	}

	@Override
	public void run() {
		int port = 60421;
		DatagramSocket socket = null;

		// Construct the socket
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			LOGGER.info("Failed to create socket");
			e.printStackTrace();
		} finally {
			socket.close();
		}

		LOGGER.info("The server is ready...");

		for (;;) {
			// Create a packet
			DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);

			// Receive a packet (blocking)
			try {
				socket.receive(packet);
			} catch (IOException e) {
				LOGGER.info("Failed on receive");
				e.printStackTrace();
			}

			// Print the packet
			LOGGER.info(packet.getAddress() + " " + packet.getPort() + ": " + new String(packet.getData()));

			processReceivedMesasge(new String(packet.getData()));

		}
	}

	private void processReceivedMesasge(String message) {
		// Message formats are:

		// $1_platformName:PLATFORM_STATE
		// $2_platformName/pdID:PD_STATE
		// $3_serviceUID:AVAILABILITY_STATE

		if (message.contains("$1_")) {
			// This is a platform state message.
			final String platformName = message.substring(3, message.indexOf(":"));
			final String platformState = message.substring(message.indexOf(":") + 1, message.trim().length());

			// Update the GUI
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (Component guiComponent : ecoaMonitor.getLCPPanel().getComponents()) {
						if (guiComponent instanceof LCPButton) {
							LCPButton button = (LCPButton) guiComponent;

							if (button.getLCP().getName().equalsIgnoreCase(platformName)) {
								button.setState(platformState);
							}
						}
					}
				}
			});
		} else if (message.contains("$2_")) {
			// This is a PD state message
			final String lcpName = message.substring(3, message.indexOf("/"));
			final String pdIDString = message.substring(message.indexOf("/") + 1, message.indexOf(":"));
			final String pdState = message.substring(message.indexOf(":") + 1, message.trim().length());

			// Update the GUI
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// Get the PD Name from the ID...
					// Not sure this is the best way to be doing this - will it
					// always be the same order as the Container PD_IDs?!
					String pdName = "";
					int pdNum = 1;
					for (SM_LogicalComputingPlatform lcp : ecoaMonitor.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
						if (lcp.getName().equalsIgnoreCase(lcpName)) {
							for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
								if (pdNum == Integer.parseInt(pdIDString)) {
									pdName = pd.getName();
								}
								pdNum++;
							}
						}
					}

					for (Component guiComponent : ecoaMonitor.getLCPPanel().getComponents()) {
						if (guiComponent instanceof LCPButton) {
							// TODO - this is a bit naff..
							LCPButton button = (LCPButton) guiComponent;
							PDLabel label = button.getPDLabel(pdName);
							if (label != null) {
								label.setState(pdState);
							}
						}
					}
				}
			});
		} else if (message.contains("$3_")) {
			// This is a service availability message
			final String serviceUIDString = message.substring(3, message.indexOf(":"));
			final String serviceAvailability = message.substring(message.indexOf(":") + 1, message.trim().length());

			// Update the GUI
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// Get the service instance name from UID...
					String serviceInstName = "";
					for (SM_ComponentInstance compInst : ecoaMonitor.getSystemModel().getFinalAssembly().getComponentInstances()) {
						for (SM_UIDServiceInst uid : compInst.getUIDList()) {
							if (Integer.parseInt(serviceUIDString) == uid.getID()) {
								serviceInstName = compInst.getName() + "/" + uid.getServiceInstance().getName();
							}
						}
					}

					for (Component guiComponent : ecoaMonitor.getLCPPanel().getComponents()) {
						if (guiComponent instanceof LCPButton) {
							// TODO - this is a bit naff..
							LCPButton button = (LCPButton) guiComponent;
							ServiceAvailLabel label = button.getServiceAvailLabel(serviceInstName);
							if (label != null) {
								label.setAvailability(serviceAvailability);
							}
						}
					}
				}
			});
		} else {
			LOGGER.info("Incorrect message received: " + message);
		}

	}
}
