package br.unb.fga.software.multiagent;

import java.awt.Color;
import java.util.Vector;

public class AgentMultiton {
	private static Vector<AgentMultiton> agents = new Vector<AgentMultiton>();
	
	private String id;
	private Color color;
	
	
	private AgentMultiton(String id, Color color) {
		this.id = id;
		this.color = color;
	}

	public Color getColor(){
		return color;
	}

	public String getId(){
		return id;
	}
	
	public static void clear(){
		agents.clear();
	}

	public static void put(String key) {
		AgentMultiton.agents.add(new AgentMultiton(key, Color.CYAN));
	}

	public static Vector<Color> getAllColors() {
		Vector<Color> colors = new Vector<Color>();
		for (AgentMultiton agent : agents) {
			colors.add(agent.getColor());
		}
		return colors;
	}

	public static void update(String key, Color agent) {
		for (AgentMultiton agentMultiton : agents) {
			if(agentMultiton.getId().equals(key))
				agentMultiton.color = agent;
		}
	}

	public static boolean isEmpty() {
		return agents.isEmpty();
	}
	
	public static Vector<AgentMultiton> getAll(){
		return agents;
	}
}
