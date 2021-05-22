
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 27/09/2015
 */

package lib.localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration;

import lib.localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import lib.localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import lib.localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import lib.localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import lib.localsearch.domainspecific.vehiclerouting.vrp.moves.TwoOptMove2;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class FirstImprovementTwoOptMove2Explorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	
	public FirstImprovementTwoOptMove2Explorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	
	public FirstImprovementTwoOptMove2Explorer(ISearch search, VRManager mgr, LexMultiFunctions F){
		this.search = search;
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.bestValue = search.getIncumbentValue();
	}
	
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub 
		for (int i = 1; i <= XR.getNbRoutes(); i++) {
			if(N.size() > 0) break;
			for (int j = i + 1; j <= XR.getNbRoutes(); j++) {
				if(N.size() > 0) break;
				for (Point x = XR.next(XR.getStartingPointOfRoute(i)); XR.isClientPoint(x); x = XR.next(x)) {
					if(N.size() > 0) break;
					for (Point y = XR.next(XR.getStartingPointOfRoute(j)); XR.isClientPoint(y); y = XR.next(y)) {
						if(N.size() > 0) break;
						if (XR.checkPerformTwoOptMove(x, y)) {
							LexMultiValues eval = F.evaluateTwoOptMove2(x, y);
							if (eval.lt(bestEval)){
								N.add(new TwoOptMove2(mgr, eval, x, y));
							}
						}
					}
				}
			}
		}
	}
	public String name(){
		return "FirstImprovementTwoOptMove2Explorer";
	}

	public void performMove(IVRMove m){
		//DO NOTHING
	}
}
