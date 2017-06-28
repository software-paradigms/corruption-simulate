package br.unb.fga.software.multiagent.behaviour;

import br.unb.fga.software.multiagent.agent.HumanAgent;
import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentUpdaterBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = 1L;
	
	private ParallelBehaviour behaviour;
	
	public AgentUpdaterBehaviour(HumanAgent agent, long clock, 
			ParallelBehaviour behaviour) {
		super(agent, clock);

		this.behaviour = behaviour;
	}

	@Override
	protected void onTick() {
		if(behaviour.done()) {
			HumanAgent humanAgent = (HumanAgent) myAgent;
			humanAgent.setUpIteration();

			ACLMessage stateInform = new ACLMessage(ACLMessage.INFORM);
			stateInform.addReceiver(new AID("space", AID.ISLOCALNAME));
			
			stateInform.setContent(humanAgent.getCurrentState().getStateName());
			humanAgent.send(stateInform);
			
			humanAgent.clearNeighborsStatus();
		} if(((HumanAgent) myAgent).canStartObserveNeighbors()) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.clearAllReceiver();
			msg.clearAllReplyTo();
			behaviour.reset();
			myAgent.addBehaviour(behaviour);
			((HumanAgent) myAgent).setCanStartObserveNeighbors(false);
		}
	}

}
