package divinerpg.dimensions.arcana;

import divinerpg.objects.blocks.arcana.BlockArcanaPortalFrame;
import divinerpg.objects.items.vanilla.ItemTeleportationCrystal;
import divinerpg.registry.ModBlocks;
import divinerpg.registry.ModDimensions;
import divinerpg.utils.NbtUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ArcanaTeleporter extends Teleporter {

    /**
     * Storing long value here
     */
    private final String ArcanaKeyPos = "ArcanaPos";
    protected WorldServer myWorld;

    public ArcanaTeleporter(WorldServer var1) {
        super(var1);
        this.myWorld = var1;
    }

    public static int getTopBlock(World world, int x, int z) {
        for (int i = 128; i > 0; i--) {
            if (world.getBlockState(new BlockPos(x, i, z)) != Blocks.AIR.getDefaultState()) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        super.placeEntity(world, entity, yaw);

        if (world.provider.getDimensionType() == ModDimensions.arcanaDimension && entity instanceof EntityPlayer) {
            NBTTagCompound tag = NbtUtil.getPersistedDivineTag((EntityPlayer) entity);
            if (tag.hasKey(ArcanaKeyPos)) {
                BlockPos pos = BlockPos.fromLong(tag.getLong(ArcanaKeyPos));

                // using this value as precision of teleporting (7 blocks radius is enough I guess)
                double precisionSq = 7 * 7;

                if (entity.getPosition().distanceSq(pos) > precisionSq) {
                    // Just using the old code here
                    ItemTeleportationCrystal.teleportToDimension(entity, world.provider.getDimensionType(), pos);
                }
            }
        }
    }

    public boolean findPortalBlockNearEntity(Entity entity, int yLimit) {
        int chunkX = (MathHelper.floor(entity.posX) & ~0xf);
        int chunkZ = (MathHelper.floor(entity.posZ) & ~0xf);
        int y;
        double offset;

        if (entity.dimension == ModDimensions.arcanaDimension.getId()) {
            offset = 2.0;
        } else {
            offset = 1.5;
        }
        for (y = 1; y < yLimit; y++) {
            for (int x2 = chunkX; x2 < chunkX + 16; x2++) {
                for (int z2 = chunkZ; z2 < chunkZ + 16; z2++) {
                    if (this.myWorld.getBlockState(new BlockPos(x2, y, z2)) == ModBlocks.arcanaPortal
                            .getDefaultState()) {
                        if (myWorld.provider.getDimension() == 0) {
                            entity.setLocationAndAngles(x2 + offset, getTopBlock(myWorld, x2, z2), z2 + offset, entity.rotationYaw, 0.0F);
                        } else {
                            entity.setLocationAndAngles(x2 + offset, y + 0.5D, z2 + offset, entity.rotationYaw, 0.0F);

                            if (entity instanceof EntityPlayer) {

                                //
                                // Should save position here
                                //

                                NBTTagCompound tag = NbtUtil.getPersistedDivineTag((EntityPlayer) entity);
                                tag.setLong(ArcanaKeyPos, entity.getPosition().toLong());
                            }
                        }
                        entity.motionX = entity.motionY = entity.motionZ = 0.0D;
                        return true;
                    }
                }
            }

        }

        return false;

    }

    @Override
    public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
        if (entity.dimension == ModDimensions.arcanaDimension.getId()) {
            int chunkX = (MathHelper.floor(entity.posX) & ~0xf);
            int chunkZ = (MathHelper.floor(entity.posZ) & ~0xf);
            int y;

            // Find existing portal
            boolean foundPortal = findPortalBlockNearEntity(entity, 40);
            if (foundPortal) {
                return true;
            }

            // If there is no existsing portal, find a location to create a new portal room, avoiding double high rooms
            for (y = 8; y < 40; y += 8) {
                if (this.myWorld.getBlockState(new BlockPos(chunkX + 7, y, chunkZ + 7)) != Blocks.AIR.getDefaultState()
                        && this.myWorld.getBlockState(new BlockPos(chunkX + 7, y + 8, chunkZ + 7)) != Blocks.AIR
                        .getDefaultState()) {
                    generatePortalRoom(this.myWorld, new BlockPos(chunkX, y, chunkZ));
                    foundPortal = findPortalBlockNearEntity(entity, 40);
                    if (foundPortal) {
                        return true;
                    }
                }
            }
        } else {
            findPortalBlockNearEntity(entity, 256);
            entity.motionX = entity.motionY = entity.motionZ = 0.0D;
            return true;
        }

        return false;
    }

    private void generatePortalRoom(World world, BlockPos pos) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        IBlockState arcanaPortal = ModBlocks.arcanaPortal.getDefaultState();
        IBlockState arcanaHardPortalFrameNorth = ModBlocks.arcanaHardPortalFrame.getDefaultState();
        IBlockState arcanaHardPortalFrameSouth = ModBlocks.arcanaHardPortalFrame.getDefaultState()
                .withProperty(BlockArcanaPortalFrame.FACING, EnumFacing.SOUTH);
        IBlockState arcanaHardPortalFrameEast = ModBlocks.arcanaHardPortalFrame.getDefaultState()
                .withProperty(BlockArcanaPortalFrame.FACING, EnumFacing.EAST);
        IBlockState arcanaHardPortalFrameWest = ModBlocks.arcanaHardPortalFrame.getDefaultState()
                .withProperty(BlockArcanaPortalFrame.FACING, EnumFacing.WEST);
        IBlockState dungeonBricks = ModBlocks.degradedBrick.getDefaultState();
        IBlockState ancientbricks = ModBlocks.ancientBrick.getDefaultState();
        IBlockState ancientTile = ModBlocks.ancientTile.getDefaultState();
        IBlockState arcanaPower = ModBlocks.arcaniumPower.getDefaultState();

        for (int n = 0; n < 16; n++) {
            for (int m = 0; m < 16; m++) {
                for (int o = 1; o < 8; o++) {
                    world.setBlockState(new BlockPos(x + n, y + o, z + m), Blocks.AIR.getDefaultState());
                }
            }
        }

        world.setBlockState(new BlockPos(x + 0, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 1), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 3), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 5), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 10), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 12), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 7), arcanaPower);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 8), arcanaPower);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 4, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 1), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 3), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 5), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 7), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 9), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 11), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 13), ancientTile);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 5, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 0, y + 6, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 7), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 8), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 0, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 1), ModBlocks.dungeonLamp.getDefaultState());
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 14), ModBlocks.dungeonLamp.getDefaultState());
        world.setBlockState(new BlockPos(x + 1, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 2, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 1, y + 2, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 1, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 5, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 1, y + 5, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 1, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 1, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 5, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 2, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 2, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 2, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 2, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 3, y + 2, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 3, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 5, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 3, y + 5, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 3, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 3, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 5, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 4, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 4, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 4, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 1, z + 6), arcanaHardPortalFrameWest);
        world.setBlockState(new BlockPos(x + 5, y + 1, z + 7), arcanaHardPortalFrameWest);
        world.setBlockState(new BlockPos(x + 5, y + 1, z + 8), arcanaHardPortalFrameWest);
        world.setBlockState(new BlockPos(x + 5, y + 1, z + 9), arcanaHardPortalFrameWest);
        world.setBlockState(new BlockPos(x + 5, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 2, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 5, y + 2, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 5, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 5, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 5, y + 5, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 5, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 5, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 5), arcanaHardPortalFrameNorth);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 6), arcanaPortal);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 7), arcanaPortal);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 8), arcanaPortal);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 9), arcanaPortal);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 10), arcanaHardPortalFrameSouth);
        world.setBlockState(new BlockPos(x + 6, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 5, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 6, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 6, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 6, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 1, z + 5), arcanaHardPortalFrameNorth);
        world.setBlockState(new BlockPos(x + 7, y + 1, z + 6), arcanaPortal);
        world.setBlockState(new BlockPos(x + 7, y + 1, z + 7), arcanaPortal);
        world.setBlockState(new BlockPos(x + 7, y + 1, z + 8), arcanaPortal);
        world.setBlockState(new BlockPos(x + 7, y + 1, z + 9), arcanaPortal);
        world.setBlockState(new BlockPos(x + 7, y + 1, z + 10), arcanaHardPortalFrameSouth);
        world.setBlockState(new BlockPos(x + 7, y + 3, z + 0), arcanaPower);
        world.setBlockState(new BlockPos(x + 7, y + 3, z + 15), arcanaPower);
        world.setBlockState(new BlockPos(x + 7, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 5, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 7, y + 5, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 7, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 7, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 1, z + 5), arcanaHardPortalFrameNorth);
        world.setBlockState(new BlockPos(x + 8, y + 1, z + 6), arcanaPortal);
        world.setBlockState(new BlockPos(x + 8, y + 1, z + 7), arcanaPortal);
        world.setBlockState(new BlockPos(x + 8, y + 1, z + 8), arcanaPortal);
        world.setBlockState(new BlockPos(x + 8, y + 1, z + 9), arcanaPortal);
        world.setBlockState(new BlockPos(x + 8, y + 1, z + 10), arcanaHardPortalFrameSouth);
        world.setBlockState(new BlockPos(x + 8, y + 3, z + 0), arcanaPower);
        world.setBlockState(new BlockPos(x + 8, y + 3, z + 15), arcanaPower);
        world.setBlockState(new BlockPos(x + 8, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 5, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 8, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 8, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 8, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 5), arcanaHardPortalFrameNorth);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 6), arcanaPortal);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 7), arcanaPortal);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 8), arcanaPortal);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 9), arcanaPortal);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 10), arcanaHardPortalFrameSouth);
        world.setBlockState(new BlockPos(x + 9, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 5, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 9, y + 5, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 9, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 9, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 1, z + 6), arcanaHardPortalFrameEast);
        world.setBlockState(new BlockPos(x + 10, y + 1, z + 7), arcanaHardPortalFrameEast);
        world.setBlockState(new BlockPos(x + 10, y + 1, z + 8), arcanaHardPortalFrameEast);
        world.setBlockState(new BlockPos(x + 10, y + 1, z + 9), arcanaHardPortalFrameEast);
        world.setBlockState(new BlockPos(x + 10, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 2, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 10, y + 2, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 10, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 5, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 10, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 10, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 10, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 5, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 11, y + 5, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 11, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 11, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 2, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 12, y + 2, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 12, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 5, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 12, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 12, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 12, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 5, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 13, y + 5, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 13, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 13, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 1), ModBlocks.dungeonLamp.getDefaultState());
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 14), ModBlocks.dungeonLamp.getDefaultState());
        world.setBlockState(new BlockPos(x + 14, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 2, z + 0), ancientTile);
        world.setBlockState(new BlockPos(x + 14, y + 2, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 14, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 4, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 5, z + 15), ancientTile);
        world.setBlockState(new BlockPos(x + 14, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 6, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 14, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 14, y + 7, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 0, z + 15), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 1, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 1), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 3), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 5), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 10), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 12), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 14), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 2, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 7), arcanaPower);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 8), arcanaPower);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 3, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 4, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 1), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 3), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 5), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 7), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 9), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 11), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 13), ancientTile);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 5, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 0), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 1), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 2), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 3), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 4), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 5), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 6), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 7), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 8), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 9), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 10), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 11), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 12), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 13), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 14), ancientbricks);
        world.setBlockState(new BlockPos(x + 15, y + 6, z + 15), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 0), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 1), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 2), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 3), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 4), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 5), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 6), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 7), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 8), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 9), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 10), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 11), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 12), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 13), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 14), dungeonBricks);
        world.setBlockState(new BlockPos(x + 15, y + 7, z + 15), dungeonBricks);
    }

    @Override
    public boolean makePortal(Entity entity) {
        return false;
    }
}
