package commands;

import domain.Auction;
import econMain.Main;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Class that serves as the executor for the /auction command.
 *
 * @author Nick
 */
public class AuctionExecutor implements CommandExecutor {

    private Main plugin;
    private Player p;
    private Auction auc;

    public AuctionExecutor(Main plugin) {
        this.plugin = plugin;
    }

    //auc 'amount' 'price'
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("You're not a player!");
            return true;
        }

        p = (Player) cs;

        auc = plugin.getAuc();

        switch (args.length) {
            //auc
            case 0:
                displayHelpMessage();
                return true;
            //auc info
            //auc time
            case 1:
                switch (args[1].toLowerCase()) {
                    case "info":
                        displayHelpMessage();
                        return true;
                    case "time":
                        displayRemainingTime();
                        return true;
                    case "cancel":
                        cancelAuction(p);
                        return true;
                    default:
                        displayHelpMessage(true);
                        return true;
                }
            //auc amount price
            case 2:
                if (args[1].equalsIgnoreCase("bid")) //If it's a bid
                    return bidOnAuction(p, args);

                try {
                    int amount = Integer.parseInt(args[1]);
                    double price = Double.parseDouble(args[2]);

                    if (!plugin.hasAuction()) {
                        p.sendMessage(ChatColor.RED + "No auction going at the moment!");
                        return true;
                    }
                    createAuction(amount, price, p);
                } catch (NumberFormatException e) {
                    displayHelpMessage(true);
                }
                return true;

        }

        return true;
    }

    /**
     * Displays a help message.
     */
    private void displayHelpMessage(boolean isError) {
        p.sendMessage((isError ? ChatColor.RED : ChatColor.BLUE) + String.format("How to use Auctions:%n%s: %s%n%s: %s%n%s: %s",
                "/auction [amount] [price]",
                "Auctions the amount specified of the item you're currently holding for the specified price",
                "/auction info",
                "Shows information about the current auction.",
                "/auction time",
                "Shows the remaining time of the current auction."));
    }

    /**
     * Not an error
     */
    private void displayHelpMessage() {
        displayHelpMessage(false);
    }

    /**
     * Handles the event of a Player bidding on an auction.
     *
     * @param p Player
     * @param args Arguments
     */
    private boolean bidOnAuction(Player p, String[] args) {
        try {
            return plugin.getAuc().bidOnAuction(p, Double.parseDouble(args[2]));//If the last argument isn't a valid number, throws error
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Displays time left in the auction. Displays message if no auction is
     * active.
     */
    private void displayRemainingTime() {
        if (!plugin.hasAuction()) {
            p.sendMessage(ChatColor.RED + "No auction going at the moment!");
            return;
        }
        long time = plugin.getAuc().getTimeLeft();
        p.sendMessage(ChatColor.BLUE + String.format("%d second%s left in this auction", time, (time == 1L) ? "" : "s"));
    }

    private void createAuction(int amount, double price, Player p) {
        PlayerInventory inven = p.getInventory();
        ItemStack item = inven.getItemInMainHand();
        int max = item.getMaxStackSize();
        if (!inven.containsAtLeast(item, amount)) {
            p.sendMessage(ChatColor.RED + "You don't have enough of that item!");
            return;
        }

        List<ItemStack> items = new ArrayList<>();

        if (amount == 1)
            items.add(item);
        else {
            int temp = amount;
            while (temp > 0) {
                int min = Math.max(max, temp);
                items.add(new ItemStack(item.getType(), min));
                temp -= min;
            }
        }

        plugin.setAuc(new Auction(p, price, items, plugin.getConfig().getLong("auctionTime") * 1000, plugin));
    }

    private void cancelAuction(Player p) {
        if (!plugin.hasAuction()) {
            p.sendMessage(ChatColor.RED + "No auction going at the moment!");
            return;
        }
        if (!p.getUniqueId().equals(plugin.getAuc().getSeller().getUniqueId()) || p.hasPermission("economy.cancel")) {;
            p.sendMessage(ChatColor.RED + "You can't cancel someone else's auction!");
            return;
        }

        plugin.getAuc().cancel();
    }

}
