package br.unb.fga.software.multiagent;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class AgentMultiton {
	private static Map<String, Color> agents;
	
	public static void init(Integer size) {
		AgentMultiton.agents = new HashMap<String, Color>();
	}
	
	public static void put(String key, Color agent) {
		AgentMultiton.agents.put(key, agent);
	}
	
	public static Color get(String key) {
		return agents.get(key);
	}

	public static Vector<Color> getAll() {
		return new Vector<Color>(agents.values());
	}

	public static boolean isEmpty() {
		return agents.isEmpty();
	}
}
