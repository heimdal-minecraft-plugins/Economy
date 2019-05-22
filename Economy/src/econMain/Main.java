/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package econMain;

import domain.Auction;
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

}
