package br.unb.fga.software.multiagent.agent;

import br.unb.fga.software.multiagent.AgentState;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class HumanAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	public static final String INDEXES_SEPARATOR = "x"; 

	// Between [0, 1], starts with average 0,5 and variance 0,25
	private Double corruptionAversionInitial;
	
	// Between [0, 1], changes every turn
	private Double corruptionAversion;

	// Value of corruption arround agent. Starts equals arrestProbability
	private Double corruptionRate;

	// Corruption around, one average of corruption aversion around this agent
	private Double  corruptionAversionAround;

	// Value of probability observed by Agent.
	private Double arrestProbabilityObserved;

	// Rate arrest observed arround
	private Double dangerOfArrest;
	
	private boolean isArested;
	
	private Double costOfPunishment;

	@Override
	protected void setup() {
		// Should refresh simulation every time
		addBehaviour(new TickerBehaviour(this, 1000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				ACLMessage stateInform = new ACLMessage(ACLMessage.INFORM);
				stateInform.addReceiver(new AID("space", AID.ISLOCALNAME));
				stateInform.setContent(AgentState.CORRUPT.getStateName());
				send(stateInform);
			}
		});
	}

	/**
	 * To see decision of agent each turn
	 */
	public boolean isCorrupt() {
		Double corruptionMotivation = ((1 - getCorruptionAversion()) / getArrestProbabilityObserved());
		boolean isCorruptInThisRound = corruptionMotivation > getCostOfPunishment();

		return isCorruptInThisRound;
	}
	
	public Double getCorruptionAversionInitial() {
		return corruptionAversionInitial;
	}

	/**
	 * TODO
	 * 
	 * Changes to random setup, starting with average 0,5 and variance 0,25
	 */
	@Deprecated
	public void setCorruptionAversionInitial(Double corruptionAversionInitial) {
		this.corruptionAversionInitial = corruptionAversionInitial;
	}

	public Double getCorruptionAversion() {
		return corruptionAversion;
	}

	/**
	 * Set with past value {@link HumanAgent#getCorruptionAversion()}
	 */
	public void setCorruptionAversion() {
		this.corruptionAversion = (getCorruptionAversionInitial() + getCorruptionRate()) / 2;
	}

	public Double getCorruptionRate() {
		return corruptionRate;
	}

	/**
	 * Set with past value {@link HumanAgent#getCorruptionRate()}
	 */
	public void setCorruptionRate() {
		this.corruptionRate = (getCorruptionRate() + getCorruptionAversionAround()) ;
	}

	public Double getCorruptionAversionAround() {
		return corruptionAversionAround;
	}

	/**
	 * TODO
	 * Setup this automatically.
	 * Averages of corruputionAversion around. 
	 */
	public void setCorruptionAversionAround(Double corruptionAversionAround) {
		this.corruptionAversionAround = corruptionAversionAround;
	}

	public Double getArrestProbabilityObserved() {
		return arrestProbabilityObserved;
	}

	/**
	 * Set with past value {@link HumanAgent#getArrestProbabilityObserved()}
	 */
	public void setArrestProbabilityObserved() {
		this.arrestProbabilityObserved = 
				(getArrestProbabilityObserved() + getDangerOfArrest()) / 2;
	}

	public Double getDangerOfArrest() {
		return dangerOfArrest;
	}

	/**
	 * Setup rate of 
	 * 
	 * @param corrupts
	 * @param arrestedCorrupts
	 * @param honests
	 */
	public void setDangerOfArrest(Integer corrupts, Integer arrestCorrupted , Integer honests) {
		this.dangerOfArrest =  (arrestCorrupted.doubleValue() 
				+ honests.doubleValue()) / (corrupts.doubleValue() + 1);
	}

	public boolean isArested() {
		return isArested;
	}

	public void setArested(boolean isArested) {
		this.isArested = isArested;
	}

	public Double getCostOfPunishment() {
		return costOfPunishment;
	}

	public void setCostOfPunishment(Double costOfPunishment) {
		this.costOfPunishment = costOfPunishment;
	}
}
