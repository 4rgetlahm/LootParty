package lt.Argetlahm.LootParty;

import lt.Argetlahm.LootParty.Arena.Cuboid;
import lt.Argetlahm.LootParty.Menu.DropInventory;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DropPartyManager implements Listener {

    private boolean partyStarted = false;
    private DropInventory dropInventory;
    private ArrayList<ItemStack> itemList;
    private Cuboid cuboid;
    private Location teleportLocation;
    private ArrayList<Block> chestList = new ArrayList<>();
    private ArrayList<Block> chestEffectList = new ArrayList<>();
    ParticleManager particleManager;

    private Player partyMaker;

    private HashMap<Player, Location> previousLocations = new HashMap<>();
    private HashMap<Player, Integer> openChestCount = new HashMap<>();
    private HashMap<Block, Player> openChests = new HashMap<>();

    public DropPartyManager(DropInventory dropInventory){
        this.dropInventory = dropInventory;
        this.particleManager = new ParticleManager();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
        prepareArea();
    }

    private void prepareArea(){
        cuboid = new Cuboid(Main.config.minLocation, Main.config.maxLocation);
        teleportLocation = Main.config.teleportLocation;
    }

    public void teleportToArea(Player player){
        if(previousLocations.containsKey(player)){
            player.teleport(previousLocations.get(player));
            previousLocations.remove(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.returnMessage));
        } else {
            previousLocations.put(player, player.getLocation());
            player.teleport(teleportLocation);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.teleportMessage));
        }
    }

    public void endParty(){
        if(!isPartyStarted()) { return; }
        for (Map.Entry<Player, Location> entry : previousLocations.entrySet()) {
            entry.getKey().teleport(entry.getValue());
        }
        for(Block block : chestList){
            block.setType(Material.AIR);
        }
        for(ItemStack itemStack : itemList){
            if(partyMaker.getInventory().firstEmpty() != -1){
                partyMaker.getInventory().addItem(itemStack);
            } else{
                partyMaker.getLocation().getWorld().dropItem(partyMaker.getLocation(), itemStack);
            }
        }
        itemList.clear();
        chestEffectList.clear();
        chestList.clear();
        previousLocations.clear();
        openChestCount.clear();
        openChests.clear();
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.config.broadcastEventEndMessage.replace("{PLAYER}", partyMaker.getName())));
        partyStarted = false;
    }

    private void startSpawnCountdown(){
        if(!isPartyStarted()){
            return;
        }
        /*if(itemList.size() == 0){
            endParty();
        }*/
        new BukkitRunnable(){
            @Override
            public void run() {
                if(isPartyStarted() && itemList.size() > 0) {
                    spawnLootBox();
                    startSpawnCountdown();
                }
            }
        }.runTaskLater(Main.plugin, Main.config.chestSpawnTime*20);
    }

    private void startEffects(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!isPartyStarted()){ return;}
                for(Block block : chestEffectList) {
                    Location particleLocation = block.getLocation().clone();
                    particleLocation.add(0.5,0.5,0.5);
                    particleManager.spawnShape(ParticleManager.ShapeType.RING_WAVE, EnumParticle.FLAME, particleLocation.clone(), null, 1, 1);
                    particleManager.spawnShape(ParticleManager.ShapeType.LINE_UP, EnumParticle.FLAME, particleLocation.clone(), null, 1, 3);
                }
                startEffects();
            }
        }.runTaskLaterAsynchronously(Main.plugin, Main.config.effectRepetition*20);
    }

    private int countItems(){
        int itemCount = 0;
        for(ItemStack itemStack : itemList){
            itemCount += itemStack.getAmount();
        }
        return itemCount;
    }

    private void fillChest(Chest chest){
        float rollChance = (float)1/(float)itemList.size();
        System.out.println(rollChance);
        Random random = new Random();
        int itemStackCount = 0;
        ArrayList<ItemStack> removeList = new ArrayList<>();
        for(ItemStack itemStack : itemList){
            if(random.nextFloat() <= rollChance){
                ItemStack newItemStack = itemStack.clone();
                int amount = random.nextInt(itemStack.getAmount()) + 1;
                if(amount > Main.config.maxItemsPerStack){
                    amount = Main.config.maxItemsPerStack;
                }
                newItemStack.setAmount(amount);
                chest.getBlockInventory().addItem(newItemStack);
                if(itemStack.getAmount() >= amount){
                    removeList.add(itemStack);
                } else {
                    itemStack.setAmount(itemStack.getAmount()-amount);
                }
                itemStackCount++;
            }
            if(itemStackCount == Main.config.maxItemCount){
                break;
            }
        }
        // IF TO USE REMOVE ALL, POSSIBLE REMOVAL OF ALL INSTANCES OF SAME ITEMSTACK
        for(ItemStack itemStack : removeList){
            itemList.remove(itemStack);
        }
        //itemList.removeAll(removeList);
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent e){
        if(!isPartyStarted()) {return;}
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() != Material.CHEST) {
            return;
        }
        if(openChests.get(e.getClickedBlock()) != null){
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.chestTaken));
            e.setCancelled(true);
        } else{
            if(chestEffectList.contains(e.getClickedBlock())){
                if(openChestCount.get(e.getPlayer()) != null){
                    int count = openChestCount.get(e.getPlayer());
                    if(count == Main.config.chestOpenLimit){
                        e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.limitReached));
                        e.setCancelled(true);
                        return;
                    }
                    openChestCount.remove(e.getPlayer());
                    openChestCount.put(e.getPlayer(), count+1);
                } else{
                    openChestCount.put(e.getPlayer(), 1);
                }
                chestEffectList.remove(e.getClickedBlock());
                openChests.put(e.getClickedBlock(), e.getPlayer());
            }
        }
    }

    public boolean isInventoryEmpty(Inventory inv) {
        for(ItemStack item : inv.getContents()) {
            if(item != null) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent e){
        for (Map.Entry<Block, Player> entry : openChests.entrySet()) {
            if(e.getPlayer().equals(entry.getValue())){
                Chest chest = (Chest)entry.getKey().getState();
                openChests.remove(entry.getKey());
                break;
            }
        }
        if(isPartyStarted()){
            for(Block block : chestList){
                Chest chest = (Chest) block.getState();
                if(e.getInventory().equals(chest.getBlockInventory())){
                    if(isInventoryEmpty(chest.getBlockInventory())){
                        block.setType(Material.AIR);
                        chestList.remove(block);
                    }
                    break;
                }
            }
        }
    }

    private void spawnLootBox() {
        Random random = new Random();
        try {
            Location spawnLocation = new Location(cuboid.getWorld(),
                    random.nextInt(cuboid.getUpperX() - cuboid.getLowerX()) + cuboid.getLowerX(),
                    random.nextInt(cuboid.getUpperY() - cuboid.getLowerY()) + cuboid.getLowerY(),
                    random.nextInt(cuboid.getUpperZ() - cuboid.getLowerZ()) + cuboid.getLowerZ());
            while (spawnLocation.getBlock().getType() == Material.AIR) {
                spawnLocation.add(0, -1, 0);
            }
            while (spawnLocation.getBlock().getType() != Material.AIR) {
                spawnLocation.add(0, 1, 0);
            }
            spawnLocation.getBlock().setType(Material.CHEST);
            chestList.add(spawnLocation.getBlock());
            chestEffectList.add(spawnLocation.getBlock());
            System.out.println(spawnLocation.getX() + " " + spawnLocation.getY() + " " + spawnLocation.getZ());
            fillChest((Chest) spawnLocation.getBlock().getState());
        } catch (Exception e){
            System.out.println("ERROR WHILE SPAWNING CHEST");
        }
    }

    public void startParty(Player creator, ArrayList<ItemStack> itemList){
        if(isPartyStarted()){ return; }

        this.partyMaker = creator;
        this.itemList = itemList;

        for(ItemStack itemStack : itemList){
            dropInventory.getMainInventory().addItem(itemStack);
        }
        partyStarted = true;
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.config.broadcastStartedDropParty.replace("{PLAYER}", creator.getName())));
        startMessenger();
        startSpawnCountdown();
        startEffects();
        startEndCountdown();
    }

    private void startEndCountdown(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!isPartyStarted()){ return; }
                endParty();
            }
        }.runTaskLater(Main.plugin, Main.config.eventTime*20);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null){ return; }
        if(!isPartyStarted()){ return; }
        /*if(!previousLocations.containsKey(e.getWhoClicked())){
            return;
        }*/
        /*

        TODO: CHECK IF CHEST INVENTORY!!!!!!!!!!!!!!!!!!
         */
        boolean chestInventory = false;
        for(Block block : chestList){
            Chest chest = (Chest) block.getState();
            if(chest.getBlockInventory().equals(e.getInventory())){
                chestInventory = true;
            }
        }

        if(!chestInventory){
            return;
        }

        if(e.getClickedInventory().equals(e.getWhoClicked().getInventory())){
            if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || e.getAction() == InventoryAction.PICKUP_ALL
                || e.getAction() == InventoryAction.PICKUP_HALF
                || e.getAction() == InventoryAction.PICKUP_ONE
                || e.getAction() == InventoryAction.PICKUP_SOME){
                e.setCancelled(true);
            }
        }
    }

    public void updateDropInventory(){
        dropInventory.updateInventory(itemList);
    }

    public boolean isPartyStarted(){
        return partyStarted;
    }

    public void startMessenger(){

    }
}
