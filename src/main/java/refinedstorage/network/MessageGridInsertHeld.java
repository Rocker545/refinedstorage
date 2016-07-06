package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.container.ContainerGrid;

public class MessageGridInsertHeld extends MessageHandlerPlayerToServer<MessageGridInsertHeld> implements IMessage {
    private boolean single;

    public MessageGridInsertHeld() {
    }

    public MessageGridInsertHeld(boolean single) {
        this.single = single;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        single = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(single);
    }

    @Override
    public void handle(MessageGridInsertHeld message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGridHandler handler = ((ContainerGrid) container).getGrid().getGridHandler();

            if (handler != null) {
                handler.onInsertHeldItem(message.single, player);
            }
        }
    }
}
