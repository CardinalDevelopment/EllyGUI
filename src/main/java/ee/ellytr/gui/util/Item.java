package ee.ellytr.gui.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class Item {

  private Material material = Material.AIR;
  private int amount = 1;
  private short durability = 0;
  private String name;
  private List<String> lore = Lists.newArrayList();
  private Map<Enchantment, Integer> enchantments = Maps.newHashMap();

  public Item type(Material material) {
    this.material = material;
    return this;
  }

  public Item amount(int amount) {
    this.amount = amount;
    return this;
  }

  public Item durability(short durability) {
    this.durability = durability;
    return this;
  }

  public Item name(String name) {
    this.name = name;
    return this;
  }

  public Item lore(String lore) {
    this.lore.add(lore);
    return this;
  }

  public Item lore(List<String> lore) {
    this.lore = lore;
    return this;
  }

  public Item enchantment(Enchantment enchantment, int level) {
    enchantments.put(enchantment, level);
    return this;
  }

  public ItemStack build() {
    ItemStack item = new ItemStack(material, amount, durability);
    ItemMeta meta = item.getItemMeta();
    if (name != null) {
      meta.setDisplayName(name);
    }
    if (!lore.isEmpty()) {
      meta.setLore(lore);
    }
    for (Enchantment enchantment : enchantments.keySet()) {
      item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment));
    }
    item.setItemMeta(meta);
    return item;
  }

}
