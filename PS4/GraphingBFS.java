import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * CS 10 PS4
 * @author You-Chi Liu, Kevine Twagizihirwe, Dartmouth CS 10, Winter 2022
 */

public class GraphingBFS<V, E>  {
    static String movie_actorsTest = "inputs/movie-actorsTest.txt";
    static String actorsTest = "inputs/actorsTest.txt";
    static String moviesTest = "inputs/moviesTest.txt";
    /**
     * Finds the shortest path tree for a current center of the universe
     * @param g graph
     * @param source center of universe
     * @return path tree as a graph
     */

    public static <V, E> Graph<V, E> bfs(Graph<V, E> g, V source) {
        Graph<V, E> tree = new AdjacencyMapGraph<V, E>(); // graph
        Set<V> visited = new HashSet<V>(); //Set to track which vertices have already been visited
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS
        tree.insertVertex(source); //insert the start vertex to the tree
        queue.add(source); //enqueue start vertex
        visited.add(source); //add start to visited Set
        while (!queue.isEmpty()) { //loop until no more vertices
            V u = queue.remove(); //dequeue
            for (V v : g.outNeighbors(u)) { //loop over out neighbors
                if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
                    visited.add(v); //add neighbor to visited Set
                    queue.add(v); //enqueue neighbor
                    tree.insertVertex(v); // insert vertex to the tree
                    tree.insertDirected(v, u, g.getLabel(u, v)); // insert directed edges to the tree
                }
            }
        }
        return tree;
    }

    /**
     * Constructs a path from ta vertex back to the center of the universe.
     * @param tree shortest path tree
     * @param v vertex
     * @return a path from the vertex back to the center of the universe
     */
    public static <V, E> List<V> getPath(Graph<V, E> tree, V v) {
        ArrayList<V> vertexCenterPath = new ArrayList<>();
        vertexCenterPath.add(v); // add the initial vertex to the path
        while (tree.outDegree(v) > 0) {
            Iterator<V> outNeighbors = tree.outNeighbors(v).iterator(); // iterate over the vertex's out-neighbor
            // if there are still outNeighbors, add the target vertex to the path
            while (outNeighbors.hasNext()) {
                V atVertex = outNeighbors.next();
                vertexCenterPath.add(atVertex);
                v = atVertex; // update the vertex
            }
        }
        return vertexCenterPath;
    }

    /**
     * Determine which vertices are in a graph but not the subgraph
     * @param graph graph/universe
     * @param subgraph shortest path tree
     * @return a set of vertices that are in the graph but not in the subgraph
     */
    public static <V, E> Set<V> missingVertices(Graph<V, E> graph, Graph<V, E> subgraph) {
        Set<V> verticesNotBFS = new HashSet<V>();
        Iterator<V> i = graph.vertices().iterator();
        // if the vertices are not in the subgraph but in the graph add it into the set verticesNotBFS
        // it will then avoid duplicates
        while (i.hasNext()) {
            V currentV = i.next();
            if (!subgraph.hasVertex(currentV)) {
                verticesNotBFS.add(currentV);
            }
        }
        return verticesNotBFS;
    }

    /**
     * Finds the average distance-from-root in a shortest path tree
     * @param tree shortest path tree
     * @param root current center of the universe
     * @return average distance from root
     */

    public static <V, E> double averageSeparation(Graph<V, E> tree, V root) {
        // Vertices in tree excluding the root
        int verticesInTree = tree.numVertices() - 1;
        double avgSeparation;
        double totalSeparation = 0;
        // going over all the vertices and account for the total separation
        totalSeparation = averageSeparationHelper(tree,root,0);
        avgSeparation = totalSeparation / verticesInTree;
        return avgSeparation;
    }

    /**
     *
     * @param tree
     * @param vertex
     * @param totalSeparation
     * @return
     */
    public static <V,E> double averageSeparationHelper(Graph<V,E> tree, V vertex, double totalSeparation){
        double levelTotal= totalSeparation;
//        while (tree.inDegree(vertex)>0){
        Iterator<V> inNeighbors = tree.inNeighbors(vertex).iterator();
        while(inNeighbors.hasNext()){
            vertex = inNeighbors.next();
            levelTotal += averageSeparationHelper(tree,vertex,totalSeparation+1);
            }

        return levelTotal;
    }



    /**
     * averageSeparation Comparator
     */

    public static class averageSeparationComparator<V, E> implements Comparator<V> {

        Graph<V, E> Gtree; // new graph

        // constructor
        public averageSeparationComparator(Graph<V, E> tree) {
            this.Gtree = tree;
        }

        // compare vertices
        public int compare(V v1, V v2) {
            if (averageSeparation(bfs(Gtree, v1),v1) > averageSeparation(bfs(Gtree, v2),v2)) return -1;
            else if ((averageSeparation(bfs(Gtree, v1),v1) < averageSeparation(bfs(Gtree, v2),v2))) return 1;
            else return 0;
        }


    }


    /**
     * In-degree Comparator
     */

    public static class DegreeComparator<V, E> implements Comparator<V> {

        Graph<V, E> OriginalTree; // new graph

        // constructor
        public DegreeComparator(Graph<V, E> tree) {
            this.OriginalTree = tree;
        }

        // compare vertices
        public int compare(V v1, V v2) {
            return Integer.compare(OriginalTree.outDegree(v1), OriginalTree.outDegree(v2));
        }


    }

    /**
     * separation Comparator
     */
    public static class separationComparator<V, E> implements Comparator<V> {

        Graph<V, E> OriginalTree; // new graph
        Graph<V, E> shortestPath; // new graph

        // constructor
        public separationComparator(Graph<V, E> tree, Graph<V, E> shortestPath) {
            this.OriginalTree = tree;
            this.shortestPath = shortestPath;
        }

        // compare vertices
        public int compare(V v1, V v2) {
            return Integer.compare(getPath(shortestPath, v1).size(), getPath(shortestPath, v2).size());
        }


    }

    /**
     * Create costar graph
     * @param movie_actors actor ID and movie ID
     * @param actors actor ID and actor name
     * @param movies movie ID and movie name
     * @return movie graph
     */
    public static Graph <String, Set<String>> universeGraphCreator (String movie_actors, String actors, String movies) throws IOException {

        Graph<String, Set<String>> movieGraph = new AdjacencyMapGraph<>(); // costar graph
        String line;
        Map<String, String> ActorIDtoName = new HashMap<>(); // map of actor ID and actor name
        Map<String, String> MovieIDtoMovie = new HashMap<>(); // map of movie ID and movie name
        Map<String, Set<String>> MovietoActor = new HashMap<>(); // map of movie name and set of actors in the movie

        BufferedReader movieActor = new BufferedReader(new FileReader(movie_actors)); // movie ID to actor ID
        BufferedReader Actor = new BufferedReader(new FileReader(actors)); // actor ID to actor name
        BufferedReader Movies = new BufferedReader(new FileReader(movies)); // movie ID to movie Name



        // Mapping Movie ID to Movie Name
        while (!((line = Movies.readLine()) == null)) {
            String[] eachLine = line.split("\\|");
            MovieIDtoMovie.put(eachLine[0], eachLine[1]);

        }
        // Mapping Actor ID to Actor Name
        while (!((line = Actor.readLine()) == null)) {
            String[] eachLine = line.split("\\|");
            ActorIDtoName.put(eachLine[0], eachLine[1]);
        }

        // Mapping Movie to Set of Actors
        while (!((line = movieActor.readLine()) == null)) {
            String[] eachLine = line.split("\\|");
            // get movie name using movie ID from the appropriate map
            String movieName = MovieIDtoMovie.get(eachLine[0]);
            // get actor name using movie ID from the appropriate map
            String ActorName = ActorIDtoName.get(eachLine[1]);

            // map each movie to the names of the actors starring in the movie
            if (!(MovietoActor.containsKey(movieName))) {
                Set<String> movieActors = new TreeSet<String>();
                MovietoActor.put(movieName, movieActors);
                movieActors.add(ActorName);
            } else {
                MovietoActor.get(movieName).add(ActorName);
            }
        }


        // create vertices (actor names) for the movie graph
        for (String actor : ActorIDtoName.values()) {
            movieGraph.insertVertex(actor);
        }

        // construct movie graph
        for (String vertex : movieGraph.vertices()) {
            for (String movie : MovietoActor.keySet()) {
                // check if the actor starred in the movie
                if (MovietoActor.get(movie).contains(vertex)) {
                    // Insert an undirected edge (movie the actors costarred in) between 2 vertices
                    for (String actor : MovietoActor.get(movie)) {
                        // exclude edge from actor and himself
                        if (!vertex.equals(actor)) {
                            if (!movieGraph.hasEdge(vertex, actor)) {
                                // create new edge (movie set) if it does not exist already
                                Set<String> coStars = new TreeSet<>();
                                movieGraph.insertUndirected(vertex, actor, coStars);
                                coStars.add(movie);
                            }
                            // add movie to set if the edge already exists
                            else movieGraph.getLabel(vertex, actor).add(movie);
                        }
                    }
                }


            }
        }

        return movieGraph;
    }

    /**
     * Builds connection path
     * @param tree
     * @param path
     */

    public static <V, E> void pathBuilder (Graph<V,E> tree,  List<V> path){
        for (int i = 0; i < path.size() - 1; i++) {
            System.out.println(path.get(i) + " appeared in " + tree.getLabel(path.get(i), path.get(i+1))
                    + " with " + path.get(i+1));
        }

    }
    public static void testing (String movie_actorsTest, String actorsTest, String moviesTest) throws IOException {
        Graph<String, Set<String>> testUniverse = universeGraphCreator(movie_actorsTest, actorsTest, moviesTest);
        Graph<String, Set<String>> shortestTestGraph;
        String centerOfTestUniverse;
        centerOfTestUniverse = "Kevin Bacon";

        // Construct the shortest path tree during BFS
        System.out.println("The TestUniverseTest graph:");
        System.out.println((testUniverse));
        shortestTestGraph = bfs(testUniverse, centerOfTestUniverse);
        System.out.println("\nThe shortest path tree:");
        System.out.println(shortestTestGraph);
        System.out.println("\nThe path from Alice to the center:");
        System.out.println(getPath(shortestTestGraph, "Alice")); // using random Universe member
        System.out.println("\nThe vertices  missing from the shortest path tree:");
        System.out.println(missingVertices(testUniverse, shortestTestGraph));
        System.out.println("\nThe average separation is:");
        System.out.println(averageSeparation(shortestTestGraph, centerOfTestUniverse));
    }

    public static void main(String[] args) throws IOException {

        testing(movie_actorsTest, actorsTest, moviesTest);

    }



}


