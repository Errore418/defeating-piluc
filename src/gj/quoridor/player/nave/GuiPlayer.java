package gj.quoridor.player.nave;

import java.lang.reflect.Method;

import gj.quoridor.engine.Board;
import gj.quoridor.engine.Node;
import gj.quoridor.engine.Wall;
import gj.quoridor.player.Player;

public class GuiPlayer implements Player {
	private static Object board = null;
	private static int wall = -1;

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
			removeWall(arg0[1]);
			wall = arg0[1];
		}
	}

	public static void acceptBoard(Object b) {
		board = b;
	}

	private void removeWall(int wall) {
		try {
			Method map = Wall.class.getDeclaredMethod("map", int.class, int.class);
			map.setAccessible(true);
			int[][] c = (int[][]) map.invoke(null, wall, 9);

			Object nodeBoard = Tool.retrievePrivateField(board, "board");

			Object node1 = Tool.accessArray(nodeBoard, c[0][0], c[0][1]);
			Object node2 = Tool.accessArray(nodeBoard, c[0][2], c[0][3]);
			Object node3 = Tool.accessArray(nodeBoard, c[1][0], c[1][1]);
			Object node4 = Tool.accessArray(nodeBoard, c[1][2], c[1][3]);

			Method addNeighbour = Node.class.getDeclaredMethod("addNeighbour", Node.class);
			addNeighbour.setAccessible(true);

			addNeighbour.invoke(node1, node2);
			addNeighbour.invoke(node2, node1);
			addNeighbour.invoke(node3, node4);
			addNeighbour.invoke(node4, node3);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void restoreWall() throws Exception {
		try {
			if (wall != -1) {
				if (scanStackTrace()) {
					Method putWall = Board.class.getDeclaredMethod("putWall", int.class);
					putWall.setAccessible(true);
					putWall.invoke(board, wall);
					wall = -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean scanStackTrace() {
		boolean result = true;
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stackTrace.length; i++) {
			if (stackTrace[i].getMethodName().equals("isCorrectMove")) {
				result = false;
				break;
			}
		}
		return result;
	}

}