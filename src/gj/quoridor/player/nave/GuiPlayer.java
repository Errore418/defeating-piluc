package gj.quoridor.player.nave;

import gj.quoridor.player.Player;

//TODO implementare tutto
@SuppressWarnings("unused")
public class GuiPlayer implements Player {
	private Object board = null;

	public GuiPlayer() {
	}

	@Override
	public int[] move() {
		return new int[] { 0, 0 };
	}

	@Override
	public void start(boolean arg0) {
	}

	@Override
	public void tellMove(int[] arg0) {
	}

	public void riceviOggetto(Object board) {
		this.board = board;
	}

}