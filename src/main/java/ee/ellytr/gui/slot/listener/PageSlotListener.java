package ee.ellytr.gui.slot.listener;

import ee.ellytr.gui.SlotListener;
import ee.ellytr.gui.GUI;
import ee.ellytr.gui.slot.PageSlot;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@RequiredArgsConstructor
public class PageSlotListener extends SlotListener {

  private final GUI gui;
  private final PageSlot.PageSlotType type;

  @Override
  public void onClick(@NonNull Player player, @NonNull ClickType clickType) {
    if (type.equals(PageSlot.PageSlotType.PREVIOUS)) {
      gui.toPreviousPage(player);
    } else {
      gui.toNextPage(player);
    }
  }

}
