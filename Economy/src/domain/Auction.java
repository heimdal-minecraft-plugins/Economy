/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import econMain.Main;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import utils.StringUtil;

/**
 *
 * @author Nick
 */
public class Auction {

    private Player seller;
    private Player highestBidder;
    private long timeLeft;
    private long end;
    private double startingPrice;
    private double currentBid;
    private ItemStack items;
    private Timer timer;
    private TimerTask task;
    public static final long EXTRATIME = 10000;
    private Main plugin;

    public Auction(Player seller, double startingPrice, ItemStack items, long time, Main plugin) {
        this.seller = seller;
        this.startingPrice = startingPrice;
        this.items = items;
        this.timeLeft = time;
        this.plugin = plugin;
        this.task = createTimerTask();
        subtractItems();

        this.timer = new Timer();
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {

                if (highestBidder == null) {
                    seller.sendMessage(ChatColor.YELLOW + "No bidders for this auction. Returning your items.");

                }
            }

        };
    }

    public void startAuction() {
        this.end = System.currentTimeMillis() + timeLeft * 1000;

        this.timer.schedule(task, timeLeft);

        plugin.getServer().broadcastMessage(ChatColor.GOLD + String.format("New auction! %s is selling %s%s for %f%s",
                this.seller.getDisplayName(),
                ((this.items.getAmount() == 1) ? "" : String.valueOf(this.items.getAmount() + " ")),
                StringUtil.materialToReadable(this.items.getType().toString()),
                startingPrice,
                plugin.getConfig().getString("currencySymbol")
        ));
    }

    /**
     * Removes the items from the seller's inventory
     */
    private void subtractItems() {
        seller.getInventory().remove(items);
    }

    private void finishAuction() {
        //Add item to buyer
        //Remove funds from buyer
        //Add funds to seller
        //Finishing message
    }

    private void revertAuction() {
        //Add items back to seller
    }

    public long getTimeLeft() {
        return this.end - System.currentTimeMillis();
    }

    public boolean bidOnAuction(Player p, double amount) {
        if (highestBidder.equals(p)) {
            p.sendMessage(ChatColor.RED + "You're already the highest bidder!");
            return false;
        }
        if (amount <= currentBid) {//Not high enough
            p.sendMessage(ChatColor.RED + "Your bid is lower than the current bid. You have to bid more than " + currentBid + "!");
            return false;
        }

        this.currentBid = amount;
        this.highestBidder = p;

        plugin.getServer().broadcastMessage(ChatColor.GOLD + String.format("%s bid %f%s", p.getDisplayName(), amount, plugin.getConfig().getString("currencySymbol")));

        long remaining = this.end - System.currentTimeMillis();
        if (remaining <= 5000) {
            task.cancel();
            task = createTimerTask();
            end = remaining + EXTRATIME;
            timer.schedule(task, end);
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "ANTI-SNIPE! Added " + EXTRATIME / 1000 + " seconds to the timer!");
        }
        return true;
    }

}
