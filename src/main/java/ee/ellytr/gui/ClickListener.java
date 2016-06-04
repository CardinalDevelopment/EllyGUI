package ee.ellytr.gui;

import lombok.NonNull;
import org.bukkit.entity.Player;

public abstract class ClickListener {

  public abstract void onClick(@NonNull Player player);

}
