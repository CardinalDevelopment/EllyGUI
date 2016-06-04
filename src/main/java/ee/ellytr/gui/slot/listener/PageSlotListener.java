package ee.ellytr.gui.slot.listener;

import ee.ellytr.gui.ClickListener;
import ee.ellytr.gui.EllyGUI;
import ee.ellytr.gui.slot.PageSlot;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PageSlotListener extends ClickListener {

  private final EllyGUI gui;
  private final PageSlot.PageSlotType type;

  @Override
  public void onClick(@NonNull Player player) {
    if (type.equals(PageSlot.PageSlotType.PREVIOUS)) {
      gui.toPreviousPage(player);
    } else {
      gui.toNextPage(player);
    }
  }

}
