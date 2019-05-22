/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import econMain.Main;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

        //Get all values stored in files
        Arrays.asList(datafolder.listFiles()).forEach(s -> {
            UUID temp = UUID.fromString(s.getName());
            if (players.contains(temp)) {
                funds.put(temp, readFile(s));
            }
        });

        //Add 0 values for players not stored in funds
        players.removeAll(funds.keySet());
        players.forEach(s -> funds.put(s, 0D));

        return funds;
    }

    /**
     * Updates the Fund value in a Player's file. This overwrites the file so
     * the value needs to be absolute.
     *
     * @param id UUID of the Player
     * @param value New value
     * @throws IllegalArgumentException When a negative value is set but that's
     * not allowed.
     */
    public void changeFunds(UUID id, double value) throws IllegalArgumentException {
        if (value < 0 && !plugin.getConfig().getBoolean("negativeFundsAllowed"))//If value is negative but negative values aren't allowed
            throw new IllegalArgumentException("Negative funds not allowed");
        List<File> files = Arrays.asList(datafolder.listFiles());

        File f = (files.stream().anyMatch(s -> s.getName().equals(id.toString())))
                ? files.get(files.stream().map(File::getName).collect(Collectors.toList()).indexOf(id.toString()))
                : new File(datafolder, id.toString());

        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(f, false))) {
            stream.writeDouble(value);
        } catch (Exception e) {
            Logger.getLogger(FundMapper.class.getName()).log(Level.SEVERE, "File could not be overwritten");
        }
    }

    /**
     * Updates the Fund value in a Player's file. Sets the value to the default
     * value. Used to add files for new Players.
     *
     * @param id Id of the Player
     * @throws IllegalArgumentException When a negative value is set but that's
     */
    public void changeFunds(UUID id) throws IllegalArgumentException {
        changeFunds(id, this.plugin.getConfig().getDouble("startFunds"));
    }

    private Double readFile(File s) {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(s))) {
            return stream.readDouble();
        } catch (Exception e) {
            return 0D;
        }
    }

}
