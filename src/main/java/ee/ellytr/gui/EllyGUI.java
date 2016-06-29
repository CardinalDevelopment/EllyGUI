package ee.ellytr.gui;

import ee.ellytr.chat.LocaleRegistry;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EllyGUI implements Listener {

  @Getter
  private static EllyGUI instance;

  private final List<GUI> guis = new ArrayList<>();

  public EllyGUI(@NonNull Plugin plugin) {
    Bukkit.getPluginManager().registerEvents(this, plugin);

    LocaleRegistry registry = new LocaleRegistry();
    registry.addLocaleFile(new Locale("en", "US"), EllyGUI.class.getResourceAsStream("/lang/gui/en_US.properties"));
    registry.register();
  }

  public void addGUI(@NonNull GUI gui) {
    guis.add(gui);
  }

  public void removeGUI(@NonNull GUI gui) {
    guis.remove(gui);
  }

  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    GUI gui = getOpened(player);
    if (gui != null) {
      int position = event.getRawSlot();
      if (position < gui.getSize()) {
        gui.click(player, position);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Player player = (Player) event.getPlayer();
    GUI gui = getOpened(player);
    if (gui != null) {
      gui.close(player);
    }
  }

  public GUI getOpened(@NonNull Player player) {
    for (GUI gui : guis) {
      if (gui.getOpened().containsKey(player)) {
        return gui;
      }
    }
    return null;
  }

}
