package net.eaglercraft.eaglertone;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.client.multiplayer.WorldClient;

/** Eaglercraft 1.12.2 adapter for the version-neutral EaglerTone core. */
public final class EaglerToneClientAdapter12 {
    private static final EaglerToneEngine ENGINE = new EaglerToneEngine();
    private static int pathIndex;
    private static World lastWorld;
    private static EaglerToneWorldCache worldCache;
    private static long blockNavigationKey = Long.MIN_VALUE;
    private static double lastPlayerX;
    private static double lastPlayerY;
    private static double lastPlayerZ;
    private static int stuckTicks;

    private EaglerToneClientAdapter12() {
    }

    public static EaglerToneEngine engine() {
        return ENGINE;
    }

    public static void enable() {
        ENGINE.enable();
    }

    public static void disable() {
        ENGINE.disable();
        ENGINE.clearPlans();
        pathIndex = 0;
        lastWorld = null;
        worldCache = null;
        blockNavigationKey = Long.MIN_VALUE;
        stuckTicks = 0;
    }

    /** Replans only while a valid world/player context and active goal exist. */
    public static void tick(Minecraft minecraft) {
        if (minecraft == null || minecraft.world != lastWorld) {
            if (lastWorld != null) {
                ENGINE.disable();
            }
            lastWorld = minecraft == null ? null : minecraft.world;
            pathIndex = 0;
            blockNavigationKey = Long.MIN_VALUE;
            worldCache = lastWorld == null ? null : new EaglerToneWorldCache(new WorldView(lastWorld), 16384);
        }
        if (minecraft == null || minecraft.world == null || minecraft.player == null) {
            return;
        }
        if (minecraft.currentScreen != null) {
            // Do not leave automation armed while a GUI, chat box, or pause
            // screen owns input. disable() also clears the stale path/goal.
            if (ENGINE.isEnabled()) {
                ENGINE.disable();
                pathIndex = 0;
            }
            return;
        }
        if (!ENGINE.isEnabled()) {
            return;
        }
        if (processBlockPlan(minecraft)) {
            return;
        }
        if (ENGINE.getGoal() == null) {
            return;
        }
        if (ENGINE.getPath().isEmpty()) {
            pathIndex = 0;
            BlockPos position = new BlockPos(minecraft.player.posX, minecraft.player.posY, minecraft.player.posZ);
            if (worldCache == null) {
                worldCache = new EaglerToneWorldCache(new WorldView(minecraft.world), 16384);
            }
            ENGINE.plan(worldCache, position.getX(), position.getY(), position.getZ());
        }
    }

    /**
     * Executes one block-plan step through the normal 1.12.2 client controller.
     * The controller performs the normal reach, permission, inventory, and server
     * validation; this adapter never constructs interaction packets itself.
     */
    private static boolean processBlockPlan(Minecraft minecraft) {
        EaglerToneBlockPlan plan = ENGINE.getBlockPlan();
        EntityPlayerSP player = minecraft.player;
        PlayerControllerMP controller = minecraft.playerController;
        if (plan == null || plan.isComplete() || player == null || controller == null
                || minecraft.world == null) {
            return false;
        }
        EaglerToneBlockPlan.Entry entry = plan.current();
        BlockPos target = new BlockPos(entry.x, entry.y, entry.z);
        long targetKey = target.toLong();
        if (!minecraft.world.isBlockLoaded(target)) {
            return false;
        }
        if (!withinReach(player, target, controller.getBlockReachDistance())) {
            if (blockNavigationKey != targetKey) {
                blockNavigationKey = targetKey;
                ENGINE.setGoal(EaglerToneEngine.Goal.getToBlock(entry.x, entry.y, entry.z));
            }
            return false;
        }
        blockNavigationKey = Long.MIN_VALUE;
        if (entry.operation == EaglerToneBlockPlan.Operation.BREAK) {
            if (minecraft.world.isAirBlock(target)) {
                plan.advance();
                return true;
            }
            selectBestTool(player, minecraft.world, target);
            if (!canSee(minecraft.world, player, target)) {
                return false;
            }
            faceTarget(player, target);
            controller.onPlayerDamageBlock(target, EnumFacing.func_190914_a(target, player));
            if (minecraft.world.isAirBlock(target)) {
                plan.advance();
            }
            return true;
        }

        if (!minecraft.world.isAirBlock(target)) {
            plan.advance();
            return true;
        }
        ItemBlock itemBlock = findBlockItem(player, entry.block);
        if (itemBlock == null) {
            ENGINE.cancel();
            return true;
        }
        int slot = findHotbarSlot(player, itemBlock);
        if (slot < 0) {
            ENGINE.cancel();
            return true;
        }
        BlockPos support = null;
        EnumFacing side = null;
        for (EnumFacing candidate : EnumFacing._VALUES) {
            BlockPos possible = target.offset(candidate);
            if (minecraft.world.isBlockLoaded(possible)
                    && minecraft.world.getBlockState(possible).isNormalCube()) {
                support = possible;
                side = candidate.getOpposite();
                break;
            }
        }
        if (support == null || !withinReach(player, support, controller.getBlockReachDistance())
                || !canSee(minecraft.world, player, support)) {
            ENGINE.cancel();
            return true;
        }
        player.inventory.currentItem = slot;
        faceTarget(player, support);
        Vec3d hit = new Vec3d(support.getX() + 0.5D + side.getFrontOffsetX() * 0.5D,
                support.getY() + 0.5D + side.getFrontOffsetY() * 0.5D,
                support.getZ() + 0.5D + side.getFrontOffsetZ() * 0.5D);
        EnumActionResult result = controller.processRightClickBlock(player, (WorldClient) minecraft.world,
                support, side, hit, EnumHand.MAIN_HAND);
        if (result == EnumActionResult.SUCCESS || !minecraft.world.isAirBlock(target)) {
            plan.advance();
        }
        if (plan.isComplete()) {
            blockNavigationKey = Long.MIN_VALUE;
        }
        return true;
    }

    private static boolean withinReach(EntityPlayerSP player, BlockPos target, float reach) {
        double dx = target.getX() + 0.5D - player.posX;
        double dy = target.getY() + 0.5D - (player.posY + player.getEyeHeight());
        double dz = target.getZ() + 0.5D - player.posZ;
        return dx * dx + dy * dy + dz * dz <= (double) reach * reach;
    }

    private static boolean canSee(World world, EntityPlayerSP player, BlockPos target) {
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d end = new Vec3d(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D);
        RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
        return hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK && target.equals(hit.getBlockPos());
    }

    private static void faceTarget(EntityPlayerSP player, BlockPos target) {
        double dx = target.getX() + 0.5D - player.posX;
        double dy = target.getY() + 0.5D - (player.posY + player.getEyeHeight());
        double dz = target.getZ() + 0.5D - player.posZ;
        player.rotationYaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI - 90.0D);
        player.rotationPitch = (float) (-Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)) * 180.0D / Math.PI);
    }

    private static ItemBlock findBlockItem(EntityPlayerSP player, String name) {
        Block block = Block.getBlockFromName(name);
        if (block != null) {
            Item item = Item.getItemFromBlock(block);
            return item instanceof ItemBlock ? (ItemBlock) item : null;
        }
        Item item = Item.getByNameOrId(name);
        return item instanceof ItemBlock ? (ItemBlock) item : null;
    }

    private static void selectBestTool(EntityPlayerSP player, World world, BlockPos target) {
        net.minecraft.block.state.IBlockState state = world.getBlockState(target);
        int bestSlot = player.inventory.currentItem;
        float bestSpeed = 0.0F;
        for (int slot = 0; slot < 9; ++slot) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!stack.func_190926_b()) {
                float speed = stack.getStrVsBlock(state);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = slot;
                }
            }
        }
        player.inventory.currentItem = bestSlot;
    }

    private static int findHotbarSlot(EntityPlayerSP player, ItemBlock item) {
        for (int slot = 0; slot < 9; ++slot) {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!stack.func_190926_b() && stack.getItem() == item) {
                return slot;
            }
        }
        return -1;
    }

    /** Applies one ordinary movement input update toward the current path node. */
    public static void applyMovement(net.minecraft.client.entity.EntityPlayerSP player) {
        if (!ENGINE.isEnabled() || player == null || player.movementInput == null || ENGINE.getPath().isEmpty()) {
            return;
        }
        double moved = Math.abs(player.posX - lastPlayerX) + Math.abs(player.posY - lastPlayerY)
                + Math.abs(player.posZ - lastPlayerZ);
        if (moved < 0.01D) {
            ++stuckTicks;
        } else {
            stuckTicks = 0;
        }
        lastPlayerX = player.posX;
        lastPlayerY = player.posY;
        lastPlayerZ = player.posZ;
        if (stuckTicks >= 40) {
            ENGINE.requestReplan();
            pathIndex = 0;
            stuckTicks = 0;
            return;
        }
        // The input object was populated by Minecraft immediately before this hook.
        // Any non-zero user input is an explicit handoff back to the player.
        if (Math.abs(player.movementInput.field_192832_b) > 0.01F
                || Math.abs(player.movementInput.moveStrafe) > 0.01F) {
            ENGINE.disable();
            pathIndex = 0;
            return;
        }

        java.util.List<EaglerTonePathfinder.Node> path = ENGINE.getPath();
        if (pathIndex >= path.size()) {
            ENGINE.completeGoal();
            pathIndex = 0;
            return;
        }
        EaglerTonePathfinder.Node target = path.get(pathIndex);
        double dx = target.x + 0.5D - player.posX;
        double dz = target.z + 0.5D - player.posZ;
        if (dx * dx + dz * dz < 0.30D && Math.abs(target.y - player.posY) < 1.25D) {
            ++pathIndex;
            if (pathIndex >= path.size()) {
                ENGINE.completeGoal();
                pathIndex = 0;
                player.movementInput.field_192832_b = 0.0F;
                player.movementInput.moveStrafe = 0.0F;
                player.movementInput.jump = false;
                return;
            }
            target = path.get(pathIndex);
            dx = target.x + 0.5D - player.posX;
            dz = target.z + 0.5D - player.posZ;
        }
        player.rotationYaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI - 90.0D);
        player.movementInput.field_192832_b = 1.0F;
        player.movementInput.moveStrafe = 0.0F;
        player.movementInput.jump = target.y > Math.floor(player.posY + 0.1D);
        player.movementInput.sneak = target.y < Math.floor(player.posY - 0.1D);
        if (((Boolean) ENGINE.getSettings().get("allowSprint")).booleanValue()
                && player.getFoodStats().getFoodLevel() > 6 && !player.isSneaking()) {
            player.setSprinting(true);
        }
    }

    /** Replans after the server corrects player position or rotation. */
    public static void onServerCorrection() {
        if (ENGINE.isEnabled() && ENGINE.getGoal() != null) {
            ENGINE.requestReplan();
            pathIndex = 0;
            stuckTicks = 0;
        }
    }

    /** Invalidates one changed block so the next replan observes server updates. */
    public static void onBlockChanged(int x, int y, int z) {
        if (worldCache != null) {
            worldCache.invalidate(x, y, z);
        }
        if (ENGINE.getGoal() != null && !ENGINE.getPath().isEmpty()) {
            ENGINE.requestReplan();
        }
    }

    /** Invalidates a chunk after chunk load/unload or a bulk block update. */
    public static void onChunkChanged(int chunkX, int chunkZ) {
        if (worldCache != null) {
            worldCache.invalidateChunk(chunkX, chunkZ);
        }
        if (ENGINE.getGoal() != null) {
            ENGINE.requestReplan();
        }
    }

    /** Returns a snapshot of client-visible entities; no server query is issued. */
    public static java.util.List<EaglerToneEntity> entities(World world) {
        java.util.List<EaglerToneEntity> result = new java.util.ArrayList<EaglerToneEntity>();
        if (world == null || world.loadedEntityList == null) {
            return result;
        }
        for (Entity entity : world.loadedEntityList) {
            if (entity != null) {
                result.add(new EaglerToneEntity(entity.getEntityId(), entity.getName(),
                        entity.getClass().getSimpleName(), entity.posX, entity.posY, entity.posZ,
                        entity.isEntityAlive(), entity instanceof IMob));
            }
        }
        return java.util.Collections.unmodifiableList(result);
    }

    public static boolean executeCommand(String command) {
        boolean accepted = ENGINE.executeCommand(command);
        if (accepted && ENGINE.getPath().isEmpty()) {
            pathIndex = 0;
        }
        return accepted;
    }

    /** Converts the 1.12 world API into the core's loader-neutral contract. */
    private static final class WorldView implements EaglerToneWorld {
        private final World world;

        private WorldView(World world) {
            this.world = world;
        }

        @Override
        public boolean isPassable(int x, int y, int z) {
            if (!isLoaded(x, y, z)) {
                return false;
            }
            BlockPos position = new BlockPos(x, y, z);
            return world.isAirBlock(position) || world.getBlockState(position).getBlock().isPassable(world, position);
        }

        @Override
        public boolean isSolid(int x, int y, int z) {
            return isLoaded(x, y, z) && world.getBlockState(new BlockPos(x, y, z)).isNormalCube();
        }

        @Override
        public boolean isLoaded(int x, int y, int z) {
            return world.isBlockLoaded(new BlockPos(x, y, z));
        }

        @Override
        public boolean isHazard(int x, int y, int z) {
            if (!isLoaded(x, y, z)) {
                return true;
            }
            Material material = world.getBlockState(new BlockPos(x, y, z)).getMaterial();
            return material == Material.LAVA || material == Material.FIRE || material == Material.CACTUS
                    || material == Material.WEB;
        }

        @Override
        public float movementCost(int x, int y, int z) {
            if (!isLoaded(x, y, z)) {
                return Float.POSITIVE_INFINITY;
            }
            Material material = world.getBlockState(new BlockPos(x, y, z)).getMaterial();
            return material == Material.WATER ? 4.0F : 1.0F;
        }
    }
}
