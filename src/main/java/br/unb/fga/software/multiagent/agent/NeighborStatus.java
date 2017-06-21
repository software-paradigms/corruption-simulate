package br.unb.fga.software.multiagent.agent;

import br.unb.fga.software.multiagent.AgentState;

public class NeighborStatus {
	private Double corruptionAversion;
	private AgentState state;
	
	public NeighborStatus(Double corruptionAversion, AgentState state) {
		this.corruptionAversion = corruptionAversion;
		this.state = state;
	}

	public Double getCorruptionAversion() {
		return corruptionAversion;
	}
	public void setCorruptionAversion(Double corruptionAversion) {
		this.corruptionAversion = corruptionAversion;
	}
	public AgentState getState() {
		return state;
	}
	public void setState(AgentState state) {
		this.state = state;
	}
	
	
}
