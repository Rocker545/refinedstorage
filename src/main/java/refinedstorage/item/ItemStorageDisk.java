package refinedstorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageItems;
import refinedstorage.apiimpl.storage.NBTStorage;
import refinedstorage.block.EnumStorageType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemStorageDisk extends ItemBase {
    public static final int TYPE_1K = 0;
    public static final int TYPE_4K = 1;
    public static final int TYPE_16K = 2;
    public static final int TYPE_64K = 3;
    public static final int TYPE_CREATIVE = 4;
    public static final int TYPE_DEBUG = 5;

    private NBTTagCompound debugDiskTag;

    public ItemStorageDisk() {
        super("storage_disk");

        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 6; ++i) {
            list.add(i == TYPE_DEBUG ? createDebugDisk() : NBTStorage.createStackWithNBT(new ItemStack(item, 1, i)));
        }
    }

    private ItemStack createDebugDisk() {
        ItemStack debugDisk = new ItemStack(RefinedStorageItems.STORAGE_DISK, 1, ItemStorageDisk.TYPE_DEBUG);

        if (debugDiskTag == null) {
            debugDiskTag = NBTStorage.createNBT();

            NBTStorage storage = new NBTStorage(debugDiskTag, -1, null) {
                @Override
                public int getPriority() {
                    return 0;
                }
            };

            Iterator<Item> it = Item.REGISTRY.iterator();

            while (it.hasNext()) {
                Item item = it.next();

                if (item != RefinedStorageItems.STORAGE_DISK) {
                    List<ItemStack> stacks = new ArrayList<ItemStack>();

                    item.getSubItems(item, CreativeTabs.INVENTORY, stacks);

                    for (ItemStack itemStack : stacks) {
                        storage.insertItem(itemStack, 1000, false);
                    }
                }
            }

            storage.writeToNBT();
        }

        debugDisk.setTagCompound(debugDiskTag.copy());

        return debugDisk;
    }

    @Override
    public void addInformation(ItemStack disk, EntityPlayer player, List list, boolean b) {
        int capacity = EnumStorageType.getById(disk.getItemDamage()).getCapacity();

        if (capacity == -1) {
            list.add(I18n.format("misc.refinedstorage:storage.stored", NBTStorage.getStoredFromNBT(disk.getTagCompound())));
        } else {
            list.add(I18n.format("misc.refinedstorage:storage.stored_capacity", NBTStorage.getStoredFromNBT(disk.getTagCompound()), capacity));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.isSneaking() && NBTStorage.getStoredFromNBT(stack.getTagCompound()) == 0 && stack.getMetadata() != TYPE_CREATIVE) {
            ItemStack storagePart = new ItemStack(RefinedStorageItems.STORAGE_PART, 1, stack.getMetadata());

            if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
            }

            return new ActionResult(EnumActionResult.SUCCESS, new ItemStack(RefinedStorageItems.STORAGE_HOUSING));
        }

        return new ActionResult(EnumActionResult.PASS, stack);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        NBTStorage.createStackWithNBT(stack);
    }
}
