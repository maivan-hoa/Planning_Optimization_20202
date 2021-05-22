import com.sun.javaws.IconUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static Double[][] c; // ma trận khoảng cách
    static Double[][] t; // ma trận thời gian di chuyển
    static Double[] d; // thời gian phục vụ tại mỗi điểm
    static Double[] e; // thời gian bắt đầu phục vụ tại mỗi điểm
    static Double[] l; // thời gian kết thúc phục vụ tại mỗi điểm
    static double costResult; // kết quả đúng của bộ dữ liệu
    static int numCity; // số lượng thành phố
    static int sizePopulation;
    static int ITERATIONs;
    static double pOfMutation;
    static int nN;



    public static void main(String[] args) throws FileNotFoundException {
        String filename = "data_100.txt";
        readData(filename);

        sizePopulation = 2000;
        ITERATIONs = 2000;  //số thế hệ
        nN = 2000;          //số lần lai ghép đột biến
        pOfMutation= 0.2;   // lớn hơn thì xảy ra lai ghép

        double maxTime = 300000; // mili giây
        double[] result;
        double[] f = new double[10];
        double[] time = new double[10];
        int nRun = 10;


        // chạy nRun lần
        for(int i=0; i<nRun; i++){
            GA ga = new GA(sizePopulation, maxTime, pOfMutation);
            result = ga.run(nN);
            f[i] = result[0];
            time[i] = result[1];
            System.out.println("Lần chạy thứ " + i +": ");
            System.out.println("f: "+ f[i]);
            System.out.println("time: "+ time[i]);
            System.out.println("--------------------------------------------");
        }


        double fMax = 0;
        double fMin = 1000000;
        double fAvg = 0;
        double fStd = 0;
        double tAvg = 0;

        System.out.println("--------------------------------------------");
        System.out.println("Result");
        System.out.print("f:  ");
        for(int i=0; i<nRun; i++){
            System.out.print(f[i]+ "  ");
            if(f[i] > fMax){
                fMax = f[i];
            }
            if(f[i]< fMin){
                fMin = f[i];
            }
            fAvg += f[i];
        }
        System.out.println();

        fAvg = fAvg*1.0/nRun;

        for(int i=0; i<nRun; i++){
            fStd += (f[i]-fAvg)*(f[i]-fAvg);
        }
        fStd = Math.sqrt(fStd*1.0/nRun);

        System.out.println("fMin: " + fMin);
        System.out.println("fMax: " + fMax);
        System.out.println("fAvg: " + fAvg);
        System.out.println("fSdt: " + fStd);

        System.out.print("time execute:  ");
        for(int i=1; i<nRun; i++){
            System.out.print(time[i]+ "  ");
            tAvg += time[i];
        }

        System.out.println();
        System.out.println("tAvg: " + tAvg*1.0/((nRun-1)*1000));


    }

    public static void readData(String filename) throws FileNotFoundException {
        File file = new File("C:\\Users\\DELL\\Desktop\\20202\\Planning_Optimization\\project\\Planning_Optimization_20202\\data\\"+ filename);
        Scanner input = new Scanner(file);

        numCity = Integer.parseInt(input.nextLine());

        e = new Double[numCity];
        l = new Double[numCity];
        d = new Double[numCity];
        String str;
        for(int i=0; i<numCity; i++){
            str = input.nextLine();
            String[] temp = str.split(" ");
            e[i] = Double.parseDouble(temp[0]);
            l[i] = Double.parseDouble(temp[1]);
            d[i] = Double.parseDouble(temp[2]);
        }

        t = new Double[numCity][numCity];

        for(int i=0; i<numCity; i++){
            str = input.nextLine();
            String[] temp = str.split(" ");
            for(int j=0; j<numCity; j++){
                t[i][j] = Double.parseDouble(temp[j]);
            }
        }

        c = new Double[numCity][numCity];
        for(int i=0; i<numCity; i++){
            str = input.nextLine();
            String[] temp = str.split(" ");
            for(int j=0; j<numCity; j++){
                c[i][j] = Double.parseDouble(temp[j]);
            }
        }

        str = input.nextLine();
        String[] temp = str.split("  ");
        costResult = Double.parseDouble(temp[1]);

        System.out.println("Read done");
        System.out.println("n: " + numCity );
//        System.out.println("--------------------------------------------");
//        for(int i=0; i<numCity; i++){
//            System.out.println(e[i] + " " + l[i] + " " + d[i]);
//        }

//        System.out.println("--------------------------------------------");
//        for(int i=0; i<numCity; i++){
//            for(int j=0; j<numCity; j++){
//                System.out.print(t[i][j] + " ");
//            }
//            System.out.println();
//        }

//        System.out.println("--------------------------------------------");
//        for(int i=0; i<numCity; i++){
//            for(int j=0; j<numCity; j++){
//                System.out.print(c[i][j] + " ");
//            }
//            System.out.println();
//        }

        System.out.println("--------------------------------------------");
        System.out.println("Cost: "+ costResult);
        System.out.println("--------------------------------------------");
    }



    public static int checkGenValid(ArrayList<Integer> gen){ // kiểm tra gen kết quả có hợp lệ với ràng buộc thời gian
        double sumTime = 0.0; // thời gian bắt đầu phục vụ
        for(int i=1; i<numCity; i++){
            sumTime += t[gen.get(i-1)][gen.get(i)] + d[gen.get(i-1)];
            if(sumTime < e[gen.get(i)]){
                sumTime = e[gen.get(i)];
            }
            if(sumTime > l[gen.get(i)]){
                return 0;
            }
        }

        sumTime += t[gen.get(numCity-1)][0];
        if(sumTime > l[0]){
            return 0;
        }
        return 1;
    }

}
