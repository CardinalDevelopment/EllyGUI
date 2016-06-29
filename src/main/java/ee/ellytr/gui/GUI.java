package ee.ellytr.gui;

import com.google.common.collect.Maps;
import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.LanguageComponent;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.gui.slot.PageSlot;
import ee.ellytr.gui.slot.Slot;
import ee.ellytr.gui.util.Components;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class GUI {

  private final LanguageComponent title;
  private final int size;
  private final Map<Integer, Slot> slots = Maps.newHashMap();
  private final Map<Integer, Slot> defaultSlots = Maps.newHashMap();

  private final Map<Integer, EllyInventory> inventories = new HashMap<>();
  private final Map<Player, Integer> opened = Maps.newHashMap();

  private final Map<Player, Locale> locales = new HashMap<>();

  public GUI(LanguageComponent title, int size) {
    this.title = title;
    this.size = size;
  }

  public void openInventory(@NonNull Player player, int page, @NonNull Locale locale) {
    player.openInventory(getInventory(page, locale));
    opened.put(player, page);

    locales.put(player, locale);
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

    int page = getPage(position);
    if (page >= 2) {
      updatePage(page - 1);
    }
    updatePage(page);
    if (page < getPages()) {
      updatePage(page + 1);
    }
  }

  public void setDefaultSlot(int position, Slot slot) {
    defaultSlots.put(position, slot);

    for (int page = 1; page <= getPages(); page++) {
      updatePage(page);
    }
  }

  private void updatePage(int page) {
    EllyInventory inventory = new EllyInventory();
    for (int position : defaultSlots.keySet()) {
      inventory.setSlot(position, defaultSlots.get(position));
    }
    int position = page * size;
    if (page != getPages()) {
      slots.put(position - 1, new PageSlot(this, PageSlot.PageSlotType.NEXT));
    }
    if (page != 1) {
      slots.put(position - 9, new PageSlot(this, PageSlot.PageSlotType.PREVIOUS));
    }
    for (position = size * (page - 1); position < size * page; position ++) {
      if (slots.containsKey(position)) {
        inventory.setSlot(position % size, slots.get(position));
      }
    }

    inventories.put(page, inventory);
  }

  private Inventory getInventory(int page, @NonNull Locale locale) {
    int pages = getPages();
    if (page > pages) {
      throw new IllegalArgumentException(
          "Cannot get page " + page + " of \"" + title + "\" inventory, max page is "+ pages);
    }

    Inventory inventory = Bukkit.createInventory(null, size,
        Components.compress(title.getComponents(locale)).toLegacyText() + (getPages() != 1 ?
          Components.compress(
              new UnlocalizedComponentBuilder(" - {0}",
                  new LocalizedComponentBuilder(ChatConstant.getConstant("inventory.page"),
                      new UnlocalizedComponentBuilder(page + "").color(ChatColor.DARK_GRAY).build()
                  ).color(ChatColor.DARK_GRAY).build()
              ).color(ChatColor.DARK_GRAY).build().getComponents(locale)).toLegacyText()
        : "")
    );
    Map<Integer, Slot> slots = inventories.get(page).getSlots();
    for (int position : slots.keySet()) {
      Slot slot = slots.get(position);
      ItemStack item = slot.getItem();
      LanguageComponent name = slot.getName();
      List<LanguageComponent> lore = slot.getLore();
      if (name != null || lore != null) {
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
          meta.setDisplayName(Components.compress(name.getComponents(locale)).toLegacyText());
        }
        if (lore != null) {
          meta.setLore(lore.stream().map(component
              -> Components.compress(component.getComponents(locale)).toLegacyText()).collect(Collectors.toList()));
        }
        item.setItemMeta(meta);
      }
      inventory.setItem(position, item);
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
    openInventory(player, currentPage - 1, locales.get(player));
  }

  public void toNextPage(@NonNull Player player) {
    int currentPage = opened.get(player);
    if (currentPage == getPages()) {
      throw new IllegalArgumentException("Cannot go to next page");
    }
    openInventory(player, currentPage + 1, locales.get(player));
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
