package lib.localsearch.domainspecific.vehiclerouting.vrp.online.invariants;

import lib.localsearch.domainspecific.vehiclerouting.vrp.InvariantVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public interface OInvariantVR extends InvariantVR {
	public void updateWhenReachingTimePoint(int t);
	public void addPoint(Point p);
}
