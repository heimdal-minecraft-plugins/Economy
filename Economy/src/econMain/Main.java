/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package econMain;

import domain.Auction;
import java.util.Map;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nick
 */
public class Main extends JavaPlugin {

    private Auction auc;

    private Map funds;

    @Override
    public void onEnable() {
        System.out.println("Hello World!");
    }

    public Auction getAuc() {
        return auc;
    }

    public void initAuction(Auction auc) {
        this.auc = auc;
    }

}
