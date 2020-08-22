package lt.Argetlahm.LootParty;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class LootUtils {

    public static ItemStack blackPane = null;
    public static ItemStack confirmationGlass = null;
    public static ItemStack declineGlass = null;
    public static ItemStack createButton = null;
    public static ItemStack returnButton = null;
    public static ItemStack declineButton = null;
    public static ItemStack teleportButton = null;

    public static void generateMenu(){
        blackPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
        ItemMeta blackPaneMeta = blackPane.getItemMeta();
        blackPaneMeta.setDisplayName(" ");
        blackPaneMeta.setLore(Collections.emptyList());
        blackPane.setItemMeta(blackPaneMeta);

        confirmationGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
        ItemMeta confirmationGlassMeta = confirmationGlass.getItemMeta();
        confirmationGlassMeta.setDisplayName(ChatColor.GREEN + "PATVIRTINTI");
        confirmationGlassMeta.setLore(Collections.emptyList());
        confirmationGlass.setItemMeta(confirmationGlassMeta);

        declineGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
        ItemMeta declineGlassMeta = declineGlass.getItemMeta();
        declineGlassMeta.setDisplayName(ChatColor.RED + "ATŠAUKTI");
        declineGlassMeta.setLore(Collections.emptyList());
        declineGlass.setItemMeta(declineGlassMeta);

        createButton = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
        ItemMeta createButtonMeta = createButton.getItemMeta();
        createButtonMeta.setDisplayName(ChatColor.GREEN + "KURTI DROP PARTY");
        createButton.setItemMeta(createButtonMeta);

        declineButton = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
        ItemMeta declineButtonMeta = declineButton.getItemMeta();
        declineButtonMeta.setDisplayName(ChatColor.RED + "IŠEITI");
        declineButton.setItemMeta(declineButtonMeta);

        teleportButton = new ItemStack(Material.BED, 1);
        ItemMeta teleportButtonMeta = teleportButton.getItemMeta();
        teleportButtonMeta.setDisplayName(ChatColor.WHITE + "EITI/IŠEITI Į/IŠ DROP PARTY");
        teleportButton.setItemMeta(teleportButtonMeta);

        returnButton = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
        ItemMeta returnButtonMeta = returnButton.getItemMeta();
        returnButtonMeta.setDisplayName(ChatColor.RED + "GRĮŽTI");
        returnButton.setItemMeta(returnButtonMeta);

    }

    public static boolean isMenuItem(ItemStack itemStack){
        if(itemStack == null){
            return true;
        }
        if(itemStack.equals(blackPane) || itemStack.equals(createButton) || itemStack.equals(declineButton) || itemStack.equals(returnButton) ||
        itemStack.equals(teleportButton) || itemStack.equals(confirmationGlass) || itemStack.equals(declineGlass)){
            return true;
        }
        return false;
    }

    public static void checkMenuItems(){
        if(blackPane == null || createButton == null || declineButton == null || returnButton == null || teleportButton == null
                || confirmationGlass == null || declineGlass == null){
            generateMenu();
        }
    }


}
