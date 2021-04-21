package me.william278.bungeetowny.object.town;

import me.william278.bungeetowny.HuskTowns;
import me.william278.bungeetowny.MessageManager;
import me.william278.bungeetowny.TownLimitsCalculator;
import me.william278.bungeetowny.object.chunk.ClaimedChunk;
import me.william278.bungeetowny.object.teleport.TeleportationPoint;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class Town {

    // Set of all chunks claimed by the town, including PlotChunks and FarmChunks
    private final HashSet<ClaimedChunk> claimedChunks;

    // TeleportationPoint of the town's spawn position
    private final TeleportationPoint townSpawn;

    // HashSet of all town members
    private final HashMap<UUID, TownRole> memberUUIDs;

    // Name of the town
    private final String name;

    // Amount of money deposited into town
    private final double moneyDeposited;

    // Greeting and farewell messages
    private final String greetingMessage;
    private final String farewellMessage;

    // Timestamp when the town was founded
    private final long foundedTimestamp;

    /**
     * Create a new Town at the Mayor's position
     * @param mayor the Player who will be the mayor of the town
     */
    public Town(Player mayor, String name) {
        this.townSpawn = null; //new TeleportationPoint(mayor.getLocation(), HuskTowns.getSettings().getServerID());
        this.moneyDeposited = 0D;
        this.name = name;

        this.memberUUIDs = new HashMap<>();

        this.claimedChunks = new HashSet<>();
        this.claimedChunks.add(new ClaimedChunk(mayor, name));

        this.greetingMessage = MessageManager.getRawMessage("default_greeting_message", name);
        this.farewellMessage = MessageManager.getRawMessage("default_farewell_message", name);

        this.foundedTimestamp = Instant.now().getEpochSecond();

        this.memberUUIDs.put(mayor.getUniqueId(), TownRole.MAYOR);
    }

    /**
     * Create a town object with specified parameters
     * @param townName The name of the town
     * @param claimedChunks Set of claimed/plot/farm chunks
     * @param memberUUIDs Map of UUIDs of town members & their role
     * @param townSpawn Town spawn TeleportationPoint
     * @param moneyDeposited Amount of money deposited into town
     */
    public Town(String townName, HashSet<ClaimedChunk> claimedChunks, HashMap<UUID,TownRole> memberUUIDs, TeleportationPoint townSpawn, double moneyDeposited, String greetingMessage, String farewellMessage, long foundedTimestamp) {
        this.name = townName;
        this.claimedChunks = claimedChunks;
        this.memberUUIDs = memberUUIDs;
        this.townSpawn = townSpawn;
        this.moneyDeposited = moneyDeposited;
        this.foundedTimestamp = foundedTimestamp;
        this.greetingMessage = greetingMessage;
        this.farewellMessage = farewellMessage;
    }

    public HashSet<ClaimedChunk> getClaimedChunks() {
        return claimedChunks;
    }

    // Returns all the claimed chunks a town has *on the server*
    public HashSet<ClaimedChunk> getServerClaimedChunks() {
        HashSet<ClaimedChunk> serverClaimedChunks = new HashSet<>();
        for (ClaimedChunk chunk : claimedChunks) {
            if (chunk.getServer().equalsIgnoreCase(HuskTowns.getSettings().getServerID())) {
                serverClaimedChunks.add(chunk);
            }
        }
        return serverClaimedChunks;
    }

    public int getClaimedChunksNumber() {
        return claimedChunks.size();
    }

    // Returns the town spawn point (null if not set)
    public TeleportationPoint getTownSpawn() {
        return townSpawn;
    }

    /**
     * Get a Map<UUID, TownRole> of all a town's members
     * @return Map of all members to their town role
     */
    public HashMap<UUID, TownRole> getMembers() {
        return memberUUIDs;
    }

    public int getMaximumClaimedChunks() {
        return TownLimitsCalculator.getMaxClaims(getLevel());
    }

    public int getLevel() {
        return TownLimitsCalculator.getLevel(moneyDeposited);
    }

    public String getName() {
        return name;
    }

    public double getMoneyDeposited() {
        return moneyDeposited;
    }

    // Converts a string into an integer value, used in getting town color
    private static long getStringValue(String string) {
        long value = 0;
        for (String c : string.split("")) {
            value++;
            int characterInt = c.charAt(0);
            value = value * (long) characterInt;
        }
        return value;
    }

    // Returns the randomly calculated color of a town, in format #xxxxxx
    public static String getTownColor(String townName) {
        // Generates a random color code to color a town, seeded based on the town name
        Random random = new Random(getStringValue(townName));
        int randomHex = random.nextInt(0xffffff + 1);
        return String.format("#%06x", randomHex);
    }

    public String getTownColor() {
        return getTownColor(this.name);
    }
}
