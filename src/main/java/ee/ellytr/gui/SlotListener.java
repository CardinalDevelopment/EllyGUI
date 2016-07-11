package ee.ellytr.gui;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public abstract class SlotListener {

  public abstract void onClick(@NonNull Player player, @NonNull ClickType clickType);

}
