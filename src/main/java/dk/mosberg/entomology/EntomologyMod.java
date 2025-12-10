package dk.mosberg.entomology;

import dk.mosberg.entomology.api.EntomologyAPI;
import dk.mosberg.entomology.command.EntomologyCommands;
import dk.mosberg.entomology.config.ConfigManager;
import dk.mosberg.entomology.item.BugNetItem;
import dk.mosberg.entomology.item.FieldGuideItem;
import dk.mosberg.entomology.item.SpecimenJarItem;
import dk.mosberg.entomology.block.ResearchStationBlock;
import dk.mosberg.entomology.block.SpecimenJarBlock;
import dk.mosberg.entomology.data.DataDrivenRegistry;
import dk.mosberg.entomology.registry.ModRegistry;
import dk.mosberg.entomology.screen.ResearchStationScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod initializer for Entomology.
 *
 * Architecture Overview:
 * - API Layer: Extensible plugin system (api/)
 * - Config Layer: Hot-reloadable configuration (config/)
 * - Mechanics Layer: Data-driven gameplay logic (mechanics/)
 * - Component Layer: Modular specimen behaviors (component/)
 * - Registry Layer: Centralized system initialization (registry/)
 * - Schema Layer: JSON validation and integrity (schema/)
 *
 * @version 1.0.0
 */
public class EntomologyMod implements ModInitializer {
  public static final String MODID = "entomology";
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  // Items
  public static Item basicBugNet;
  public static Item ironBugNet;
  public static Item goldenBugNet;
  public static Item diamondBugNet;
  public static Item netheriteBugNet;
  public static Item specimenJarItem;
  public static Item fieldGuide;

  // Blocks
  public static Block specimenJarBlock;
  public static Block researchStationBlock;
  public static Block displayCaseBlock;

  // Block entities
  public static BlockEntityType<?> specimenJarBe;
  public static BlockEntityType<?> researchStationBe;
  public static BlockEntityType<?> displayCaseBe;

  // Screen handlers
  public static ScreenHandlerType<ResearchStationScreenHandler> researchStationScreenHandler;

  // Custom creative tab
  public static final RegistryKey<ItemGroup> ENTOMOLOGY_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP,
      id("entomology"));
  public static ItemGroup entomologyItemGroup;

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing Entomology v1.0.0");
    LOGGER.info("Architecture: Modular | Data-Driven | API-Extensible");

    // Initialize core systems
    ConfigManager.getInstance(); // Load configurations
    ModRegistry.initialize(); // Register mechanics, components, validators
    DataDrivenRegistry.bootstrap(); // Register data reloaders

    // Register content
    registerContent();

    // Register custom creative tab
    registerCreativeTab();

    // Register commands
    CommandRegistrationCallback.EVENT.register(EntomologyCommands::register);

    // Register to item groups
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
      entries.add(basicBugNet);
      entries.add(ironBugNet);
      entries.add(goldenBugNet);
      entries.add(diamondBugNet);
      entries.add(netheriteBugNet);
    });
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
      entries.add(specimenJarItem);
      entries.add(fieldGuide);
      entries.add(researchStationBlock);
      entries.add(displayCaseBlock);
    });

    // Log API information
    EntomologyAPI api = EntomologyAPI.getInstance();
    LOGGER.info("Entomology API v{} initialized with {} mechanics",
        api.getApiVersion(), api.getMechanics().size());
    LOGGER.info("Mod initialization complete!");
  }

  private void registerContent() {
    // Register all bug net tiers (removed basic bug_net, kept others)
    basicBugNet = registerItem("basic_bug_net",
        new BugNetItem(new Item.Settings().maxDamage(64)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id("basic_bug_net")))));
    ironBugNet = registerItem("iron_bug_net",
        new BugNetItem(new Item.Settings().maxDamage(256)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id("iron_bug_net")))));
    goldenBugNet = registerItem("golden_bug_net",
        new BugNetItem(new Item.Settings().maxDamage(192)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id("golden_bug_net")))));
    diamondBugNet = registerItem("diamond_bug_net",
        new BugNetItem(new Item.Settings().maxDamage(512)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id("diamond_bug_net")))));
    netheriteBugNet = registerItem("netherite_bug_net",
        new BugNetItem(new Item.Settings().maxDamage(1024)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id("netherite_bug_net")))));

    fieldGuide = registerItem("field_guide",
        new FieldGuideItem(new Item.Settings().maxCount(1)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id("field_guide")))));

    // Register blocks (without automatic BlockItems since we have custom ones)
    specimenJarBlock = registerBlockOnly("specimen_jar",
        new SpecimenJarBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id("specimen_jar")))));
    researchStationBlock = registerBlock("research_station",
        new ResearchStationBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id("research_station")))));
    displayCaseBlock = registerBlock("display_case",
        new dk.mosberg.entomology.block.DisplayCaseBlock(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id("display_case")))));

    // Specimen jar item is special - it's a custom BlockItem
    specimenJarItem = registerItem("specimen_jar",
        new SpecimenJarItem(new Item.Settings().maxCount(16)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id("specimen_jar")))));

    specimenJarBe = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        id("specimen_jar"),
        FabricBlockEntityTypeBuilder.create(
            dk.mosberg.entomology.block.entity.SpecimenJarBlockEntity::new,
            specimenJarBlock).build());

    researchStationBe = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        id("research_station"),
        FabricBlockEntityTypeBuilder.create(
            dk.mosberg.entomology.block.entity.ResearchStationBlockEntity::new,
            researchStationBlock).build());

    displayCaseBe = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        id("display_case"),
        FabricBlockEntityTypeBuilder.create(
            dk.mosberg.entomology.block.entity.DisplayCaseBlockEntity::new,
            displayCaseBlock).build());

    // Register screen handler
    researchStationScreenHandler = Registry.register(
        Registries.SCREEN_HANDLER,
        id("research_station"),
        new ScreenHandlerType<>(ResearchStationScreenHandler::new, null));

    // Store reference in screen handler class
    ResearchStationScreenHandler.screenHandlerType = researchStationScreenHandler;
    registerDataReloaders();
  }

  @SuppressWarnings("deprecation") // registerReloadListener still functional in current API
  private static void registerDataReloaders() {
    // Register data reloaders for specimens & items/blocks/research definitions
    ResourceManagerHelper resourceHelper = ResourceManagerHelper.get(ResourceType.SERVER_DATA);
    resourceHelper.registerReloadListener(new DataDrivenRegistry.SpecimenReloader());
    resourceHelper.registerReloadListener(new DataDrivenRegistry.DefinitionReloader());
    resourceHelper.registerReloadListener(new DataDrivenRegistry.MechanicsReloader());
  }

  public static Identifier id(String path) {
    return Identifier.of(MODID, path);
  }

  private void registerCreativeTab() {
    entomologyItemGroup = FabricItemGroup.builder()
        .icon(() -> new ItemStack(fieldGuide))
        .displayName(Text.translatable("itemGroup.entomology.entomology"))
        .entries((context, entries) -> {
          // Bug nets
          entries.add(basicBugNet);
          entries.add(ironBugNet);
          entries.add(goldenBugNet);
          entries.add(diamondBugNet);
          entries.add(netheriteBugNet);

          // Tools and containers
          entries.add(specimenJarItem);
          entries.add(fieldGuide);

          // Blocks
          entries.add(researchStationBlock);
          entries.add(displayCaseBlock);
        })
        .build();

    Registry.register(Registries.ITEM_GROUP, ENTOMOLOGY_GROUP, entomologyItemGroup);
  }

  private static Item registerItem(String name, Item item) {
    return Registry.register(Registries.ITEM, id(name), item);
  }

  private static Block registerBlockOnly(String name, Block block) {
    return Registry.register(Registries.BLOCK, id(name), block);
  }

  private static Block registerBlock(String path, Block block) {
    Registry.register(Registries.BLOCK, id(path), block);
    Registry.register(Registries.ITEM, id(path),
        new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id(path)))));
    return block;
  }
}
