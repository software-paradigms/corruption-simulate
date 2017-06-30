package br.unb.fga.software.multiagent;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.unb.fga.software.multiagent.agent.AgentRepresentation;
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
	
	private static ConcurrentLinkedQueue<AgentRepresentation> humanAgents;

	@Override
	protected void setup() {

		Object[] args = getArguments();

		if (args.length == 0) {
			throw new IllegalArgumentException("You should pass one argument");
		} else {
			setSpaceLenght(Integer.parseInt(args[0].toString()));
		}

		Space.humanAgents = new ConcurrentLinkedQueue<AgentRepresentation>();

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

		for (Integer id = 0; id < row * row; id++) {
			try {
				System.out.println("Creating agent id: " + String.valueOf(id));
				getContainerController()
					.createNewAgent(id.toString(), HumanAgent.class.getName(), params)
					.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
			AgentMultiton.put(String.valueOf(id));
		}
	}
	
	public synchronized static void add(String id, AgentState state) {
		Space.humanAgents.add(new AgentRepresentation(id, state));
	}
	
	public static boolean isEmpty() {
		return humanAgents.isEmpty();
	}
	
	public static int size() {
		return humanAgents.size();
	}
	
	public static AgentState get(String id) {
		Iterator<AgentRepresentation> iterator = humanAgents.iterator();

		while(iterator.hasNext()) {
			AgentRepresentation ar = iterator.next();

			if (ar.getId().equals(id)) {
				return ar.getState();
			}
		}

		// treat owns exception
		throw new RuntimeException("AgentRepresentation " + id + " not exist!");
	}

	public static void clear() {
		while(humanAgents.iterator().hasNext()) {
			humanAgents.poll();
		}
	}
}
