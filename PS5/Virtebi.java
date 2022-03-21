import java.io.*;
import java.util.*;

/**
 * POS Training and Virtebi Decoding Class
 * @author Kevine Twagizihirwe, You-Chi Liu, CS10, PS5, 22W
 */
public class Virtebi {
    static String example_sentences = "PS5/example-sentences.txt";
    static String example_tags = "PS5/example-tags.txt";
    static String example_test_sentence = "PS5/example-test-sentence.txt";
    static String brown_train_sentences = "PS5/brown-train-sentences.txt";
    static String brown_train_tags = "PS5/brown-train-tags.txt";
    static double unobserved = -10.0;
    // Transitions
    static Map<String, Map<String, Double>> transitions = new HashMap<String, Map<String, Double>>();
    // Observations
    static Map<String, Map<String, Double>> observations = new HashMap<String, Map<String, Double>>();

    /**
     * Hidden Markov Model and Part of speech training
     * @param sentences train sentences
     * @param sentenceTags train tags
     */
    public static void POSTraining(String sentences, String sentenceTags) throws Exception {
        // Training Sentence File
        BufferedReader sentencesFile = new BufferedReader(new FileReader(sentences));
        // Training Tags File
        BufferedReader tagsFile = new BufferedReader(new FileReader(sentenceTags));

        String tag = tagsFile.readLine();
        String sentence = sentencesFile.readLine();

        // Handling start of sentence
        String start = "#";

        while (tag != null && sentence != null) {
            // Loading data, splitting into lower-case words
            String[] tags = tag.split(" ");
            String[] words = sentence.toLowerCase().split(" ");

            for (int i = 0; i < tags.length && i < words.length; i++) {
                String currentWord = words[i];
                String currentState = tags[i];

                // COUNTING TRANSITIONS
                // handle the starting state "#" if at the beginning of the sentence
                if (i == 0) {
                    // Add new transition state from start if it does not have any next states
                    if (transitions.get(start) == null) {
                        transitions.put(start, new HashMap<String, Double>());
                        // New transition state from start
                        transitions.get(start).put(tags[i], 1.0);
                    }
                    else {
                        // Update the count of transitions from start to next state
                        if (transitions.get(start).containsKey(tags[i])) {
                            transitions.get(start).put(tags[i], transitions.get(start).get(tags[i]) + 1.0);
                        }
                        else {
                            transitions.get(start).put(tags[i], 1.0);
                        }
                    }

                }
                // handle other states in the middle of the sentence
                else {
                    // Add new transition state from current state if it does not have any next states
                    if (transitions.get(tags[i - 1]) == null) {
                        transitions.put(tags[i - 1], new HashMap<String, Double>());
                        // Record new transition state from current state
                        transitions.get(tags[i - 1]).put(tags[i], 1.0);
                    }
                    else {
                        // Update the count of transitions from current state to next state
                        if (transitions.get(tags[i - 1]).containsKey(tags[i])) {
                            transitions.get(tags[i - 1]).put(tags[i], transitions.get(tags[i - 1]).get(tags[i]) + 1.0);
                        } else {
                            transitions.get(tags[i - 1]).put(tags[i], 1.0);
                        }
                    }

                }

                // COUNTING OBSERVATIONS
                if (observations.get(currentState) == null) {
                    observations.put(currentState, new HashMap<String, Double>());
                    // Record new observation of the word at current state
                    observations.get(currentState).put(currentWord, 1.0);
                }
                else {
                    // Update the count of observation of word at current state
                    if (observations.get(currentState).containsKey(currentWord)) {
                        observations.get(currentState).put(currentWord, observations.get(currentState).get(currentWord) + 1);
                    } else {
                        observations.get(currentState).put(currentWord, 1.0);
                    }
                }

            }

            tag = tagsFile.readLine();
            sentence = sentencesFile.readLine();
        }

        sentencesFile.close();
        tagsFile.close();

        // TRAINING
        for (String state : transitions.keySet()) {
            // state total
            int stateTotal = 0;
            // Calculate state total
            for (String nextState : transitions.get(state).keySet()) {
                stateTotal += transitions.get(state).get(nextState);
            }
            // Normalizing each state's counts to log probabilities
            for (String nextState : transitions.get(state).keySet()) {
                transitions.get(state).put(nextState, Math.log(transitions.get(state).get(nextState) / stateTotal));
            }

        }

        for (String state : observations.keySet()) {
            // state total
            int stateTotal = 0;
            // calculate state total
            for (String observation : observations.get(state).keySet()) {
                stateTotal += observations.get(state).get(observation);
            }
            // Normalizing each state's counts to log probabilities
            for (String observation : observations.get(state).keySet()) {
                observations.get(state).put(observation, Math.log(observations.get(state).get(observation) / stateTotal));
            }

        }


    }

    /**
     * File-based test method of Virtebi training
     * @param sentences test sentences
     */

    public static void readFile(String sentences) throws Exception {
        List<String> allObservations = new ArrayList<String>();
//
        try {
            BufferedReader input = new BufferedReader(new FileReader(sentences));
            String sentence = input.readLine();
            while (sentence != null) {
                System.out.println(sentence);
                String[] words = sentence.toLowerCase().split(" ");
                allObservations.add("#");
                allObservations.addAll(Arrays.asList(words));
                // Tag each sentence
                System.out.println(VirtebiTagging(allObservations));
                allObservations = new ArrayList<>();
                sentence = input.readLine();
            }
            input.close();

        }
        catch (Exception e) {
            System.out.println("Invalid input/No file");
        }


    }


    /**
     * Virtebi Tagging
     * @param allObservations sentence from test sentences file
     */
    public static List <String> VirtebiTagging(List<String> allObservations){
        // Backtrace (Previous State with
        List <Map<String,String>> backTrace = new ArrayList<>();
        Set<String> currStates = new HashSet<>();
        currStates.add("#");
        Map<String, Double> currScores = new HashMap<>();
        currScores.put("#", 0.0);
        String highestScore = null; //
        List<String> backTracePath = new ArrayList<String>();
        for (int i = 0; i < allObservations.size() - 1; i++) {
            Set<String> nextStates = new HashSet<>();
            // Map containing all the next states with the current score and the best score from the previous states
            Map<String, Double> nextScores = new HashMap<>();
            // Map containing all the next states and their previous states
            Map<String, String> backtrack = new HashMap <>();

            for (String currState : currStates) {
                for (String nextState : transitions.get(currState).keySet()) {
                    nextStates.add(nextState);
                    double nextScore;
                    // charge a penalty if the word is not observed
                    if (observations.get(nextState).get(allObservations.get(i + 1)) == null) {
                       nextScore = currScores.get(currState) + transitions.get(currState).get(nextState) + unobserved;
                    }
                    else {
                        nextScore = currScores.get(currState) + transitions.get(currState).get(nextState) + observations.get(nextState).get(allObservations.get(i + 1));
                    }

                    if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                        nextScores.put(nextState, nextScore);
                        backtrack.put(nextState, currState);
                    }
                }
            }
            // Backtrace
            backTrace.add(backtrack);
            currStates = nextStates;
            currScores = nextScores;


            }

        // Find the highest score in all current scores (next scores here)
        for (String score : currScores.keySet()) {
            if (highestScore== null || currScores.get(score)>currScores.get(highestScore)){
                highestScore=score;
            }
        }

        // Backtrace from end to the beginning
        String currentWord = highestScore;
        for (int x = backTrace.size() - 1; x >= 0; x--){
            backTracePath.add(currentWord);
            currentWord = backTrace.get(x).get(currentWord);
        }

        // Reverse path since we added from end to beginning
        Collections.reverse(backTracePath);

        return backTracePath;
    }

    /**
     * console Test for keyboard input
     */
    public static void consoleTest() {
        Scanner in = new Scanner(System.in);
        List<String> allObservations = new ArrayList<>();
        while (in.hasNext()) {
            String input = in.nextLine();
            String[] words = input.toLowerCase().split(" ");
            allObservations.add("#");
            allObservations.addAll(Arrays.asList(words));
            // tag each sentence from keyboard
            System.out.println(VirtebiTagging(allObservations));
            allObservations = new ArrayList<>();
        }
        in.close();

    }



    public static void main(String[] args) throws Exception {
        // Train the model
        POSTraining(example_sentences,example_tags);
        // File-based testing
        readFile(example_test_sentence);
        // Console-based testing
        consoleTest();




    }
}