package lt.Argetlahm.LootParty;

import lt.Argetlahm.LootParty.Menu.DropInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    public static Plugin plugin;
    public static Config config;

    public static DropInventory dropInventory;
    public static DropPartyManager dropPartyManager;

    public void onEnable(){
        Bukkit.getServer().getLogger().log(Level.WARNING, "[LootParty] Pluginas isijungia.");
        plugin = this;
        config = new Config();
        config.loadDefaultConfig();
        config.readConfig();
        dropInventory = new DropInventory();
        dropPartyManager = new DropPartyManager(dropInventory);
    }

    public void onDisable(){
        dropPartyManager.endParty();
        Bukkit.getServer().getLogger().log(Level.WARNING, "[LootParty] Pluginas issijungia.");
    }

    public static DropPartyManager getDropPartyManager(){
        return dropPartyManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("drop")){
            if(player.hasPermission("lootparty.see")){
                dropInventory.openPublicInventory(player);
            } else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.noPermissionToAccess));
            }
        }

        return false;
    }

}
