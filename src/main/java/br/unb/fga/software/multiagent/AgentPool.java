package br.unb.fga.software.multiagent;

import br.unb.fga.software.multiagent.agent.HumanAgent;

public class AgentPool {
	private static HumanAgent[][] agents;
	
	public AgentPool(Integer size) {
		AgentPool.agents = new HumanAgent[size][size];
	}
	
	public static void put(HumanAgent[][] agents) {
		AgentPool.agents = agents;
	}
	
	public static HumanAgent[][] get() {
		return agents;
	}
}
