package ee.ellytr.gui.slot;

import ee.ellytr.gui.ClickListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Slot {

  private ItemStack item;
  private ClickListener listener;

}
