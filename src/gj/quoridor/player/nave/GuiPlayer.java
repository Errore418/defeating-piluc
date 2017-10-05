package gj.quoridor.player.nave;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import gj.quoridor.player.Player;

public class GuiPlayer implements Player {
	private static Object board = null;
	private static boolean catchThisBoard = true;

	public GuiPlayer() {
		Tool.poison("gui");
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
		if (arg0[0] == 1) {
			resetBoard();
		}
	}

	public static void acceptBoard(Object b) {
		if (catchThisBoard) {
			board = b;
		}
	}

	private Object generateCleanBoard() throws Exception {
		Constructor<?> constructorBoard = Class.forName("gj.quoridor.engine.Board").getDeclaredConstructor(int.class,
				int.class);
		constructorBoard.setAccessible(true);
		catchThisBoard = false;
		Object anotherBoard = constructorBoard.newInstance(9, 9);
		catchThisBoard = true;
		return retrievePrivateField(anotherBoard, "board");
	}

	private Object retrievePrivateField(Object target, String name) throws Exception {
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.get(target);
	}

	private void insertPrivateField(Object target, String name, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}

	private void resetBoard() {
		try {
			Object cleanBoard = generateCleanBoard();
			insertPrivateField(board, "board", cleanBoard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}