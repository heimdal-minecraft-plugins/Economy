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

    public Auction getAuc() {
        return auc;
    }

    public void initAuction(Auction auc) {
        this.auc = auc;
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);//Sets so the config should copy from the default
        saveConfig();
    }

    public double getFunds(UUID id) {
        return funds.getOrDefault(id, 0D);
    }

    public void addFunds(UUID id, double value) {
        funds.replace(id, getFunds(id) + value);
    }

    public boolean hasAuction() {
        return this.auc != null;
    }

    public boolean removeAuction() {
        if (this.auc == null)
            return false;
        this.auc = null;
        return true;
    }

    public void setAuc(Auction auc) {
        this.auc = auc;
    }
    
    

}
