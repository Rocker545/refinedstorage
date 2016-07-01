package refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.externalstorage.TileExternalStorage;

public class BlockExternalStorage extends BlockSlave {
    public BlockExternalStorage() {
        super("external_storage");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileExternalStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.STORAGE, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        super.neighborChanged(state, world, pos, block);

        if (!world.isRemote) {
            ((TileExternalStorage) world.getTileEntity(pos)).refreshStorage();
        }
    }

    @Override
    public boolean hasOppositeFacingOnSneakPlace() {
        return true;
    }
}
