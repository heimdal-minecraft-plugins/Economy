package domain;

import econMain.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import utils.StringUtil;

/**
 * Object that handles auctions.
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
    private List<ItemStack> items;
    private int amount;
    private Timer timer;
    private TimerTask task;
    private Main plugin;
    private String currency;

    public static final long EXTRATIME = 10000;

    /**
     * Creates a new Auction.
     *
     * @param seller Player that sells the item(s)
     * @param startingPrice Starting price
     * @param items Items to sell
     * @param time Duration of the Auction
     * @param plugin Main object
     */
    public Auction(Player seller, double startingPrice, List<ItemStack> items, long time, Main plugin) {
        this.seller = seller;
        this.startingPrice = startingPrice;

        this.items = items;
        this.amount = this.items.stream().mapToInt(ItemStack::getAmount).sum();

        this.timeLeft = time;
        this.plugin = plugin;
        this.currency = plugin.getConfig().getString("currencySymbol");

        this.task = createTimerTask();
        subtractItems();
        resortItemStacks();

        this.timer = new Timer();
    }

    /**
     * Creates the task executed when the auction ends.
     *
     * @return TimerTask
     */
    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (highestBidder == null) {//If no one has bid
                    seller.sendMessage(ChatColor.YELLOW + "No bidders for this auction. Returning your items.");
                    addItemsToInventory(seller);//Returns items
                    return;
                }
                finishAuction();//Finishes the auctions

                timer.cancel();//Kills the timer
                timer.purge();//Purges cancelled tasks
            }
        };
    }

    /**
     * Starts this Auction. A timer runs for a given timer. When the Timer ends,
     * the items are either sold (when someone bought them) or returned.
     */
    public void startAuction() {
        this.end = System.currentTimeMillis() + timeLeft * 1000;

        plugin.getServer().broadcastMessage(ChatColor.GOLD + String.format("New auction!%n%s is selling %d %s for %f%s!", //Nick is selling 100 Dirt for 1H
                this.seller.getDisplayName(),//Seller
                this.amount,//Amount of items sold
                StringUtil.materialToReadable(this.items.get(0).getType().toString()),//Material in Hand
                startingPrice,//Price to start bidding
                currency//Currency symbol
        ));

        this.timer.schedule(task, timeLeft);
    }

    /**
     * Removes the items from the seller's inventory
     */
    private void subtractItems() {
        items.forEach(s -> seller.getInventory().remove(s));
    }

    /**
     * Sorts the provided item stacks so that there is a minimal amount of
     * stacks.
     */
    private void resortItemStacks() {
        if (items.size() == 1)//Single stack doesn't need to be resorted
            return;
        List<ItemStack> list = new ArrayList<>();//Temp storage
        int max = this.items.get(0).getMaxStackSize();//Max stack size
        Material mat = this.items.get(0).getType();//Material of the stack
        int temp = this.amount;//Total amount of items

        while (temp > 0) {//While there are still items
            int min = Math.max(temp, max);//If more items than max stack size, max stack size. Else remaining items
            list.add(new ItemStack(mat, min));//Add it to the list
            temp -= min;//Decrement
        }

        this.items = list;//Sorted list
    }

    /**
     * Everything that happens when an auction finishes with a bidder
     */
    private void finishAuction() {
        //Add item to buyer
        addItemsToInventory(highestBidder);
        //Remove funds from buyer
        plugin.addFunds(highestBidder.getUniqueId(), -currentBid);
        //Add funds to seller
        plugin.addFunds(seller.getUniqueId(), currentBid);
        //Finishing message
        plugin.getServer().broadcastMessage(ChatColor.GOLD + String.format("Auction finished!%nItem%s sold to %s for %f%s!",
                this.amount == 1 ? "" : "s",
                highestBidder.getDisplayName(),
                currentBid,
                currency
        ));
    }

    /**
     * Adds all the items to the provided player's Inventory. Drops the items
     * that don't fit on the ground near the player.
     *
     * @param p Player
     */
    private void addItemsToInventory(Player p) {
        Map<Integer, ItemStack> rem = p.getInventory().addItem(this.items.stream().toArray(ItemStack[]::new));//Adds all the items that fit in the inventory
        p.sendMessage(ChatColor.BLUE + "Adding the items to your inventory. Items that don't fit will be dropped at your feet.");
        if (rem.isEmpty())//If all items fit, return
            return;

        World world = p.getWorld();//Current world of the player
        Location loc = p.getLocation();//Current location of the player
        rem.values().stream().forEach(s -> world.dropItem(loc, s));//Drops the remaining location 
    }

    /**
     * Returns the remaining time left in seconds.
     *
     * @return Remaining time in seconds
     */
    public long getTimeLeft() {
        return (this.end - System.currentTimeMillis()) / 1000;
    }

    /**
     * Bids on this Auction. When the Player is already the highest bidder or
     * the Player has insufficient funds when they can't go negative, the bid is
     * rejected.
     *
     * @param p Player that bids
     * @param amount Price
     * @return True if the bid was accepted, else false
     */
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
