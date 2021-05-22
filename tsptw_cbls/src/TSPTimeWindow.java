import lib.localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import lib.localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.constraints.timewindows.CEarliestArrivalTimeVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import lib.localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import lib.localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import lib.localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class DataObject {
    Double[][] c; // ma trận khoảng cách
    Double[][] t; // ma trận thời gian di chuyển
    Double[] d; // thời gian phục vụ tại mỗi điểm
    Double[] e; // thời gian bắt đầu phục vụ tại mỗi điểm
    Double[] l; // thời gian kết thúc phục vụ tại mỗi điểm
    int N; // số lượng thành phố
    int numClient; // số lượng thành phố

    public static DataObject getDefault() {
        DataObject d = new DataObject();
        try {
            d.readData("data_8.txt");
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return d;
    }

    public static DataObject getData(String fileName) {
        DataObject d = new DataObject();
        try {
            d.readData(fileName);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return d;
    }

    public void readData(String filename) throws FileNotFoundException {
        Path pathAbsolute = Paths.get("").toAbsolutePath();
        File file = new File(pathAbsolute + "/src/data/" + filename);
        Scanner input = new Scanner(file);

        N = Integer.parseInt(input.nextLine());
        numClient = N - 1;

        e = new Double[N];
        l = new Double[N];
        d = new Double[N];
        String str;
        for (int i = 0; i < N; i++) {
            str = input.nextLine();
            String[] temp = str.split(" ");
            e[i] = Double.parseDouble(temp[0]);
            l[i] = Double.parseDouble(temp[1]);
            d[i] = Double.parseDouble(temp[2]);
        }

        t = new Double[N][N];

        for (int i = 0; i < N; i++) {
            str = input.nextLine();
            String[] temp = str.split(" ");
            for (int j = 0; j < N; j++) {
                t[i][j] = Double.parseDouble(temp[j]);
            }
        }

        c = new Double[N][N];
        for (int i = 0; i < N; i++) {
            str = input.nextLine();
            String[] temp = str.split(" ");
            for (int j = 0; j < N; j++) {
                c[i][j] = Double.parseDouble(temp[j]);
            }
        }

        System.out.println("Read done");
        System.out.println("n: " + N);
        System.out.println("--------------------------------------------");
        for (int i = 0; i < N; i++) {
            System.out.println(e[i] + " " + l[i] + " " + d[i]);
        }

        System.out.println("--------------------------------------------");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(t[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("--------------------------------------------");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(c[i][j] + " ");
            }
            System.out.println();
        }
    }
}

public class TSPTimeWindow {
    DataObject dataObject;

    Point startPoint;
    Point endPoint;
    ArrayList<Point> clientPoints;
    ArrayList<Point> allPoints;

    ArcWeightsManager distanceEdgeWeightsManager;// luu tru trong so ve khoang cach tren canh noi giua cac point
    ArcWeightsManager timeEdgeWeightsMananger;// luu tru trong so ve thoi gian tren canh noi giua cac point

    HashMap<Point, Integer> mapPoint2ID;

    // time window
    HashMap<Point, Integer> earliestAllowedArrivalTime = new HashMap<>();
    HashMap<Point, Integer> latestAllowedArrivalTime = new HashMap<>();
    HashMap<Point, Integer> serviceDuration = new HashMap<>();

    // modelling
    VRManager vrmanager;
    VarRoutesVR varRoutesVR;// bien loi giai (luu tap cac route)
    ConstraintSystemVR constraintSystemVR;
    IFunctionVR totalCostObj;
    Random random = new Random();

    public TSPTimeWindow() {
        dataObject = DataObject.getDefault();
    }

    public TSPTimeWindow(String filename) {
        dataObject = DataObject.getData(filename);
    }

    public void mapping() {
        clientPoints = new ArrayList<Point>();
        allPoints = new ArrayList<Point>();
        mapPoint2ID = new HashMap<>();
        // khoi tao cac diem bat dau va ket thuc cua xe (route)
        startPoint = new Point(0);
        endPoint = new Point(0);
        allPoints.add(startPoint);
        allPoints.add(endPoint);
        mapPoint2ID.put(startPoint, 0);
        mapPoint2ID.put(endPoint, 0);

        for (int i = 1; i <= dataObject.numClient; i++) {
            Point p = new Point(i);
            clientPoints.add(p);
            allPoints.add(p);
            mapPoint2ID.put(p, i);
        }

        distanceEdgeWeightsManager = new ArcWeightsManager(allPoints);
        timeEdgeWeightsMananger = new ArcWeightsManager(allPoints);

        for (Point p : allPoints) {
            for (Point q : allPoints) {
                int ip = mapPoint2ID.get(p);
                int iq = mapPoint2ID.get(q);
                distanceEdgeWeightsManager.setWeight(p, q, dataObject.c[ip][iq]);
                timeEdgeWeightsMananger.setWeight(p, q, dataObject.t[ip][iq]);
            }
        }

        for (Point p : allPoints) {
            int id = mapPoint2ID.get(p);
            earliestAllowedArrivalTime.put(p, dataObject.e[id].intValue());
            latestAllowedArrivalTime.put(p, dataObject.l[id].intValue());
            serviceDuration.put(p, dataObject.d[id].intValue());
        }

    }

    public void stateModel() {
        vrmanager = new VRManager();
        varRoutesVR = new VarRoutesVR(vrmanager);
        varRoutesVR.addRoute(startPoint, endPoint);

        for (Point p : clientPoints)
            varRoutesVR.addClientPoint(p);// khai bao XR co the se di qua diem p

        // thiet lap rang buoc
        constraintSystemVR = new ConstraintSystemVR(vrmanager);

        EarliestArrivalTimeVR earliestArrivalTimeVR = new EarliestArrivalTimeVR(varRoutesVR,
                timeEdgeWeightsMananger,
                earliestAllowedArrivalTime,
                serviceDuration);

        CEarliestArrivalTimeVR cEarliestArrivalTimeVR = new CEarliestArrivalTimeVR(earliestArrivalTimeVR,
                latestAllowedArrivalTime);
        constraintSystemVR.post(cEarliestArrivalTimeVR);

        totalCostObj = new TotalCostVR(varRoutesVR, distanceEdgeWeightsManager);
        vrmanager.close();
    }

    public void initialSolution() {
        System.out.println("Init Solution");
        ArrayList<Point> listPoints = new ArrayList<Point>();
        listPoints.add(startPoint);
        for (Point p : clientPoints) {
            Point point = listPoints.get(random.nextInt(listPoints.size()));
            vrmanager.performAddOnePoint(p, point);
            System.out.println(varRoutesVR.toString() + "violations = " + constraintSystemVR.violations()
                    + ", cost = " + totalCostObj.getValue());
            listPoints.add(p);
        }
        System.out.println("----");
    }

    public void exploreNeighborhood(ArrayList<Move> cand) {
        cand.clear();
        int minDeltaC = Integer.MAX_VALUE;
        double minDeltaF = minDeltaC;

        for (Point pointY = startPoint; pointY != endPoint; pointY = varRoutesVR.next(pointY)) {
            for (Point pointX : clientPoints)
                if (pointX != pointY && pointX != varRoutesVR.next(pointY)) {
                    int deltaC = constraintSystemVR.evaluateOnePointMove(pointX, pointY);
                    double deltaF = totalCostObj.evaluateOnePointMove(pointX, pointY);
                    if (!(deltaC < 0 || deltaC == 0 && deltaF < 0)) continue;
                    if (deltaC < minDeltaC || deltaC == minDeltaC && deltaF < minDeltaF) {
                        cand.clear();
                        cand.add(new Move(pointX, pointY));
                        minDeltaC = deltaC;
                        minDeltaF = deltaF;
                    } else if (deltaC == minDeltaC && deltaF == minDeltaF) cand.add(new Move(pointX, pointY));
                }
        }
    }

    public void search(int maxIter) {
        initialSolution();
        int it = 0;
        ArrayList<Move> cand = new ArrayList<Move>();
        while (it < maxIter) {
            exploreNeighborhood(cand);
            if (cand.size() <= 0) {
                System.out.println("Reach local optimum");
                break;
            }

            Move m = cand.get(random.nextInt(cand.size()));
            vrmanager.performOnePointMove(m.x, m.y);
            System.out.println("Step " + it + ", XR = " + varRoutesVR.toString() + "violations = "
                    + constraintSystemVR.violations() + ", cost = " + totalCostObj.getValue());
            it++;
        }
    }

    class Move {
        Point x;
        Point y;

        public Move(Point x, Point y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
//        List<Integer> dataLens = Arrays.asList(3, 4, 5, 6, 7, 8, 10, 100, 200, 300);
//        List<Integer> dataLens = Arrays.asList(10, 30, 40, 60, 80, 100, 200);
//        List<Integer> dataLens = Arrays.asList(100);
        List<Integer> dataLens = Arrays.asList(300);
        Map<Integer, Double> timeExecution = new HashMap<>();
        for (int dataLen: dataLens) {
            TSPTimeWindow A = new TSPTimeWindow(String.format("data_%d.txt", dataLen));
            A.mapping();
            A.stateModel();
            long start = System.currentTimeMillis();
            A.search(100000000);
            long end = System.currentTimeMillis();
            timeExecution.put(dataLen, (end-start)/1000.0); // prints PT1M3.553S
        }

        for (int dataLen: dataLens) {
            System.out.println(String.format("Data len %s -> %s", dataLen, timeExecution.get(dataLen)));
        }

    }

}

