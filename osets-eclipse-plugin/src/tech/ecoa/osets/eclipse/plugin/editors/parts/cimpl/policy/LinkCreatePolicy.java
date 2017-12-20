/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.LinkCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceTerminalNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Enums;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceTerminalNode;

public class LinkCreatePolicy extends GraphicalNodeEditPolicy {

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		try {
			Node end = (Node) getHost().getModel();
			LinkCreateCommand cmd = (LinkCreateCommand) request.getStartCommand();
			Link lnk = cmd.getLink();
			Node start = cmd.getSource();
			if (start instanceof ServiceOperationNode) {
				ServiceOperationNode son = (ServiceOperationNode) start;
				if (son.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.DATA.name())) {
					if (end instanceof ModuleOperationNode) {
						ModuleOperationNode mop = (ModuleOperationNode) end;
						if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_READ.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_WRITE.name())) {
							cmd.setTarget(end);
							lnk.setType(Enums.LinkTypes.DATA.name());
							lnk.setsInst(((ServiceNode) son.getParent()).getIntf());
							return cmd;
						}
					}
				} else if (son.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.EVENT.name())) {
					if (end instanceof ModuleOperationNode) {
						ModuleOperationNode mop = (ModuleOperationNode) end;
						if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_RECEIVED.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_SENT.name())) {
							cmd.setTarget(end);
							lnk.setType(Enums.LinkTypes.EVENT.name());
							lnk.setsInst(((ServiceNode) son.getParent()).getIntf());
							return cmd;
						}
					} else if (end instanceof TriggerInstanceTerminalNode) {
						cmd.setTarget(end);
						lnk.setType(Enums.LinkTypes.EVENT.name());
						lnk.setsInst(((ServiceNode) son.getParent()).getIntf());
						return cmd;
					} else if (end instanceof DynamicTriggerInstanceTerminalNode) {
						cmd.setTarget(end);
						lnk.setType(Enums.LinkTypes.EVENT.name());
						lnk.setsInst(((ServiceNode) son.getParent()).getIntf());
						return cmd;
					}
				} else if (son.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.REQ_RES.name())) {
					if (end instanceof ModuleOperationNode) {
						ModuleOperationNode mop = (ModuleOperationNode) end;
						if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_SENT.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_RECEIVED.name())) {
							cmd.setTarget(end);
							lnk.setType(Enums.LinkTypes.REQUEST.name());
							lnk.setsInst(((ServiceNode) son.getParent()).getIntf());
							return cmd;
						}
					}
				}
			} else if (start instanceof TriggerInstanceTerminalNode) {
				TriggerInstanceTerminalNode node = (TriggerInstanceTerminalNode) start;
				if (end instanceof ServiceOperationNode) {
					ServiceOperationNode sop = (ServiceOperationNode) end;
					if (sop.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.EVENT.name())) {
						cmd.setTarget(end);
						lnk.setType(Enums.LinkTypes.EVENT.name());
						lnk.settInst(((ServiceNode) sop.getParent()).getIntf());
						lnk.setsInst(((TriggerInstanceNode) node.getParent()).getName());
						return cmd;
					}
				} else if (end instanceof ModuleOperationNode) {
					ModuleOperationNode mop = (ModuleOperationNode) end;
					if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_RECEIVED.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_SENT.name())) {
						cmd.setTarget(end);
						lnk.setType(Enums.LinkTypes.EVENT.name());
						lnk.setsInst(((TriggerInstanceNode) node.getParent()).getName());
						return cmd;
					}
				}
			} else if (start instanceof DynamicTriggerInstanceTerminalNode) {
				DynamicTriggerInstanceTerminalNode node = (DynamicTriggerInstanceTerminalNode) start;
				if (end instanceof ServiceOperationNode) {
					ServiceOperationNode sop = (ServiceOperationNode) end;
					if (sop.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.EVENT.name())) {
						cmd.setTarget(end);
						lnk.setType(Enums.LinkTypes.EVENT.name());
						lnk.settInst(((ServiceNode) sop.getParent()).getIntf());
						lnk.setsInst(((DynamicTriggerInstanceNode) node.getParent()).getName());
						return cmd;
					}
				} else if (end instanceof ModuleOperationNode) {
					ModuleOperationNode mop = (ModuleOperationNode) end;
					if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_RECEIVED.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_SENT.name())) {
						cmd.setTarget(end);
						lnk.setType(Enums.LinkTypes.EVENT.name());
						lnk.setsInst(((DynamicTriggerInstanceNode) node.getParent()).getName());
						return cmd;
					}
				}
			} else if (start instanceof ModuleOperationNode) {
				ModuleOperationNode mon = (ModuleOperationNode) start;
				if (mon.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_READ.name()) || mon.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_WRITE.name())) {
					if (end instanceof ServiceOperationNode) {
						ServiceOperationNode sop = (ServiceOperationNode) end;
						if (sop.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.DATA.name())) {
							cmd.setTarget(end);
							lnk.setType(Enums.LinkTypes.DATA.name());
							lnk.settInst(((ServiceNode) sop.getParent()).getIntf());
							return cmd;
						}
					} else if (end instanceof ModuleOperationNode) {
						ModuleOperationNode mop = (ModuleOperationNode) end;
						if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_READ.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_WRITE.name())) {
							cmd.setTarget(end);
							lnk.setType(Enums.LinkTypes.DATA.name());
							return cmd;
						}
					}
				} else if (mon.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_RECEIVED.name()) || mon.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_SENT.name())) {
					if (end instanceof ServiceOperationNode) {
						ServiceOperationNode sop = (ServiceOperationNode) end;
						if (sop.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.EVENT.name())) {
							cmd.setTarget(end);
							lnk.settInst(((ServiceNode) sop.getParent()).getIntf());
							lnk.setType(Enums.LinkTypes.EVENT.name());
							return cmd;
						}
					} else if (end instanceof DynamicTriggerInstanceTerminalNode) {
						DynamicTriggerInstanceTerminalNode node = (DynamicTriggerInstanceTerminalNode) end;
						cmd.setTarget(end);
						lnk.setType(Enums.LinkTypes.EVENT.name());
						lnk.setsInst(((DynamicTriggerInstanceNode) node.getParent()).getName());
						return cmd;
					} else if (end instanceof ModuleOperationNode) {
						ModuleOperationNode mop = (ModuleOperationNode) end;
						if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_RECEIVED.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.EVENT_SENT.name())) {
							cmd.setTarget(end);
							lnk.setType(Enums.LinkTypes.EVENT.name());
							return cmd;
						}
					}
				} else if (mon.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_SENT.name()) || mon.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_RECEIVED.name())) {
					if (end instanceof ServiceOperationNode) {
						ServiceOperationNode sop = (ServiceOperationNode) end;
						if (sop.getType().equalsIgnoreCase(Enums.ServiceOperationTypes.REQ_RES.name())) {
							cmd.setTarget(end);
							lnk.settInst(((ServiceNode) sop.getParent()).getIntf());
							lnk.setType(Enums.LinkTypes.REQUEST.name());
							return cmd;
						}
					} else if (end instanceof ModuleOperationNode) {
						ModuleOperationNode mop = (ModuleOperationNode) end;
						if (mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_SENT.name()) || mop.getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_SENT.name())) {
							cmd.setTarget(end);
							lnk.setType(Enums.LinkTypes.REQUEST.name());
							return cmd;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Node start = (Node) getHost().getModel();
		if (start instanceof ServiceOperationNode || start instanceof TriggerInstanceTerminalNode || start instanceof DynamicTriggerInstanceTerminalNode || start instanceof ModuleOperationNode) {
			LinkCreateCommand cmd = new LinkCreateCommand();
			cmd.setSource(start);
			cmd.setLink((Link) request.getNewObject());
			request.setStartCommand(cmd);
			return cmd;
		}
		return null;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}

}
