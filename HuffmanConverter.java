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
    tree.printTree(); 

    String input = StdinToString.read();
    //System.out.println("Input: " + input);

    if (mode.equals("decode")) {
      StringBuilder decoded = new StringBuilder();

      // Reset the tree iterator
      tree.resetIterator();

      // Variable to track if extra zeros are encountered
      boolean encounteredEndOfMessage = false;

      for (char bit : input.toCharArray()) {
          // If we already encountered \e, skip processing further bits
          if (encounteredEndOfMessage) {
              break;
          }

          String symbol = tree.advanceCurrent(bit);

          // If we reached the end-of-message symbol, stop decoding
          if (symbol == null) {
              encounteredEndOfMessage = true;
              System.out.println("reached");
              break; // Stop decoding after encountering the end-of-message
          }

          decoded.append(symbol); // Append the symbol to the output
      }

      // Print the decoded string
      System.out.println(decoded.toString());
    } else if (mode.equals("analyze")) {

      // TODO - implement this!
      System.out.println("analyze mode not implemented");

    } else if (mode.equals("encode")) {
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

  private static boolean encodeCharacterRecursive(HuffmanTree.HuffmanNode node, char c, String currentPath, StringBuilder result) {
      if (node == null) return false;

      if (node.left == null && node.right == null) { // Leaf node
          //System.out.println("Leaf node symbol: " + node.symbols + " currentPath: " + currentPath);
          if (HuffmanTree.convertSymbolToChar(node.symbols).equals(String.valueOf(c))) {
              result.append(currentPath);
              return true;
          }
          return false;
      }

      return encodeCharacterRecursive(node.left, c, currentPath + "0", result) ||
             encodeCharacterRecursive(node.right, c, currentPath + "1", result);
  }
}
