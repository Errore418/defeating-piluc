package gj.quoridor.player.avversario;

import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class AvversarioPlayer implements Player {

	@Override
	public int[] move() {
		throwException();
		return new int[] { 0, 0 };
	}

	@Override
	public void start(boolean arg0) {
	}

	@Override
	public void tellMove(int[] arg0) {
		throwException();
	}

	private void throwException() {
		if (ThreadLocalRandom.current().nextInt() % 5 == 0) {
			throw new RuntimeException();
		}
	}

}
