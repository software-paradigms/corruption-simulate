package br.unb.fga.software.multiagent.behaviour;

import br.unb.fga.software.multiagent.AgentState;
import br.unb.fga.software.multiagent.agent.HumanAgent;
import br.unb.fga.software.multiagent.agent.NeighborStatus;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class ResponseStatusBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 1L;

	private HumanAgent agent;
	private ObserveNeighborBehaviour observeNeighborBehaviour;

	public ResponseStatusBehaviour(HumanAgent agent) {
		this.agent = agent;
	}

	@Override
	public void onStart() {
		observeNeighborBehaviour = new ObserveNeighborBehaviour(agent);
		observeNeighborBehaviour.action();
		super.onStart();
	}

	@Override
	public void action() {
		while(!observeNeighborBehaviour.done());

		ACLMessage tokenResponse = agent.receive();

		if (tokenResponse != null) {
			// Well, if anyone sends me a message, he is one neighbor
			if (tokenResponse.getPerformative() == ACLMessage.REQUEST) {
				updateNeighborStatus(tokenResponse);

				// Now reply to this sender
				ACLMessage reply = tokenResponse.createReply();

				String content = agent.getResponseToken();
				reply.setContent(content);
			}
		}
	}

	/**
	 * To understand token, see bellow method getResponseToken()
	 */
	private void updateNeighborStatus(ACLMessage tokenResponse) {
		Integer agentID = Integer.valueOf(tokenResponse.getSender().getLocalName());

		String[] token = tokenResponse.getContent().split(HumanAgent.PARAMS_SEPARATOR);

		NeighborStatus neighborStatus = new NeighborStatus(Double.valueOf(token[0]),
				AgentState.getByString(token[1]));

		if(!agent.getNeighborsStatus().containsKey(agentID)) {
			agent.updateNeighborsStatus(agentID, neighborStatus);			
		}
	}

	@Override
	public boolean done() {
		return agent.hasFinishIteration();
	}

}
