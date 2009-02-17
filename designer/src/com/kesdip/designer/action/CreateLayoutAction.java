package com.kesdip.designer.action;

import org.eclipse.jface.action.Action;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class CreateLayoutAction extends Action {
	private Deployment deployment;
	
	public CreateLayoutAction(Deployment deployment) {
		super("Create Layout");
		this.deployment = deployment;
	}

	@Override
	public void run() {
		deployment.addLayout(new Layout());
	}

}
