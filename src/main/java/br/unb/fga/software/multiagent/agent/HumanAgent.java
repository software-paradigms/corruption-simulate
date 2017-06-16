package br.unb.fga.software.multiagent.agent;

import jade.core.Agent;

public class HumanAgent extends Agent {

	private static final long serialVersionUID = 1L;

	// Between [0, 1], starts with average 0,5 and variance 0,25
	private Double corruptionAversion;

	// Value of corruption arround agent. Starts equals arrestProbability
	private Double corruptionRate;

	// Value of probability observed by Agent.
	private Double arrestProbabilityObserved;

	// Rate arrest observed arround
	private Double rateArrestObserved;
	
	private boolean isArested;
	
	private Double recompense;
	
	@Override
	protected void setup() {
		super.setup();
	}
}
