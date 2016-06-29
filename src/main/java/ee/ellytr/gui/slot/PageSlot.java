package ee.ellytr.gui.slot;

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.gui.GUI;
import ee.ellytr.gui.slot.listener.PageSlotListener;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class PageSlot extends Slot {

  private final PageSlotType type;

  public PageSlot(@NonNull GUI gui, @NonNull PageSlotType type) {
    super(
        type.equals(PageSlotType.PREVIOUS)
            ? new LocalizedComponentBuilder(ChatConstant.getConstant("item.page.previous")).color(ChatColor.RED).build()
            : new LocalizedComponentBuilder(ChatConstant.getConstant("item.page.next")).color(ChatColor.GREEN).build(),
        null,
        new ItemStack(Material.NETHER_STAR),
        new PageSlotListener(gui, type)
    );
    this.type = type;
  }

  public enum PageSlotType {
    PREVIOUS, NEXT
  }

}
