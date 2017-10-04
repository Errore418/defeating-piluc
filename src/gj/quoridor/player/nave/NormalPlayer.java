package gj.quoridor.player.nave;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
		int meRow;
		int meColumn;
		int[] movement;
		if (red) {
			meRow = (int) accessArray(position, 0, 0);
			meColumn = (int) accessArray(position, 0, 1);
			movement = this.movement[0];
		} else {
			meRow = (int) accessArray(position, 1, 0);
			meColumn = (int) accessArray(position, 1, 1);
			movement = this.movement[1];
		}

		int direction = move[1];
		int buffer;
		if (direction < 2) {
			buffer = meRow;
		} else {
			buffer = meColumn;
		}

		return (buffer + movement[move[1]] < 0) || (buffer + movement[move[1]] > 8);
	}

	@Override
	public void start(boolean arg0) {
		if (arg0) {
			criticalLine = 1;
		} else {
			criticalLine = 7;
		}
		red = arg0;
		retrievePosition();
		retrieveBoard();
	}

	@Override
	public void tellMove(int[] arg0) {
		if (arg0[0] == 1) {
			resetBoard();
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
		int enemyRow;
		int enemyColumn;
		if (red) {
			enemyRow = (int) accessArray(position, 1, 0);
			enemyColumn = (int) accessArray(position, 1, 1);
		} else {
			enemyRow = (int) accessArray(position, 0, 0);
			enemyColumn = (int) accessArray(position, 1, 1);
		}

		if (isEnemyConfused) {
			return (enemyRow == criticalLine);
		} else {
			return ((enemyRow == criticalLine) || (enemyColumn == 0) || (enemyColumn == 8));
		}
	}

	private void resetBoard() {
		insertPrivateField(gameManager, "board", cleanBoard);
	}

	private void retrievePosition() {
		position = retrievePrivateField(gameManager, "position");
	}

	private void retrieveBoard() {
		Object board = retrievePrivateField(gameManager, "board");		
		try {
			Constructor<?> constructorBoard = board.getClass().getDeclaredConstructor(int.class, int.class);
			constructorBoard.setAccessible(true);
			cleanBoard = constructorBoard.newInstance(9,9);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object retrievePrivateField(Object target, String name) {
		try {
			Field field = target.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void insertPrivateField(Object target, String name, Object value) {
		try {
			Field field = target.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object accessArray(Object array, int... index) {
		Object buffer = array;
		for (int i = 0; i < index.length; i++) {
			buffer = Array.get(buffer, index[i]);
		}
		return buffer;
	}

}