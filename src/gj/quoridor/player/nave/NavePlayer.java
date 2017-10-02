package gj.quoridor.player.nave;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import gj.quoridor.engine.GameManager;
import gj.quoridor.player.Player;

public class NavePlayer implements Player {
	private static GameManager gameManager = null;

	public NavePlayer() {
		try {
			// caricamento javassist
			File f = new File(".\\src\\gj\\quoridor\\player\\nave\\Util.txt");
			URLClassLoader urlCl = new URLClassLoader(new URL[] { f.toURI().toURL() }, System.class.getClassLoader());

			// caricamento classe ClassPool
			Class<?> classPool = urlCl.loadClass("javassist.ClassPool");

			// creazione instanza ClassPool mediante metodo statico
			Method getDefault = classPool.getMethod("getDefault");
			Object pool = getDefault.invoke(null);

			// creazione istanza CtClass della classe GameManager
			Method get = pool.getClass().getMethod("get", new Class<?>[] { String.class });
			Object gameManager = get.invoke(pool, "gj.quoridor.engine.GameManager");

			// creazione istanza CtMethod del metodo playGame
			Method getDeclaredMethod = gameManager.getClass().getDeclaredMethod("getDeclaredMethod",
					new Class<?>[] { String.class });
			getDeclaredMethod.setAccessible(true);
			Object methodPlayGame = getDeclaredMethod.invoke(gameManager, "playGame");

			// avvelenamento metodo playGame
			Method insertBefore = methodPlayGame.getClass().getMethod("insertBefore", new Class<?>[] { String.class });
			insertBefore.invoke(methodPlayGame, "gj.quoridor.player.nave.NavePlayer.riceviGameManager(this);");

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
		return new int[] { 0, 2 };
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
				newPosition = new int[] { 8, 1 };
				index = 0;
			} else {
				newPosition = new int[] { 0, 1 };
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
		if (gm.getClass() == GameManager.class) {
			gameManager = (GameManager) gm;
		}
	}

}