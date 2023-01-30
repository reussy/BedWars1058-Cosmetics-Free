package me.defender.cosmetics.api.categories.killmessage.util;

import com.hakan.core.utils.ColorUtil;
import me.defender.cosmetics.api.BwcAPI;
import me.defender.cosmetics.api.categories.killmessage.KillMessage;
import me.defender.cosmetics.api.enums.CosmeticsType;
import me.defender.cosmetics.api.enums.FieldsType;
import me.defender.cosmetics.api.enums.RarityType;
import me.defender.cosmetics.api.utils.StartupUtils;
import me.defender.cosmetics.api.configuration.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KillMessageUtils {

    /**
     * Check if list is empty for id kill message
     * @param id selected or ID for the kill message, example is "none"
     * @param type Accepted values are, PvP, Explosion, Shoot and Void
     * @return true if exists, false otherwise.
     */
    public static boolean exists(String id, String type){
       List<String> messages = ConfigUtils.getKillMessages().getYml().getStringList(CosmeticsType.KillMessage.getSectionKey() + "." + id + "." + type + "-Kill");
        return !messages.isEmpty();
    }


    /**
     * Sends a kill message or preview message to a player based on the type of death.
     *
     * @param player            The player to send the message to
     * @param victim            The name of the victim
     * @param killer            The player object of the killer
     * @param finalKill         A flag indicating whether this was the final kill
     * @param victimColor       The color to use for the victim's name
     * @param killerColor       The color to use for the killer's name
     * @param type              The type of death. Accepted values: "PvP", "Void", "Shoot", "Explosion"
     * @param preview           If true, it will send all messages from the list.
     * @param previewID         leave null if not preview, if preview than pass the id for example, none.
     * @param previewKillerName leave null if not preview, if preview than pass the name for killer.
     * @param oldMessage        never used, leave as null.
     */
    public static void sendKillMessage(Player player, String victim, Player killer, boolean finalKill, ChatColor victimColor, ChatColor killerColor, String type, String oldMessage , boolean preview, String previewID, String previewKillerName) {
        String selectedMessage = new BwcAPI().getSelectedCosmetic(killer, CosmeticsType.KillMessage);
        List<String> messages;
        if(preview){
            messages = ConfigUtils.getKillMessages().getYml().getStringList(CosmeticsType.KillMessage.getSectionKey() + "." + previewID + "." + type + "-Kill");
        }else{
            messages = ConfigUtils.getKillMessages().getYml().getStringList(CosmeticsType.KillMessage.getSectionKey() + "." + selectedMessage + "." + type + "-Kill");
        }
        for(KillMessage killMessage : StartupUtils.killMessageList){
            if(preview){
                if(killMessage.getIdentifier().equals(previewID)){
                    if(killMessage.getField(FieldsType.RARITY, player) != RarityType.NONE) {
                        for (String message : messages) {
                            player.sendMessage(ColorUtil.colored(message
                                    .replace("{killer}", killerColor + previewKillerName)
                                    .replace("{victim}", victimColor + victim)));
                        }
                    }
                    return;
                }
            }
            if(killMessage.getIdentifier().equals(selectedMessage)) {
                if (killMessage.getField(FieldsType.RARITY, player) == RarityType.NONE) return;
            }
        }
        if(messages.isEmpty()) return;
        String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));
        message = message.replace("{victim}", victimColor + victim);
        message = message.replace("{killer}", killerColor + killer.getDisplayName());

        if (finalKill) {
            message += " &b&lFINAL KILL!";
        }
        player.sendMessage(ColorUtil.colored(message));
    }
}
