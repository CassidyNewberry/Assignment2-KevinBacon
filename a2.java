import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;   //imports from the JSON simple jar that help parse the data set
import java.io.BufferedReader;
import java.io.*;
import java.util.*;
import java.io.FileReader;
import java.lang.String;


public class a2 {


    HashMap<String, Set<String>> adjList = new HashMap<>();   //stores the key and value pairs


    public static void main(String[] args) {

        String path = args[0];  //This gets the file from the command line
                                //for example mine is "/Users/CassidyNewberry/Desktop/tmdb_5000_credits.csv



        a2 SixDegrees = new a2();  //creates an object of the class to reference throughout project

        Scanner scan = new Scanner(System.in);


        try {

            FileReader reader = new FileReader(path);  //reads the file in from the path provided at the command line
            BufferedReader buffer = new BufferedReader(reader); //creates a buffer reader

            //reads first line of file and marks it as null so it isn't passed onto the parser
            buffer.readLine();
            String line = null;
            int begin, end;


            while ((line = buffer.readLine()) != null) {

                line = line.replaceAll("\"\"", "\"");
                begin = line.indexOf("[");  //reads the blank casts
                end = line.indexOf("]");
                String column = line.substring(begin, end + 1);

                JSONParser parser = new JSONParser();   //builds the Parse and JSON array
                JSONArray J_Array;

                try {
                    J_Array = (JSONArray) parser.parse(column);

                } catch (Exception e) {  //this catches exceptions in the data set
                    line = buffer.readLine();
                    line = line.replaceAll("\"\"", "\"");
                    begin = line.indexOf("[");
                    end = line.indexOf("]");

                    column = line.substring(begin, end + 1);

                    // System.out.println(column);
                    J_Array = (JSONArray) parser.parse(column);

                }


                for (Object obj1 : J_Array) {    //add the values in the data set into our graph by going through each name
                    JSONObject data = (JSONObject) obj1;
                    String actor1 = ((String) data.get("name")).toLowerCase(); //puts it all to lower case so it does not matter


                    if (!SixDegrees.actorExists(actor1)) {       //checks if Name exists in graph

                        SixDegrees.addActor(actor1);
                    }

                    for (Object obj2 : J_Array) {  //add the values in the data set into our graph by going through each name
                        JSONObject data2 = (JSONObject) obj2;
                        String actor2 = ((String) data2.get("name")).toLowerCase();
                        if (!SixDegrees.actorExists(actor2)) {
                            SixDegrees.addActor(actor2);
                        }
                        SixDegrees.addEdge(actor1, actor2);
                    }

                }

            }


        }//main try

        catch (ParseException e) {    //catches a file not found error if the command line path does not exist
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getNames(SixDegrees); //calls the getName function to get the names of actors and to find the path

        scan.close(); //closes the scanner


    } //end of main

    static void getNames(a2 connection){
        Scanner scan  = new Scanner(System.in);      //gets user input for actor 1 and searches if it is in the data set
        System.out.print("Enter an actor's name: ");
        String name1 = scan.nextLine().toLowerCase();
        // check if actor exists
        if(!connection.actorExists(name1)){
            System.out.println("Actor entered does not exist!"); //if actor does not exist this prints

        }
        System.out.print("Enter another actor's name: ");
        String name2 = scan.nextLine().toLowerCase();
        //check other actor
        if(!connection.actorExists(name2)){
            System.out.println("Actor entered does not exist!");
        }
        scan.close();
        try {
            checkPaths(name1, name2, connection);    //calls the method check paths to get the shortest path between actors
        }

        catch(NullPointerException e){
            System.out.println("One of the actors was not found, terminating now!");  //terminates if the actor does not exist
        }
    }
    //check Paths between the given Strings
    //returns null if no path can be found
    static void checkPaths(String name1, String name2, a2 connection){

        //check Paths between the given Strings
        //returns null if no path can be found

        LinkedList<String> pathFinder = connection.computePaths(name1, name2);


        if(pathFinder == null){
            System.out.println("No path was found"); }
        String n = "";
        for(int i = 0; i < pathFinder.size(); i++){
            n += pathFinder.get(i) + "--> ";

            if (pathFinder.get(i).equals(name1)){
                i=pathFinder.size()+1;
            }
        }
        System.out.println("Path between " + name1 + " and " + name2 + ":");

        System.out.print(name2 + "--> " + n); //prints out the path


    }

    public LinkedList<String> computePaths(String name1, String name2) {

        //finds paths between two strings
        //uses Linked List to track which neighbors have been visited

        LinkedList<String> path = new LinkedList<>();
        LinkedList<String> visited = new LinkedList<>();

        name1 = name1.toLowerCase();
        name2 = name2.toLowerCase();

        if(name1.equals(name2)){
            path.add(name1);
            return path;
        }

        //iterates through adjacency list
        for (String n : adjList.keySet()) {
            if (n.toLowerCase().equals(name1)) {
                path.add(n);
                visited.add(n);
            }
        }

        path.add(name1);
        while(!path.isEmpty()) {
            String name = path.remove();
            visited.add(name);

            if (isNeighbor(name, name2)) {
                for (String actors : adjList.keySet()) {
                    if (actors.toLowerCase().equals(name2)) {
                        path.add(actors.toLowerCase());
                        visited.add(actors);
                        System.out.println(actors);
                        return path;
                    }
                }
            }


            //retrieves list of neighbors from name;
            LinkedList<String> neigh = new LinkedList<>(neighbor(name));

            for(int i = 0; i < neigh.size(); i++){
                path.add(name);
                path.add(neigh.get(i));

                if(!visited.contains(neigh.get(i))){
                    path.add(neigh.get(i).toLowerCase());
                    visited.add(neigh.get(i).toLowerCase());
                }
            }

        }

        return null;  //returns null if there is no path between actors

    }

    public boolean actorExists(String name) {  //finds if the actor is in the graph
        if (adjList.containsKey(name)) {
            return true;
        }

        return false;
    }

    public void addEdge(String name1, String name2) {  //adds an edge in the graph

        //adds edge between both name1 and name2
        Set<String> src1 = adjList.get(name1);
        Set<String> src2 = adjList.get(name2);
        src1.add(name2);
        src2.add(name1);

    }

    public void addActor(String name) { //adds a name


        adjList.put(name, new HashSet<>());

    }

    //check if actor1 and actor2 are "related" or connected as neighbors- directly connected
    public boolean isNeighbor(String name1, String name2){

        Set<String> src1 = adjList.get(name1);

        if(src1.contains(name2)){
            return true;
        }

        return false;

    }

    public LinkedList<String> neighbor(String actor){
        return new LinkedList<String> (adjList.get(actor));
    }


}