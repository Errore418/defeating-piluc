package gj.quoridor.player.nave;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Tool {
	private static URLClassLoader urlCl = null;

	static {
		try {
			// caricamento javassist
			File f = new File(".\\src\\gj\\quoridor\\player\\nave\\javassist.jar");
			urlCl = new URLClassLoader(new URL[] { f.toURI().toURL() }, System.class.getClassLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void poison(String mode) {
		try {
			if (mode.equals("normal")) {
				poison("gj.quoridor.engine.GameManager", "([Lgj/quoridor/player/Player;)V",
						"gj.quoridor.player.nave.NormalPlayer.acceptGameManager($0);", "playGame", true);
			} else {
				poison("gj.quoridor.engine.Board", "(II)V", "gj.quoridor.player.nave.GuiPlayer.acceptBoard($0);",
						"next", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void poison(String clazz, String desc, String src, String method, boolean isGameManager)
			throws Exception {
		Object ctClass = loadCtClass(clazz);

		Object constructor = loadCtConstructor(ctClass, desc);

		poisonCtConstructor(constructor, src);

		Object ctMethod = loadCtMethod(ctClass, method);

		if (isGameManager) {
			poisonMethodAt(ctMethod, "moves--;", 114);

			Object playTurn = loadCtMethod(ctClass, "playTurn");

			addCatch(playTurn, "{gj.quoridor.player.nave.NormalPlayer.mayDay(); return this.isWinner($1 == 0)?$1:-1;}",
					"java.lang.Exception");
		} else {
			poisonMethodBefore(ctMethod, "gj.quoridor.player.nave.GuiPlayer.restoreWallGameBoard();");
		}

		compilePoisedClass(ctClass);
	}

	private static Object loadCtClass(String name) throws Exception {
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

	private static void poisonMethodBefore(Object method, String src) throws Exception {
		// avvelenamento metodo
		method.getClass().getMethod("insertBefore", String.class).invoke(method, src);
	}

	private static void addCatch(Object method, String src, String exceptionType) throws Exception {
		// aggiunta clausola catch
		Method addCatch = method.getClass().getSuperclass().getDeclaredMethod("addCatch", String.class,
				urlCl.loadClass("javassist.CtClass"));
		addCatch.invoke(method, src, loadCtClass(exceptionType));
	}

	private static void compilePoisedClass(Object ctClass) throws Exception {
		// compilazione classe modificata
		ctClass.getClass().getMethod("toClass").invoke(ctClass);

		// chiusura ClassLoader
		urlCl.close();
	}

	public static Object retrievePrivateField(Object target, String name) throws Exception {
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.get(target);
	}

	public static void insertPrivateField(Object target, String name, Object value) throws Exception {
		Field field = target.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}

	public static Object accessArray(Object array, int... index) {
		Object buffer = array;
		for (int i = 0; i < index.length; i++) {
			buffer = Array.get(buffer, index[i]);
		}
		return buffer;
	}

}