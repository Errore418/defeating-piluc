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
		boolean[][] control = new boolean[9][9];
		Queue<Wrapper> queue = new LinkedList<>();
		control[start.getRow()][start.getColumn()] = true;
		queue.add(new Wrapper(start));
		while (!queue.isEmpty()) {
			Wrapper current = queue.poll();
			for (Node n : current.node.getnNeighbours()) {
				if (!control[n.getRow()][n.getColumn()]) {
					Wrapper buffer = new Wrapper(n, current);
					if (n.getRow() == goal) {
						return buildPath(buffer);
					}
					queue.add(buffer);
					control[n.getRow()][n.getColumn()] = true;
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

}