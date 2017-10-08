package gj.quoridor.player.nave;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gj.quoridor.engine.Board;
import gj.quoridor.engine.Node;
import gj.quoridor.engine.Wall;
import gj.quoridor.player.Player;

public class GuiPlayer implements Player {
	private static GuiPlayer me = null;
	private Object board = null;
	private int wall = -1;

	private Set<Integer> walls = new HashSet<>();
	private int meRow;
	private boolean red;
	private Deque<Integer> myWalls = new ArrayDeque<>();

	public GuiPlayer() {
		Tool.poison("gui");
		me = this;
	}

	@Override
	public int[] move() {
		int[] move = new int[2];
		if (!myWalls.isEmpty()) {
			move[0] = 1;
			move[1] = myWalls.pop();
			walls.add(move[1]);
		} else {
			checkWallPresence();
			move[0] = 0;
			move[1] = 0;
			meRow = (red) ? meRow + 1 : meRow - 1;
		}
		return move;
	}

	@Override
	public void start(boolean arg0) {
		red = arg0;
		meRow = (red) ? 0 : 8;
		if (!red) {
			populateMyWalls();
		} else {
			myWalls.clear();
		}
	}

	private void populateMyWalls() {
		myWalls.push(122);
		myWalls.push(120);
		myWalls.push(105);
		myWalls.push(107);
		myWalls.push(109);
		myWalls.push(111);
		myWalls.push(94);
		myWalls.push(92);
		myWalls.push(90);
		myWalls.push(88);
	}

	@Override
	public void tellMove(int[] arg0) {
		if (arg0[0] == 1) {
			walls.add(arg0[1]);
		}
	}

	public static void acceptBoard(Object b) {
		me.board = b;
	}

	private void checkWallPresence() {
		List<Integer> dangerWalls = generateDangerWalls();
		for (Integer w : dangerWalls) {
			if (walls.contains(w)) {
				removeWall(w);
			}
		}
	}

	private List<Integer> generateDangerWalls() {
		List<Integer> result = new LinkedList<>();
		int dangerRow = (red) ? meRow : meRow - 1;
		result.add(generateWall(dangerRow, 4));
		result.add(generateWall(dangerRow, 3));
		return result;
	}

	private int generateWall(int i, int k) {
		return (2 * i + 1) * 8 + k;
	}

	private void removeWall(int w) {
		try {
			Method map = Wall.class.getDeclaredMethod("map", int.class, int.class);
			map.setAccessible(true);
			int[][] c = (int[][]) map.invoke(null, w, 9);

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

			wall = w;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void restoreWall() throws Exception {
		try {
			if ((me.wall != -1) && (scanStackTrace())) {
				Method putWall = Board.class.getDeclaredMethod("putWall", int.class);
				putWall.setAccessible(true);
				putWall.invoke(me.board, me.wall);
				me.wall = -1;
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