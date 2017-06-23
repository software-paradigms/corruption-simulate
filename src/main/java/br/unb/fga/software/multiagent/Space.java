package br.unb.fga.software.multiagent;

import java.util.ArrayList;

import br.unb.fga.software.multiagent.agent.HumanAgent;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;

public class Space extends Agent {

	private static final long serialVersionUID = 1L;

	/*
	 * Probability of agent[i][j] be arrest
	 */
	private Double[][] arrestProbability;

	/*
	 * Index of investigation about corruption
	 */
	private Double researchEffort;

	/*
	 * Quantity of agents in this environment
	 */
	private Integer spaceLenght;

	/*
	 * Quantity of iterations
	 */
	private Integer iterations = 0;

	private Integer actualIteration;

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

		final SpaceWindow space = new SpaceWindow(getSpaceLenght());
		space.setVisible(true);

		// Should refresh simulation every time
		addBehaviour(new CyclicBehaviour(this) {

			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = receive();

				if (msg != null) {
					String agentId = msg.getSender().getLocalName();
					String msgState = msg.getContent();

					// System.out.println("Receiving state from: " + agentId);

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
				}
			}
		});
		
		addBehaviour(new TickerBehaviour(this, 250) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				if (!AgentMultiton.isEmpty()) {
					space.updatePainel(AgentMultiton.getAllColors());
				}
			}
		});
	}

	private void createAgents() {
		Integer row = spaceLenght;
		String[] params = {row.toString()};

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

	public Double[][] getArrestProbability() {
		return arrestProbability;
	}

	public void setArrestProbability(Double[][] arrestProbability) {
		this.arrestProbability = arrestProbability;
	}

	public Double getResearchEffort() {
		return researchEffort;
	}

	public void setResearchEffort(Double researchEffort) {
		this.researchEffort = researchEffort;
	}

	public Integer getSpaceLenght() {
		return spaceLenght;
	}

	public void setSpaceLenght(Integer spaceLenght) {
		this.spaceLenght = spaceLenght;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public Integer getActualIteration() {
		return actualIteration;
	}

	public void setActualIteration(Integer actualIteration) {
		this.actualIteration = actualIteration;
	}
}
