package ee.ellytr.gui.slot;

import ee.ellytr.gui.GUI;
import ee.ellytr.gui.slot.listener.PageSlotListener;
import ee.ellytr.gui.util.Item;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Getter
public class PageSlot extends Slot {

  private final PageSlotType type;

  public PageSlot(@NonNull GUI gui, @NonNull PageSlotType type) {
    this.type = type;
    setItem(new Item().type(Material.NETHER_STAR)
        .name(type.equals(PageSlotType.PREVIOUS) ? ChatColor.RED + "Previous Page" : ChatColor.GREEN + "Next Page")
        .build());
    setListener(new PageSlotListener(gui, type));
  }

  public enum PageSlotType {
    PREVIOUS, NEXT
  }

}
