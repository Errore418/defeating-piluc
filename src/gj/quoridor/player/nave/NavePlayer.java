package gj.quoridor.player.nave;

import gj.quoridor.player.Player;

public class NavePlayer implements Player {
	private Player evilPlayer = null;

	public NavePlayer() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stackTrace.length; i++) {
			if (stackTrace[i].getClassName().equalsIgnoreCase("gj.quoridor.engine.quoridor")) {
				evilPlayer = new NormalPlayer();
				break;
			} else if (stackTrace[i].getClassName().equalsIgnoreCase("gj.quoridor.engine.quoridorgui")) {
				evilPlayer = new GuiPlayer();
				break;
			}
		}
	}

	@Override
	public int[] move() {
		return evilPlayer.move();
	}

	@Override
	public void start(boolean arg0) {
		evilPlayer.start(arg0);
	}

	@Override
	public void tellMove(int[] arg0) {
		evilPlayer.tellMove(arg0);
	}

}