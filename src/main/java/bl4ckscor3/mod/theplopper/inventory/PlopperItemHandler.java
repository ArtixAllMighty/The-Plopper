package bl4ckscor3.mod.theplopper.inventory;

import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class PlopperItemHandler implements IItemHandlerModifiable
{
	private TileEntityPlopper tileEntity;

	public PlopperItemHandler(TileEntityPlopper te)
	{
		tileEntity = te;
	}

	@Override
	public int getSlots()
	{
		return PlopperInventory.SLOTS;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return tileEntity.getInventory().getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stackToInsert, boolean simulate)
	{
		if(stackToInsert.isEmpty() || slot < 0 || slot >= getSlots())
			return stackToInsert;

		ItemStack slotStack = getStackInSlot(slot);
		int limit = getSlotLimit(slot);

		if(slotStack.isEmpty())
		{
			if(!simulate)
				setStackInSlot(slot, stackToInsert);

			return ItemStack.EMPTY;
		}
		else if(slotStack.getItem() == stackToInsert.getItem() && slotStack.getCount() < limit)
		{
			if(limit - slotStack.getCount() >= stackToInsert.getCount())
			{
				if(!simulate)
					slotStack.setCount(slotStack.getCount() + stackToInsert.getCount());

				return ItemStack.EMPTY;
			}
			else
			{
				ItemStack toInsert = stackToInsert.copy();
				ItemStack toReturn = toInsert.splitStack((slotStack.getCount() + stackToInsert.getCount()) - limit); //this is the remaining stack that could not be inserted

				if(!simulate)
					slotStack.setCount(slotStack.getCount() + toInsert.getCount());

				return toReturn;
			}
		}

		return stackToInsert;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack stack = getStackInSlot(slot);

		if(stack.isEmpty() || slot < 0 || slot >= getSlots() || amount < 1)
			return ItemStack.EMPTY;
		else if(amount >= stack.getCount())
		{
			if(!simulate)
				setStackInSlot(slot, ItemStack.EMPTY);

			return stack.copy();
		}
		else
		{
			if(!simulate)
				stack.shrink(amount);

			return stack.copy().splitStack(amount);
		}
	}

	@Override
	public int getSlotLimit(int slot)
	{
		ItemStack stack = getStackInSlot(slot);

		return stack.isEmpty() ? 64 : stack.getItem().getItemStackLimit(stack);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		tileEntity.getInventory().getContents().set(slot, stack);
	}

	/**
	 * @return The TileEntity that is bound to the inventory whith this item handler
	 */
	public TileEntityPlopper getTileEntity()
	{
		return tileEntity;
	}
}
