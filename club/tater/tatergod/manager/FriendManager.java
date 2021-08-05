package club.tater.tatergod.manager;

import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.PlayerUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager extends Feature {

    private List friends = new ArrayList();

    public FriendManager() {
        super("Friends");
    }

    public boolean isFriend(String name) {
        this.cleanFriends();
        return this.friends.stream().anyMatch(test<invokedynamic>(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return this.isFriend(player.getName());
    }

    public void addFriend(String name) {
        FriendManager.Friend friend = this.getFriendByName(name);

        if (friend != null) {
            this.friends.add(friend);
        }

        this.cleanFriends();
    }

    public void removeFriend(String name) {
        this.cleanFriends();
        Iterator iterator = this.friends.iterator();

        while (iterator.hasNext()) {
            FriendManager.Friend friend = (FriendManager.Friend) iterator.next();

            if (friend.getUsername().equalsIgnoreCase(name)) {
                this.friends.remove(friend);
                break;
            }
        }

    }

    public void onLoad() {
        this.friends = new ArrayList();
        this.clearSettings();
    }

    public void saveFriends() {
        this.clearSettings();
        this.cleanFriends();
        Iterator iterator = this.friends.iterator();

        while (iterator.hasNext()) {
            FriendManager.Friend friend = (FriendManager.Friend) iterator.next();

            this.register(new Setting(friend.getUuid().toString(), friend.getUsername()));
        }

    }

    public void cleanFriends() {
        this.friends.stream().filter(test<invokedynamic>()).filter(test<invokedynamic>());
    }

    public List getFriends() {
        this.cleanFriends();
        return this.friends;
    }

    public FriendManager.Friend getFriendByName(String input) {
        UUID uuid = PlayerUtil.getUUIDFromName(input);

        if (uuid != null) {
            FriendManager.Friend friend = new FriendManager.Friend(input, uuid);

            return friend;
        } else {
            return null;
        }
    }

    public void addFriend(FriendManager.Friend friend) {
        this.friends.add(friend);
    }

    private static boolean lambda$cleanFriends$1(FriendManager.Friend friend) {
        return friend.getUsername() != null;
    }

    private static boolean lambda$isFriend$0(String name, FriendManager.Friend friend) {
        return friend.username.equalsIgnoreCase(name);
    }

    public static class Friend {

        private final String username;
        private final UUID uuid;

        public Friend(String username, UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }

        public String getUsername() {
            return this.username;
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}
