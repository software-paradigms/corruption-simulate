package br.unb.fga.software.multiagent;

public enum AgentState {
	CORRUPT("corrupt"), NEUTRAL("neutral"), HONEST("honest"), ARRESTED("arrested");

	private String stateName;
	
	private AgentState(String stateName) {
		this.stateName = stateName;
	}
	
	public String getStateName() {
		return stateName;
	}

	public static AgentState getByString(String state) {
		for(AgentState s : AgentState.values()) {
			if(s.getStateName().equals(state)) {
				return s;
			}
		}

		return null;
	}
}
