package lt.Argetlahm.LootParty.Menu;

import lt.Argetlahm.LootParty.LootUtils;
import lt.Argetlahm.LootParty.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfirmationInventory implements Listener {

    Player player;
    CreateDropInventory createDropInventory;
    Inventory inventory;

    public ConfirmationInventory(Player player, CreateDropInventory createDropInventory){
        LootUtils.checkMenuItems();
        this.player = player;
        this.createDropInventory = createDropInventory;
        this.inventory = createInventory();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }

    public Inventory createInventory(){
        Inventory inventory = Bukkit.createInventory(null, 45, ChatColor.RED + "Drop Party patvirtinimas");

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
        for(int i = 4; i < 39; i+=9){
            inventory.setItem(i, LootUtils.blackPane);
        }

        for(int i = 10; i != 13; i++){
            inventory.setItem(i, LootUtils.confirmationGlass);
        }
        for(int i = 19; i != 22; i++){
            inventory.setItem(i, LootUtils.confirmationGlass);
        }
        for(int i = 28; i != 31; i++){
            inventory.setItem(i, LootUtils.confirmationGlass);
        }

        for(int i = 14; i != 17; i++){
            inventory.setItem(i, LootUtils.declineGlass);
        }
        for(int i = 23; i != 26; i++){
            inventory.setItem(i, LootUtils.declineGlass);
        }
        for(int i = 32; i != 35; i++){
            inventory.setItem(i, LootUtils.declineGlass);
        }

        return inventory;
    }

    public void openInventory(Player player){
        player.openInventory(inventory);
    }

    public void returnBack(){
        createDropInventory.openInventory(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null){
            return;
        }
        if(e.getInventory().equals(inventory)){
            if(e.getCurrentItem().equals(LootUtils.confirmationGlass)){
                ArrayList<ItemStack> dropItems = new ArrayList<>();
                for(ItemStack itemStack : createDropInventory.getInventory().getContents()){
                    if(itemStack != null) {
                        if (!LootUtils.isMenuItem(itemStack)) {
                            dropItems.add(itemStack);
                        }
                    }
                }
                if(dropItems.size() == 0){
                    returnBack();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.config.noItems));
                } else{
                    if(!Main.getDropPartyManager().isPartyStarted()){
                        player.closeInventory();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.startedDropParty));
                        Main.getDropPartyManager().startParty(player, dropItems);
                    } else{
                        returnBack();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.alreadyStarted));
                    }
                }
            } else if(e.getCurrentItem().equals(LootUtils.declineGlass)){
                returnBack();
            }
            e.setCancelled(true);
        }
    }

}
