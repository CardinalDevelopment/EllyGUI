package ee.ellytr.gui.util;

import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;

public class Components {

  /**
   * Compresses an array of base components into one.
   *
   * @param components The array of components.
   * @return The compressed component.
   */
  public static BaseComponent compress(@NonNull BaseComponent[] components) {
    if (components.length == 0) {
      throw new IllegalArgumentException("Array of components cannot be empty");
    }
    BaseComponent component = components[0];
    for (int i = 1; i < components.length; i++) {
      component.addExtra(components[i]);
    }
    return component;
  }

}
