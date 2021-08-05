package club.tater.tatergod.manager;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.EnumConverter;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfigManager implements Util {

    public ArrayList features = new ArrayList();
    public String config = "tater/config/";

    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        String s = setting.getType();
        byte b0 = -1;

        switch (s.hashCode()) {
        case -1808118735:
            if (s.equals("String")) {
                b0 = 4;
            }
            break;

        case -672261858:
            if (s.equals("Integer")) {
                b0 = 3;
            }
            break;

        case 2070621:
            if (s.equals("Bind")) {
                b0 = 5;
            }
            break;

        case 2165025:
            if (s.equals("Enum")) {
                b0 = 6;
            }
            break;

        case 67973692:
            if (s.equals("Float")) {
                b0 = 2;
            }
            break;

        case 1729365000:
            if (s.equals("Boolean")) {
                b0 = 0;
            }
            break;

        case 2052876273:
            if (s.equals("Double")) {
                b0 = 1;
            }
        }

        switch (b0) {
        case 0:
            setting.setValue(Boolean.valueOf(element.getAsBoolean()));
            return;

        case 1:
            setting.setValue(Double.valueOf(element.getAsDouble()));
            return;

        case 2:
            setting.setValue(Float.valueOf(element.getAsFloat()));
            return;

        case 3:
            setting.setValue(Integer.valueOf(element.getAsInt()));
            return;

        case 4:
            String str = element.getAsString();

            setting.setValue(str.replace("_", " "));
            return;

        case 5:
            setting.setValue((new Bind.BindConverter()).doBackward(element));
            return;

        case 6:
            try {
                EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                Enum value = converter.doBackward(element);

                setting.setValue(value == null ? setting.getDefaultValue() : value);
            } catch (Exception exception) {
                ;
            }

            return;

        default:
            Tater.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
        }
    }

    private static void loadFile(JsonObject input, Feature feature) {
        Iterator iterator = input.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String settingName = (String) entry.getKey();
            JsonElement element = (JsonElement) entry.getValue();

            if (feature instanceof FriendManager) {
                try {
                    Tater.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                boolean settingFound = false;
                Iterator iterator1 = feature.getSettings().iterator();

                while (iterator1.hasNext()) {
                    Setting setting = (Setting) iterator1.next();

                    if (settingName.equals(setting.getName())) {
                        try {
                            setValueFromJson(feature, setting, element);
                        } catch (Exception exception1) {
                            exception1.printStackTrace();
                        }

                        settingFound = true;
                    }
                }

                if (settingFound) {
                    ;
                }
            }
        }

    }

    public void loadConfig(String name) {
        List files = (List) Arrays.stream((Object[]) Objects.requireNonNull((new File("tater")).listFiles())).filter(File::isDirectory).collect(Collectors.toList());

        if (files.contains(new File("tater/" + name + "/"))) {
            this.config = "tater/" + name + "/";
        } else {
            this.config = "tater/config/";
        }

        Tater.friendManager.onLoad();
        Iterator iterator = this.features.iterator();

        while (iterator.hasNext()) {
            Feature feature = (Feature) iterator.next();

            try {
                this.loadSettings(feature);
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        this.saveCurrentConfig();
    }

    public boolean configExists(String name) {
        List files = (List) Arrays.stream((Object[]) Objects.requireNonNull((new File("tater")).listFiles())).filter(File::isDirectory).collect(Collectors.toList());

        return files.contains(new File("tater/" + name + "/"));
    }

    public void saveConfig(String name) {
        this.config = "tater/" + name + "/";
        File path = new File(this.config);

        if (!path.exists()) {
            path.mkdir();
        }

        Tater.friendManager.saveFriends();
        Iterator iterator = this.features.iterator();

        while (iterator.hasNext()) {
            Feature feature = (Feature) iterator.next();

            try {
                this.saveSettings(feature);
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        this.saveCurrentConfig();
    }

    public void saveCurrentConfig() {
        File currentConfig = new File("tater/currentconfig.txt");

        try {
            FileWriter e;
            String tempConfig;

            if (currentConfig.exists()) {
                e = new FileWriter(currentConfig);
                tempConfig = this.config.replaceAll("/", "");
                e.write(tempConfig.replaceAll("tater", ""));
                e.close();
            } else {
                currentConfig.createNewFile();
                e = new FileWriter(currentConfig);
                tempConfig = this.config.replaceAll("/", "");
                e.write(tempConfig.replaceAll("tater", ""));
                e.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public String loadCurrentConfig() {
        File currentConfig = new File("tater/currentconfig.txt");
        String name = "tater";

        try {
            if (currentConfig.exists()) {
                Scanner e;

                for (e = new Scanner(currentConfig); e.hasNextLine(); name = e.nextLine()) {
                    ;
                }

                e.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return name;
    }

    public void resetConfig(boolean saveConfig, String name) {
        Iterator iterator = this.features.iterator();

        while (iterator.hasNext()) {
            Feature feature = (Feature) iterator.next();

            feature.reset();
        }

        if (saveConfig) {
            this.saveConfig(name);
        }

    }

    public void saveSettings(Feature feature) throws IOException {
        new JsonObject();
        File directory = new File(this.config + this.getDirectory(feature));

        if (!directory.exists()) {
            directory.mkdir();
        }

        String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
        Path outputFile = Paths.get(featureName, new String[0]);

        if (!Files.exists(outputFile, new LinkOption[0])) {
            Files.createFile(outputFile, new FileAttribute[0]);
        }

        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        String json = gson.toJson(this.writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile, new OpenOption[0])));

        writer.write(json);
        writer.close();
    }

    public void init() {
        this.features.addAll(Tater.moduleManager.modules);
        this.features.add(Tater.friendManager);
        String name = this.loadCurrentConfig();

        this.loadConfig(name);
        Tater.LOGGER.info("Config loaded.");
    }

    private void loadSettings(Feature feature) throws IOException {
        String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
        Path featurePath = Paths.get(featureName, new String[0]);

        if (Files.exists(featurePath, new LinkOption[0])) {
            this.loadPath(featurePath, feature);
        }
    }

    private void loadPath(Path path, Feature feature) throws IOException {
        InputStream stream = Files.newInputStream(path, new OpenOption[0]);

        try {
            loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException illegalstateexception) {
            Tater.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
            loadFile(new JsonObject(), feature);
        }

        stream.close();
    }

    public JsonObject writeSettings(Feature feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        Iterator iterator = feature.getSettings().iterator();

        while (iterator.hasNext()) {
            Setting setting = (Setting) iterator.next();

            if (setting.isEnumSetting()) {
                EnumConverter e = new EnumConverter(((Enum) setting.getValue()).getClass());

                object.add(setting.getName(), e.doForward((Enum) setting.getValue()));
            } else {
                if (setting.isStringSetting()) {
                    String e1 = (String) setting.getValue();

                    setting.setValue(e1.replace(" ", "_"));
                }

                try {
                    object.add(setting.getName(), jp.parse(setting.getValueAsString()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        return object;
    }

    public String getDirectory(Feature feature) {
        String directory = "";

        if (feature instanceof Module) {
            directory = directory + ((Module) feature).getCategory().getName() + "/";
        }

        return directory;
    }
}
