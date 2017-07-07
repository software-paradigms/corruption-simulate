package br.unb.fga.software.multiagent.agent;

import br.unb.fga.software.multiagent.AgentState;

public class AgentRepresentation {
	private String id;
	private AgentState state;
	
	public AgentRepresentation(String id, AgentState state) {
		this.id = id;
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AgentState getState() {
		return state;
	}

	public void setState(AgentState state) {
		this.state = state;
	}
}
