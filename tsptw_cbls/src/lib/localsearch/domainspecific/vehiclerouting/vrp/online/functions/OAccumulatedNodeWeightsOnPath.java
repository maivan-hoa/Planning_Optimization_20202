package lib.localsearch.domainspecific.vehiclerouting.vrp.online.functions;


import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import lib.localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedNodeWeightsOnPathVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.online.invariants.OAccumulatedWeightNodes;

public class OAccumulatedNodeWeightsOnPath extends AccumulatedNodeWeightsOnPathVR
		implements OFunctionVR {

	public OAccumulatedNodeWeightsOnPath(OAccumulatedWeightNodes accWN, Point v){
		super(accWN,v);
		accWN.getVRManager().post(this);
	}
	@Override
	public void updateWhenReachingTimePoint(int t) {
		// TODO Auto-generated method stub
		// DO NOTHING because this was done by accWN
	}

	public void addPoint(Point p){
		// DO NOTHING, this was donw by accWN
	}
}
