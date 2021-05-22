package lib.localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood;

import lib.localsearch.domainspecific.vehiclerouting.apps.sharedaride.Util.RandomUtil;
import lib.localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import lib.localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import lib.localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import lib.localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import lib.localsearch.domainspecific.vehiclerouting.vrp.moves.TwoPointsMove;
import lib.localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import lib.localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

import java.util.ArrayList;

public class GreedyTwoPointsMoveExplorerLimit implements INeighborhoodExplorer {
	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private LexMultiValues bestValue;
	int K;
	public GreedyTwoPointsMoveExplorerLimit(VarRoutesVR XR, LexMultiFunctions F, int K) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.K = K;
	}
	public String name(){
		return "GreedyTwoPointsMoveExplorerLimit";
	}
	
	@Override
	public void exploreNeighborhood(Neighborhood N, LexMultiValues bestEval) {
		int nRoute = XR.getNbRoutes();
		ArrayList<Integer>listI = RandomUtil.randomKFromN(K, nRoute);
		ArrayList<Integer>listJ = RandomUtil.randomKFromN(K, nRoute);
		for (int i : listI)
		for (int j : listJ) {
			for (Point x = XR.startPoint(i); x != XR.endPoint(i); x = XR.next(x)) {
				for (Point y = XR.startPoint(j); y != XR.endPoint(j); y = XR.next(y)) {
					if (XR.checkPerformTwoPointsMove(x, y)) {
						LexMultiValues eval = F.evaluateTwoPointsMove(x, y);
						if (eval.lt(bestEval)){
							N.clear();
							N.add(new TwoPointsMove(mgr, eval, x, y, this));
							bestEval.set(eval);
						} else if (eval.eq(bestEval)) {
							N.add(new TwoPointsMove(mgr, eval, x, y, this));
						}
					}
				}
			}
		}
	}

	@Override
	public void performMove(IVRMove m){
		// DO NOTHING
	}
}

