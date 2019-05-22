package commands;

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

    public AuctionExecutor(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("You're not a player!");
            return true;
        }

        p = (Player) cs;

        switch (args.length) {
            case 0:
                displayHelpMessage();
                return true;
            case 1:
                switch(args[1]){
                    //WIP
                }
        }

        return true;
    }

    //auc 'amount' 'price'
    //auc info
    //auc time
    //
    private void displayHelpMessage() {
        p.sendMessage(ChatColor.RED + String.format("Did you mean?%n%s: %s%n%s: %s%n%s: %s",
                "/auction [amount] [price]",
                "Auctions the amount specified of the item you're currently holding for the specified price",
                "/auction info",
                "Shows information about the current auction.",
                "/auction time",
                "Shows the remaining time of the current auction."));
    }

}
