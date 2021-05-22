import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Individual {
    ArrayList<Integer> gene;
    double fitness;

    Individual(ArrayList<Integer> gene){
        this.gene = gene;
        setFitness();
    }


    // fitness with time constraint
    public void setFitness(){
        fitness = 0.0; // có cộng thêm độ phạt
        double sumTime = 0.0; // tổng thời gian đã đi, không cộng thêm phạt

        for(int i=1; i< Main.numCity; i++){
            fitness += Main.c[gene.get(i-1)][gene.get(i)]; // Cộng thêm chi phí đường đi
            sumTime += Main.t[gene.get(i-1)][gene.get(i)] + Main.d[gene.get(i-1)];

            if(sumTime < Main.e[gene.get(i)]){
                // thời gian chờ
//                double w = Main.e[gene.get(i)] - sumTime;
//                sumTime += w;
                //fitness += t;
                sumTime = Main.e[gene.get(i)];
            }

            if(sumTime > Main.l[gene.get(i)]){
                fitness += 2*Math.pow(sumTime-Main.l[gene.get(i)], 2);
            }
        }

        // tính toán khi quay về 0
        fitness += Main.c[gene.get(Main.numCity-1)][0];
        sumTime += Main.t[gene.get(Main.numCity-1)][0] + Main.d[gene.get(Main.numCity-1)];

        if(sumTime > Main.l[0]){
            fitness += 2*Math.pow(sumTime-Main.l[0], 2);
        }

    }

    public double getFitness(){
        return fitness;
    }

    public ArrayList<Integer> getGene(){
        return gene;
    }

    public double getCost(){ // tính toán chi phí đường đi
        double cost = 0.0;
        double sumTime = 0.0;
        for(int i=1; i<Main.numCity; i++){
            cost += Main.c[gene.get(i-1)][gene.get(i)];
            sumTime += Main.t[gene.get(i-1)][gene.get(i)] + Main.d[gene.get(i-1)];

            if(sumTime < Main.e[gene.get(i)]){
                // thời gian chờ
                sumTime = Main.e[gene.get(i)];
            }

            if(sumTime > Main.l[gene.get(i)]){
                cost += sumTime-Main.l[gene.get(i)];
            }
        }
        cost += Main.c[gene.get(Main.numCity-1)][0];
        sumTime += Main.t[gene.get(Main.numCity-1)][0] + Main.d[gene.get(Main.numCity-1)];

        if(sumTime > Main.l[0]){
            cost += sumTime-Main.l[0];
        }
        return cost;
    }
}
