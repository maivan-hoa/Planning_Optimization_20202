
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
import lib.localsearch.domainspecific.vehiclerouting.vrp.moves.ThreeOptMove1;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class GreedyThreeOptMove1Explorer implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	private boolean firstImprovement = true;
	public GreedyThreeOptMove1Explorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	public GreedyThreeOptMove1Explorer(VarRoutesVR XR, LexMultiFunctions F, boolean firstImprovement) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.firstImprovement = firstImprovement;
	}
	
	public GreedyThreeOptMove1Explorer(ISearch search, VRManager mgr, LexMultiFunctions F){
		this.search = search;
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.bestValue = search.getIncumbentValue();
	}

	public String name(){
		return "GreedyThreeOptMove1Explorer";
	}
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		if(firstImprovement && N.hasImprovement()){
			//System.out.println(name() + "::exploreNeighborhood, has improvement --> RETURN");
			return;
		}

		for (Point x : XR.getClientPoints()) {
			for (Point y = XR.next(x); y != null && XR.isClientPoint(y); y = XR.next(y)) {
				for (Point z = XR.next(y); XR.isClientPoint(z); z = XR.next(z)) {
					if (XR.checkPerformThreeOptMove(x, y, z)) {
						LexMultiValues eval = F.evaluateThreeOptMove1(x, y, z);
						if (eval.lt(bestEval)){
							N.clear();
							N.add(new ThreeOptMove1(mgr, eval, x, y, z, this));
							bestEval.set(eval);
						} else if (eval.eq(bestEval)) {
							N.add(new ThreeOptMove1(mgr, eval, x, y, z, this));
						}
						if(firstImprovement){
							if(eval.lt(0)) return;
						}
					}
				}
			}
		}
	}
	
	public void performMove(IVRMove m){
		//DO NOTHING
	}

}