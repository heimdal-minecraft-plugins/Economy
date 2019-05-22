/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import econMain.Main;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author Nick
 */
public class FundMapper {

    private Main plugin;
    private File datafolder;

    public FundMapper(Main plugin) {
        this.plugin = plugin;
        datafolder = plugin.getDataFolder();
        if (datafolder == null)
            datafolder.mkdir();
    }

    public Map<UUID, Double> getFunds() {
        Map<UUID, Double> funds = new HashMap<>();
        List<UUID> players = (List) plugin.getServer().getOnlinePlayers();

        Arrays.asList(datafolder.listFiles()).forEach(s -> {
            UUID temp = UUID.fromString(s.getName());
            if (players.contains(temp)) {
                funds.put(temp, readFile(s));
            }

        });

        return funds;
    }

    private Double readFile(File s) {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(s))) {
            return stream.readDouble();
        } catch (Exception e) {
            return 0D;
        }
    }

}
