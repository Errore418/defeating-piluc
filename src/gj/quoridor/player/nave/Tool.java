package gj.quoridor.player.nave;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Tool {
	private static URLClassLoader urlCl = null;

	public static void poison(String mode) {
		try {
			if (mode.equals("normal")) {
				poison("gj.quoridor.engine.GameManager", "([Lgj/quoridor/player/Player;)V",
						"gj.quoridor.player.nave.NormalPlayer.acceptGameManager(this);", true);
			} else {
				poison("gj.quoridor.engine.Board", "(II)V", "gj.quoridor.player.nave.GuiPlayer.acceptBoard(this);",
						false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void poison(String clazz, String desc, String src, boolean isGameManager) throws Exception {
		Object ctClass = loadCtClass(clazz);

		Object constructor = loadCtConstructor(ctClass, desc);

		poisonCtConstructor(constructor, src);

		if (isGameManager) {
			Object ctMethod = loadCtMethod(ctClass, "playGame");

			poisonMethodAt(ctMethod, "moves--;", 114);
		}

		compilePoisedClass(ctClass);
	}

	private static Object loadCtClass(String name) throws Exception {
		// caricamento javassist
		File f = new File(".\\src\\gj\\quoridor\\player\\nave\\javassist.jar");
		urlCl = new URLClassLoader(new URL[] { f.toURI().toURL() }, System.class.getClassLoader());

		// caricamento classe ClassPool
		Class<?> classPool = urlCl.loadClass("javassist.ClassPool");

		// creazione instanza ClassPool mediante metodo statico
		Object pool = classPool.getMethod("getDefault").invoke(null);

		// ritorno istanza CtClass della classe richiesta
		return pool.getClass().getMethod("get", String.class).invoke(pool, name);
	}

	private static Object loadCtConstructor(Object target, String desc) throws Exception {
		// creazione istanza CtConstructor del costruttore della classe
		Method getConstructor = target.getClass().getDeclaredMethod("getConstructor", String.class);
		getConstructor.setAccessible(true);
		return getConstructor.invoke(target, desc);
	}

	private static Object loadCtMethod(Object target, String name) throws Exception {
		// creazione istanza CtMethod del metodo richiesto
		Method getDeclaredMethod = target.getClass().getDeclaredMethod("getDeclaredMethod", String.class);
		getDeclaredMethod.setAccessible(true);
		return getDeclaredMethod.invoke(target, name);
	}

	private static void poisonCtConstructor(Object constructor, String src) throws Exception {
		// avvelenamento costruttore
		constructor.getClass().getMethod("insertAfter", String.class).invoke(constructor, src);
	}

	private static void poisonMethodAt(Object method, String src, int line) throws Exception {
		// avvelenamento metodo
		method.getClass().getMethod("insertAt", int.class, String.class).invoke(method, line, src);
	}

	private static void compilePoisedClass(Object ctClass) throws Exception {
		// compilazione classe modificata
		ctClass.getClass().getMethod("toClass").invoke(ctClass);

		// chiusura ClassLoader
		urlCl.close();
	}

}