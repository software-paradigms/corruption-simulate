package io.github.pwener.corruption_simulate;

import org.junit.Test;

import br.unb.fga.software.multiagent.agent.HumanAgent;

public class AgentMath {

	@Test
	public void testAgentAversionInitial() {
		HumanAgent h = new HumanAgent();
		for (int x = 0; x < 10; x++) {
			h.setCorruptionAversionInitial();
			System.out.println(h.getCorruptionAversion());
		}
	}
}
