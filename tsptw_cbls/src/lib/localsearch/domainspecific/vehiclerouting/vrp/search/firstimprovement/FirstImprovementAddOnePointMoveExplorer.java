package lib.localsearch.domainspecific.vehiclerouting.vrp.search.firstimprovement;

import lib.localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import lib.localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import lib.localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import lib.localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import lib.localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class FirstImprovementAddOnePointMoveExplorer implements INeighborhoodExplorer {

	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	
	public FirstImprovementAddOnePointMoveExplorer(VarRoutesVR XR, LexMultiFunctions F) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
	}
	
	public String name(){
		return "FirstImprovementAddOnePointMoveExplorer";
	}
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		// TODO Auto-generated method stub
		for (Point x : XR.getClientPoints()) {
			for (Point y : XR.getAllPoints()) {
				if (XR.checkPerformAddOnePoint(x, y)) {
					LexMultiValues eval = F.evaluateAddOnePoint(x, y);
					if (eval.lt(bestEval)) {
						mgr.performAddOnePoint(x, y);
					}
				}
			}
		}
	}

	
	public void performMove(IVRMove m) {
		// TODO Auto-generated method stub

	}

}
