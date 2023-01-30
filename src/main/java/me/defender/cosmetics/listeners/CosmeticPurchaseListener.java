package me.defender.cosmetics.listeners;

import me.defender.cosmetics.api.events.CosmeticPurchaseEvent;
import me.defender.cosmetics.api.utils.Utility;
import me.defender.cosmetics.database.PlayerOwnedData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CosmeticPurchaseListener implements Listener {

    @EventHandler
    public void onPurchase(CosmeticPurchaseEvent e){
        if(e.isCancelled())
            return;
        PlayerOwnedData ownedData = Utility.playerOwnedDataList.get(e.getPlayer().getUniqueId());
        ownedData.updateOwned();
    }
}
