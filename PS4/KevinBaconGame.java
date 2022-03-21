import java.io.IOException;
import java.util.*;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Kevin Bacon Game
 * @author You-Chi Liu, Kevine Twagizihirwe, Dartmouth CS 10, Winter 2022
 */
public class KevinBaconGame extends GraphingBFS {

    static String movie_actors = "inputs/movie-actors.txt";
    static String actors = "inputs/actors.txt";
    static String movies = "inputs/movies.txt";
    static Graph<String, Set<String>> shortestGraph;
    static String centerOfUniverse;


    public static void main(String[] args) throws IOException {
        //The initial center of the universe is Kevin Bacon
        centerOfUniverse = "Kevin Bacon";
        try {
            // Create the universe graph
            Graph<String, Set<String>> MovieUniverse = universeGraphCreator(movie_actors, actors, movies);
            // Construct the shortest path tree during BFS
            shortestGraph = bfs(MovieUniverse, centerOfUniverse);


        // Game commands for game
        String commands =
                "Commands:\n" +
                        "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                        "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                        "i: list actors with infinite separation from the current center\n" +
                        "p <name>: find path from <name> to current center of the universe\n" +
                        "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                        "u <name>: make <name> the center of the universe\n" +
                        "q: quit game";


        System.out.println(commands);

        // Creating the interactive game interface
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println(centerOfUniverse + " game >");
            // Take the input from the keyboard
            String words = in.nextLine();
            
            // Split the inputted words in by space for commands with boundaries
            String[] input = words.split(" ");
            
            // Split the inputted words in 2 to ensure long names are read
            String[] name = words.split("\\s", 2);
            
            // compile the regex to create a pattern
            Pattern pt = Pattern.compile("[cdipsuq]");
            
            // get a matcher object from pattern
            Matcher mt = pt.matcher(input[0]);
            
            // Check if the first letter that is put in is included in the list of commands
            boolean result = mt.matches();


            if (result) {

                // List top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
                if (input[0].equals("c")) {

                    try {

                        int order = Integer.parseInt(input[1]);
                        // New list of vertices
                        List<String> listOVC = new ArrayList<>();
                        List<String> listByAVGSeparation = new ArrayList<>();

                        if (Math.abs(order) < MovieUniverse.numVertices() ) {

                            // Add vertices to list based on average separation
                            for (String vertex : MovieUniverse.vertices()) {
                                if (!(missingVertices(MovieUniverse, shortestGraph).contains(vertex))) {
                                    listOVC.add(vertex);
                                }
                            }
                            // average separation comparator
                            Comparator<String> avgComparator = new averageSeparationComparator(MovieUniverse);

                            // sort list
                            listOVC.sort(avgComparator);

                            // List bottom centers of universe, if the entered number is positive
                            if (order > 0) {
                                for (int i = 0; i < Math.abs(order); i++) {
                                    listByAVGSeparation.add(listOVC.get(i));
                                }
                            }
                            // List bottom centers of universe, if the entered number is negative
                            else if (order < 0) {
                                for (int i = listOVC.size() - 1; i > listOVC.size() - Math.abs(order) - 1; i--) {
                                    listByAVGSeparation.add(listOVC.get(i));
                                }
                            }
                            // print the list of vertices sorted by average separation
                            System.out.println(listByAVGSeparation);
                        }
                        else {
                            System.out.println("There are only " + MovieUniverse.numVertices() + " actors in the universe. " +
                                    "Please enter a number that is below " + MovieUniverse.numVertices());
                        }
                    } catch(Exception e) {
                        System.out.println("Enter a valid number");
                    }

                }

                // List actors sorted by non-infinite separation from the current center, with separation between low
                // and high
                if (input[0].equals("s")) {

                    try {
                        // Get high and low boundaries
                        int lowSeparation = Integer.parseInt(input[1]);
                        int highSeparation = Integer.parseInt(input[2]);

                        // New list of vertices, ordered by non-infinite separation from the current center
                        List<String> verticesSeparation = new ArrayList<String>();
                        if (lowSeparation >=0 && highSeparation < MovieUniverse.numVertices()) {
                            for (String vertex : MovieUniverse.vertices()) {
                                // Only include numbers that have non-infinite separation
                                if (!(missingVertices(MovieUniverse, shortestGraph).contains(vertex))) {
                                    // record separation
                                    int separation = getPath(shortestGraph, vertex).size() - 1;
                                    // add the vertex to the list if it has a separation within the specified boundary
                                    if (separation >= lowSeparation && separation <= highSeparation) {
                                        verticesSeparation.add(vertex);
                                    }
                                }
                            }
                            // separation comparator
                            Comparator<String> sepComparator = new separationComparator(MovieUniverse, shortestGraph);
                            // sort list based on separation
                            verticesSeparation.sort(sepComparator);

                            System.out.println("The actors sorted by non-finite separation from " + centerOfUniverse +
                                    " with separation between " + lowSeparation + " and " + highSeparation + " are: \n"
                                    + verticesSeparation);
                        }
                        else {
                            System.out.println("There are only " + MovieUniverse.numVertices() + " actors in the universe. " +
                                    "Please enter an appropriate boundary.");
                        }

                    } catch (Exception e) {
                        System.out.println("Enter a valid boundary");
                    }
                }

//              Find path from <name> to current center of the universe
                if (name[0].equals("p")) {
                    // check if name is in universe
                    if (MovieUniverse.hasVertex(name[1])) {
                        if (!(missingVertices(MovieUniverse, shortestGraph).contains(name[1]))) {
                            // find path from name to the center
                            List<String> path = getPath(shortestGraph, name[1]);
                            System.out.println(name[1] +"'s number is "+ (path.size() - 1));
                            // build the path
                            pathBuilder(MovieUniverse, path);
                        } else
                            System.out.println("The actor is unreachable");

                    } else System.out.println("Actor unknown");
                }

//             list actors sorted by degree, with degree between low and high
                    if (input[0].equals("d")) {

                        try {
                            // Get high and low boundaries
                            int lowDegree = Integer.parseInt(input[1]);
                            int highDegree = Integer.parseInt(input[2]);
                            // New list of vertices sorted by Degree
                            List<String> verticesByDegree = new ArrayList<String>();

                            if (lowDegree >= 0 && highDegree < MovieUniverse.numVertices()) {

                                // Add vertices to list if their degree are within a specified boundary
                                for (String vertex : MovieUniverse.vertices()) {
                                    if (MovieUniverse.outDegree(vertex) >= lowDegree && MovieUniverse.outDegree(vertex) <= highDegree) {
                                        verticesByDegree.add(vertex);
                                    }
                                }

                                // Degree comparator
                                Comparator<String> degComparator = new DegreeComparator(MovieUniverse);

                                // sort vertices by degree from low to high
                                verticesByDegree.sort(degComparator);

                                System.out.println(verticesByDegree);
                            }
                            else {
                                System.out.println("There are only " + MovieUniverse.numVertices() + " actors in the universe. " +
                                        "Please enter an appropriate boundary.");
                            }

                        } catch (Exception e) {
                            System.out.println("Enter a valid boundary");
                        }
                    }

//              Make <name> the center of the universe
                    if (name[0].equals("u")) {

                        if (MovieUniverse.hasVertex(name[1])) {
                            // change center of the universe
                            centerOfUniverse = name[1];
                            // create new shortest path tree based on the new center
                            shortestGraph = bfs(MovieUniverse, name[1]);
                            System.out.println(centerOfUniverse + " is now the center of the universe, connected to " +
                                    "" + (shortestGraph.numVertices() - 1) + "/" + MovieUniverse.numVertices()+ " actor(s) with average separation "
                                    + averageSeparation(shortestGraph, name[1]));

                        } else System.out.println("Actor unknown");
                    }

                    // list actors with infinite separation from the current center
                    if (input[0].equals("i")) {
                        System.out.println(missingVertices(MovieUniverse, shortestGraph));
                    }

                    // Quit game
                    if (input[0].equals("q")) {
                        System.exit(0);
                    }
                } else {
                System.out.println("Please enter valid input");
            }
            }
        } catch (Exception e){
            System.out.println("Invalid input file. Please check whether your files exist and try again");
        }
        }
    }




