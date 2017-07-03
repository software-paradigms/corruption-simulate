package br.unb.fga.software.multiagent.behaviour;

import br.unb.fga.software.multiagent.agent.HumanAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class ObserveNeighborBehaviour extends SimpleBehaviour {
	private static final long serialVersionUID = 1L;
	
	private HumanAgent agent;
	
	public ObserveNeighborBehaviour(HumanAgent humanAgent) {
		this.agent = humanAgent;
	}

	@Override
	public void action() {
		// Request parameter tokens and send your self parameters
		for(Integer neighborID : agent.getNeighborhood()) {
			// If this status is null, means that he never response to him
			if(!agent.getNeighborsStatus().containsKey(neighborID)) {
				ACLMessage requestToken = new ACLMessage(ACLMessage.REQUEST);
				requestToken.addReceiver(new AID(neighborID.toString(), AID.ISLOCALNAME));
				requestToken.setContent(agent.getResponseToken());
				agent.send(requestToken);
			} else {
				// if already have neighbor status, not do anything
			}
		}
	}

	@Override
	public boolean done() {
		return agent.hasFinishIteration();
	}
}
