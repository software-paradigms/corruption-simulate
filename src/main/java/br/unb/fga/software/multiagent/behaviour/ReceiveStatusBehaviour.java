package br.unb.fga.software.multiagent.behaviour;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.unb.fga.software.multiagent.AgentMultiton;
import br.unb.fga.software.multiagent.Space;
import br.unb.fga.software.multiagent.SpaceWindow;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class ReceiveStatusBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ReceiveStatusBehaviour.class);

	private Integer spaceLength;
	private SpaceWindow spaceGUI;

	public ReceiveStatusBehaviour(Agent agent, Integer spaceLength) {
		super(agent);

		this.spaceLength = spaceLength;

		spaceGUI = new SpaceWindow((int) Math.sqrt(spaceLength));
		spaceGUI.setVisible(true);
	}

	@Override
	public void action() {
		if(Space.size() == getSpaceLength()) {
			logger.debug("New iteration, should update space now!");

			for(Integer id = 0; id < getSpaceLength(); id++) {
				switch (Space.get(id.toString())) {
					case CORRUPT:
						AgentMultiton.update(String.valueOf(id), SpaceWindow.CORRUPT);
						break;
					case NEUTRAL:
						AgentMultiton.update(String.valueOf(id), SpaceWindow.NEUTRAL);
						break;
					case HONEST:
						AgentMultiton.update(String.valueOf(id), SpaceWindow.HONEST);
						break;
					case ARRESTED:
						AgentMultiton.update(String.valueOf(id), SpaceWindow.ARRESTED);
						break;
					}
			}
			// Update view
			spaceGUI.updatePainel(AgentMultiton.getAllColors());
			Space.clear();
		}
	}

	private Integer getSpaceLength() {
		return spaceLength;
	}
}
