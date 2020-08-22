package lt.Argetlahm.LootParty.Menu;

import lt.Argetlahm.LootParty.LootUtils;
import lt.Argetlahm.LootParty.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public class CreateDropInventory implements Listener {
    
    Inventory createDropInventory;
    Player creator;
    boolean continuation = false;

    public CreateDropInventory(Player player){
        this.creator = player;
        LootUtils.checkMenuItems();
        this.createDropInventory = createInventory();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }


    public Inventory createInventory(){
        Inventory inventory = Bukkit.createInventory(null, 45, ChatColor.RED + "Drop Party kūrimo meniu");

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
        inventory.setItem(42, LootUtils.returnButton);

        return inventory;
    }

    public void openInventory(Player player){
        player.openInventory(createDropInventory);
    }

    public void closeCreationInventory(){
        if(continuation){ continuation = false; return; }

        for(ItemStack itemStack : createDropInventory.getContents()){
            if(itemStack != null) {
                if (!LootUtils.isMenuItem(itemStack)) {
                    if (creator.getInventory().firstEmpty() != -1) {
                        creator.getInventory().addItem(itemStack);
                    } else {
                        creator.getLocation().getWorld().dropItem(creator.getLocation(), itemStack);
                    }
                }
            }
        }
    }

    public Inventory getInventory(){
        return createDropInventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null){
            return;
        }

        if(e.getClickedInventory().equals(createDropInventory)){
            if(e.getCurrentItem().equals(LootUtils.blackPane)){
                e.setCancelled(true);
            }
            else if(e.getCurrentItem().equals(LootUtils.returnButton)){
                Main.dropInventory.openPublicInventory((Player)e.getWhoClicked());
                e.setCancelled(true);
            } else if(e.getCurrentItem().equals(LootUtils.createButton)){
                ConfirmationInventory confirmationInventory = new ConfirmationInventory((Player)e.getWhoClicked(), this);
                confirmationInventory.openInventory((Player)e.getWhoClicked());
                continuation = true;
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryExit(InventoryCloseEvent e){
        if(e.getInventory().equals(createDropInventory)){
            new BukkitRunnable(){
                @Override
                public void run(){
                    if(!continuation) {
                        closeCreationInventory();
                        Main.dropInventory.openPublicInventory((Player) e.getPlayer());
                    }
                }
            }.runTaskLater(Main.plugin, 1L);
        }
    }
}
