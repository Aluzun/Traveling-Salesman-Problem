package com.company;

import java.io.*;
import java.time.Duration;
import java.time.Instant;

import java.util.*;

public class Algorithms {

    static String Input_48_City = "src\\com\\company\\Input_48_City.txt";
    static String Input_City_XY_Coordinates = "src\\com\\company\\Input_City_XY_Coordinates.txt";

    public static void main(String[] args) throws IOException {

        ArrayList<Town> cities = dataInput(Input_48_City, Input_City_XY_Coordinates, false);

        Instant start = Instant.now();
        System.out.println("Greedy Algorithm");
        greedy(cities);
        Instant end = Instant.now();
        System.out.print("Total time elapsed :");
        System.out.println(Duration.between(start, end));
        System.out.println();
        start = Instant.now();
        System.out.println("Nearest Neighbor  :\n"+ totalDistanceCalculator(NearestNeigborTownFinder(cities,6)));
        end = Instant.now();
        System.out.print("Total time elapsed :");
        System.out.println(Duration.between(start, end));
        System.out.println();
        System.out.println("Divide and Conquer ");
        start = Instant.now();
        System.out.println(totalDistanceCalculator(divideAndConquer(cities)));
        end = Instant.now();
        System.out.print("Total time elapsed :");
        System.out.println(Duration.between(start, end));
    }

    public static ArrayList<Town> divideAndConquer(ArrayList imp_cities){
        int totalX=0;
        int totalY=0;
        ArrayList<Town> towns = imp_cities;
        ArrayList<Town> towns2 = new ArrayList<Town>();
        ArrayList<Town> towns1 = new ArrayList<Town>();

        for (Town object : towns){
            totalX += (int)object.takeX();
            totalY += (int)object.takeY();
        }
        int centerX = totalX/towns.size();
        int centerY = totalY/towns.size();

        for (Town object :towns){
            if(object.takeX() >= centerX){
                towns2.add(object);
            }else towns1.add(object);

        }

        Random random = new Random();

        for (int k = 0; k<1000000;k++){
            ArrayList<Town> test = (ArrayList<Town>) towns1.clone();
            int length1 = totalDistanceCalculator(towns1);
            int random1 = random.nextInt(test.size());
            int random2 = random.nextInt(test.size());
            Town first = test.get(random1);
            Town second = test.get(random2);
            test.set(random2,first);
            test.set(random1,second);
            int length2 = totalDistanceCalculator(test);

            if(length2<length1) {
                towns1 = (ArrayList<Town>) test.clone();
                k = 0;

            }else {
                test = null;
            }

        }
        for (int k = 0; k<10000000;k++){
            ArrayList<Town> test = (ArrayList<Town>) towns2.clone();
            int length1 = totalDistanceCalculator(towns2);
            int random1 = random.nextInt(test.size());
            int random2 = random.nextInt(test.size());
            Town first = test.get(random1);
            Town second = test.get(random2);
            test.set(random2,first);
            test.set(random1,second);
            int length2 = totalDistanceCalculator(test);

            if(length2<length1) {
                towns2 = (ArrayList<Town>) test.clone();
                k = 0;

            }else {
                test = null;
            }

        }
        double a =  towns1.get(0).dictanceTo(towns2.get(0));
        double b =  towns1.get(0).dictanceTo(towns2.get(towns2.size()-1));
        double c =  towns1.get(towns1.size()-1).dictanceTo(towns2.get(0));
        double d =  towns1.get(towns1.size()-1).dictanceTo(towns2.get(towns2.size()-1));

        if (a<b && a<c && a<d){
            for(Town object : towns2){
                towns1.add(0,object);
            }
        }
        if (b<a && b<c && b<d){
            for(int i = towns2.size(); i>0;i--){
                towns1.add(0,towns2.get(i-1));
            }
        }
        if (c<a && c<b && c<d){
            for(Town object : towns2){
                towns1.add(towns1.size(),object);
            }
        }
        if (d<a && d<b && d<c){
            for(int i = towns2.size(); i>0;i--){
                towns1.add(towns1.size(),towns2.get(i-1));
            }
        }
        return towns1;
    }

    public static ArrayList<Town> greedy(ArrayList<Town> imp_cities) {
        ArrayList<Town> citiess = (ArrayList<Town>) imp_cities.clone();
        ArrayList<Segment> segmentArrayList = shortestWayComperator(citiess);
        ArrayList<Integer> loopChecker = new ArrayList<>();
        int totalDist = 0;

        for (int i = 0; i < segmentArrayList.size(); i++) {
            Town town1 = segmentArrayList.get(i).takeTown1();
            Town town2 = segmentArrayList.get(i).takeTown2();
            if (town1.isConnectionAvaible() && town2.isConnectionAvaible()) {
                totalDist += segmentArrayList.get(i).takeDistance();
                town1.plug(town2);
                town2.plug(town1);
                int next = citiess.indexOf(town1);
                int num = -1;
                int tour = 0;
                while (true) {

                    var c = citiess.get(next).getID();
                    var con1 = citiess.get(next).getCon1();
                    var con2 = citiess.get(next).getCon2();
                    if (con1 == -1 || con2 == -1) break;
                    if (!(num == -1) && con1 == num) next = con2;
                    else
                        next = con1;
                    num = c;

                    if (citiess.indexOf(town1) == next && !(tour == 0)) {
                        //System.out.println("Invalid Connection is Founded");
                        town1.unplug(town2);
                        town2.unplug(town1);
                        totalDist -= segmentArrayList.get(i).takeDistance();
                        break;
                    }
                    tour++;
                }
            }
        }
        ArrayList<Town> lasts = new ArrayList<>();
        for (Town object1 : citiess) {
            if (object1.getCon2() == -1) {
                lasts.add(object1);
            }
        }

        lasts.get(0).plug(lasts.get(1));
        lasts.get(1).plug(lasts.get(0));

        totalDist += lasts.get(0).dictanceTo(lasts.get(1));
        System.out.println(totalDist);
        return citiess;
    }


    public static ArrayList<Segment> shortestWayComperator(ArrayList<Town> imp_cities) {
        ArrayList<Town> cities = (ArrayList<Town>) imp_cities.clone();
        ArrayList<Segment> segmentArrayList = new ArrayList<Segment>();
        for (Town object : cities) {
            for (Town object2 : cities) {
                if (object == object2) {
                    continue;
                }
                segmentArrayList.add(new Segment(object, object2, object.dictanceTo(object2)));

            }
        }

        Collections.sort(segmentArrayList, new Comparator<Segment>() {
            @Override
            public int compare(Segment p1, Segment p2) {
                if (p1.takeDistance() < p2.takeDistance()) return -1;
                if (p1.takeDistance() > p2.takeDistance()) return 1;
                return 0;

            }
        });

        ArrayList<Segment> cleanedSegmentArrayList = new ArrayList<Segment>();
        for (int k = 0; k < segmentArrayList.size(); k++) {
            if (k % 2 == 0) {
                cleanedSegmentArrayList.add(segmentArrayList.get(k));
            }

        }


        return cleanedSegmentArrayList;
    }


    public static ArrayList<Town> NearestNeigborTownFinder(ArrayList<Town> unvisitedCitiesa, int start) {
        ArrayList<Town> unvisitedCities = (ArrayList<Town>) unvisitedCitiesa.clone();
        ArrayList<Town> visitedCities = new ArrayList<Town>();
        int unvisetedSize = unvisitedCities.size();
        Town a = unvisitedCities.get(start);
        Town b;
        for (int z = 0; z < unvisetedSize; z++) {
            if (unvisitedCities.size() > 1) {
                b = a.closest(unvisitedCities);
                visitedCities.add(a);
                unvisitedCities.remove(a);
                //System.out.println(a.getName());
                a = b;
            } else {

                unvisitedCities.remove(a);
                visitedCities.add(a);

            }

        }
        return visitedCities;
    }


    public static int totalDistanceCalculator(ArrayList<Town> visitedCities) {
        int visitedSize = visitedCities.size();
        int totalDistance = 0;
        for (int z = 0; z < visitedSize; z++) {
            if (!(z == (visitedSize - 1))) {
                totalDistance += visitedCities.get(z).dictanceTo(visitedCities.get(z + 1));

            } else {

                totalDistance += visitedCities.get(z).dictanceTo(visitedCities.get(0));

            }
        }

        return totalDistance;
    }


    private static ArrayList<Town> dataInput(String coordinatesLocaiton, String idLocation, boolean print) {
        ArrayList<Town> citiesArr = new ArrayList<Town>();
        File coordinatesFile = new File(coordinatesLocaiton);
        File idFile = new File(idLocation);

        try {
            Scanner coordinateReader = new Scanner(coordinatesFile);
            Scanner idReader = new Scanner(idFile);

            String x_cord;
            String y_cord;
            String name;
            int id;
            while (coordinateReader.hasNext() && idReader.hasNext()) {
                x_cord = coordinateReader.next();
                y_cord = coordinateReader.next();
                name = idReader.next();
                id = Integer.parseInt(name) - 1;
                citiesArr.add(new Town(id, name, x_cord, y_cord));
            }
        } catch (IOException e) {
            // Handle error...
        }
        Collections.sort(citiesArr, new Comparator<Town>() {
            @Override
            public int compare(Town o1, Town o2) {
                return Integer.parseInt(o1.takeName()) - Integer.parseInt(o2.takeName());

            }
        });
        if (print) System.out.println(citiesArr);

        return citiesArr;
    }
}
class Town {


    private double x;
    private double y;

    private int con1 = -1;
    private int con2 = -1;

    private final String name;
    private final int num;



    public Town(int num, String townName, String x, String y) {
        this.name = townName;
        this.x = Integer.parseInt(x);
        this.y = Integer.parseInt(y);
        this.num = num;
    }


    public double dictanceTo (Town town){
        return Math.sqrt((town.y - this.y) * (town.y - this.y) + (town.x - this.x) * (town.x - this.x));
    }


    public Town closest(ArrayList<Town> list){

        Town smallest;
        do {
            Random random = new Random();
            int index = random.nextInt(list.size());
            smallest = list.get(index);
        }while(smallest == this);

        for (Town object:list) {
            if( !(this == object)){
                //System.out.println(dictanceTo(object));
                if(this.dictanceTo(object)<this.dictanceTo(smallest)){
                    smallest = object;
                }
            }
        }
        return smallest;
    }


    public int getCon1() {
        return con1;
    }

    public int getCon2() {
        return con2;
    }

    public void plug(Town town){
        if (town.isConnectionAvaible() || town.con1 == this.getID()|| town.con2 == this.getID()){
            if (!isConnected(con1) ){
                con1 = town.getID();

            }
            else if (!isConnected(con2)|| town.con1 == this.getID()|| town.con2 == this.getID()){
                con2 = town.getID();
            }
        }



    }

    public void unplug(Town town) {
        var a = town.getID();
        if (a == con1){
            con1 = -1;
        }else if (a == con2){
            con2 = -1;
        }
        //System.out.println(this.id+" to "+a+" disconnected.");
    }

    private boolean isConnected(int con){
        if (con == -1) return false;
        else return true;
    }

    public boolean isConnectionAvaible(){
        if(isConnected(con1) && isConnected(con2)) return false;
        else return true;
    }
    public String takeName() {
        return name;
    }
    public int getID() {
        return num;
    }

    public double takeX() {
        return x;
    }

    public double takeY() {
        return y;
    }



}
class Segment {
    private Town town1;
    private Town town2;
    private double distance;


    public Segment(Town town1, Town town2, double length) {
        this.town1 = town1;
        this.town2 = town2;
        this.distance = length;
    }

    public Town takeTown1() {
        return town1;
    }

    public Town takeTown2() {
        return town2;
    }

    public double takeDistance() {
        return distance;
    }


}