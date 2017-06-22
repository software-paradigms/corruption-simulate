package io.github.pwener.corruption_simulate;

import org.junit.Test;

import br.unb.fga.software.multiagent.agent.HumanAgent;

public class NeighborTest {

	/**
	 * 5 -> 1, 2, 4, 7, 8
	 * 4 -> 0, 1, 2, 3, 5, 6, 7, 8
	 * 7 -> 3, 4, 5, 6, 8
	 */
	@Test
	public void testNeighBor() {
		final Integer order = 9;

		HumanAgent h = new HumanAgent() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object[] getArguments() {
				Object[] args = {order.toString()};
				return args;
			}
		};

		System.out.println(h.setNeighborhood("5").toString());
		System.out.println(h.setNeighborhood("4").toString());
		System.out.println(h.setNeighborhood("7").toString());
	}
}
