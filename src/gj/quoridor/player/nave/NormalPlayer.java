package gj.quoridor.player.nave;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class NormalPlayer implements Player {
	private static Object gameManager = null;
	private Object position = null;
	private Object cleanBoard = null;

	private boolean red;
	private int[][] movement = new int[][] { { 1, -1, 1, -1 }, { -1, 1, -1, 1 } };
	private int criticalLine;
	private boolean isEnemyConfused = false;

	public NormalPlayer() {
		Tool.poison("normal");
	}

	public static void acceptGameManager(Object gm) {
		gameManager = gm;
	}

	@Override
	public int[] move() {
		if (isEnemyInDangerPlace()) {
			vadeRetro();
		}
		int[] move = new int[] { 0, -1 };
		do {
			move[1] = ThreadLocalRandom.current().nextInt(4);
		} while (wrongMove(move));
		return move;
	}

	private boolean wrongMove(int[] move) {
		int[] mePosition = currentPosition(true);
		int[] movement = (red) ? this.movement[0] : this.movement[1];
		int buffer = (move[1] < 2) ? mePosition[0] : mePosition[1];
		return (buffer + movement[move[1]] < 0) || (buffer + movement[move[1]] > 8);
	}

	@Override
	public void start(boolean arg0) {
		red = arg0;
		criticalLine = (red) ? 1 : 7;
		try {
			retrievePosition();
			generateCleanBoard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tellMove(int[] arg0) {
		if (arg0[0] == 1) {
			try {
				resetBoard();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void vadeRetro() {
		int[] newPosition = null;
		int index;
		if (red) {
			newPosition = new int[] { 7, ThreadLocalRandom.current().nextInt(1, 8) };
			index = 1;
		} else {
			newPosition = new int[] { 1, ThreadLocalRandom.current().nextInt(1, 8) };
			index = 0;
		}
		Array.set(position, index, newPosition);
		isEnemyConfused = true;
	}

	private boolean isEnemyInDangerPlace() {
		int[] enemyPosition = currentPosition(false);
		if (isEnemyConfused) {
			return (enemyPosition[0] == criticalLine);
		} else {
			return ((enemyPosition[0] == criticalLine) || (enemyPosition[1] == 0) || (enemyPosition[1] == 8));
		}
	}

	private int[] currentPosition(boolean me) {
		int index = (red ^ me) ? 1 : 0;
		return (int[]) Tool.accessArray(position, index);
	}

	private void resetBoard() throws Exception {
		Tool.insertPrivateField(gameManager, "board", cleanBoard);
	}

	private void retrievePosition() throws Exception {
		position = Tool.retrievePrivateField(gameManager, "position");
	}

	private void generateCleanBoard() throws Exception {
		Object board = Tool.retrievePrivateField(gameManager, "board");
		try {
			Constructor<?> constructorBoard = board.getClass().getDeclaredConstructor(int.class, int.class);
			constructorBoard.setAccessible(true);
			cleanBoard = constructorBoard.newInstance(9, 9);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}