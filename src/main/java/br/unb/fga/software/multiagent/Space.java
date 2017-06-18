package br.unb.fga.software.multiagent;

import br.unb.fga.software.multiagent.agent.HumanAgent;
import jade.core.Agent;
import jade.core.behaviours.ReceiverBehaviour;
import jade.core.behaviours.ReceiverBehaviour.NotYetReady;
import jade.core.behaviours.ReceiverBehaviour.TimedOut;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

public class Space extends Agent {

	private static final long serialVersionUID = 1L;

	/*
	 * Probability of agent[i][j] be arrest
	 */
	private Double[][] arrestProbability;

	/*
	 * Index of investigation about corruption
	 */
	private Double researchEffort;

	/*
	 * Quantity of agents in this environment
	 */
	private Integer spaceLenght;
	
	/*
	 * Quantity of iterations 
	 */
	private Integer iterations;
	
	private Integer actualIteration;

	private PlatformController container;

	@Override
	protected void setup() {
		this.container = getContainerController();

		Object[] args = getArguments();

		if (args.length == 0) {
			throw new IllegalArgumentException("You should pass one argument");
		} else {
			System.out.println("Starting with " + args[0]);
			setSpaceLenght(Integer.parseInt(args[0].toString()));
		}

		AgentMultiton.init(getOrder());
		
		// Creates all agents to fills square space
		createAgents(getOrder());

		final SpaceWindow space = new SpaceWindow(getSpaceLenght());
		space.setVisible(true);

		// Should refresh simulation every time
		addBehaviour(new TickerBehaviour(this, 2000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				if (isFinish()) {
					done();
				} else {
					if(!AgentMultiton.isEmpty()) {						
						space.updatePainel(AgentMultiton.getAll());
					}
				}
			}

			private boolean isFinish() {
				return false;
			}
		});
		
		MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ReceiverBehaviour b = new ReceiverBehaviour(this, 1000, messageTemplate);

		if(b.done()) {
			String agentId = null;
			String msgState = null;
			
			try {
				agentId = b.getMessage().getSender().getLocalName();
				msgState = b.getMessage().getContent();
			} catch (TimedOut e) {
				e.printStackTrace();
			} catch (NotYetReady e) {
				e.printStackTrace();
			}

			System.out.println("Receiving state from: " + agentId);
			
			switch(AgentState.getByString(msgState)) {
				case CORRUPT:
					AgentMultiton.put(agentId, SpaceWindow.CORRUPT);
					break;
				case NEUTRAL:
					AgentMultiton.put(agentId, SpaceWindow.NEUTRAL);
					break;
				case HONEST:
					AgentMultiton.put(agentId, SpaceWindow.HONEST);
					break;
				case ARRESTED:
					AgentMultiton.put(agentId, SpaceWindow.ARRESTED);
					break;
			}
		}
	}

	private void createAgents(Integer order) {
		for(int id = 0; id < order; id++) {
			try {
				System.out.println("Agent: " + HumanAgent.class.getName());
				System.out.println("Agent id: " + String.valueOf(id));
				container.createNewAgent(String.valueOf(id), HumanAgent.class.getName(), null);
			} catch (ControllerException e) {
				e.printStackTrace();
			}
		}
	}

	private Integer getOrder() {
		return getSpaceLenght() * getSpaceLenght();
	}

	public Double[][] getArrestProbability() {
		return arrestProbability;
	}

	public void setArrestProbability(Double[][] arrestProbability) {
		this.arrestProbability = arrestProbability;
	}

	public Double getResearchEffort() {
		return researchEffort;
	}

	public void setResearchEffort(Double researchEffort) {
		this.researchEffort = researchEffort;
	}

	public Integer getSpaceLenght() {
		return spaceLenght;
	}

	public void setSpaceLenght(Integer spaceLenght) {
		this.spaceLenght = spaceLenght;
	}

	public Integer getIterations() {
		return iterations;
	}

	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}

	public Integer getActualIteration() {
		return actualIteration;
	}

	public void setActualIteration(Integer actualIteration) {
		this.actualIteration = actualIteration;
	}
}
