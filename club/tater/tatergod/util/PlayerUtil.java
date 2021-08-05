package club.tater.tatergod.util;

import club.tater.tatergod.features.command.Command;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Predicate;
import javax.net.ssl.HttpsURLConnection;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;

public class PlayerUtil implements Util {

    private static final JsonParser PARSER = new JsonParser();

    public static String getNameFromUUID(UUID uuid) {
        try {
            PlayerUtil.lookUpName e = new PlayerUtil.lookUpName(uuid);
            Thread thread = new Thread(e);

            thread.start();
            thread.join();
            return e.getName();
        } catch (Exception exception) {
            return null;
        }
    }

    public static String getNameFromUUID(String uuid) {
        try {
            PlayerUtil.lookUpName e = new PlayerUtil.lookUpName(uuid);
            Thread thread = new Thread(e);

            thread.start();
            thread.join();
            return e.getName();
        } catch (Exception exception) {
            return null;
        }
    }

    public static UUID getUUIDFromName(String name) {
        try {
            PlayerUtil.lookUpUUID e = new PlayerUtil.lookUpUUID(name);
            Thread thread = new Thread(e);

            thread.start();
            thread.join();
            return e.getUUID();
        } catch (Exception exception) {
            return null;
        }
    }

    public static String requestIDs(String data) {
        try {
            String e = "https://api.mojang.com/profiles/minecraft";
            URL url = new URL(e);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();

            os.write(data.getBytes(StandardCharsets.UTF_8));
            os.close();
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            String res = convertStreamToString(in);

            in.close();
            conn.disconnect();
            return res;
        } catch (Exception exception) {
            return null;
        }
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = (new Scanner(is)).useDelimiter("\\A");

        return s.hasNext() ? s.next() : "/";
    }

    public static List getHistoryOfNames(UUID id) {
        try {
            JsonArray ignored = getResources(new URL("https://api.mojang.com/user/profiles/" + getIdNoHyphens(id) + "/names"), "GET").getAsJsonArray();
            ArrayList temp = Lists.newArrayList();
            Iterator iterator = ignored.iterator();

            while (iterator.hasNext()) {
                JsonElement e = (JsonElement) iterator.next();
                JsonObject node = e.getAsJsonObject();
                String name = node.get("name").getAsString();
                long changedAt = node.has("changedToAt") ? node.get("changedToAt").getAsLong() : 0L;

                temp.add(name + "Ã‚Â§8" + new Date(changedAt));
            }

            Collections.sort(temp);
            return temp;
        } catch (Exception exception) {
            return null;
        }
    }

    public static String getIdNoHyphens(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    private static JsonElement getResources(URL url, String request) throws Exception {
        return getResources(url, request, (JsonElement) null);
    }

    private static JsonElement getResources(URL url, String request, JsonElement element) throws Exception {
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(request);
            connection.setRequestProperty("Content-Type", "application/json");
            if (element != null) {
                DataOutputStream scanner = new DataOutputStream(connection.getOutputStream());

                scanner.writeBytes(AdvancementManager.GSON.toJson(element));
                scanner.close();
            }

            Scanner scanner1 = new Scanner(connection.getInputStream());
            StringBuilder builder = new StringBuilder();

            while (scanner1.hasNextLine()) {
                builder.append(scanner1.nextLine());
                builder.append('\n');
            }

            scanner1.close();
            String json = builder.toString();
            JsonElement data = PlayerUtil.PARSER.parse(json);
            JsonElement jsonelement = data;

            return jsonelement;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }
    }

    public static class lookUpName implements Runnable {

        private final String uuid;
        private final UUID uuidID;
        private volatile String name;

        public lookUpName(String input) {
            this.uuid = input;
            this.uuidID = UUID.fromString(input);
        }

        public lookUpName(UUID input) {
            this.uuidID = input;
            this.uuid = input.toString();
        }

        public void run() {
            this.name = this.lookUpName();
        }

        public String lookUpName() {
            EntityPlayer player = null;

            if (Util.mc.world != null) {
                player = Util.mc.world.getPlayerEntityByUUID(this.uuidID);
            }

            if (player == null) {
                String url = "https://api.mojang.com/user/profiles/" + this.uuid.replace("-", "") + "/names";

                try {
                    String exception = IOUtils.toString(new URL(url));

                    if (exception.contains(",")) {
                        List names = Arrays.asList(exception.split(","));

                        Collections.reverse(names);
                        return ((String) names.get(1)).replace("{\"name\":\"", "").replace("\"", "");
                    } else {
                        return exception.replace("[{\"name\":\"", "").replace("\"}]", "");
                    }
                } catch (IOException ioexception) {
                    ioexception.printStackTrace();
                    return null;
                }
            } else {
                return player.getName();
            }
        }

        public String getName() {
            return this.name;
        }
    }

    public static class lookUpUUID implements Runnable {

        private final String name;
        private volatile UUID uuid;

        public lookUpUUID(String name) {
            this.name = name;
        }

        public void run() {
            NetworkPlayerInfo profile;

            try {
                ArrayList s = new ArrayList(((NetHandlerPlayClient) Objects.requireNonNull(Util.mc.getConnection())).getPlayerInfoMap());

                profile = (NetworkPlayerInfo) s.stream().filter((networkPlayerInfo) -> {
                    return networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(this.name);
                }).findFirst().orElse((Object) null);

                assert profile != null;

                this.uuid = profile.getGameProfile().getId();
            } catch (Exception exception) {
                profile = null;
            }

            if (profile == null) {
                Command.sendMessage("Player isn\'t online. Looking up UUID..");
                String s1 = PlayerUtil.requestIDs("[\"" + this.name + "\"]");

                if (s1 != null && !s1.isEmpty()) {
                    JsonElement element = (new JsonParser()).parse(s1);

                    if (element.getAsJsonArray().size() == 0) {
                        Command.sendMessage("Couldn\'t find player ID. (1)");
                    } else {
                        try {
                            String e = element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();

                            this.uuid = UUIDTypeAdapter.fromString(e);
                        } catch (Exception exception1) {
                            exception1.printStackTrace();
                            Command.sendMessage("Couldn\'t find player ID. (2)");
                        }
                    }
                } else {
                    Command.sendMessage("Couldn\'t find player ID. Are you connected to the internet? (0)");
                }
            }

        }

        public UUID getUUID() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }
    }
}
