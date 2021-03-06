import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GA {
    Random rd = new Random();
    Population population;
    double pOfMutation; // ngưỡng xác suất để lai ghép hoặc đột biến
//    int ITERATIONs;
    double maxTime;
    Individual bestSolution;
    int timeReset = 1500;

    public GA(int sizePopulation, double maxTime, double pOfMutation){
        population = new Population(sizePopulation);
        this.maxTime = maxTime;
        this.pOfMutation = pOfMutation;
    }

    public double[] run(int nN){ //nN số lần thực hiện lai ghép và đột biến
        population.init();
        //bestSolution = population.getBestIndividual(); //lấy ngẫu nhiên một cá thể coi như tốt nhất
        bestSolution = population.getIndividuals().get(0);
        int changeBest = 0; // Nếu sau timeReset thế hệ mà bestSolution không đổi thì khởi tạo lại quần thể


        double[] result = new double[2];
        double s = System.currentTimeMillis();
        while (System.currentTimeMillis()-s < maxTime){
            List<Individual> individuals = population.getIndividuals();
            ArrayList<Individual> children = new ArrayList<>();

            for(int i=0; i<nN; i++){
                Individual a = individuals.get(rd.nextInt(individuals.size()));
                Individual b = individuals.get(rd.nextInt(individuals.size()));

                while(a==b){ // == so sánh địa chỉ object trên memory
                    b = individuals.get(rd.nextInt(individuals.size()));
                }

                double p = rd.nextDouble();
                if(p > pOfMutation){
                    children.addAll(crossOver(a, b));
                }
                else{
                    Individual ia = mutation(a);
                    Individual ib = mutation(b);
                    children.add(ia);
                    children.add(ib);
                }
            }

            // nếu muốn thực hiện hàm selection3() thì phải tách riêng các cá thể con mới tạo ra
            //Individual bestInter = selection3(children);

            population.add(children);
            Individual bestInter = selection(); // thực hiện chọn lọc

            if(bestInter.getFitness() < bestSolution.getFitness()){
                bestSolution = bestInter;
                changeBest = 0;
            }

            changeBest++;
            if(changeBest > timeReset){
                population.init();
                changeBest = 0;
            }

            if(bestSolution.getFitness() == Main.costResult){
                result[0] = bestSolution.getCost();
                result[1] = System.currentTimeMillis() - s;
                return result;
            }

            //System.out.println("Thế hệ: " + (iter+1));
            //System.out.println("Best Fitness: " + bestSolution.getFitness());
//            System.out.println("Cost: " + bestSolution.getCost());
            //System.out.println("Gen: " + bestSolution.getGene());
            //System.out.println("Gen Valid: " + Main.checkGenValid(bestSolution.getGene()));
            //System.out.println("--------------------------------------------------------");
        }

        result[0] = bestSolution.getCost();
        result[1] = System.currentTimeMillis() - s;
        return result;
    }

    // Cross over ------------------------------------------------------------------------------------------------------
    ArrayList<Individual> crossOver(Individual a, Individual b){
        ArrayList<Individual> children = new ArrayList<>();
        ArrayList<Integer> ca = new ArrayList<>();
        ArrayList<Integer> cb = new ArrayList<>();

        // chọn hai điểm cắt
        int m1, m2;
        do{
            m1 = rd.nextInt(a.getGene().size());
            m2 = rd.nextInt(a.getGene().size());
        }while (m1>m2 || m1==m2);

        for(int i=0; i<m1; i++){
            ca.add(b.getGene().get(i));
            cb.add(a.getGene().get(i));
        }

        for(int i=m1; i<=m2; i++){
            ca.add(a.getGene().get(i));
            cb.add(b.getGene().get(i));
        }

        for(int i=m2+1; i<a.getGene().size(); i++){
            ca.add(b.getGene().get(i));
            cb.add(a.getGene().get(i));
        }

        if(!checkIndividualValid(ca)){
            makeIndividualValid(ca);
        }

        if(!checkIndividualValid(cb)){
            makeIndividualValid(cb);
        }

        Individual ind1 = new Individual(ca);
        Individual ind2 = new Individual(cb);

        children.add(ind1);
        children.add(ind2);

        return children;
    }

    // Mutation --------------------------------------------------------------------------------------------------------
    Individual mutation(Individual a){ // đảo hai vị trí bất kì trên gen
        int t1;

        do{
            t1 = rd.nextInt(a.getGene().size());
        }
        while(t1 == 0);

        int t2;
        do{
            t2 = rd.nextInt(a.getGene().size());
        }
        while(t2==0 || t2==t1);

        ArrayList<Integer> ca = new ArrayList<>(a.getGene());
        ca.set(t1, a.getGene().get(t2));
        ca.set(t2, a.getGene().get(t1));

        Individual ind = new Individual(ca);
        return ind;
    }

    Individual mutation2(Individual a){ // đảo ngược một đoạn gen
        int m1, m2;
        do{
            m1 = rd.nextInt(a.getGene().size());
            m2 = rd.nextInt(a.getGene().size());
        }while (m1>m2 || m1==m2);

        ArrayList<Integer> ca = new ArrayList<>();
        for(int i=0; i<m1; i++){
            ca.add(a.getGene().get(i));
        }

        for(int i=m2; i>=m1; i--){
            ca.add(a.getGene().get(i));
        }

        for(int i=m2+1; i<a.getGene().size(); i++){
            ca.add(a.getGene().get(i));
        }

        Individual ind = new Individual(ca);
        return ind;
    }

    // Selection -------------------------------------------------------------------------------------------------------
    Individual selection(){ // chọn 70% tốt nhất và 30% tồi nhất trong quần thể có cả parent + children
        population.getIndividuals().sort((i1, i2) -> {
           Double di1 = i1.getFitness();
           Double di2 = i2.getFitness();
           return di1.compareTo(di2);
        });

        ArrayList<Individual> newIndividuals = new ArrayList<>();
        int m = (int)(0.7*Main.sizePopulation);

        int l = population.getIndividuals().size()-1;
        for(int i=0; i<m; i++){
            newIndividuals.add(population.getIndividuals().get(i));
        }

        for(int i=0; i<Main.sizePopulation-m; i++){
            newIndividuals.add(population.getIndividuals().get(l-i));
        }

//        int l = population.getIndividuals().size()-1;
//        for(int i=0; i<population.sizePopulation/2; i++){
//            newIndividuals.add(population.getIndividuals().get(i));
//            newIndividuals.add(population.getIndividuals().get(l-i));
//        }

        population.setIndividuals(newIndividuals);
        return population.getIndividuals().get(0); // trả về cá thể tốt nhất trong quần thể hiện tại
    }

    Individual selection2(){
        // lựa chọn theo bánh xe roulett

        Individual bestInd = population.getBestIndividual(); // lấy cá thể tốt nhất trong quần thể gồm parent + child để trả về
        ArrayList<Double> pI = new ArrayList<>();
        double sumP = 0.0;
        for(int i=0; i<population.getIndividuals().size(); i++){
            sumP += population.getIndividuals().get(i).getFitness();
        }

        pI.add(population.getIndividuals().get(0).getFitness()*1.0/sumP);
        for(int i=1; i<population.getIndividuals().size(); i++){
            pI.add(pI.get(i-1) + population.getIndividuals().get(i).getFitness()*1.0/sumP);
        }

        ArrayList<Individual> newIndividuals = new ArrayList<>();
        for(int i=0; i<Main.sizePopulation; i++){
            double r = rd.nextDouble();
            for(int j=0; j<population.getIndividuals().size(); j++){
                if(pI.get(j)>r){
                    newIndividuals.add(new Individual(population.getIndividuals().get(j).getGene()));
                    break;
                }
            }
        }
        population.setIndividuals(newIndividuals);
        return bestInd;
    }

    Individual selection3(ArrayList<Individual> children){
        // lựa chọn 50% cá thể cũ tốt nhất và 50% cá thể mới tốt nhất
        ArrayList<Individual> newIndividuals = new ArrayList<>();
        Individual bestInd = population.getBestIndividual();

        population.getIndividuals().sort((i1, i2) -> {
            Double di1 = i1.getFitness();
            Double di2 = i2.getFitness();
            return di1.compareTo(di2);
        });

        bestInd = population.getIndividuals().get(0);
        int m = (int)(0.2*Main.sizePopulation);
        for(int i=0; i<m; i++){
            newIndividuals.add(population.getIndividuals().get(i));
        }

        population.setIndividuals(children);
        population.getIndividuals().sort((i1, i2) -> {
            Double di1 = i1.getFitness();
            Double di2 = i2.getFitness();
            return di1.compareTo(di2);
        });
        for(int i=0; i<Main.sizePopulation-m; i++){
            newIndividuals.add(population.getIndividuals().get(i));
        }

        if(bestInd.getFitness() >= population.getIndividuals().get(0).getFitness()){
            bestInd = population.getIndividuals().get(0);
        }

        population.setIndividuals(newIndividuals);
        return bestInd;
    }

    // Check and Make individual valid ---------------------------------------------------------------------------------
    boolean checkIndividualValid(ArrayList<Integer> a){ // kiểm tra gen có trùng lặp node (chưa cần ràng buộc thời gian)
        for(int i=0; i<a.size()-1; i++){
            for(int j=i+1; j<a.size(); j++){
                if(a.get(i) == a.get(j)){
                    return false;
                }
            }
        }

        return true;
    }

    void makeIndividualValid(ArrayList<Integer> a){
        ArrayList<Integer> check = new ArrayList<>();
        for(int i=0; i<a.size(); i++){
            check.add(i);
        }

        for(int i=0; i<a.size(); i++){
            boolean ok = true;
            for(int j=0; j<i; j++){
                if(a.get(i) == a.get(j)){
                    ok = false;
                    break;
                }
            }
            if(ok){
                check.remove(check.indexOf(a.get(i)));
            }
        }

        Collections.shuffle(check); // tăng độ ngẫu nhiên khi sửa gen
        for(int i=0; i<a.size(); i++){
            for(int j=0; j<i; j++){
                if(a.get(i) == a.get(j)){
                    a.set(i, check.get(0));
                    check.remove(0);
                }
            }
        }
    }

}
