package club.tater.tatergod.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Objects;

public class ReflectionUtil {

    public static void copyOf(Object from, Object to, boolean ignoreFinal) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Class clazz = from.getClass();
        Field[] afield = clazz.getDeclaredFields();
        int i = afield.length;

        for (int j = 0; j < i; ++j) {
            Field field = afield[j];

            makePublic(field);
            if (!isStatic(field) && (!ignoreFinal || !isFinal(field))) {
                makeMutable(field);
                field.set(to, field.get(from));
            }
        }

    }

    public static void copyOf(Object from, Object to) throws NoSuchFieldException, IllegalAccessException {
        copyOf(from, to, false);
    }

    public static boolean isStatic(Member instance) {
        return (instance.getModifiers() & 8) != 0;
    }

    public static boolean isFinal(Member instance) {
        return (instance.getModifiers() & 16) != 0;
    }

    public static void makeAccessible(AccessibleObject instance, boolean accessible) {
        Objects.requireNonNull(instance);
        instance.setAccessible(accessible);
    }

    public static void makePublic(AccessibleObject instance) {
        makeAccessible(instance, true);
    }

    public static void makePrivate(AccessibleObject instance) {
        makeAccessible(instance, false);
    }

    public static void makeMutable(Member instance) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(instance);
        Field modifiers = Field.class.getDeclaredField("modifiers");

        makePublic(modifiers);
        modifiers.setInt(instance, instance.getModifiers() & -17);
    }

    public static void makeImmutable(Member instance) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(instance);
        Field modifiers = Field.class.getDeclaredField("modifiers");

        makePublic(modifiers);
        modifiers.setInt(instance, instance.getModifiers() & 16);
    }
}
