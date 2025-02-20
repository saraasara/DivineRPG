package divinerpg.compat.projecte;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import divinerpg.registries.*;
import moze_intel.projecte.api.mapper.*;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import javax.annotation.Nonnull;
import java.util.*;

@EMCMapper
public class ProjectECompat implements IEMCMapper<NormalizedSimpleStack, Long> {
    private static final Map<NormalizedSimpleStack, Long> CUSTOM_EMC_VALUES = new HashMap<>();
    public static void init() {
        //Resources
        register(NSSItem.createItem(ItemRegistry.cyclops_eye_shards.get()), 46);
        register(NSSItem.createItem(ItemRegistry.crab_claw.get()), 150);
        register(NSSItem.createItem(ItemRegistry.aquatic_pellets.get()), 800);
        register(NSSItem.createItem(ItemRegistry.shark_fin.get()), 1020);
        register(NSSItem.createItem(ItemRegistry.whale_fin.get()), 1280);
        register(NSSItem.createItem(ItemRegistry.liopleurodon_teeth.get()), 2242);
        register(NSSItem.createItem(ItemRegistry.liopleurodon_skull.get()), 4942);
        register(NSSItem.createItem(ItemRegistry.realmite_ingot.get()), 384);
        register(NSSItem.createItem(ItemRegistry.arlemite_ingot.get()), 8064);
        register(NSSItem.createItem(ItemRegistry.rupee_ingot.get()), 9216);
        register(NSSItem.createItem(ItemRegistry.healing_stone.get()), 8192);
        register(NSSItem.createItem(ItemRegistry.ice_shards.get()), 910);
        register(NSSItem.createItem(ItemRegistry.terran_shards.get()), 910);
        register(NSSItem.createItem(ItemRegistry.jungle_shards.get()), 910);
        register(NSSItem.createItem(ItemRegistry.molten_shards.get()), 910);
        register(NSSItem.createItem(ItemRegistry.corrupted_shards.get()), 1420);
        register(NSSItem.createItem(ItemRegistry.ender_shards.get()), 1820);
        register(NSSItem.createItem(ItemRegistry.divine_shards.get()), 2048);
        register(NSSItem.createItem(ItemRegistry.torridite_ingot.get()), 1024);
        register(NSSItem.createItem(ItemRegistry.bloodgem.get()), 8256);
        register(NSSItem.createItem(ItemRegistry.purple_blaze.get()), 1536);
        register(NSSItem.createItem(ItemRegistry.fury_fire.get()), 6144);
        register(NSSItem.createItem(ItemRegistry.soulfire_stone.get()), 8092);
        register(NSSItem.createItem(ItemRegistry.anthracite.get()), 256);
        register(NSSItem.createItem(ItemRegistry.olivine.get()), 32);
        register(NSSItem.createItem(ItemRegistry.oxdrite_ingot.get()), 512);
        register(NSSItem.createItem(ItemRegistry.snowflake.get()), 512);
        register(NSSItem.createItem(ItemRegistry.seng_fur.get()), 128);
        register(NSSItem.createItem(ItemRegistry.sabear_fur.get()), 128);
        register(NSSItem.createItem(ItemRegistry.sabear_tooth.get()), 128);
        register(NSSItem.createItem(ItemRegistry.watching_eye.get()), 4096);
        register(NSSItem.createItem(ItemRegistry.color_template.get()), 20121);
        register(NSSItem.createItem(ItemRegistry.aquatic_coating_template.get()), 12192);
        register(NSSItem.createItem(ItemRegistry.aquamarine.get()), 128);
        register(NSSItem.createItem(ItemRegistry.firestock.get()), 64);
        register(NSSItem.createItem(ItemRegistry.lamona.get()), 64);
        register(NSSItem.createItem(ItemRegistry.marsine.get()), 64);
        register(NSSItem.createItem(ItemRegistry.veilo.get()), 64);
        register(NSSItem.createItem(ItemRegistry.eucalyptus_root_seeds.get()), 32);
        register(NSSItem.createItem(ItemRegistry.dungeon_tokens.get()), 17109);
        register(NSSItem.createItem(ItemRegistry.collector_fragments.get()), 1820);
        register(NSSItem.createItem(ItemRegistry.arcanium.get()), 17109);
        register(NSSItem.createItem(ItemRegistry.ancient_key.get()), 49140);
        register(NSSItem.createItem(ItemRegistry.degraded_key.get()), 49140);
        register(NSSItem.createItem(ItemRegistry.sludge_key.get()), 49140);
        register(NSSItem.createItem(ItemRegistry.soul_key.get()), 49140);
        register(NSSItem.createItem(ItemRegistry.eden_soul.get()), 1024);
        register(NSSItem.createItem(ItemRegistry.wildwood_soul.get()), 1536);
        register(NSSItem.createItem(ItemRegistry.apalachia_soul.get()), 2048);
        register(NSSItem.createItem(ItemRegistry.skythern_soul.get()), 4072);
        register(NSSItem.createItem(ItemRegistry.mortum_soul.get()), 6096);
        register(NSSItem.createItem(ItemRegistry.eden_heart.get()), 4096);
        register(NSSItem.createItem(ItemRegistry.wildwood_heart.get()), 6144);
        register(NSSItem.createItem(ItemRegistry.apalachia_heart.get()), 8192);
        register(NSSItem.createItem(ItemRegistry.skythern_heart.get()), 16288);
        register(NSSItem.createItem(ItemRegistry.mortum_heart.get()), 24384);
        register(NSSItem.createItem(ItemRegistry.dirty_pearls.get()), 128);
        register(NSSItem.createItem(ItemRegistry.clean_pearls.get()), 256);
        register(NSSItem.createItem(ItemRegistry.polished_pearls.get()), 512);
        register(NSSItem.createItem(ItemRegistry.shiny_pearls.get()), 1024);
        register(NSSItem.createItem(ItemRegistry.rock_chunks.get()), 2048);
        register(NSSItem.createItem(ItemRegistry.acid.get()), 64);
        register(NSSItem.createItem(ItemRegistry.cannon_template.get()), 384);
        register(NSSItem.createItem(ItemRegistry.claw_template.get()), 384);
        register(NSSItem.createItem(ItemRegistry.backsword_template.get()), 384);
        register(NSSItem.createItem(ItemRegistry.bow_template.get()), 384);
        register(NSSItem.createItem(ItemRegistry.disk_template.get()), 384);
        register(NSSItem.createItem(ItemRegistry.dissipator_template.get()), 640);
        register(NSSItem.createItem(ItemRegistry.hammer_template.get()), 384);
        register(NSSItem.createItem(ItemRegistry.staff_template.get()), 384);
        register(NSSItem.createItem(ItemRegistry.degraded_template.get()), 640);
        register(NSSItem.createItem(ItemRegistry.finished_template.get()), 1280);
        register(NSSItem.createItem(ItemRegistry.glistening_template.get()), 2560);
        register(NSSItem.createItem(ItemRegistry.demonized_template.get()), 2560);
        register(NSSItem.createItem(ItemRegistry.tormented_template.get()), 5120);

        //Food & Drinks
        register(NSSItem.createItem(ItemRegistry.tomato.get()), 64);
        register(NSSItem.createItem(ItemRegistry.white_mushroom.get()), 32);
        register(NSSItem.createItem(ItemRegistry.winterberry.get()), 16);
        register(NSSItem.createItem(ItemRegistry.peppermints.get()), 14);
        register(NSSItem.createItem(ItemRegistry.robbin_egg.get()), 32);
        register(NSSItem.createItem(ItemRegistry.cauldron_flesh.get()), 64);
        register(NSSItem.createItem(ItemRegistry.raw_seng_meat.get()), 64);
        register(NSSItem.createItem(ItemRegistry.raw_wolpertinger_meat.get()), 64);
        register(NSSItem.createItem(ItemRegistry.snow_cones.get()), 64);
        register(NSSItem.createItem(ItemRegistry.chocolate_log.get()), 122);
        register(NSSItem.createItem(ItemRegistry.fruit_cake.get()), 182);
        register(NSSItem.createItem(ItemRegistry.egg_nog.get()), 800);
        register(NSSItem.createItem(ItemRegistry.weak_arcana_potion.get()), 5460);
        register(NSSItem.createItem(ItemRegistry.strong_arcana_potion.get()), 8190);
        register(NSSItem.createItem(ItemRegistry.hitchak.get()), 128);
        register(NSSItem.createItem(ItemRegistry.pinfly.get()), 128);
        register(NSSItem.createItem(ItemRegistry.raw_empowered_meat.get()), 64);
        register(NSSItem.createItem(ItemRegistry.forbidden_fruit.get()), 128);
        register(NSSItem.createItem(ItemRegistry.magic_meat.get()), 128);
        register(NSSItem.createItem(ItemRegistry.moonbulb.get()), 64);
        register(NSSItem.createItem(ItemRegistry.pink_glowbone.get()), 64);
        register(NSSItem.createItem(ItemRegistry.purple_glowbone.get()), 64);
        register(NSSItem.createItem(ItemRegistry.enriched_magic_meat.get()), 256);
        register(NSSItem.createItem(ItemRegistry.sky_flower.get()), 32);
        register(NSSItem.createItem(ItemRegistry.dream_carrot.get()), 64);
        register(NSSItem.createItem(ItemRegistry.dream_sweets.get()), 64);
        register(NSSItem.createItem(ItemRegistry.dream_sours.get()), 256);
        register(NSSItem.createItem(ItemRegistry.dream_cake.get()), 160);
        register(NSSItem.createItem(ItemRegistry.dream_pie.get()), 130);
        register(NSSItem.createItem(ItemRegistry.dream_melon.get()), 96);
        register(NSSItem.createItem(ItemRegistry.honeysuckle.get()), 1);
        register(NSSItem.createItem(ItemRegistry.honeychunk.get()), 1);

        //Equipment & Stuff
        register(NSSItem.createItem(ItemRegistry.frossivence.get()), 24023);
        register(NSSItem.createItem(ItemRegistry.inferno_sword.get()), 4868);
        register(NSSItem.createItem(ItemRegistry.fury_maul.get()), 171753);
        register(NSSItem.createItem(ItemRegistry.icicle_bane.get()), 10240);
        register(NSSItem.createItem(ItemRegistry.glacier_sword.get()), 53006);
        register(NSSItem.createItem(ItemRegistry.frostking_sword.get()), 24077);
        register(NSSItem.createItem(ItemRegistry.enderice.get()), 202080);
        register(NSSItem.createItem(ItemRegistry.icine_sword.get()), 94212);
        register(NSSItem.createItem(ItemRegistry.frozen_maul.get()), 134232);
        register(NSSItem.createItem(ItemRegistry.snowflake_shuriken.get()), 192);
        register(NSSItem.createItem(ItemRegistry.icicle_bow.get()), 58025);
        register(NSSItem.createItem(ItemRegistry.snowstorm_bow.get()), 44676);
        register(NSSItem.createItem(ItemRegistry.sound_of_carols.get()), 21519);
        register(NSSItem.createItem(ItemRegistry.frostclaw_cannon.get()), 13651);
        register(NSSItem.createItem(ItemRegistry.fractite_cannon.get()), 99840);
        register(NSSItem.createItem(ItemRegistry.arcanite_blaster.get()), 307962);
        register(NSSItem.createItem(ItemRegistry.generals_staff.get()), 307962);
        register(NSSItem.createItem(ItemRegistry.meteor_mash.get()), 290853);
        register(NSSItem.createItem(ItemRegistry.starlight.get()), 68436);
        register(NSSItem.createItem(ItemRegistry.staff_of_starlight.get()), 342180);
        register(NSSItem.createItem(ItemRegistry.ghostbane.get()), 342180);
        register(NSSItem.createItem(ItemRegistry.captains_sparkler.get()), 342180);
        register(NSSItem.createItem(ItemRegistry.firefly.get()), 205308);
        register(NSSItem.createItem(ItemRegistry.meriks_missile.get()), 256635);
        register(NSSItem.createItem(ItemRegistry.grenade.get()), 1710);
        register(NSSItem.createItem(ItemRegistry.la_vekor.get()), 102654);
        register(NSSItem.createItem(ItemRegistry.storm_sword.get()), 85545);
        register(NSSItem.createItem(ItemRegistry.arcanite_blade.get()), 513270);
        register(NSSItem.createItem(ItemRegistry.arcanium_saber.get()), 136872);
        register(NSSItem.createItem(ItemRegistry.shadow_saber.get()), 136872);
        register(NSSItem.createItem(ItemRegistry.livicia_sword.get()), 513111);
        register(NSSItem.createItem(ItemRegistry.vemos_helmet.get()), 102654);
        register(NSSItem.createItem(ItemRegistry.vemos_chestplate.get()), 171090);
        register(NSSItem.createItem(ItemRegistry.vemos_leggings.get()), 171090);
        register(NSSItem.createItem(ItemRegistry.vemos_boots.get()), 102654);
        register(NSSItem.createItem(ItemRegistry.korma_helmet.get()), 102654);
        register(NSSItem.createItem(ItemRegistry.korma_chestplate.get()), 171090);
        register(NSSItem.createItem(ItemRegistry.korma_leggings.get()), 171090);
        register(NSSItem.createItem(ItemRegistry.korma_boots.get()), 102654);
        register(NSSItem.createItem(ItemRegistry.angelic_chestplate.get()), 381784);
        register(NSSItem.createItem(ItemRegistry.wizards_book.get()), 34218);
        register(NSSItem.createItem(ItemRegistry.wither_reaper_helmet.get()), 256);
        register(NSSItem.createItem(ItemRegistry.wither_reaper_chestplate.get()), 256);
        register(NSSItem.createItem(ItemRegistry.wither_reaper_leggings.get()), 256);
        register(NSSItem.createItem(ItemRegistry.wither_reaper_boots.get()), 256);
        register(NSSItem.createItem(ItemRegistry.skeleman_helmet.get()), 2800);
        register(NSSItem.createItem(ItemRegistry.skeleman_chestplate.get()), 4160);
        register(NSSItem.createItem(ItemRegistry.skeleman_leggings.get()), 4160);
        register(NSSItem.createItem(ItemRegistry.skeleman_boots.get()), 2800);
        register(NSSItem.createItem(ItemRegistry.jack_o_man_helmet.get()), 2464);
        register(NSSItem.createItem(ItemRegistry.jack_o_man_chestplate.get()), 4208);
        register(NSSItem.createItem(ItemRegistry.jack_o_man_leggings.get()), 4208);
        register(NSSItem.createItem(ItemRegistry.jack_o_man_boots.get()), 2464);
        register(NSSItem.createItem(ItemRegistry.scythe.get()), 7696);
        register(NSSItem.createItem(FluidRegistry.SMOLDERING_TAR_BUCKET.get()), 832);
        register(NSSItem.createItem(ItemRegistry.gem_fin_bucket.get()), 832);
        register(NSSItem.createItem(ItemRegistry.cauldron_fish_bucket.get()), 832);
        register(NSSItem.createItem(ItemRegistry.miners_amulet.get()), 3840);
        register(NSSItem.createItem(ItemRegistry.band_of_lheiva_hunting.get()), 25600);
        register(NSSItem.createItem(ItemRegistry.dream_flint.get()), 10240);
        register(NSSItem.createItem(ItemRegistry.moon_clock.get()), 40960);
        register(NSSItem.createItem(ItemRegistry.teaker_arrow.get()), 8);
        register(NSSItem.createItem(ItemRegistry.darven_arrow.get()), 16);
        register(NSSItem.createItem(ItemRegistry.pardimal_arrow.get()), 32);
        register(NSSItem.createItem(ItemRegistry.karos_arrow.get()), 48);
        register(NSSItem.createItem(ItemRegistry.ever_arrow.get()), 128);
        register(NSSItem.createItem(ItemRegistry.dream_axe.get()), 1278);
        register(NSSItem.createItem(ItemRegistry.dream_pickaxe.get()), 1278);
        register(NSSItem.createItem(ItemRegistry.dream_shovel.get()), 1278);
        register(NSSItem.createItem(ItemRegistry.karos_rockmaul.get()), 51200);

        //Dirt, Grass, Sand, etc.
        register(NSSItem.createItem(BlockRegistry.frozenDirt.get()), 1);
        register(NSSItem.createItem(BlockRegistry.arcaniteDirt.get()), 16);
        register(NSSItem.createItem(BlockRegistry.edenDirt.get()), 1);
        register(NSSItem.createItem(BlockRegistry.wildwoodDirt.get()), 1);
        register(NSSItem.createItem(BlockRegistry.apalachiaDirt.get()), 1);
        register(NSSItem.createItem(BlockRegistry.skythernDirt.get()), 1);
        register(NSSItem.createItem(BlockRegistry.mortumDirt.get()), 1);
        register(NSSItem.createItem(BlockRegistry.dreamDirt.get()), 1);
        register(NSSItem.createItem(BlockRegistry.frozenGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.arcaniteGrass.get()), 16);
        register(NSSItem.createItem(BlockRegistry.edenGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.wildwoodGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.apalachiaGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.skythernGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.mortumGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.flameGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.dreamGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.evergrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.scorchedGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.gelidite.get()), 1);
        register(NSSItem.createItem(BlockRegistry.frozenGravel.get()), 4);
        register(NSSItem.createItem(BlockRegistry.soulSludgeBreakable.get()), 49);
        register(NSSItem.createItem(BlockRegistry.hiveWall.get()), 1);

        //Stone & Bricks
        register(NSSItem.createItem(BlockRegistry.asphalt.get()), 16);
        register(NSSItem.createItem(BlockRegistry.milkStone.get()), 1);
        register(NSSItem.createItem(BlockRegistry.frozenStone.get()), 1);
        register(NSSItem.createItem(BlockRegistry.cobbledFrozenStone.get()), 1);
        register(NSSItem.createItem(BlockRegistry.snowBricks.get()), 16);
        register(NSSItem.createItem(BlockRegistry.icyStone.get()), 16);
        register(NSSItem.createItem(BlockRegistry.icyBricks.get()), 16);
        register(NSSItem.createItem(BlockRegistry.chiseledIcyBricks.get()), 16);
        register(NSSItem.createItem(BlockRegistry.icicle.get()), 16);
        register(NSSItem.createItem(BlockRegistry.thermalVent.get()), 16);
        register(NSSItem.createItem(BlockRegistry.icyStone.get()), 16);
        register(NSSItem.createItem(BlockRegistry.coalstone.get()), 16);
        register(NSSItem.createItem(BlockRegistry.cobaltite.get()), 32);
        register(NSSItem.createItem(BlockRegistry.arcaniteStone.get()), 1);
        register(NSSItem.createItem(BlockRegistry.soulStoneBreakable.get()), 16);
        register(NSSItem.createItem(BlockRegistry.ancientStoneBreakable.get()), 16);
        register(NSSItem.createItem(BlockRegistry.ancientBricksBreakable.get()), 16);
        register(NSSItem.createItem(BlockRegistry.degradedBricksBreakable.get()), 16);
        register(NSSItem.createItem(BlockRegistry.ancientTileBreakable.get()), 16);
        register(NSSItem.createItem(BlockRegistry.arcaniumMetalBreakable.get()), 16);
        register(NSSItem.createItem(BlockRegistry.arcaniumPowerBreakable.get()), 256);
        register(NSSItem.createItem(BlockRegistry.twilightStone.get()), 1);
        register(NSSItem.createItem(BlockRegistry.divineMossStone.get()), 9);
        register(NSSItem.createItem(BlockRegistry.dreamStone.get()), 1);
        register(NSSItem.createItem(BlockRegistry.lunaStone.get()), 16);
        register(NSSItem.createItem(BlockRegistry.lunaBricks.get()), 64);
        register(NSSItem.createItem(BlockRegistry.redDreamBricks.get()), 8);
        register(NSSItem.createItem(BlockRegistry.darkDreamBricks.get()), 8);
        register(NSSItem.createItem(BlockRegistry.lightDreamBricks.get()), 8);

        //Plants & Fungi
        register(NSSItem.createItem(BlockRegistry.brittleGrass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.brittleMoss.get()), 12);
        register(NSSItem.createItem(BlockRegistry.winterberryBush.get()), 32);
        register(NSSItem.createItem(BlockRegistry.winterberryVinesHead.get()), 32);
        register(NSSItem.createItem(BlockRegistry.arcanaBrush.get()), 1);
        register(NSSItem.createItem(BlockRegistry.arcanaBush.get()), 1);
        register(NSSItem.createItem(BlockRegistry.arcaniteMoss.get()), 12);
        register(NSSItem.createItem(BlockRegistry.arcaniteVinesHead.get()), 16);
        register(NSSItem.createItem(BlockRegistry.weedwoodVine.get()), 8);
        register(NSSItem.createItem(BlockRegistry.blossomedWeedwoodVine.get()), 8);
        register(NSSItem.createItem(BlockRegistry.edenBrush.get()), 16);
        register(NSSItem.createItem(BlockRegistry.wildwoodVine.get()), 16);
        register(NSSItem.createItem(BlockRegistry.moonlightFern.get()), 16);
        register(NSSItem.createItem(BlockRegistry.wildwoodTallgrass.get()), 16);
        register(NSSItem.createItem(BlockRegistry.truffle.get()), 144);
        register(NSSItem.createItem(BlockRegistry.apalachiaTallgrass.get()), 32);
        register(NSSItem.createItem(BlockRegistry.skythernBrush.get()), 32);
        register(NSSItem.createItem(BlockRegistry.mortumBrush.get()), 32);
        register(NSSItem.createItem(BlockRegistry.dreamglow.get()), 16);

        //Misc Blocks
        register(NSSItem.createItem(BlockRegistry.coldHellfireSponge.get()), 122488);
        register(NSSItem.createItem(BlockRegistry.coalstoneFurnace.get()), 128);
        register(NSSItem.createItem(BlockRegistry.frostedChest.get()), 1024);
        register(NSSItem.createItem(BlockRegistry.presentBox.get()), 2048);
        register(NSSItem.createItem(BlockRegistry.steelDoor.get()), 512);
        register(NSSItem.createItem(BlockRegistry.redCandyCane.get()), 126);
        register(NSSItem.createItem(BlockRegistry.yellowCandyCane.get()), 126);
        register(NSSItem.createItem(BlockRegistry.greenCandyCane.get()), 126);
        register(NSSItem.createItem(BlockRegistry.blueCandyCane.get()), 126);
        register(NSSItem.createItem(BlockRegistry.pinkCandyCane.get()), 126);
        register(NSSItem.createItem(BlockRegistry.redFairyLights.get()), 14);
        register(NSSItem.createItem(BlockRegistry.yellowFairyLights.get()), 14);
        register(NSSItem.createItem(BlockRegistry.greenFairyLights.get()), 14);
        register(NSSItem.createItem(BlockRegistry.blueFairyLights.get()), 14);
        register(NSSItem.createItem(BlockRegistry.purpleFairyLights.get()), 14);
        register(NSSItem.createItem(BlockRegistry.frostedGlass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.workshopLamp.get()), 256);
        register(NSSItem.createItem(BlockRegistry.arcaniteTubes.get()), 14);
        register(NSSItem.createItem(BlockRegistry.arcaniteLadder.get()), 14);
        register(NSSItem.createItem(BlockRegistry.dungeonLampBreakable.get()), 256);
        register(NSSItem.createItem(BlockRegistry.demonFurnace.get()), 153981);
        register(NSSItem.createItem(BlockRegistry.greenlightFurnace.get()), 51327);
        register(NSSItem.createItem(BlockRegistry.moltenFurnace.get()), 34218);
        register(NSSItem.createItem(BlockRegistry.moonlightFurnace.get()), 85545);
        register(NSSItem.createItem(BlockRegistry.oceanfireFurnace.get()), 68436);
        register(NSSItem.createItem(BlockRegistry.whitefireFurnace.get()), 119763);
        register(NSSItem.createItem(BlockRegistry.starBridge.get()), 8554);
        register(NSSItem.createItem(BlockRegistry.slimeLight.get()), 1920);
        register(NSSItem.createItem(BlockRegistry.elevantium.get()), 5703);
        register(NSSItem.createItem(BlockRegistry.acceleron.get()), 17109);
        register(NSSItem.createItem(BlockRegistry.barredDoor.get()), 512);
        register(NSSItem.createItem(BlockRegistry.smoothGlass.get()), 1);
        register(NSSItem.createItem(BlockRegistry.hiveEgg.get()), 16384);
        register(NSSItem.createItem(BlockRegistry.firelight.get()), 512);
        register(NSSItem.createItem(BlockRegistry.fireCrystal.get()), 512);
        register(NSSItem.createItem(BlockRegistry.dreamLamp.get()), 1792);
        register(NSSItem.createItem(BlockRegistry.cellLamp.get()), 256);
        register(NSSItem.createItem(BlockRegistry.villageLamp.get()), 256);
        register(NSSItem.createItem(BlockRegistry.metalCaging.get()), 64);
    }
    public static void register(@Nonnull NormalizedSimpleStack stack, long emcValue) {CUSTOM_EMC_VALUES.put(stack, emcValue);}
    @Override public String getName() {return "DivineRPGMapper";}
    @Override public String getDescription() {return "Adds EMC to DivineRPG";}
    @Override public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> iMappingCollector, CommentedFileConfig commentedFileConfig, ReloadableServerResources reloadableServerResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
        for(Map.Entry<NormalizedSimpleStack, Long> entry : CUSTOM_EMC_VALUES.entrySet()) {
            NormalizedSimpleStack normStack = entry.getKey();
            long value = entry.getValue();
            iMappingCollector.setValueBefore(normStack, value);
        }
    }
}