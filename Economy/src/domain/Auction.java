/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.Timer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Nick
 */
public class Auction {

    private Player seller;
    private Player highestBidder;
    private double timeLeft;
    private double currentBid;
    private ItemStack items;
    //private Timer timer;

    public Auction(Player seller, ItemStack items, double time) {
        this.seller = seller;
        this.items = items;
        this.timeLeft = time;
        //timer=new Timer();

    }

}
