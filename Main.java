import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Main {

    private static LinkedList<Node> coloredNodes = new LinkedList<Node>();
    private static int count;
    private static int backtrackSteps = 0;


    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        BufferedReader br = new BufferedReader(new FileReader("input.txt"));
        StringBuilder sb = new StringBuilder();
        ArrayList<String> colors = new ArrayList<String>();
        ArrayList<Node> nodes = new ArrayList<Node>();
        int blank_line_count = 0;
//        Node map[] = new Node[48];
        int count = 0;
        String line = br.readLine();
        while (line != null && blank_line_count<3) {
            if(line.length() != 0){
                if(blank_line_count == 0){
                    colors.add(line);
                }
                //Create reading in each line and creating a node object to represent each state
                else if(blank_line_count == 1){
                    nodes.add(new Node(line));
//                    map[count] = new Node((String) line);
//                    count++;
                    //Reading in each edge and adding the edge to both states.
                }else if(blank_line_count == 2){
                    String[] strs = line.trim().split("\\s+");
                    Node state1 = findState(nodes, strs[0]);
                    Node state2 = findState(nodes, strs[1]);
                    state1.addNeighbor(state2);
                    state2.addNeighbor(state1);
                }
            }else{
                blank_line_count++;
            }

            line = br.readLine();
        }

        String[] colorsArray = new String[colors.size()];
        Node[] map = new Node[nodes.size()];
        for(int i = 0; i<nodes.size(); i++){
            map[i] = nodes.get(i);
        }
        for(int i = 0; i<colors.size(); i++){
            colorsArray[i] = colors.get(i);
        }
        for(int i = 0; i<map.length; i++){
            map[i].setPossibleColors(colorsArray);
        }

        System.out.println("Backtracking Solution" );
        backtrackingSolutionUtil(map);
        printMap(map);
        System.out.println("Backtracking Solution Searched " + backtrackSteps + " States to complete");
        System.out.println();
        System.out.println("///////////////");
        System.out.println();
        System.out.println("Local Search Solution");
        localSearchSolution(map);
        printMap(map);
        System.out.println();
        System.out.println("Local Search Solution had " + sumLocalSolutionSteps(map) + " changes to proposed solution");



    }





    public static void backtrackingSolutionUtil(Node[] maps){
        //Takes the created map of states and send each node to the algorithm
        for(int i = 0; i<maps.length; i++){
            Node node = maps[i];
            if(!node.getHasColor()){
                backtrackingSolutionUtil(node);
            }
        }
    }

    public static void backtrackingSolutionUtil(Node node){
        //checks if the node is null, if it's null then the method ends and we've made it to the head of the tree.
        if(node != null){
            //tracking how many states have been searched.
            backtrackSteps++;
            //if there is no color on the state then try to color it
            if(!node.getHasColor()) {
                //node.setColor() will return a boolean if the coloring was successful
                Boolean set = node.setColor();
                if(!set){
                    //if the coloring of the current node doesn't work then go back up the tree and change color and call the function again
                    node = node.getPrevious();
                    node.changeColor();
                    backtrackingSolutionUtil(node);
                }
            }
            //get's the current nodes next uncolored neighbor
            Node neighbor = node.getUncoloredNeighbor();
            if (neighbor != null) {
                //if neighbor is not null then call then set the neighbor as the current node's next and recursively call the function with the neighbor
                node.setNext(neighbor);
                neighbor.setPrevious(node);
                backtrackingSolutionUtil(neighbor);
            } else {
                //if the neighbor is null then there are no uncolored neighbors left and move back up the tree
                node = node.getPrevious();
                if (node != null) {
                    //double check the node's color is correct as you move back up the tree
                    node.checkNeighborColor2(node.getNeighbors(), node.getColor());
                }
                backtrackingSolutionUtil(node);
            }
        }
    }





    public static void localSearchSolution(Node[] map){
        //assign each state with a color
        setRandomMap(map);
        //iterate through each state and check how many conflicts each state has after randomly assigning it
        checkConflicts(map);
        //int count is to prevent the loop from iterating too much
        int count = 0;
        //the string previous is just a placeholder to hold the name of the node that was previously colored
        //this string prevents the aglorithm from trying to color the same node two times in a row, otherwise it would get caught in an infinite loop
        String previous = "XX";

        //after each node is colored, check the map to see if it's
        while(!checkSolution(map) && count <= 150){
            //sort the nodes by the number of conflicts each state has
            Arrays.sort(map);

            if(!previous.equals(map[map.length-1].get_name())) {
                //try to color the node with the most conflicts
                map[map.length-1].setColorLocal();
                map[map.length-1].setConflicts(0);
                map[map.length-1].checkConflicts();
                previous = map[map.length-1].get_name();
            }else{
                //color the node with the second most conflicts
                map[map.length-2].setColorLocal();
                map[map.length-2].setConflicts(0);
                map[map.length-2].checkConflicts();
                previous = map[map.length-2].get_name();
            }
            //check the amount of conficts so the map can be resorted
            checkConflicts(map);
            count++;
        }

        if(!checkSolution(map)){
            setRandomMap(map);
            localSearchSolution(map);
        }
    }






    public static Boolean checkSolution(Node[] map){
        for(int i = 0; i<map.length; i++){
            if(map[i].getConflicts() != 0) {
                return false;
            }
        }
        return true;
    }


    public static void setRandomMap(Node[] map){
        for(int i = 0; i<map.length; i++){
            Node node = map[i];
            node.setRandomColor();
        }
    }

    public static void resetConflicts(Node [] map){
        for(int i = 0; i<map.length; i++){
            map[i].setConflicts(0);
        }
    }

    public static void checkConflicts(Node [] map){
        resetConflicts(map);
        for(int i = 0; i<map.length; i++){
            map[i].checkConflicts();
        }
    }


    public static Node findState(Node[] states, String name){

        for(int i = 0; i<states.length; i++){
            if(states[i].get_name().equals(name)){
                return states[i];
            }
        }
        System.out.println("Returning a null node");
        return null;
    }

    public static Node findState(ArrayList<Node> states, String name){

        for(int i = 0; i<states.size(); i++){
            if(states.get(i).get_name().equals(name)){
                return states.get(i);
            }
        }
        System.out.println("Returning a null node");
        return null;
    }


    public static void printMap(Node[] map) {
        for (int i = 0; i < map.length; i++) {
            System.out.println(map[i].get_name() + " : " + map[i].getColor());

        }
    }

    public static int sumLocalSolutionSteps(Node[] map) {
        int count = 0;
        for (int i = 0; i < map.length; i++) {
            count += map[i].getSteps();
        }
        return count;
    }

}


















