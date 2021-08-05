package club.tater.tatergod.manager;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.modules.Module;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager extends Feature {

    private final Path base = this.getMkDirectory(this.getRoot(), new String[] { "tater"});
    private final Path config;

    public FileManager() {
        this.config = this.getMkDirectory(this.base, new String[] { "config"});
        this.getMkDirectory(this.base, new String[] { "pvp"});
        Iterator iterator = Tater.moduleManager.getCategories().iterator();

        while (iterator.hasNext()) {
            Module.Category category = (Module.Category) iterator.next();

            this.getMkDirectory(this.config, new String[] { category.getName()});
        }

    }

    public static boolean appendTextFile(String data, String file) {
        try {
            Path e = Paths.get(file, new String[0]);

            Files.write(e, Collections.singletonList(data), StandardCharsets.UTF_8, new OpenOption[] { Files.exists(e, new LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE});
            return true;
        } catch (IOException ioexception) {
            System.out.println("WARNING: Unable to write file: " + file);
            return false;
        }
    }

    public static List readTextFileAllLines(String file) {
        try {
            Path e = Paths.get(file, new String[0]);

            return Files.readAllLines(e, StandardCharsets.UTF_8);
        } catch (IOException ioexception) {
            System.out.println("WARNING: Unable to read file, creating new file: " + file);
            appendTextFile("", file);
            return Collections.emptyList();
        }
    }

    private String[] expandPath(String fullPath) {
        return fullPath.split(":?\\\\\\\\|\\/");
    }

    private Stream expandPaths(String... paths) {
        return Arrays.stream(paths).map(this::expandPath).flatMap(Arrays::stream);
    }

    private Path lookupPath(Path root, String... paths) {
        return Paths.get(root.toString(), paths);
    }

    private Path getRoot() {
        return Paths.get("", new String[0]);
    }

    private void createDirectory(Path dir) {
        try {
            if (!Files.isDirectory(dir, new LinkOption[0])) {
                if (Files.exists(dir, new LinkOption[0])) {
                    Files.delete(dir);
                }

                Files.createDirectories(dir, new FileAttribute[0]);
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    private Path getMkDirectory(Path parent, String... paths) {
        if (paths.length < 1) {
            return parent;
        } else {
            Path dir = this.lookupPath(parent, paths);

            this.createDirectory(dir);
            return dir;
        }
    }

    public Path getBasePath() {
        return this.base;
    }

    public Path getBaseResolve(String... paths) {
        String[] names = (String[]) this.expandPaths(paths).toArray((x$0) -> {
            return new String[x$0];
        });

        if (names.length < 1) {
            throw new IllegalArgumentException("missing path");
        } else {
            return this.lookupPath(this.getBasePath(), names);
        }
    }

    public Path getMkBaseResolve(String... paths) {
        Path path = this.getBaseResolve(paths);

        this.createDirectory(path.getParent());
        return path;
    }

    public Path getConfig() {
        return this.getBasePath().resolve("config");
    }

    public Path getCache() {
        return this.getBasePath().resolve("cache");
    }

    public Path getMkBaseDirectory(String... names) {
        return this.getMkDirectory(this.getBasePath(), new String[] { (String) this.expandPaths(names).collect(Collectors.joining(File.separator))});
    }

    public Path getMkConfigDirectory(String... names) {
        return this.getMkDirectory(this.getConfig(), new String[] { (String) this.expandPaths(names).collect(Collectors.joining(File.separator))});
    }
}
