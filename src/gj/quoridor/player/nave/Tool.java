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

		Object constructor = loadCtConstructor(gameManager, "([Lgj/quoridor/player/Player;)V");

		poisonCtConstructor(constructor, "gj.quoridor.player.nave.NormalPlayer.acceptGameManager(this);");

		compilePoisedClass(gameManager);
	}

	private static void poisonBoard() throws Exception {
		Object board = loadCtClass("gj.quoridor.engine.Board");

		Object constructor = loadCtConstructor(board, "(II)V");

		poisonCtConstructor(constructor, "gj.quoridor.player.nave.GuiPlayer.acceptBoard(this);");

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

	private static Object loadCtConstructor(Object target, String desc) throws Exception {
		// creazione istanza CtConstructor del costruttore
		Method getConstructor = target.getClass().getDeclaredMethod("getConstructor", String.class);
		getConstructor.setAccessible(true);
		return getConstructor.invoke(target, desc);
	}

	private static void poisonCtConstructor(Object constructor, String src) throws Exception {
		// avvelenamento costruttore
		Method insertAfter = constructor.getClass().getMethod("insertAfter", String.class);
		insertAfter.invoke(constructor, src);
	}

	private static void compilePoisedClass(Object ctClass) throws Exception {
		// compilazione classe modificata
		Method toClass = ctClass.getClass().getMethod("toClass");
		toClass.invoke(ctClass);

		// chiusura ClassLoader
		urlCl.close();
	}

}
