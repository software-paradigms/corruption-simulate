package br.unb.fga.software.multiagent.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import br.unb.fga.software.multiagent.AgentState;
import br.unb.fga.software.multiagent.behaviour.ObserveNeighborBehaviour;
import br.unb.fga.software.multiagent.behaviour.ResponseStatusBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class HumanAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	public static final String INDEXES_SEPARATOR = "x";

	public static final String PARAMS_SEPARATOR = ";";

	private static final Double TO_START_CORRUPT = 0.3;

	private static final double ARRESTED_PROBABILITY = 0.7; 
	
	private final Double maxProbabilityToArrested = 0.95;
	
	private Double costOfPunishment = 3.2;
	
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

	private Vector<Integer> neighborhood;
	
	private Map<Integer, NeighborStatus> neighborsStatus;
	
	private boolean canStart = true;

	@Override
	protected void setup() {
		
		setInitialAgentAttributes();

		ObserveNeighborBehaviour requestNeighborBehaviour = new ObserveNeighborBehaviour(this); 

		ResponseStatusBehaviour responseNeighborBehaviour = new ResponseStatusBehaviour(this);

		final ParallelBehaviour parallelBehaviour = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
		parallelBehaviour.addSubBehaviour(requestNeighborBehaviour);
		parallelBehaviour.addSubBehaviour(responseNeighborBehaviour);

		addBehaviour(parallelBehaviour);

		// Should refresh simulation every time, should be syncronized with
		addBehaviour(new TickerBehaviour(this, 1000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {

				if(parallelBehaviour.done()) {
					setUpIteration();

					ACLMessage stateInform = new ACLMessage(ACLMessage.INFORM);
					stateInform.addReceiver(new AID("space", AID.ISLOCALNAME));
					
					stateInform.setContent(getCurrentState().getStateName());
					send(stateInform);
					
					neighborsStatus.clear();
				} if(canStart()) {
					parallelBehaviour.reset();
					addBehaviour(parallelBehaviour);
					canStart = false;
				}
			}
		});
	}

	private void setInitialAgentAttributes() {
		setCorruptionAversionInitial();

		this.corruptionRate = getCorruptionAversion();
		
		if(getCorruptionAversionInitial() < TO_START_CORRUPT) {
			setCurrentState(AgentState.CORRUPT);
		} else {
			setCurrentState(AgentState.HONEST);
		}

		this.arrestProbabilityObserved = 0.0;

		// Find all neighbors 
		setNeighborhood(getLocalName());

		this.neighborsStatus = new HashMap<Integer, NeighborStatus>(neighborhood.size());

		setDangerOfArrest();
	}

	private void setUpIteration() {
		// (av)it
		setCorruptionAversionAround();
		// bit
		setCorruptionRate();
		// ait
		setCorruptionAversion();

		// cit
		setDangerOfArrest();
		// pit
		setArrestProbabilityObserved();

		setCurrentState(calculateCurrentState());

		watchAgent(4);
	}
	
	private AgentState calculateCurrentState(){
		AgentState state = null;

		switch (getCurrentState()) {
			case ARRESTED:
				// keep arrested
				state = AgentState.ARRESTED;
				break;
			case CORRUPT:
				// could be arrested
				if(getRealArrestedCaptured() >= ARRESTED_PROBABILITY) {
					state = AgentState.ARRESTED;
				} else {
					state = decideWhatToBe();
				}
				break;
			case HONEST:
				state = decideWhatToBe();
				break;
			default:
				throw new RuntimeException("Impossible state of calculateCurrentState");
		}

		return state;
	}

	private AgentState decideWhatToBe() {
		if(isCorrupt()) {
			return AgentState.CORRUPT;
		} else {
			return AgentState.HONEST;						
		}
	}

	private double getRealArrestedCaptured() {
		double p = getMaxProbabilityToArrested();
		p *= (count(AgentState.HONEST) + 1.0)/(neighborhood.size() + 2);
		return p;
	}

	/**
	 * Watch status of unique agent
	 */
	private void watchAgent(int agentID) {
		if(Integer.valueOf(getLocalName()) == agentID) {
			System.out.println("(Av)it = " + getCorruptionAversionAround());
			System.out.println("bit = " + getCorruptionRate());
			System.out.println("ait = " + getCorruptionAversion());
			System.out.println("pit = " + getArrestProbabilityObserved());
			System.out.println("cit = " + getDangerOfArrest());
			System.out.println("Pit = " + getRealArrestedCaptured());
			for(NeighborStatus ns : neighborsStatus.values()) 
				System.out.println("Vizinhos is " + ns.getState());
			System.out.println("Corrupts is" + count(AgentState.CORRUPT));
			System.out.println("Honests is" + count(AgentState.HONEST));
			System.out.println("All is " + neighborhood.size());
		}
	}
	
	private int count(AgentState state) {
		int count = 0;

		for(NeighborStatus ns : neighborsStatus.values()) {
			if(ns.getState() == state) {
				count++;
			}
		}

		return count;
	}

	public Double calculateAversionArround() {
		Double average = 0.0;

		for(NeighborStatus neighborStatus : neighborsStatus.values()) {
			average += neighborStatus.getCorruptionAversion();
		}
		average += this.getCorruptionAversion();
		average = average / (neighborhood.size()+1);

		return average;
	}

	/**
	 * Get corruption aversion and your state
	 */
	public String getResponseToken() {
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

	public void setCorruptionAversionInitial() {
		// distribution between 0.25 and 0.75
		this.corruptionAversionInitial = ThreadLocalRandom
				.current().nextDouble(0.25, 0.75);

		// init as initial
		this.corruptionAversion = this.corruptionAversionInitial;
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

	public void setCorruptionAversionAround() {
		this.corruptionAversionAround = calculateAversionArround();
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
	 * Setup rate of danger of arest
	 */
	public void setDangerOfArrest() {
		// should be runs after neighborsStatus are finished
		int corrupts = count(AgentState.CORRUPT);
		int arrested = count(AgentState.ARRESTED);
		int honests = count(AgentState.HONEST);
		
		this.dangerOfArrest =  (double) (arrested + honests) / (corrupts + 1);
	}

	public void setNeighborsStatus(Map<Integer, NeighborStatus> neighborsStatus) {
		this.neighborsStatus = neighborsStatus;
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

	public Vector<Integer> setNeighborhood(String agentID) {
		neighborhood = new Vector<Integer>();

		Double agentPosition = Double.parseDouble(agentID);		
		Double gridOrder = Double.parseDouble(getArguments()[0].toString());

		Double lineDouble = agentPosition / gridOrder;
		int line = lineDouble.intValue();

		int col = (int) (agentPosition % gridOrder);

		int row = gridOrder.intValue();

		// NL
		if(validNeigboarhood(col - 1, line - 1, row))
			neighborhood.add(calcNeigboarhood(col-1, line-1, row));
		// N
		if(validNeigboarhood(col, line - 1, row))
			neighborhood.add(calcNeigboarhood(col, line-1, row));
		// NW
		if(validNeigboarhood(col + 1, line - 1, row))
			neighborhood.add(calcNeigboarhood(col+1, line-1, row));
		// W
		if(validNeigboarhood(col + 1, line, row))
			neighborhood.add(calcNeigboarhood(col+1, line, row));
		// SW
		if(validNeigboarhood(col + 1, line + 1, row))
			neighborhood.add(calcNeigboarhood(col+1, line+1, row));
		// S
		if(validNeigboarhood(col, line + 1, row))
			neighborhood.add(calcNeigboarhood(col, line+1, row));
		// SL
		if(validNeigboarhood(col - 1, line + 1, row))
			neighborhood.add(calcNeigboarhood(col-1, line+1, row));
		// L
		if(validNeigboarhood(col - 1, line, row))
			neighborhood.add(calcNeigboarhood(col-1, line, row));
		
		return neighborhood;
	}

	private boolean validNeigboarhood(int col, int line, int qtd) {
		boolean basicCondition = (col >= 0) && (line >= 0) 
				&& (col < qtd) && (line < qtd);
		return basicCondition; 
	}

	private Integer calcNeigboarhood(int col, int line, int qtd) {
		int location = (line * qtd) + col;
		return location;
	}

	public Double getMaxProbabilityToArrested() {
		return maxProbabilityToArrested;
	}

	public Vector<Integer> getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(Vector<Integer> neighborhood) {
		this.neighborhood = neighborhood;
	}

	public Map<Integer, NeighborStatus> getNeighborsStatus() {
		return neighborsStatus;
	}

	public boolean canStart() {
		return canStart;
	}

	public void setCanStart(boolean canStart) {
		this.canStart = canStart;
	}

	public void updateNeighborsStatus(Integer agentID, NeighborStatus neighborStatus) {
		this.neighborsStatus.put(agentID, neighborStatus);
	}
}
