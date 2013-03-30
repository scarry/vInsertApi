package org.vinsert.bot.util;

import org.vinsert.Configuration;
import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptInfo;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.ScriptType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author iJava
 */
public class ScriptLoader {

    private static List<Class<?>> classes = new ArrayList<Class<?>>();
    private static List<ScriptInfo> scripts = new ArrayList<ScriptInfo>();

    public static void loadLocal() {
        try {
            File dir = new File(Configuration.COMPILED_DIR);
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String name = file.getName().replace('/', '.').replace('\\', '.');
                    URLClassLoader loader = new URLClassLoader(new URL[]{dir.toURI().toURL()});
                    Class<?> script = loader.loadClass(name.substring(0, name.length() - 6));
                    if (script.isAnnotationPresent(ScriptManifest.class)) {
                        ScriptManifest scriptManifest = script.getAnnotation(ScriptManifest.class);
                        scripts.add(new ScriptInfo(scriptManifest.name(), scriptManifest.description(), script, scriptManifest.authors(), scriptManifest.version(), ScriptType.LOCAL));
                        continue;
                    }
                    classes.add(script);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Script loadScript(int index) {
        Script script = null;
        try {
            script = scripts.get(index).getClazz().asSubclass(Script.class).newInstance();
        }catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return script;
    }

    public static List<ScriptInfo> getScripts() {
        return scripts;
    }

    public static List<Class<?>> getClasses() {
        return classes;
    }
}
