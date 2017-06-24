package br.unb.fga.software.multiagent.behaviour;

import br.unb.fga.software.multiagent.AgentState;
import br.unb.fga.software.multiagent.agent.HumanAgent;
import br.unb.fga.software.multiagent.agent.NeighborStatus;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class ResponseStatusBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 1L;

	private HumanAgent agent;

	public ResponseStatusBehaviour(HumanAgent agent) {
		this.agent = agent;
	}

	@Override
	public void action() {
		ACLMessage tokenResponse = agent.receive();

		if (tokenResponse != null) {
			// Space sends message? So restart parallel messages to all agents 
			if (isSpaceThatSend(tokenResponse)) {
				agent.setCanStart(true);
			} else {
				// Well, if anyone sends me a message, he is one neighbor
				if (!amsIsTryingTalk(tokenResponse)) {
					updateNeighborStatus(tokenResponse);

					// Now reply to this sender
					ACLMessage reply = tokenResponse.createReply();

					String content = agent.getResponseToken();
					reply.setContent(content);
				}
			}
		}
	}

	private boolean amsIsTryingTalk(ACLMessage tokenResponse) {
		return tokenResponse.getSender().getLocalName().equals("ams");
	}

	private boolean isSpaceThatSend(ACLMessage tokenResponse) {
		return tokenResponse.getSender().getLocalName().equals("space");
	}

	/**
	 * To understand token, see bellow method getResponseToken()
	 */
	private void updateNeighborStatus(ACLMessage tokenResponse) {
		Integer agentID = Integer.valueOf(tokenResponse.getSender().getLocalName());

		String[] token = tokenResponse.getContent().split(HumanAgent.PARAMS_SEPARATOR);

		NeighborStatus neighborStatus = new NeighborStatus(Double.valueOf(token[0]),
				AgentState.getByString(token[1]));

		agent.updateNeighborsStatus(agentID, neighborStatus);
	}

	@Override
	public boolean done() {
		return agent.getNeighborsStatus().size() == agent.getNeighborhood().size();
	}

}
