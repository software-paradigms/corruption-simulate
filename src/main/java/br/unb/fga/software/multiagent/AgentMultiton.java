package br.unb.fga.software.multiagent;

import java.util.HashMap;
import java.util.Map;

import br.unb.fga.software.multiagent.agent.HumanAgent;

public class AgentMultiton {
	private static Map<String, HumanAgent> agents;
	
	public AgentMultiton(Integer size) {
		AgentMultiton.agents = new HashMap<String, HumanAgent>();
	}
	
	public static void put(String key, HumanAgent agent) {
		AgentMultiton.agents.put(key, agent);
	}
	
	public static HumanAgent get(String key) {
		return agents.get(key);
	}
}
