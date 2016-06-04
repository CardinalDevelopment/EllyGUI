package ee.ellytr.gui;

import com.google.common.collect.Maps;
import ee.ellytr.gui.slot.PageSlot;
import ee.ellytr.gui.slot.Slot;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class EllyGUI implements Listener {

  private final String title;
  private final int size;
  private final Map<Integer, Slot> slots = Maps.newHashMap();
  private final Map<Integer, Slot> defaultSlots = Maps.newHashMap();
  private final Map<Player, Integer> opened = Maps.newHashMap();

  public EllyGUI(Plugin plugin, String title, int size) {
    this.title = title;
    this.size = size;

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public void openInventory(Player player, int page) {
    player.openInventory(getInventory(page));
    opened.put(player, page);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    opened.remove(event.getPlayer());
  }

  public void setSlot(int position, Slot slot) {
    slots.put(position, slot);
  }

  public void addSlot(Slot slot) {
    int position = 0;
    while (slots.containsKey(position)) {
      position ++;
    }
    setSlot(position, slot);
  }

  private Inventory getInventory(int page) {
    int pages = getPages();
    if (page > pages) {
      throw new IllegalArgumentException(
          "Cannot get page " + page + " of \"" + title + "\" inventory, max page is "+ pages);
    }
    Inventory inventory = Bukkit.createInventory(null, size, title + (getPages() != 1 ? " - Page " + page : ""));
    for (int position : defaultSlots.keySet()) {
      inventory.setItem(position, defaultSlots.get(position).getItem());
    }
    if (page != pages) {
      int position = page * size;
      slots.put(position - 1, new PageSlot(this, PageSlot.PageSlotType.NEXT));
      if (page != 1) {
        slots.put(position - 9, new PageSlot(this, PageSlot.PageSlotType.PREVIOUS));
      }
    }
    for (int position = size * (page - 1); position < size * page; position ++) {
      inventory.setItem(position % size, slots.get(position).getItem());
    }
    return inventory;
  }

  private int getPages() {
    int maxPages = 0;
    for (int position : slots.keySet()) {
      int pages =  position / size + 1;
      if (pages > maxPages) {
        maxPages = pages;
      }
    }
    return maxPages;
  }

  public void toPreviousPage(@NonNull Player player) {
    int currentPage = opened.get(player);
    if (currentPage == 1) {
      throw new IllegalArgumentException("Cannot go to previous page");
    }
    openInventory(player, currentPage - 1);
  }

  public void toNextPage(@NonNull Player player) {
    int currentPage = opened.get(player);
    if (currentPage == getPages()) {
      throw new IllegalArgumentException("Cannot go to next page");
    }
    openInventory(player, currentPage + 1);
  }

}
