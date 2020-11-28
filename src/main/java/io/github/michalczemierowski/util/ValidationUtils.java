package io.github.michalczemierowski.util;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValidationUtils {
    public static HashMap<String, String> languageShortcutNameMap;
    static {
        languageShortcutNameMap = new HashMap<>();
        languageShortcutNameMap.put("assembly_x86", "Assembly X86");
        languageShortcutNameMap.put("batchfile", "Batch");
        languageShortcutNameMap.put("c_cpp", "C/C++");
        languageShortcutNameMap.put("csharp", "C#");
        languageShortcutNameMap.put("css", "CSS");
        languageShortcutNameMap.put("django", "Django");
        languageShortcutNameMap.put("dockerfile", "Docker");
        languageShortcutNameMap.put("elixir", "Elixir");
        languageShortcutNameMap.put("fsharp", "F#");
        languageShortcutNameMap.put("glsl", "GLSL");
        languageShortcutNameMap.put("golang", "Go");
        languageShortcutNameMap.put("html", "HTML");
        languageShortcutNameMap.put("html_elixir", "HTML Elixir");
        languageShortcutNameMap.put("html_ruby", "HTML Ruby");
        languageShortcutNameMap.put("java", "Java");
        languageShortcutNameMap.put("javascript", "Java Script");
        languageShortcutNameMap.put("json", "JSON");
        languageShortcutNameMap.put("julia", "Julia");
        languageShortcutNameMap.put("kotlin", "Kotlin");
        languageShortcutNameMap.put("lua", "Lua");
        languageShortcutNameMap.put("markdown", "Markdown");
        languageShortcutNameMap.put("matlab", "Matlab");
        languageShortcutNameMap.put("objectivec", "Objective C");
        languageShortcutNameMap.put("pgsql", "pgSQL");
        languageShortcutNameMap.put("php", "PHP");
        languageShortcutNameMap.put("plain_text", "Plain Text");
        languageShortcutNameMap.put("powershell", "Powershell");
        languageShortcutNameMap.put("python", "Python");
        languageShortcutNameMap.put("ruby", "Ruby");
        languageShortcutNameMap.put("rust", "Rust");
        languageShortcutNameMap.put("scala", "Scala");
        languageShortcutNameMap.put("sh", "SH");
        languageShortcutNameMap.put("sql", "SQL");
        languageShortcutNameMap.put("sqlserver", "SQL Server");
        languageShortcutNameMap.put("swift", "Swift");
        languageShortcutNameMap.put("typescript", "Type Script");
        languageShortcutNameMap.put("xml", "XML");
        languageShortcutNameMap.put("yaml", "YAML");
    }

    public static String DEFAULT_LANGUAGE = "plain_text";

    public static boolean isLanguageShortcutCorrect(String shortcut)
    {
        if(shortcut == null || shortcut.isEmpty())
            return false;

        return languageShortcutNameMap.containsKey(shortcut);
    }

    public static boolean isNameCorrect(String name)
    {
        // TODO
        return true;
    }
}
