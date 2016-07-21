package ee.ellytr.gui;

import ee.ellytr.chat.ChatConstant;
import ee.ellytr.chat.component.LanguageComponent;
import ee.ellytr.chat.component.builder.LocalizedComponentBuilder;
import ee.ellytr.chat.component.builder.UnlocalizedComponentBuilder;
import ee.ellytr.gui.slot.PageSlot;
import ee.ellytr.gui.slot.Slot;
import ee.ellytr.gui.slot.SlotGroup;
import ee.ellytr.gui.util.Components;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class GUI {

  private final LanguageComponent title;
  private final int size;

  private final List<GUIContext<Slot>> slots = new ArrayList<>();
  private final List<GUIContext<SlotGroup>> slotGroups = new ArrayList<>();

  private final Map<Integer, EllyInventory> inventories = new HashMap<>();
  private final Map<Player, Integer> opened = new HashMap<>();

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

  protected void click(int slot, @NonNull Player player, @NonNull ClickType clickType) {
    Map<Integer, Slot> slots = inventories.get(opened.get(player)).getSlots();
    if (slots.containsKey(slot)) {
      SlotListener listener = slots.get(slot).getListener();
      if (listener != null) {
        listener.onClick(player, clickType);
      }
    }
  }

  protected void close(@NonNull Player player) {
    opened.remove(player);
  }

  public void setSlot(@NonNull Slot slot, int position) {
    setSlot(slot, position, false);
  }

  public void setSlot(@NonNull Slot slot, int position, boolean defaultContext) {
    slots.add(new GUIContext<>(slot, position, defaultContext));

    if (defaultContext) {
      for (int page = 1; page <= getPages(); page++) {
        updatePage(page);
      }
    } else {
      int page = getPage(position);
      if (page >= 2) {
        updatePage(page - 1);
      }
      updatePage(page);
      if (page < getPages()) {
        updatePage(page + 1);
      }
    }
  }

  public void setSlotGroup(@NonNull SlotGroup group, int position) {
    setSlotGroup(group, position, false);
  }

  public void setSlotGroup(@NonNull SlotGroup group, int position, boolean defaultContext) {
    slotGroups.add(new GUIContext<>(group, position, defaultContext));

    if (defaultContext) {
      for (int page = 1; page <= getPages(); page++) {
        updatePage(page);
      }
    } else {
      int lowestPage = getPage(position - group.getLowestOffset());
      int highestPage = getPage(position + group.getHighestOffset());

      if (lowestPage >= 2) {
        updatePage(lowestPage - 1);
      }
      for (int page = lowestPage; page <= highestPage; page++) {
        updatePage(page);
      }
      if (highestPage < getPages()) {
        updatePage(highestPage + 1);
      }
    }
  }

  public Slot getSlotAt(int position) {
    for (GUIContext<Slot> context : slots) {
      if (context.getPosition() == position) {
        return context.getNode();
      }
    }
    return null;
  }

  public SlotGroup getSlotGroupAt(int position) {
    for (GUIContext<SlotGroup> context : slotGroups) {
      if (context.getPosition() == position) {
        return context.getNode();
      }
    }
    return null;
  }

  public void updatePage(int page) {
    int position = page * size;
    if (page != 1) {
      slots.add(new GUIContext<>(new PageSlot(this, PageSlot.PageSlotType.PREVIOUS), position - 9, false));
    }
    if (page != getPages()) {
      slots.add(new GUIContext<>(new PageSlot(this, PageSlot.PageSlotType.NEXT), position - 1, false));
    }

    EllyInventory inventory = new EllyInventory();
    for (GUIContext<Slot> context : slots) {
      int contextPosition = context.getPosition();
      if (context.isDefaultContext()) {
        if (contextPosition < size) {
          inventory.setSlot(contextPosition, context.getNode());
        }
      } else {
        if (getPage(contextPosition) == page) {
          inventory.setSlot(contextPosition % size, context.getNode());
        }
      }
    }
    for (GUIContext<SlotGroup> context : slotGroups) {
      int contextPosition = context.getPosition();
      Map<Integer, Slot> slots = context.getNode().getSlots();
      if (context.isDefaultContext()) {
        slots.forEach((offset, slot) -> {
          int slotPosition = contextPosition + offset;
          if (slotPosition < size) {
            inventory.setSlot(slotPosition, slot);
          }
        });
      } else {
        slots.forEach((offset, slot) -> {
          int slotPosition = contextPosition + offset;
          if (getPage(slotPosition) == page) {
            inventory.setSlot(slotPosition, slot);
          }
        });
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

  public int getPage(int position) {
    return (position / size) + 1;
  }

  public int getPages() {
    int maxPages = 0;
    for (GUIContext<Slot> context : slots) {
      int pages = getPage(context.getPosition());
      if (pages > maxPages) {
        maxPages = pages;
      }
    }
    for (GUIContext<SlotGroup> context : slotGroups) {
      int pages = getPage(context.getPosition() + context.getNode().getHighestOffset());
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
