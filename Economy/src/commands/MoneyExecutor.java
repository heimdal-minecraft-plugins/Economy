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
 *
 * @author Nick
 */
public class MoneyExecutor implements CommandExecutor {

    private Main plugin;
    private Player p;

    public MoneyExecutor(Main plugin) {
        this.plugin = plugin;
    }

    //money
    //money info
    //money pay 'player' amount
    //money add 'player' amount
    //money transfer 'from' 'to' amount
    //money config args
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("You're not a player!");
            return true;
        }

        p = (Player) cs;

        switch (args.length) {

            case 0:
                p.sendMessage(ChatColor.GOLD + String.format("You've got %f%s.", plugin.getFunds(p.getUniqueId()), plugin.getConfig().getString("currencySymbol")));
                return true;

            case 1:
                switch (args[1].toLowerCase().trim()) {
                    //Single argument

                    case "info":
                        displayHelpMessage();
                        return true;
                    default:
                        displayHelpMessage(true);
                        return true;
                }

            case 3:
                //Triple argument
                switch (args[1].toLowerCase().trim()) {
                    case "pay":
                        if (!pay(p, args))
                            displayHelpMessage(true);
                        return true;
                    //TODO
                    //Add money
                    //Config?
                }
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

    private boolean pay(Player p, String[] args) {
        if (args.length != 3)
            return false;
        Player other = plugin.getServer().getPlayer(args[1]);
        double amount = 0D;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number!");
            return true;
        }

        if (other == null) {
            p.sendMessage(ChatColor.RED + "Player '" + args[1] + "' doesn't seem to exist!");
            return true;
        }

        if (amount < 0) {
            p.sendMessage(ChatColor.RED + "You can't send a negative amount!");
            return true;
        }

        if ((plugin.getFunds(p.getUniqueId()) - amount < 0 && !plugin.getConfig().getBoolean("negativeFundsAllowed"))) {
            p.sendMessage(ChatColor.RED + "You don't have enough money for that!");
            return true;
        }

        plugin.addFunds(p.getUniqueId(), -amount);
        p.sendMessage(ChatColor.GOLD + String.format("Sending %f%s to %s", amount, plugin.getConfig().getString("currencySymbol"), other.getDisplayName()));
        plugin.addFunds(other.getUniqueId(), amount);

        return true;
    }

}
