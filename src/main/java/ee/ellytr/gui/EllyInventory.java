package ee.ellytr.gui;

import ee.ellytr.gui.slot.Slot;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EllyInventory {

  private final Map<Integer, Slot> slots = new HashMap<>();

  public void setSlot(int position, @NonNull Slot slot) {
    slots.put(position, slot);
  }

}
