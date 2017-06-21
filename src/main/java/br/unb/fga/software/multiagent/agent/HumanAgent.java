package br.unb.fga.software.multiagent.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import br.unb.fga.software.multiagent.AgentState;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
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

	private Double costOfPunishment = 0.6;

	private AgentState currentState;

	private boolean iterationFinished;

	private Map<Integer, NeighborStatus> neighborsStatus;
	
	private int neighborsLenght;

	@Override
	protected void setup() {

		setUpIteration();

		// Need Runs after each iteration
		SimpleBehaviour observesNeighborsBehaviour = new SimpleBehaviour() {
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
				this.block();
			}

			@Override
			public boolean done() {
				return neighborsStatus.size() == neighborsLenght;
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

			@Override
			public boolean done() {
				return neighborsStatus.size() == neighborsLenght;
			}
		};

		final ParallelBehaviour parallelBehaviour = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
		parallelBehaviour.addSubBehaviour(observesNeighborsBehaviour);
		parallelBehaviour.addSubBehaviour(dataNeighborBehaviour);

		// Should refresh simulation every time, should be syncronized with
		addBehaviour(new TickerBehaviour(this, 1000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				if(iterationFinished) {
					ACLMessage stateInform = new ACLMessage(ACLMessage.INFORM);
					stateInform.addReceiver(new AID("space", AID.ISLOCALNAME));
					stateInform.setContent(getCurrentState().getStateName());
					send(stateInform);
					if(parallelBehaviour.done()) {
						setUpIteration();
					}
					parallelBehaviour.reset();
				}
			}
		});
	}

	private void setUpIteration() {
		setCorruptionAversionInitial();
		setCorruptionAversionAround();
		setCorruptionRate();
		setArrestProbabilityObserved();
		neighborsStatus = new HashMap<Integer, NeighborStatus>();

		// should be runs after neighborsStatus are finished
		int corrupts = count(AgentState.CORRUPT);
		int arrested = count(AgentState.ARRESTED);
		int honests = count(AgentState.HONEST);

		setDangerOfArrest(corrupts, arrested, honests);
		setArrestProbabilityObserved();
		
		if(isCorrupt()) {
			setCurrentState(AgentState.CORRUPT);			
		} else {
			setCurrentState(AgentState.HONEST);
		}
	}

	private int count(AgentState corrupt) {
		int count = 0;

		for(NeighborStatus ns : neighborsStatus.values()) {
			if(ns.getState() == corrupt) {
				count++;
			}
		}

		return count;
	}

	private Double calculateAversionArround() {
		Double average = 0.0;

		for(NeighborStatus neighborStatus : neighborsStatus.values()) {
			average += neighborStatus.getCorruptionAversion();
		}

		average = average / 2;

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
		Random randomGenerator = new Random();
		this.corruptionAversionInitial = 
				(randomGenerator.nextGaussian() * AVERSION_VARIENCE) + AVERSION_MEAN;
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
}
