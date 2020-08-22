package lt.Argetlahm.LootParty;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;

public class Config {

    public static File configFile;

    public static int eventTime = 600;
    public static int chestOpenLimit = 5;
    public static int eventCreateLimit = 2;

    public static int effectRepetition = 2;

    public static int maxItemCount = 4;
    public static int maxItemsPerStack = 32;
    public static int chestSpawnTime = 15;

    public static String broadcastUpdateMessage;
    public static String broadcastEventEndMessage;
    public static String noDropPartyError;
    public static String noPermissionToCreate;
    public static String noPermissionToAccess;
    public static String noItems;
    public static String alreadyStarted;
    public static String startedDropParty;
    public static String broadcastStartedDropParty;
    public static String teleportMessage;
    public static String returnMessage;
    public static String chestTaken;
    public static String limitReached;

    public static Location minLocation;
    public static Location maxLocation;
    public static Location teleportLocation;

    public Config(){
        configFile = new File(Main.plugin.getDataFolder(), "config.yml");
    }

    public void loadDefaultConfig() {
        if (configFile.exists()) return;
        Main.plugin.saveDefaultConfig();
    }


    public void readConfig(){

        eventTime = Main.plugin.getConfig().getInt("Time.eventTime");
        chestSpawnTime = Main.plugin.getConfig().getInt("Time.chestSpawnTime");
        effectRepetition = Main.plugin.getConfig().getInt("Time.effectRepetition");

        chestOpenLimit = Main.plugin.getConfig().getInt("Global.openLimit");
        eventCreateLimit = Main.plugin.getConfig().getInt("Global.eventPerDay");

        maxItemCount = Main.plugin.getConfig().getInt("Global.maxItemCount");
        maxItemsPerStack = Main.plugin.getConfig().getInt("Global.maxItemsPerStack");

        broadcastStartedDropParty = Main.plugin.getConfig().getString("Message.broadcastStartedDropParty");
        broadcastUpdateMessage = Main.plugin.getConfig().getString("Message.broadcastChestUpdate");
        broadcastEventEndMessage = Main.plugin.getConfig().getString("Message.broadcastEventEnd");

        noDropPartyError = Main.plugin.getConfig().getString("Message.noDropPartyError");
        noPermissionToCreate = Main.plugin.getConfig().getString("Message.noPermissionToCreate");
        noPermissionToAccess = Main.plugin.getConfig().getString("Message.noPermissionToAccess");
        noItems = Main.plugin.getConfig().getString("Message.noItemsAdded");
        alreadyStarted = Main.plugin.getConfig().getString("Message.alreadyStarted");
        startedDropParty = Main.plugin.getConfig().getString("Message.startedDropParty");
        teleportMessage = Main.plugin.getConfig().getString("Message.teleportMessage");
        returnMessage = Main.plugin.getConfig().getString("Message.returnMessage");
        chestTaken = Main.plugin.getConfig().getString("Message.chestTaken");
        limitReached = Main.plugin.getConfig().getString("Message.limitReached");

        String world = Main.plugin.getConfig().getString("Area.World");
        double minX, minY, minZ;
        minX = Main.plugin.getConfig().getDouble("Area.Min.X");
        minY = Main.plugin.getConfig().getDouble("Area.Min.Y");
        minZ = Main.plugin.getConfig().getDouble("Area.Min.Z");
        minLocation = new Location(Bukkit.getWorld(world), minX, minY, minZ);

        double maxX, maxY, maxZ;
        maxX = Main.plugin.getConfig().getDouble("Area.Max.X");
        maxY = Main.plugin.getConfig().getDouble("Area.Max.Y");
        maxZ = Main.plugin.getConfig().getDouble("Area.Max.Z");
        maxLocation = new Location(Bukkit.getWorld(world), maxX, maxY, maxZ);

        double teleportX, teleportY, teleportZ;
        teleportX = Main.plugin.getConfig().getDouble("Area.Teleport.X");
        teleportY = Main.plugin.getConfig().getDouble("Area.Teleport.Y");
        teleportZ = Main.plugin.getConfig().getDouble("Area.Teleport.Z");
        teleportLocation = new Location(Bukkit.getWorld(world), teleportX, teleportY, teleportZ);

    }

}
