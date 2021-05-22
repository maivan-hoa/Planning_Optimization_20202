package lib.localsearch.domainspecific.vehiclerouting.vrp.largeneighborhoodexploration;

import lib.localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public interface ILargeNeighborhoodExplorer {
	public void exploreLargeNeighborhood(Neighborhood N);
	public void performMove(IVRMove m);
	public String name();
}
