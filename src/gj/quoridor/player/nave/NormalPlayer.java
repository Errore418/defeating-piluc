package gj.quoridor.player.nave;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ThreadLocalRandom;

import gj.quoridor.player.Player;

public class NormalPlayer implements Player {
	private static Object gameManager = null;

	public NormalPlayer() {
		try {
			// caricamento javassist
			File f = new File(".\\src\\gj\\quoridor\\player\\nave\\javassist.jar");
			URLClassLoader urlCl = new URLClassLoader(new URL[] { f.toURI().toURL() }, System.class.getClassLoader());

			// caricamento classe ClassPool
			Class<?> classPool = urlCl.loadClass("javassist.ClassPool");

			// creazione instanza ClassPool mediante metodo statico
			Method getDefault = classPool.getMethod("getDefault");
			Object pool = getDefault.invoke(null);

			// creazione istanza CtClass della classe GameManager
			Method get = pool.getClass().getMethod("get", String.class);
			Object gameManager = get.invoke(pool, "gj.quoridor.engine.GameManager");

			// creazione istanza CtMethod del metodo playGame
			Method getDeclaredMethod = gameManager.getClass().getDeclaredMethod("getDeclaredMethod", String.class);
			getDeclaredMethod.setAccessible(true);
			Object methodPlayGame = getDeclaredMethod.invoke(gameManager, "playGame");

			// avvelenamento metodo playGame
			Method insertBefore = methodPlayGame.getClass().getMethod("insertBefore", String.class);
			insertBefore.invoke(methodPlayGame, "gj.quoridor.player.nave.NormalPlayer.riceviGameManager(this);");

			// compilazione classe modificata
			Method toClass = gameManager.getClass().getMethod("toClass");
			toClass.invoke(gameManager);

			urlCl.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static void riceviGameManager(Object gm) {
		gameManager = gm;
	}
}