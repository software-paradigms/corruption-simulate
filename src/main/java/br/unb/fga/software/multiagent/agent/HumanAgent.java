package br.unb.fga.software.multiagent.agent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import br.unb.fga.software.multiagent.AgentState;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class HumanAgent extends Agent {

	private static final long serialVersionUID = 1L;
	
	public static final String INDEXES_SEPARATOR = "x";

	protected static final String PARAMS_SEPARATOR = ";";

	private static final Double TO_START_CORRUPT = 0.5;

	private static final double ARRESTED_PROBABILITY = 0.4; 
	
	private final Double maxProbabilityToArrested = 0.95;
	
	private Double costOfPunishment = 1.6;
	
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

		// Need Runs after each iteration
		final SimpleBehaviour observesNeighborsBehaviour = new SimpleBehaviour() {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				// Request parameter tokens and send your self parameters
				for(Integer neighborID : neighborhood) {
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

			@Override
			public boolean done() {
				return neighborsStatus.size() == neighborhood.size();
			}
		};
		/*
		 *  When a neighbor claims agent parameters, this agent should respond this.
		 */
		SimpleBehaviour dataNeighborBehaviour = new SimpleBehaviour() {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage tokenResponse = receive();
				
//				System.out.println(getLocalName() + ": my neighbors are " 
//						+ neighborhood.size());
				if(tokenResponse != null){
					if(tokenResponse.getSender().getLocalName().equals("space")){
						canStart = true;
						tokenResponse = null;
					}
					else {
						if(!tokenResponse.getSender().getLocalName().equals("ams")) {
							updateNeighborStatus(tokenResponse);
		
							// Now reply to this sender
							ACLMessage reply = tokenResponse.createReply();
		
							String content = getResponseToken();
							reply.setContent(content);
						}
					}
				}
			}

			/**
			 * To understand token, see bellow method getResponseToken()
			 */
			private void updateNeighborStatus(ACLMessage tokenResponse) {
				Integer agentID = Integer.valueOf(tokenResponse.getSender().getLocalName());

				String[] token = tokenResponse.getContent().split(PARAMS_SEPARATOR);

				NeighborStatus neighborStatus = new NeighborStatus(Double.valueOf(token[0]), 
						AgentState.getByString(token[1]));

				neighborsStatus.put(agentID, neighborStatus);
			}

			@Override
			public boolean done() {
				return neighborsStatus.size() == neighborhood.size();
			}
		};

		final ParallelBehaviour parallelBehaviour = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
		parallelBehaviour.addSubBehaviour(observesNeighborsBehaviour);
		parallelBehaviour.addSubBehaviour(dataNeighborBehaviour);

		addBehaviour(parallelBehaviour);
		
		final HumanAgent human = this;

		// Should refresh simulation every time, should be syncronized with
		addBehaviour(new TickerBehaviour(this, 1000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
//				System.out.println("Agent " + getLocalName());
//				System.out.println("Status:" + parallelBehaviour.done());
				if(parallelBehaviour.done()) {
					setUpIteration();

					ACLMessage stateInform = new ACLMessage(ACLMessage.INFORM);
					stateInform.addReceiver(new AID("space", AID.ISLOCALNAME));
					
					stateInform.setContent(getCurrentState().getStateName());
					send(stateInform);
					
					neighborsStatus.clear();
				}
				if(canStart){
					parallelBehaviour.reset();
					human.addBehaviour(parallelBehaviour);
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
//
//		System.out.println("I'm agent " + getLocalName() 
//			+ " and I have the follow neighbors: " + neighborhood.toString());
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
		if(getCurrentState() != AgentState.ARRESTED)
			if(isCorrupt()) {
				if(getRealArrestedCaptured() >= ARRESTED_PROBABILITY) {
					state = AgentState.ARRESTED;
				} else {				
					state = AgentState.CORRUPT;
				}
			} else {
					state = AgentState.HONEST;
			}
		else
		{
			state = AgentState.ARRESTED;
		}
		return state;
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
}
