package br.unb.fga.software.multiagent.behaviour;

import br.unb.fga.software.multiagent.AgentMultiton;
import br.unb.fga.software.multiagent.AgentState;
import br.unb.fga.software.multiagent.SpaceWindow;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveStatusBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;

	private Integer spaceLength;
	private Integer totalResponse;
	private SpaceWindow spaceGUI;

	public ReceiveStatusBehaviour(Agent agent, Integer spaceLength) {
		super(agent);

		this.spaceLength = spaceLength;
		this.totalResponse = 1;

		spaceGUI = new SpaceWindow((int) Math.sqrt(spaceLength));
		spaceGUI.setVisible(true);
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive();

		if (msg != null) {
			String agentId = msg.getSender().getLocalName();
			String msgState = msg.getContent();

			incrementTotalResponse();

			switch (AgentState.getByString(msgState)) {
				case CORRUPT:
					AgentMultiton.update(agentId, SpaceWindow.CORRUPT);
					break;
				case NEUTRAL:
					AgentMultiton.update(agentId, SpaceWindow.NEUTRAL);
					break;
				case HONEST:
					AgentMultiton.update(agentId, SpaceWindow.HONEST);
					break;
				case ARRESTED:
					AgentMultiton.update(agentId, SpaceWindow.ARRESTED);
					break;
			}

			if (getTotalResponse() == getSpaceLength()) {
				for (Integer i = 0; i < getSpaceLength(); i++) {
					ACLMessage stateInform = new ACLMessage(ACLMessage.INFORM);
					stateInform.addReceiver(new AID(i.toString(), AID.ISLOCALNAME));
					myAgent.send(stateInform);
				}

				// Update view
				spaceGUI.updatePainel(AgentMultiton.getAllColors());
				
				restartTotalResponse();
			}
		}
	}

	private void restartTotalResponse() {
		this.totalResponse = 1;
	}

	private Integer getSpaceLength() {
		return spaceLength;
	}

	private Integer getTotalResponse() {
		return totalResponse;
	}

	private void incrementTotalResponse() {
		totalResponse++;
	}
}
