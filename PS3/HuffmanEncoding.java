import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.*;
import java.io.*;
import java.util.Map;

/**
* @author May Oo Khine, You-chi Liu, CS10, W22
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */

public class HuffmanEncoding {

    public static BinaryTree<CharNFreq> finalTree;              // create final tree for priority queue

    // Create frequency table
    private static HashMap<Character, Integer> populateFreqMap(String pathName) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(pathName));

        try {
            input = new BufferedReader(new FileReader(pathName)); }
        catch (FileNotFoundException e) {
            System.err.println("File cannot be opened:" + e.getMessage());
        }

        // Declare new Map: character as a key and frequency as value
        HashMap<Character, Integer> FreqMap = new HashMap<>();

        // Assign to first character
        int targetUnicode = (input.read());

        // Read through input file characters
        while ((targetUnicode != -1)) {

            // Convert integers to characters
            char targetChar = (char) targetUnicode;

            // Increment frequency by 1 if existing, if not, assign it to 1
            if (FreqMap.containsKey(targetChar)) {
                FreqMap.put(targetChar, FreqMap.get(targetChar) + 1);
            }
            else {
                FreqMap.put(targetChar, 1);
            }

            targetUnicode = (input.read());
        }

        input.close();
        return FreqMap;
    }

    // Tree Comparator
    private static class TreeComparator implements Comparator<BinaryTree<CharNFreq>> {
        // Compare frequencies of two characters
        public int compare(BinaryTree<CharNFreq> bTree1, BinaryTree<CharNFreq> bTree2) {
            return bTree1.getData().getFrequency() - bTree2.getData().getFrequency();
        }
    }

    // Create Priority queue and get final tree
    private static BinaryTree<CharNFreq> putInitialTree(HashMap<Character, Integer> FreqMap) {

        TreeComparator compareTree = new TreeComparator();

        // Initialize priority queue
        PriorityQueue<BinaryTree<CharNFreq>> priorityTreeQueue = new PriorityQueue<BinaryTree<CharNFreq>>(compareTree);

        for (char keyCharacter : FreqMap.keySet()) {
            CharNFreq frequency = new CharNFreq(keyCharacter, FreqMap.get(keyCharacter));

            // Initial single-character tree
            BinaryTree<CharNFreq> singleCharTree = new BinaryTree<>(frequency);

            // Add tree to priority queue
            priorityTreeQueue.add(singleCharTree);
        }

        while (priorityTreeQueue.size() > 1) {

            // Extract the two lowest-frequency trees from the priority queue
            BinaryTree<CharNFreq> tree1 = priorityTreeQueue.remove();
            BinaryTree<CharNFreq> tree2 = priorityTreeQueue.remove();

            // Create new tree based on tree1 and tree2
            BinaryTree<CharNFreq> btree = new BinaryTree<CharNFreq>(new CharNFreq(null, tree1.getData().getFrequency() + tree2.getData().getFrequency()), tree1, tree2);

            // Add back to priority queue
            priorityTreeQueue.add(btree);
        }

        if (priorityTreeQueue.size() == 1) {
            // Update final tree from priority queue
            finalTree = priorityTreeQueue.remove();
        }

        return finalTree;
    }


    private static HashMap codeRetrieval(HashMap<Character, String> codeMap, String path, BinaryTree<CharNFreq> node) {
        // Build code map for each characters from lowest frequency nodes

        // If tree has only one leaf
        if (node.isLeaf()) {
            codeMap.put(node.getData().getCharacter(), path);
        }

        // Recursive call on left node
        if (node.hasLeft()) {
            codeRetrieval(codeMap, path + "0", node.getLeft());
        }

        // Recursive call on right node
        if (node.hasRight()) {
            codeRetrieval(codeMap, path + "1", node.getRight());//
        }

        // Return encoding map with '0' and '1' sequence
        return codeMap;
    }

    private static void compression (String infile, String outfile) throws IOException {

        BufferedReader read_Input = new BufferedReader(new FileReader(infile));
        BufferedBitWriter write_bitOutput = new BufferedBitWriter(outfile);

        // Hashmap to count character frequencies
        HashMap<Character, Integer> Character_map = populateFreqMap(infile);
        if (Character_map.size() == 0){
            // Error thrown when file is empty
            System.err.println("The file, " + infile + " is empty");
            return;
        }

        // Hashmap to get character codes
        HashMap<Character, String> Codemap = new HashMap<>();


        // Create priority queue to get final tree
        putInitialTree(Character_map);


        // Create code retrieval encoding map
        try {
            if (finalTree.size() <= 1) {
                codeRetrieval(Codemap, "0", finalTree);
            }
            else {
                codeRetrieval(Codemap, "", finalTree);
            }
        }
        catch (Exception e) {
            System.err.println("empty file");
        }

        // Go through input file for compression
        int target;
        char letter;

        try {
            while ((target = read_Input.read()) != -1) {
                letter = (char) target;
                if (Codemap.containsKey(letter)) {

                    // Gt bit string pattern
                    String string_bit = Codemap.get(letter);

                    // Go through characters in the string
                    for (int i = 0; i < string_bit.length(); i++) {

                        // Assign character '1' to true
                        if (string_bit.charAt(i) == '1') {
                            write_bitOutput.writeBit(true);
                        }
                        //Assign character '0' to false
                        else if (string_bit.charAt(i) == '0') {
                            write_bitOutput.writeBit(false);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("Cannot read input file" + e.getMessage());
        }

        read_Input.close();
        write_bitOutput.close();
    }


    private static void decompression (String compressedPath, String decompressedPath) throws IOException {

        BufferedBitReader readBits = new BufferedBitReader(compressedPath);
        BufferedWriter readBits_output;
        readBits_output = new BufferedWriter(new FileWriter(decompressedPath));

        BinaryTree<CharNFreq> traverseTree = finalTree;

        try {
            while (readBits.hasNext()) {
                boolean bit = readBits.readBit();

                if (!traverseTree.isLeaf()) {
                    // if code character is 1
                    if (bit) {
                        traverseTree  = traverseTree.getRight();
                    }
                    // if code character is 0
                    else {
                        traverseTree  = traverseTree.getLeft();
                    }

                }
                // if tree has single leaf
                if (traverseTree.isLeaf()) {
                    readBits_output.write((traverseTree.getData().getCharacter()));

                    //reset
                    traverseTree  = finalTree;
                }
            }
        }
        catch (IOException e) {
            System.err.println("No next bit" + e.getMessage());
        }

        readBits.close();
        readBits_output.close();
    }

    public static void main(String[] args) {
        //HuffmanEncoding trial = new HuffmanEncoding();

        try {
           compression("inputs/hello.txt", "inputs/hello_compressed.txt");
           decompression("inputs/hello_compressed.txt", "inputs/hello_decompressed.txt");
        }

        catch (IOException e) {
            System.err.println("Cannot read 'hello.txt' file: " + e.getMessage());
        }


        try {
            compression("inputs/a.txt", "inputs/a_compressed.txt");
            decompression("inputs/a_compressed.txt", "inputs/a_decompressed.txt");
        }

        catch (IOException e) {
            System.err.println("Cannot read 'a.txt' file: " + e.getMessage());
        }


        try {
            compression("inputs/aaaaaaaaaaaaaaaa.txt", "inputs/aaaaaaaaaaaaaaaa_compressed.txt");
            decompression("inputs/aaaaaaaaaaaaaaaa_compressed.txt", "inputs/aaaaaaaaaaaaaaaa_decompressed.txt");
        }

        catch (IOException e) {
            System.err.println("Cannot read 'aaaaaaaaaaaaaaaa.txt' file: " + e.getMessage());
        }


        try {
            compression("inputs/empty.txt", "inputs/empty_compressed.txt");
            decompression("inputs/empty_compressed.txt", "inputs/empty_decompressed.txt");
        }

        catch (IOException e) {
            System.err.println("Cannot read 'empty.txt' file: " + e.getMessage());
        }


        try {
            compression("inputs/WarAndPeace.txt", "inputs/WarAndPeace_compressed.txt");
            decompression("inputs/WarAndPeace_compressed.txt", "inputs/WarAndPeace_decompressed.txt");
        }

        catch (IOException e) {
            System.err.println("Cannot read 'WarAndPeace.txt' file: " + e.getMessage());
        }


        try {
            compression("inputs/USConstitution.txt", "inputs/USConstitution_compressed.txt");
            decompression("inputs/USConstitution_compressed.txt", "inputs/USConstitution_decompressed.txt");
        }

        catch (IOException e) {
            System.err.println("Cannot read 'USConstitution.txt' file: " + e.getMessage());
        }
    }
}
