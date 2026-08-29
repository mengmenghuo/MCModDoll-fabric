package cn.mcmod.doll;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class ModMain implements ModInitializer {
  public static final Block MCMODWIKIGIRL_DOLL = registerDoll("mcmodwiki_girl");
  public static final BlockItem MCMODWIKIGIRL = registerDollItem("mcmodwiki_girl", MCMODWIKIGIRL_DOLL);

  public static final Block MEOW_DOLL = registerDoll("meow_doll");
  public static final BlockItem MEOW = registerDollItem("meow_doll", MEOW_DOLL);

  // 26.2 起方块/物品必须先 setId 才能构造（原版由 DeferredRegister 自动完成）
  private static Block registerDoll(String name) {
    ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("mcmoddoll", name));
    return Registry.register(BuiltInRegistries.BLOCK, key, new Doll(Block.Properties.of().setId(key)));
  }

  private static BlockItem registerDollItem(String name, Block block) {
    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("mcmoddoll", name));
    // useBlockDescriptionPrefix：物品沿用方块的描述键（与原版 NeoForge 行为及语言文件一致）
    return Registry.register(BuiltInRegistries.ITEM, key, new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(key)));
  }

  private static final Identifier FILE_ID = Identifier.fromNamespaceAndPath("mcmoddoll", "splashes.txt");
  public static ImmutableList<String> SPLASHES;

  @Override
  public void onInitialize() {
    CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(event -> {
      event.accept(new ItemStack(MCMODWIKIGIRL), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
      event.accept(new ItemStack(MEOW), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    });

    try {
      onCommonSetup();
    } catch (IOException e) {
      SPLASHES = ImmutableList.of();
    }
  }

  public static void onCommonSetup() throws IOException {
    SPLASHES = ImmutableList.of();

    InputStream stream = ModMain.class.getResourceAsStream("/assets/mcmoddoll/splashes.txt");
    if (Objects.isNull(stream)) {
      return;
    }

    InputStreamReader reader = new InputStreamReader(stream);
    if (!reader.ready()) {
      return;
    }

    SPLASHES = ImmutableList.copyOf(reader.readAllLines());
    reader.close();
    stream.close();
  }
}
