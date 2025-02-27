package bl4ckscor3.mod.theplopper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Tracks all exisiting ploppers so searching for them each time an item expires is obsolete.
 * Also manages range checks
 */
public class PlopperTracker
{
	private static final Map<Integer,Collection<BlockPos>> trackedPloppers = new HashMap<>();

	/**
	 * Starts tracking a plopper
	 * @param te The plopper to track
	 */
	public static void track(TileEntityPlopper te)
	{
		getTrackedPloppers(te.getWorld()).add(te.getPos().toImmutable());
	}

	/**
	 * Stops tracking the given plopper. Use when e.g. removing the tile entity from the world
	 * @param te The plopper to stop tracking
	 */
	public static void stopTracking(TileEntityPlopper te)
	{
		getTrackedPloppers(te.getWorld()).remove(te.getPos());
	}

	/**
	 * Gets all ploppers that have the given block position in their range in the given world
	 * @param world The world
	 * @param pos The block position
	 * @return A list of all ploppers that have the given block position in their range
	 */
	public static List<TileEntityPlopper> getPloppersInRange(World world, BlockPos pos)
	{
		final Collection<BlockPos> ploppers = getTrackedPloppers(world);
		List<TileEntityPlopper> returnValue = new ArrayList<>();

		for(Iterator<BlockPos> it = ploppers.iterator(); it.hasNext(); )
		{
			BlockPos plopperPos = it.next();

			if(plopperPos != null)
			{
				TileEntity potentialPlopper = world.getTileEntity(plopperPos);

				if(potentialPlopper instanceof TileEntityPlopper)
				{
					if(canPlopperReach((TileEntityPlopper)potentialPlopper, pos))
						returnValue.add((TileEntityPlopper)potentialPlopper);

					continue;
				}
			}

			it.remove();
		}

		return returnValue;
	}

	/**
	 * Gets all block positions at which a plopper is being tracked for the given world
	 * @param world The world to get the tracked ploppers of
	 */
	private static Collection<BlockPos> getTrackedPloppers(World world)
	{
		Collection<BlockPos> ploppers = trackedPloppers.get(world.getDimension().getType().getId());

		if(ploppers == null)
		{
			ploppers = new HashSet<BlockPos>();
			trackedPloppers.put(world.getDimension().getType().getId(), ploppers);
		}

		return ploppers;
	}

	/**
	 * Checks whether the given block position is contained in the given plopper's range
	 * @param te The plopper
	 * @param pos The block position
	 */
	private static boolean canPlopperReach(TileEntityPlopper te, BlockPos pos)
	{
		AxisAlignedBB plopperRange = te.getRange();

		return plopperRange.minX <= pos.getX() && plopperRange.minY <= pos.getY() && plopperRange.minZ <= pos.getZ() && plopperRange.maxX >= pos.getX() && plopperRange.maxY >= pos.getY() && plopperRange.maxZ >= pos.getZ();
	}
}
