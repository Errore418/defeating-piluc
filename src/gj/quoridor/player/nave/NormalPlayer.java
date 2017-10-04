package gj.quoridor.player.nave;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class NormalPlayer implements Player {
	private static Object gameManager = null;
	private Node[][] board = new Node[9][9];
	private boolean red;
	private int criticalLine;

	private boolean clock = true;

	public NormalPlayer() {
		Tool.avvelena("normal");
	}

	@Override
	public int[] move() {
		if (checkEnemyPosition()) {
			vadeRetro();
		}

		if (clock) {
			clock = !clock;
			return new int[] { 0, 2 };
		} else {
			clock = !clock;
			return new int[] { 0, 3 };
		}

	}

	@Override
	public void start(boolean arg0) {
		initializeBoard();
		if (arg0) {
			criticalLine = 1;
		} else {
			criticalLine = 7;
		}
		red = arg0;
	}

	@Override
	public void tellMove(int[] arg0) {
	}

	public static void riceviGameManager(Object gm) {
		gameManager = gm;
	}

	private void initializeBoard() {
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				board[i][k] = new Node(i, k);
			}
		}
		linkNode();
	}

	private void linkNode() {
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				if (i > 0) {
					board[i][k].addNeighbor(board[i - 1][k]);
				}
				if (i < 8) {
					board[i][k].addNeighbor(board[i + 1][k]);
				}
				if (k > 0) {
					board[i][k].addNeighbor(board[i][k - 1]);
				}
				if (k < 8) {
					board[i][k].addNeighbor(board[i][k + 1]);
				}
			}
		}
	}

	private void vadeRetro() {
		try {
			Field position = gameManager.getClass().getDeclaredField("position");
			position.setAccessible(true);
			Object array = position.get(gameManager);

			int[] newPosition = null;
			int index;
			if (red) {
				newPosition = new int[] { 8, ThreadLocalRandom.current().nextInt(1, 8) };
				index = 1;
			} else {
				newPosition = new int[] { 0, ThreadLocalRandom.current().nextInt(1, 8) };
				index = 0;
			}

			Array.set(array, index, newPosition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkEnemyPosition() {
		boolean result = false;
		try {

			Field position = gameManager.getClass().getDeclaredField("position");
			position.setAccessible(true);
			Object array = position.get(gameManager);

			int enemyRow;
			if (red) {
				enemyRow = Array.getInt(Array.get(array, 1), 0);
			} else {
				enemyRow = Array.getInt(Array.get(array, 0), 0);
			}

			result = enemyRow == criticalLine;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}