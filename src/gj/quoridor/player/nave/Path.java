package gj.quoridor.player.nave;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Path {
	private static class Wrapper {
		private Node node;
		private Wrapper parent;

		private Wrapper(Node node) {
			this(node, null);
		}

		private Wrapper(Node node, Wrapper parent) {
			this.node = node;
			this.parent = parent;
		}

	}

	public static int shortPath(Node start, int goal) {
		boolean[][] control = initControl();

		Queue<Wrapper> frangia = new LinkedList<>();
		markNode(control, start);
		frangia.add(new Wrapper(start));

		while (!frangia.isEmpty()) {

			Wrapper current = frangia.poll();

			for (Node n : current.node.getNeighbors()) {

				if (!isMarked(control, n)) {

					Wrapper buffer = new Wrapper(n, current);

					if (n.getR() == goal) {
						return buildPath(buffer);
					}

					frangia.add(buffer);
					markNode(control, n);

				}

			}

		}

		return -1;
	}

	private static int buildPath(Wrapper start) {
		List<Node> result = new LinkedList<>();

		Wrapper buffer = start;
		while (buffer != null) {
			result.add(buffer.node);
			buffer = buffer.parent;
		}

		return result.size();
	}

	private static boolean isMarked(boolean[][] control, Node node) {
		return control[node.getR()][node.getC()];
	}

	private static void markNode(boolean[][] control, Node node) {
		control[node.getR()][node.getC()] = true;
	}

	private static boolean[][] initControl() {
		boolean[][] result = new boolean[9][9];
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				result[i][k] = false;
			}
		}
		return result;
	}

}