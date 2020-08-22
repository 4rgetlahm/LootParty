package lt.Argetlahm.LootParty.Menu;

import lt.Argetlahm.LootParty.Config;
import lt.Argetlahm.LootParty.LootUtils;
import lt.Argetlahm.LootParty.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;

public class DropInventory implements Listener {

    Player creator;
    Inventory mainInventory;

    public DropInventory(){
        LootUtils.checkMenuItems();
        this.mainInventory = createInventory();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }


    public Inventory createInventory(){
        Inventory inventory = Bukkit.createInventory(null, 45, ChatColor.LIGHT_PURPLE + "Drop Party meniu");

        for(int i = 0; i != 8; i++){
            inventory.setItem(i, LootUtils.blackPane);
        }
        for(int i = 35; i != 45; i++){
            inventory.setItem(i, LootUtils.blackPane);
        }
        for(int i = 9; i < 45; i+=9){
            inventory.setItem(i, LootUtils.blackPane);
            inventory.setItem(i-1, LootUtils.blackPane);
        }

        inventory.setItem(38, LootUtils.createButton);
        inventory.setItem(40, LootUtils.teleportButton);
        inventory.setItem(42, LootUtils.declineButton);

        return inventory;
    }

    public void updateInventory(ArrayList<ItemStack> itemList){
        for(ItemStack itemStack : getMainInventory().getContents()){
            if(!LootUtils.isMenuItem(itemStack)){
                getMainInventory().remove(itemStack);
            }
        }
        for(ItemStack itemStack : itemList){
            getMainInventory().addItem(itemStack);
        }
    }

    public void updateInventory(){
        if(Main.getDropPartyManager().isPartyStarted()){
            Main.getDropPartyManager().updateDropInventory();
        }
    }

    public Inventory getMainInventory(){
        return mainInventory;
    }

    public void openPublicInventory(Player player){
        updateInventory();
        if(player.getOpenInventory() != null){
            player.closeInventory();
        }
        player.openInventory(mainInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null){
            return;
        }

        Player player = (Player) e.getWhoClicked();
        if(e.getInventory().equals(mainInventory)) {
            if(e.getCurrentItem().equals(LootUtils.blackPane)){
            }
            else if(e.getCurrentItem().equals(LootUtils.declineButton)){
                e.getWhoClicked().closeInventory();
            } else if(e.getCurrentItem().equals(LootUtils.teleportButton)){
                if(Main.getDropPartyManager().isPartyStarted()){
                    Main.getDropPartyManager().teleportToArea(player);
                } else{
                    e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.noDropPartyError));
                }
            } else if(e.getCurrentItem().equals(LootUtils.createButton)){
                if(e.getWhoClicked().hasPermission("lootparty.manage")) {
                    if(!Main.getDropPartyManager().isPartyStarted()){
                        CreateDropInventory dropCreationInventory = new CreateDropInventory((Player)e.getWhoClicked());
                        dropCreationInventory.openInventory((Player)e.getWhoClicked());
                    } else{
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.alreadyStarted));
                    }
                }
                else{
                    e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.noPermissionToCreate));
                }
            }
            e.setCancelled(true);
            player.updateInventory();
        }
    }


}
