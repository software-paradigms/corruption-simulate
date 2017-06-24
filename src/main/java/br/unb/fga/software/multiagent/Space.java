package br.unb.fga.software.multiagent;

import br.unb.fga.software.multiagent.agent.HumanAgent;
import br.unb.fga.software.multiagent.behaviour.ReceiveStatusBehaviour;
import jade.core.Agent;
import jade.wrapper.StaleProxyException;

public class Space extends Agent {

	private static final long serialVersionUID = 1L;

	/*
	 * Quantity of agents in this environment
	 */
	private Integer spaceLenght;

	@Override
	protected void setup() {

		Object[] args = getArguments();

		if (args.length == 0) {
			throw new IllegalArgumentException("You should pass one argument");
		} else {
			// System.out.println("Starting with " + args[0]);
			setSpaceLenght(Integer.parseInt(args[0].toString()));
		}

		// Creates all agents to fills square space
		createAgents();

		addBehaviour(new ReceiveStatusBehaviour(this, getOrder()));
	}

	public Integer getSpaceLenght() {
		return spaceLenght;
	}

	public void setSpaceLenght(Integer spaceLenght) {
		this.spaceLenght = spaceLenght;
	}

	private Integer getOrder() {
		return getSpaceLenght() * getSpaceLenght();
	}

	private void createAgents() {
		Integer row = spaceLenght;
		String[] params = { row.toString() };

		AgentMultiton.clear();

		for (int id = 0; id < row * row; id++) {
			try {
				System.out.println("Creating agent id: " + String.valueOf(id));
				getContainerController().createNewAgent(String.valueOf(id), HumanAgent.class.getName(), params).start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			AgentMultiton.put(String.valueOf(id));
		}
	}
}
