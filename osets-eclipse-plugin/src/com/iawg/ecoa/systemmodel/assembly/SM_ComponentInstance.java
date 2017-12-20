/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.assembly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentType;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceInst;

public class SM_ComponentInstance extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_ComponentInstance.class);

	private SM_ComponentType compType;
	private SM_ComponentImplementation implementation = null;
	private List<SM_DeployedModInst> deployedModInsts = new ArrayList<SM_DeployedModInst>();
	private List<SM_DeployedTrigInst> deployedTrigInsts = new ArrayList<SM_DeployedTrigInst>();
	private List<SM_ComponentInstanceProperty> properties = new ArrayList<SM_ComponentInstanceProperty>();
	private List<SM_Wire> sourceWires = new ArrayList<SM_Wire>();
	private List<SM_Wire> targetWires = new ArrayList<SM_Wire>();
	private SM_ProtectionDomain protectionDomain;
	private List<SM_UIDServiceInst> serviceInstanceUIDs = new ArrayList<SM_UIDServiceInst>();

	public SM_ComponentInstance(String name, SM_ComponentType def) {
		super(name);
		compType = def;
	}

	public SM_ComponentInstance(String name, SM_ComponentImplementation compImpl) {
		super(name);
		implementation = compImpl;

		// set component defintion.
		compType = compImpl.getCompType();

		// Add this new instance to the list of compInstances held by the
		// componentImplementation
		compImpl.addComponentInstance(this);
	}

	public void addImplementation(SM_ComponentImplementation impl) {
		implementation = impl;
	}

	public SM_ComponentType getCompType() {
		return compType;
	}

	public SM_ComponentImplementation getImplementation() {
		if (implementation != null) {
			return implementation;
		} else {
			return null;
		}
	}

	public void addDeployedModInst(SM_DeployedModInst depModInst) {
		deployedModInsts.add(depModInst);
	}

	public List<SM_DeployedModInst> getDeployedModInsts() {
		return deployedModInsts;
	}

	public void addDeployedTrigInst(SM_DeployedTrigInst depTrigInst) {
		deployedTrigInsts.add(depTrigInst);
	}

	public List<SM_DeployedTrigInst> getDeployedTrigInsts() {
		return deployedTrigInsts;
	}

	public List<SM_ComponentInstanceProperty> getProperties() {
		return properties;
	}

	public void addProperty(SM_ComponentInstanceProperty property) {
		properties.add(property);
	}

	public void addSourceWire(SM_Wire wire) {
		this.sourceWires.add(wire);
	}

	public void addTargetWire(SM_Wire wire) {
		this.targetWires.add(wire);
	}

	public List<SM_Wire> getSourceWires() {
		return sourceWires;
	}

	public List<SM_Wire> getTargetWires() {
		return targetWires;
	}

	public List<SM_Wire> getTargetWires(SM_ServiceInstance serviceInst) {
		List<SM_Wire> wireList = new ArrayList<SM_Wire>();

		for (SM_Wire wire : targetWires) {
			if (wire.getTargetOp() == serviceInst) {
				wireList.add(wire);
			}
		}
		return wireList;
	}

	public List<SM_Wire> getSourceWires(SM_ServiceInstance serviceInst) {
		List<SM_Wire> wireList = new ArrayList<SM_Wire>();

		for (SM_Wire wire : sourceWires) {
			if (wire.getSourceOp() == serviceInst) {
				wireList.add(wire);
			}
		}
		return wireList;
	}

	public SM_UIDServiceInst getUIDForServiceInstanceByName(SM_ServiceInstance si) {
		for (SM_UIDServiceInst uid : serviceInstanceUIDs) {
			if (uid.getServiceInstance() == si) {
				return uid;
			}
		}

		LOGGER.info("Failed to find uid for service instance - " + si.getName() + " in component instance - " + name);
		
		return null;
	}

	public SM_ProtectionDomain getProtectionDomain() {
		return protectionDomain;
	}

	public void setProtectionDomain(SM_ProtectionDomain protectionDomainToSet) {
		// Ensure this component instance has not already been deployed in
		// another protection domain
		if (this.protectionDomain == null) {
			this.protectionDomain = protectionDomainToSet;
		} else if (this.protectionDomain != protectionDomainToSet) {
			LOGGER.info("Component instance " + this.name + " has already been deployed in protection domain " + this.protectionDomain.getName() + " and is also deployed in " + protectionDomainToSet.getName());
			
		}
	}

	public List<WireRank> getTargetWiresByRank(SM_ServiceInstance serviceInst) {
		List<SM_Wire> wireList = getTargetWires(serviceInst);

		// Now order by rank
		List<WireRank> wireRankList = new ArrayList<WireRank>();

		for (SM_Wire wire : wireList) {
			wireRankList.add(new WireRank(wire, wire.getRank()));
		}

		Collections.sort(wireRankList);

		return wireRankList;
	}

	public List<WireRank> getSourceWiresByRank(SM_ServiceInstance serviceInst) {
		List<SM_Wire> wireList = getSourceWires(serviceInst);

		// Now order by rank
		List<WireRank> wireRankList = new ArrayList<WireRank>();

		for (SM_Wire wire : wireList) {
			wireRankList.add(new WireRank(wire, wire.getRank()));
		}

		Collections.sort(wireRankList);

		return wireRankList;
	}

	public void addServiceInstanceUID(Integer uid, SM_ServiceInstance serviceInstance) {
		serviceInstanceUIDs.add(new SM_UIDServiceInst(uid, serviceInstance));
	}

	public class WireRank implements Comparable<WireRank> {
		private SM_Wire wire;
		private Integer rank;

		WireRank(SM_Wire wire, Integer rank) {
			this.wire = wire;
			this.rank = rank;
		}

		@Override
		public int compareTo(WireRank o) {
			return rank - o.rank;
		}

		public SM_Wire getWire() {
			return wire;
		}

		public Integer getRank() {
			return rank;
		}
	}

	public List<SM_UIDServiceInst> getUIDList() {
		return serviceInstanceUIDs;
	}

	public SM_ComponentInstanceProperty getPropertyByName(String name) {
		for (SM_ComponentInstanceProperty property : properties) {
			if (property.getName().equals(name)) {
				return property;
			}
		}

		LOGGER.info("Property - " + name + " does not exist in component instance " + this.name);
		
		return null;
	}

}
