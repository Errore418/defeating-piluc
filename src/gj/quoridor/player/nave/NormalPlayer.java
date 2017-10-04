package gj.quoridor.player.nave;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class NormalPlayer implements Player {
	private static Object gameManager = null;

	public NormalPlayer() {
		Tool.poison("normal");
	}

	@Override
	public int[] move() {
		return new int[] { 0, ThreadLocalRandom.current().nextInt(2, 4) };
	}

	@Override
	public void start(boolean arg0) {
		try {
			Field position = gameManager.getClass().getDeclaredField("position");
			position.setAccessible(true);
			Object array = position.get(gameManager);

			int[] newPosition = null;
			int index = 0;
			if (arg0) {
				newPosition = new int[] { 8, ThreadLocalRandom.current().nextInt(1, 8) };
				index = 0;
			} else {
				newPosition = new int[] { 0, ThreadLocalRandom.current().nextInt(1, 8) };
				index = 1;
			}

			Array.set(array, index, newPosition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tellMove(int[] arg0) {
	}

	public static void acceptGameManager(Object gm) {
		gameManager = gm;
	}
}