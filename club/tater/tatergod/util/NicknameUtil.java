package club.tater.tatergod.util;

import java.util.HashMap;
import java.util.Map;

public class NicknameUtil {

    private static final Map nicknames = new HashMap();

    public static void addNickname(String name, String nick) {
        NicknameUtil.nicknames.put(name, nick);
    }

    public static void removeNickname(String name) {
        NicknameUtil.nicknames.remove(name);
    }

    public static String getNickname(String name) {
        return (String) NicknameUtil.nicknames.get(name);
    }

    public static boolean hasNickname(String name) {
        return NicknameUtil.nicknames.containsKey(name);
    }

    public static Map getAllNicknames() {
        return NicknameUtil.nicknames;
    }
}
