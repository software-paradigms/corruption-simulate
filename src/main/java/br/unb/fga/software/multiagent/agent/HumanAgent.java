package br.unb.fga.software.multiagent.agent;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import br.unb.fga.software.multiagent.AgentState;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class HumanAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	private static final double AVERSION_MEAN = 0.5;
	private static final double AVERSION_VARIENCE = 0.25; 
	
	public static final String INDEXES_SEPARATOR = "x";

	protected static final String PARAMS_SEPARATOR = ";"; 

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
	
	private AgentState currentState;
	
	private Double costOfPunishment;
	
	private boolean iterationFinished;

	private Vector<Integer> neighborhood;
	
	private Map<Integer, NeighborStatus> neighborsStatus;

	@Override
	protected void setup() {

		getNeighborhood();
		
		Random randomGenerator = new Random();
		setCorruptionAversionInitial((randomGenerator.nextGaussian() * AVERSION_VARIENCE) + AVERSION_MEAN);
		
		neighborsStatus = new HashMap<Integer, NeighborStatus>();
		
		addBehaviour(new OneShotBehaviour() {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				// Request parameter tokens and send your self parameters
				for(Integer neighborID : neighborsStatus.keySet()) {
					// If this status is null, means that he never response to him
					if(neighborsStatus.get(neighborID) == null) {
						ACLMessage requestToken = new ACLMessage(ACLMessage.INFORM);
						requestToken.addReceiver(new AID(neighborID.toString(), AID.ISLOCALNAME));
						requestToken.setContent(getResponseToken());
						send(requestToken);
					} else {
						// Se ele já tem o parametro, então já enviou, não precisa pedir!!!
					}
				}
			}
		});
		
		/*
		 *  When a neighbor claims agent parameters, this agent should respond this.
		 */
		addBehaviour(new CyclicBehaviour() {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage tokenResponse = receive();

				if(tokenResponse != null) {
					updateNeighborStatus(tokenResponse);

					// Now reply to this sender
					ACLMessage reply = tokenResponse.createReply();

					String content = getResponseToken();
					reply.setContent(content);
				}
			}

			/**
			 * To understand token, see bellow method getResponseToken()
			 */
			private void updateNeighborStatus(ACLMessage tokenResponse) {
				Integer agentID = Integer.valueOf(tokenResponse.getSender().getLocalName());

				String[] token = tokenResponse.getContent().split(PARAMS_SEPARATOR);

				NeighborStatus neighborStatus = new NeighborStatus(Double.valueOf(token[0]), 
						AgentState.valueOf(token[1]));

				neighborsStatus.put(agentID, neighborStatus);
			}
		});
		
		// Should refresh simulation every time
		addBehaviour(new TickerBehaviour(this, 1000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				if(iterationFinished) {
					ACLMessage stateInform = new ACLMessage(ACLMessage.INFORM);
					stateInform.addReceiver(new AID("space", AID.ISLOCALNAME));
					stateInform.setContent(getCurrentState().getStateName());
					send(stateInform);
				}
			}
		});
	}

	/**
	 * Get corruption aversion and your state
	 */
	private String getResponseToken() {
		return getCorruptionAversion() + PARAMS_SEPARATOR 
				+ getCurrentState().getStateName();
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
		this.corruptionRate = (getCorruptionRate() + getCorruptionAversionAround())/2;
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

	public AgentState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(AgentState currentState) {
		this.currentState = currentState;
	}

	public Double getCostOfPunishment() {
		return costOfPunishment;
	}

	public void setCostOfPunishment(Double costOfPunishment) {
		this.costOfPunishment = costOfPunishment;
	}

	private void getNeighborhood(){
		neighborhood = new Vector<Integer>();
		
		int agentPosition = Integer.parseInt(getAID().getLocalName());		
		int row = (int) Math.sqrt(Double.parseDouble(getArguments()[0].toString()));

		int px = agentPosition % row;
		int py = agentPosition / row;
		
		// NL
		if(validNeigboarhood(px - 1, py - 1, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		// N
		if(validNeigboarhood(px, py - 1, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		// NW
		if(validNeigboarhood(px + 1, py - 1, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		// W
		if(validNeigboarhood(px + 1, py, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		// SW
		if(validNeigboarhood(px + 1, py + 1, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		// S
		if(validNeigboarhood(px, py + 1, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		// SL
		if(validNeigboarhood(px - 1, py + 1, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		// L
		if(validNeigboarhood(px - 1, py, row))
			neighborhood.add(calcNeigboarhood(px, py, row));
		
	}

	private boolean validNeigboarhood(int px, int py, int line){
		return (px - 1 < 0) || (py - 1 < 0)
				|| (px + 1 == line) || (py + 1 == line);
	}

	private Integer calcNeigboarhood(int px, int py, int line){
		return py * line + px;
	}

}
