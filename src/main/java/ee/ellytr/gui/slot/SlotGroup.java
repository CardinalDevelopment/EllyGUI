package ee.ellytr.gui.slot;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SlotGroup {

  private final Map<Integer, Slot> slots = new HashMap<>();

  public void setSlot(int offset, @NonNull Slot slot) {
    slots.put(offset, slot);
  }

  public Slot getSlot(int offset) {
    return slots.get(offset);
  }

  public void removeSlot(int offset) {
    slots.remove(offset);
  }

  public int getLowestOffset() {
    int lowestOffset = 0;
    for (int offset : slots.keySet()) {
      if (offset < lowestOffset) {
        lowestOffset = offset;
      }
    }
    return lowestOffset;
  }

  public int getHighestOffset() {
    int highestOffset = 0;
    for (int offset : slots.keySet()) {
      if (offset > highestOffset) {
        highestOffset = offset;
      }
    }
    return highestOffset;
  }

}
