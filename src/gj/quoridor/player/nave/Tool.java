package gj.quoridor.player.nave;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Tool {
	private static URLClassLoader urlCl = null;

	public static void poison(String type) {
		try {
			if (type.equals("normal")) {
				poisonGameManager();
			} else {
				poisonBoard();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void poisonGameManager() throws Exception {
		Object gameManager = loadCtClass("gj.quoridor.engine.GameManager");

		// creazione istanza CtMethod del metodo playGame
		Method getDeclaredMethod = gameManager.getClass().getDeclaredMethod("getDeclaredMethod", String.class);
		getDeclaredMethod.setAccessible(true);
		Object methodPlayGame = getDeclaredMethod.invoke(gameManager, "playGame");

		// avvelenamento metodo playGame
		Method insertBefore = methodPlayGame.getClass().getMethod("insertBefore", String.class);
		insertBefore.invoke(methodPlayGame, "gj.quoridor.player.nave.NormalPlayer.acceptGameManager(this);");

		compilePoisedClass(gameManager);
	}

	private static void poisonBoard() throws Exception {
		Object board = loadCtClass("gj.quoridor.engine.Board");

		// creazione istanza CtConstructor del costruttore
		Method getConstructor = board.getClass().getDeclaredMethod("getConstructor", String.class);
		getConstructor.setAccessible(true);
		Object constructor = getConstructor.invoke(board, "(II)V");

		// avvelenamento costruttore
		Method insertBefore = constructor.getClass().getMethod("insertAfter", String.class);
		insertBefore.invoke(constructor, "gj.quoridor.player.nave.GuiPlayer.acceptBoard(this);");

		compilePoisedClass(board);
	}

	private static Object loadCtClass(String name) throws Exception {
		// caricamento javassist
		File f = new File(".\\src\\gj\\quoridor\\player\\nave\\javassist.jar");
		urlCl = new URLClassLoader(new URL[] { f.toURI().toURL() }, System.class.getClassLoader());

		// caricamento classe ClassPool
		Class<?> classPool = urlCl.loadClass("javassist.ClassPool");

		// creazione instanza ClassPool mediante metodo statico
		Method getDefault = classPool.getMethod("getDefault");
		Object pool = getDefault.invoke(null);

		// creazione istanza CtClass della classe richiesta
		Method get = pool.getClass().getMethod("get", String.class);

		return get.invoke(pool, name);
	}

	private static void compilePoisedClass(Object ctClass) throws Exception {
		// compilazione classe modificata
		Method toClass = ctClass.getClass().getMethod("toClass");
		toClass.invoke(ctClass);

		// chiusura ClassLoader
		urlCl.close();
	}

}
