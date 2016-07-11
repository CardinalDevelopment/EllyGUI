package ee.ellytr.gui.slot;

import ee.ellytr.chat.component.LanguageComponent;
import ee.ellytr.gui.SlotListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Slot {

  private LanguageComponent name;
  private List<LanguageComponent> lore;

  private ItemStack item;
  private SlotListener listener;

}
