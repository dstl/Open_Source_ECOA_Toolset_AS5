/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.edpart;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import tech.ecoa.osets.eclipse.plugin.common.LabeledConnection;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceTerminalNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.BendpointPolicy;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy.LinkEditPolicy;

public class LinkEditPart extends AbstractConnectionEditPart implements Observer {

	public LinkEditPart(Link link, String containerName) {
		super();
		link.addObserver(this);
		link.setContainerName(containerName);
		setModel(link);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new LinkEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new BendpointPolicy());
	}

	@Override
	protected IFigure createFigure() {
		Link lnk = (Link) getModel();
		String dis = "";
		if (lnk.getSource() instanceof TriggerInstanceTerminalNode || lnk.getTarget() instanceof TriggerInstanceTerminalNode)
			dis = "Period : " + lnk.getPeriod();
		LabeledConnection conn = new LabeledConnection(dis);
		PolygonDecoration decoration = new PolygonDecoration();
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0, 0);
		decorationPointList.addPoint(-2, 2);
		decorationPointList.addPoint(-2, -2);
		decoration.setTemplate(decorationPointList);
		conn.setTargetDecoration(decoration);
		conn.setConnectionRouter(new BendpointConnectionRouter());
		return conn;
	}

	@Override
	public void update(Observable o, Object arg) {
		refresh();
	}

	@Override
	protected void refreshVisuals() {
		Link lnk = (Link) getModel();
		String dis = "";
		if (lnk.getSource() instanceof TriggerInstanceTerminalNode || lnk.getTarget() instanceof TriggerInstanceTerminalNode)
			dis = "Period : " + lnk.getPeriod();
		LabeledConnection connection = (LabeledConnection) getConnectionFigure();
		connection.setDisplay(dis);
		connection.redraw();
		List<Point> modelConstraint = ((Link) getModel()).getbPoints();
		List<AbsoluteBendpoint> figureConstraint = new ArrayList<AbsoluteBendpoint>();
		for (Point p : modelConstraint) {
			figureConstraint.add(new AbsoluteBendpoint(p));
		}
		connection.setRoutingConstraint(figureConstraint);
	}
}
