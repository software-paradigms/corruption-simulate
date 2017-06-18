package br.unb.fga.software.multiagent;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class AgentMultiton {
	private static Map<String, Color> agents;
	
	public AgentMultiton(Integer size) {
		AgentMultiton.agents = new HashMap<String, Color>();
	}
	
	public static void put(String key, Color agent) {
		AgentMultiton.agents.put(key, agent);
	}
	
	public static Color get(String key) {
		return agents.get(key);
	}
}
