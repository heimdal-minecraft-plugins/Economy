package commands;

import domain.Auction;
import econMain.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
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
    //auc info
    //auc time
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("You're not a player!");
            return true;
        }

        p = (Player) cs;

        auc = plugin.getAuc();

        switch (args.length) {
            case 0:
                displayHelpMessage();
                return true;
            case 1:
                switch (args[1].toLowerCase()) {
                    case "info":
                        displayHelpMessage();
                        return true;
                    case "time":
                        displayRemainingTime();
                        return true;
                }
                break;
            case 2:
                if (args[1].equalsIgnoreCase("bid")) {
                    bidOnAuction(p, args);
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    double price = Double.parseDouble(args[2]);
                    createAuction(amount, price, p);
                } catch (NumberFormatException e) {
                    displayHelpMessage(true);
                    return true;
                }

        }

        return true;
    }

    /**
     * Displays a help message.
     */
    private void displayHelpMessage(boolean error) {
        p.sendMessage(ChatColor.RED + String.format("How to use Auctions:%n%s: %s%n%s: %s%n%s: %s",
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
    private void bidOnAuction(Player p, String[] args) {
//        
//        
//        TODO
//        
//        
    }

    /**
     * Displays time left in the auction. Displays message if no auction is
     * active.
     */
    private void displayRemainingTime() {
        if (this.auc == null) {
            p.sendMessage(ChatColor.RED + "No auction going at the moment!");
            return;
        }
        long time = plugin.getAuc().getTimeLeft();
        p.sendMessage(ChatColor.BLUE + String.format("%d second%s left in this auction", time, (time == 1L) ? "" : "s"));
    }

    private void createAuction(double amount, double price, Player p) {
        
    }

}
