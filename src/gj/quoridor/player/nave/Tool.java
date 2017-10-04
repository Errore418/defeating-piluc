package gj.quoridor.player.nave;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Tool {

	public static void avvelena(String type) {
		if (type.equals("normal")) {
			avvelenaGameManager();
		} else {
			avvelenaBoard();
		}
	}

	private static void avvelenaGameManager() {
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

	private static void avvelenaBoard() {
		// TODO implementare avvelenamento
	}

}
