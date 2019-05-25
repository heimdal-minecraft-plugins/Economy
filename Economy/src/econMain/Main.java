/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package econMain;

import commands.AuctionExecutor;
import domain.Auction;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
import persistence.FundMapper;

/**
 *
 * @author Nick
 */
public class Main extends JavaPlugin {

    private Auction auc;
    private final FundMapper mapper = new FundMapper(this);
    private Map<UUID, Double> funds;

    @Override
    public void onEnable() {
        loadConfig();//Loads the config file into the default config

        funds = mapper.getFunds();//Gets all the Players

        this.getCommand("auction").setAliases(Arrays.asList(new String[]{"auc"}));
        this.getCommand("auction").setExecutor(new AuctionExecutor(this));
    }

    /**
     * Returns the current Auction.
     *
     * @return CUrrent Auction. Null if there isn't one.
     */
    public Auction getAuc() {
        return auc;
    }

    /**
     * Returns the funds of the Player associated with the given UUID.
     *
     * @param id Id of the PLayer
     * @return Funds of the Player. 0 if the Player wasn't found
     */
    public double getFunds(UUID id) {
        return funds.getOrDefault(id, 0D);
    }

    /**
     * Adds funds to a Player. Use a negative value to subtract funds.
     *
     * @param id Id of the Player
     * @param value Amount to be added
     */
    public void addFunds(UUID id, double value) {
        funds.replace(id, getFunds(id) + value);
    }

    /**
     * Checks if there is an Auction going.
     *
     * @return True if there is an Auction
     */
    public boolean hasAuction() {
        return this.auc != null;
    }

    /**
     * Removes the current Auction.
     *
     * @return False if there was no auction, else true.
     */
    public boolean removeAuction() {
        if (this.auc == null)
            return false;
        this.auc = null;
        return true;
    }

    /**
     * Sets the Auction.
     *
     * @param auc Auction to set
     */
    public void setAuc(Auction auc) {
        this.auc = auc;
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);//Sets so the config should copy from the default
        saveConfig();
    }

}
