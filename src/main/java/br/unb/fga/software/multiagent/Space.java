package br.unb.fga.software.multiagent;

import java.awt.Color;
import java.util.Vector;

import br.unb.fga.software.multiagent.agent.HumanAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.State;

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

	private PlatformController container = getContainerController();

	@Override
	protected void setup() {
		Object[] args = getArguments();

		if (args.length == 0) {
			throw new IllegalArgumentException("You should pass one argument");
		} else {
			System.out.println("Starting with " + args[0]);
			setSpaceLenght(Integer.parseInt(args[0].toString()));
		}

		// Creates all agents to fills square space
		createAgents(getOrder());

		SpaceWindow space = new SpaceWindow(getSpaceLenght());
		Vector<Color> colors = new Vector<Color>();
		space.mountPainel(colors);
		space.setVisible(true);

		// Should refresh simulation every time
		addBehaviour(new TickerBehaviour(this, 2000) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				if (isFinish()) {
					done();
				} else {
					for(Integer id = 0; id < getOrder(); id++) {
						try {
							State state = container.getAgent(String.valueOf(id)).getState();
							switch(state.getCode()) {
								case 1:
									AgentMultiton.put(id.toString(), SpaceWindow.CORRUPT);
									break;
								case 2:
									AgentMultiton.put(id.toString(), SpaceWindow.NEUTRAL);
									break;
								case 3:
									AgentMultiton.put(id.toString(), SpaceWindow.HONEST);
									break;
								case 4:
									AgentMultiton.put(id.toString(), SpaceWindow.ARRESTED);
									break;
							}
						} catch (ControllerException e) {
							e.printStackTrace();
						}
					}
				}
			}

			private boolean isFinish() {
				return getActualIteration() == getIterations();
			}
		});
	}

	private void createAgents(Integer order) {
		for(int id = 0; id < order; id++) {
			try {
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
