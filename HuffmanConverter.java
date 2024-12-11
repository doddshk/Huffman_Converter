import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class HuffmanConverter {
  // Usage from the command line:
  // cat just_to_say.txt | java HuffmanConverter encode spec.txt
  public static void main(String [] args) throws IOException {
    String mode = args[0].toLowerCase();
    String treeFile = args[1];

    String treeStr = StdinToString.readfile(treeFile);
    //System.out.println("treeStr: " + treeStr); debug
    HuffmanTree tree = HuffmanTree.loadTree(treeStr);
    //tree.printTree(); 

    String input = StdinToString.read();
    //System.out.println("Input: " + input);

    if (mode.equals("decode")) {
        StringBuilder decoded = new StringBuilder();

        // Reset the tree iterator
        tree.resetIterator();

        boolean messageStarted = false; // Flag to indicate when the message starts
        boolean encounteredEndOfMessage = false; // Flag to indicate when the end-of-message is encountered

        for (char bit : input.toCharArray()) {
            // Skip filler bits before the message starts
            if (!messageStarted) {
                String symbol = tree.advanceCurrent(bit);
                if (symbol != null && !symbol.equals("\\e")) { // A valid symbol is detected
                    messageStarted = true;  // Start of the message
                    decoded.append(symbol);  // Add the first valid symbol
                }
                continue;  // Skip further processing for filler bits
            }

            // Stop decoding if the end-of-message (\e) is encountered
            if (encounteredEndOfMessage) {
                break;  // Stop processing further bits
            }

            // Process subsequent bits after the message has started
            String symbol = tree.advanceCurrent(bit);
            if (symbol != null) { // A leaf node is reached
                if (symbol.equals("\\e")) { // End-of-message symbol
                    encounteredEndOfMessage = true;  // Stop decoding after \e
                    break;
                }
                decoded.append(symbol);  // Append the symbol to the decoded message
            }
        }

        // Print the decoded string
        System.out.println(decoded.toString());
    } else if (mode.equals("analyze")) {
      int encodedBits = 0;
      int originalCharacterCount = input.length();

      for (char c : input.toCharArray()) {
          String encodedCharacter = encodeCharacter(tree, c);
          encodedBits += encodedCharacter.length();
      }

      // Add the bits for the end-of-message (\e)
      String eomEncoding = encodeCharacter(tree, "\\e"); // Treat \e as a String
      encodedBits += eomEncoding.length();

      // Calculate the average bits per character
      double averageBitsPerCharacter = (double) encodedBits / originalCharacterCount;

      // Output the analysis results
      System.out.println("Encoded Bits: " + encodedBits);
      System.out.println("Original Character Count: " + originalCharacterCount);
      System.out.println("Average Bits Per Character: " + averageBitsPerCharacter); 

    } else if (mode.equals("encode")) {
      //System.out.println("Input: " + input);
      StringBuilder encoded = new StringBuilder();
        for (char c : input.toCharArray()) {
            encoded.append(encodeCharacter(tree, c));
        }
        System.out.println(encoded.toString());

    } else {
      System.out.println("Unknown Mode: " + mode);
    }
  }

  // Helper Method to Encode a Single Character
  private static String encodeCharacter(HuffmanTree tree, char c) {
      StringBuilder bits = new StringBuilder();
      encodeCharacterRecursive(tree.getRoot(), c, "", bits);
      return bits.toString();
  }

  private static String encodeCharacter(HuffmanTree tree, String str) {
    if (str.equals("\\e")) {
        // Handle the special case for \\e
        StringBuilder bits = new StringBuilder();
        encodeCharacterRecursive(tree.getRoot(), '\\', "", bits); // Encode the backslash
        encodeCharacterRecursive(tree.getRoot(), 'e', "", bits);  // Encode the 'e'
        return bits.toString();
    } else if (str.length() == 1) {
        // If the string has only one character, use the original method
        return encodeCharacter(tree, str.charAt(0));
    } else {
        throw new IllegalArgumentException("Invalid input: Only single characters or \\e are allowed.");
    }
  }

  private static boolean encodeCharacterRecursive(HuffmanTree.HuffmanNode node, char c, String currentPath, StringBuilder bits) {
      if (node == null) return false;

      if (node.left == null && node.right == null) { // Leaf node
          //System.out.println("Leaf node symbol: " + node.symbols + " currentPath: " + currentPath);
          if (HuffmanTree.convertSymbolToChar(node.symbols).equals(String.valueOf(c))) {
              bits.append(currentPath);
              return true;
          }
          return false;
      }

      return encodeCharacterRecursive(node.left, c, currentPath + "0", bits) ||
             encodeCharacterRecursive(node.right, c, currentPath + "1", bits);
  }
}
