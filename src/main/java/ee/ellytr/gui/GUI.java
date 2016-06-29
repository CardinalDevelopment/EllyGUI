package ee.ellytr.gui;

import com.google.common.collect.Maps;
import ee.ellytr.gui.slot.PageSlot;
import ee.ellytr.gui.slot.Slot;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GUI {

  private final String title;
  private final int size;
  private final Map<Integer, Slot> slots = Maps.newHashMap();
  private final Map<Integer, Slot> defaultSlots = Maps.newHashMap();

  private final Map<Integer, EllyInventory> inventories = new HashMap<>();
  private final Map<Player, Integer> opened = Maps.newHashMap();

  public GUI(String title, int size) {
    this.title = title;
    this.size = size;
  }

  public void openInventory(Player player, int page) {
    player.openInventory(getInventory(page));
    opened.put(player, page);
  }

  protected void click(@NonNull Player player, int slot) {
    Map<Integer, Slot> slots = inventories.get(opened.get(player)).getSlots();
    if (slots.containsKey(slot)) {
      slots.get(slot).getListener().onClick(player);
    }
  }

  protected void close(@NonNull Player player) {
    opened.remove(player);
  }

  public void setSlot(int position, Slot slot) {
    slots.put(position, slot);
    updatePage(getPage(position));
  }

  public void addSlot(Slot slot) {
    int position = 0;
    while (slots.containsKey(position)) {
      position ++;
    }
    setSlot(position, slot);

    updatePage(getPage(position));
  }

  private void updatePage(int page) {
    EllyInventory inventory = new EllyInventory();
    for (int position : defaultSlots.keySet()) {
      inventory.setSlot(position, defaultSlots.get(position));
    }
    if (page != getPages()) {
      int position = page * size;
      slots.put(position - 1, new PageSlot(this, PageSlot.PageSlotType.NEXT));
      if (page != 1) {
        slots.put(position - 9, new PageSlot(this, PageSlot.PageSlotType.PREVIOUS));
      }
    }
    for (int position = size * (page - 1); position < size * page; position ++) {
      if (slots.containsKey(position)) {
        inventory.setSlot(position % size, slots.get(position));
      }
    }

    inventories.put(page, inventory);
  }

  private Inventory getInventory(int page) {
    int pages = getPages();
    if (page > pages) {
      throw new IllegalArgumentException(
          "Cannot get page " + page + " of \"" + title + "\" inventory, max page is "+ pages);
    }

    Inventory inventory = Bukkit.createInventory(null, size, title + (getPages() != 1 ? " - Page " + page : ""));
    Map<Integer, Slot> slots = inventories.get(page).getSlots();
    for (int position : slots.keySet()) {
      inventory.setItem(position, slots.get(position).getItem());
    }

    return inventory;
  }

  private int getPage(int position) {
    return (position / size) + 1;
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

  public void update() {
    for (Player player : opened.keySet()) {
      Inventory inventory = player.getOpenInventory().getTopInventory();
      Map<Integer, Slot> slots = inventories.get(opened.get(player)).getSlots();
      for (int position = 0; position < inventory.getSize(); position++) {
        if (slots.containsKey(position)) {
          inventory.setItem(position, slots.get(position).getItem());
        } else {
          inventory.setItem(position, null);
        }
      }
    }
  }

}
