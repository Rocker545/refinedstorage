package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.config.IRedstoneModeConfig;

public class MessageRedstoneModeUpdate extends MessageHandlerPlayerToServer<MessageRedstoneModeUpdate> implements IMessage {
    private int x;
    private int y;
    private int z;

    public MessageRedstoneModeUpdate() {
    }

    public MessageRedstoneModeUpdate(IRedstoneModeConfig setting) {
        this.x = ((TileEntity) setting).getPos().getX();
        this.y = ((TileEntity) setting).getPos().getY();
        this.z = ((TileEntity) setting).getPos().getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void handle(MessageRedstoneModeUpdate message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof IRedstoneModeConfig) {
            IRedstoneModeConfig setting = (IRedstoneModeConfig) tile;

            setting.setRedstoneMode(setting.getRedstoneMode().next());
        }
    }
}
