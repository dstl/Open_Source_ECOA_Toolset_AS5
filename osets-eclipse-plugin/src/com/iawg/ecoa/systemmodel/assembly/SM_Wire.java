/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.assembly;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class SM_Wire {
	private static final Logger LOGGER = LogManager.getLogger(SM_Wire.class);

	private SM_ComponentInstance source;
	private SM_ServiceInstance referenceInstance;
	private SM_ComponentInstance target;
	private SM_ServiceInstance serviceInstance;
	private List<SM_UIDServiceOp> uidList = new ArrayList<SM_UIDServiceOp>();
	private boolean areEventsMulticast;
	private Integer rank;

	public SM_Wire(SM_ComponentInstance s, SM_ServiceInstance referenceInstance, SM_ComponentInstance t, SM_ServiceInstance serviceInstance, boolean eventsMulticast, Integer rank) {
		this.source = s;
		this.referenceInstance = referenceInstance;
		this.target = t;
		this.serviceInstance = serviceInstance;
		this.areEventsMulticast = eventsMulticast;
		this.rank = rank;

		// Add a reference to this wire for both the source and destination
		// component instances
		s.addSourceWire(this);
		t.addTargetWire(this);
	}

	public SM_Wire(SM_ComponentInstance s, SM_ServiceInstance referenceInstance, SM_ComponentInstance t, SM_ServiceInstance serviceInstance) {
		this.source = s;
		this.referenceInstance = referenceInstance;
		this.target = t;
		this.serviceInstance = serviceInstance;

		// Add a reference to this wire for both the source and destination
		// component instances
		s.addSourceWire(this);
		t.addTargetWire(this);
	}

	public SM_ComponentInstance getSource() {
		return source;
	}

	public SM_ComponentInstance getTarget() {
		return target;
	}

	public SM_ServiceInstance getSourceOp() {
		return referenceInstance;
	}

	public SM_ServiceInstance getTargetOp() {
		return serviceInstance;
	}

	public List<SM_UIDServiceOp> getUIDList() {
		return uidList;
	}

	public SM_UIDServiceOp getUID(SM_Object operation) {
		for (SM_UIDServiceOp uid : uidList) {
			if (uid.getOperation() == operation) {
				return uid;
			}
		}

		LOGGER.info("Failed to find UID for operation: " + operation.getName() + " in source " + source.getName() + " " + referenceInstance.getName());
		
		return null;
	}

	public boolean getAreEventsMulticast() {
		return areEventsMulticast;
	}

	public Integer getRank() {
		return rank;
	}

	public void addUID(Integer uid, SM_Object op, String uidString) {
		uidList.add(new SM_UIDServiceOp(uid, op, uidString));
	}

}
