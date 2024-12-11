import java.io.IOException;
import java.util.Stack;

public class HuffmanTree {
  private HuffmanNode root;
  private HuffmanNode currentNode; //used for decode
                                   //

  public static class HuffmanNode implements Comparable<HuffmanNode> {
    final public String symbols;
    final public Double frequency;
    final public HuffmanNode left, right;

    public HuffmanNode(String symbol, double frequency) {
        this.symbols = symbol;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }
    
    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.symbols = left.symbols + right.symbols;
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    public int compareTo(HuffmanNode other) {
              int freqComparison = Double.compare(this.frequency, other.frequency);
        if (freqComparison != 0) {
            return freqComparison; // Primary comparison by frequency
        }
        // Secondary comparison by symbol lexicographic order
        return this.symbols.compareTo(other.symbols);
    }

    public String toString() {
      return "<" + symbols + ", " + frequency + ">";
    }

  }
  //------------------------------------------------ Huffman node ends here \\
  //
  //

  // public HuffmanTree(HuffmanNode root) {}
 
  public void printLegend() {
    printLegendRecursive(this.root, "");
  } 

  private void printLegendRecursive(HuffmanNode node, String bits) {
    if (node.left == null && node.right == null) { // Leaf node
        System.out.println(convertSymbolToChar(node.symbols) + "\t" + bits);
        return;
    }
    if (node.right != null) printLegendRecursive(node.right, bits + "0");
    if (node.left != null) printLegendRecursive(node.left, bits + "1");
  }

  public void printTreeSpec() {
    StringBuilder sb = new StringBuilder();
    buildTreeSpec(this.root, sb);
    System.out.println(sb.toString());
  }

  private void buildTreeSpec(HuffmanNode node, StringBuilder sb) {
    if (node.left == null && node.right == null) { // Leaf node
        sb.append(convertSymbolToChar(node.symbols));
    } else {
        if (node.left != null) buildTreeSpec(node.left, sb);
        if (node.right != null) buildTreeSpec(node.right, sb);
        sb.append("|");
    }
  }

  public static BinaryHeap<HuffmanNode> freqToHeap(String frequencyStr) {
    String[] parts = frequencyStr.split(" ");
    
    HuffmanNode[] nodes = new HuffmanNode[parts.length / 2];

    for (int i = 0; i < parts.length; i += 2) {
        String symbol = parts[i];
        double frequency = Double.parseDouble(parts[i + 1]);
        nodes[i / 2] = new HuffmanNode(symbol, frequency);
    }

    return new BinaryHeap<>(nodes);
  }

  public static HuffmanTree createFromHeap(BinaryHeap<HuffmanNode> heap) {
        while (heap.getSize() > 1) {
        HuffmanNode left = heap.extractMin();
        HuffmanNode right = heap.extractMin();
        HuffmanNode parent = new HuffmanNode(left, right);
        heap.insert(parent);
        //System.out.println("Inserted parent node: " + parent); // Debug print
    }

    HuffmanTree tree = new HuffmanTree();
    tree.root = heap.extractMin();  // Set the root after combining all nodes
    //System.out.println("Final root node: " + tree.root); // Debug print
    return tree;
  }


  // Usage from the command line:
  // cat sample_legend.txt | java HuffmanTree 
  // on windows: type sample_legend.txt | java HuffmanTree
  public static void main(String [] args) throws IOException {
    String mode = (args.length == 0)? "spec": args[0];

    String frequencyStr = StdinToString.read();

    BinaryHeap<HuffmanNode> heap = freqToHeap(frequencyStr);
    HuffmanTree tree = createFromHeap(heap);

    if (mode.toLowerCase().equals("legend")) {
      tree.printLegend();
    } else {
      tree.printTreeSpec();
    }
    //tree.debugPrintTree();
  }

  public void debugPrintTree() {
    debugPrintNode(this.root, "");
}

private void debugPrintNode(HuffmanNode node, String prefix) {
    if (node == null) return;
    //System.out.println(prefix + node.toString());
    debugPrintNode(node.left, prefix + "0");
    debugPrintNode(node.right, prefix + "1");
}

  public static String convertSymbolToChar(String symbol) {
    if (symbol.equals("space")) return " ";
    if (symbol.equals("eom")) return "\\e";
    if (symbol.equals("|")) return "\\|";
    if (symbol.equals("\\")) return "\\\\";
    return symbol;
  }

  public static HuffmanTree loadTree(String treeFile) {
    // Use a stack to reconstruct the tree
    Stack<HuffmanTree.HuffmanNode> stack = new Stack<>();
    int index = 0;

    while (index < treeFile.length()) {
        char currentChar = treeFile.charAt(index);

        if (currentChar == '|') {
            // Internal node: pop two nodes, create a parent, and push it back
            if (stack.size() < 2) {
                throw new IllegalArgumentException("Invalid tree file format: not enough nodes to merge.");
            }
            HuffmanNode right = stack.pop();
            HuffmanNode left = stack.pop();
            HuffmanNode parent = new HuffmanNode(left, right);
            stack.push(parent);
        } else {
            // Handle escape sequences (e.g., '\\e', '\\|', '\\\\', etc.)
            if (currentChar == '\\' && index + 1 < treeFile.length()) {
                index++; // Skip the backslash

                char nextChar = treeFile.charAt(index);
                if (nextChar == 'e') {
                    stack.push(new HuffmanNode("\\e", 0));  // Add '\e' symbol as leaf node
                } else {
                    stack.push(new HuffmanNode(String.valueOf(nextChar), 0));  // Handle other escape sequences
                }
            } else {
                // Regular character, push as leaf node
                String symbol = String.valueOf(currentChar);
                stack.push(new HuffmanNode(symbol, 0));
            }
        }
        //debugPrintStack(stack);
        index++;
    }

    // Ensure that only one node is left in the stack (root of the tree)
    while (stack.size() > 1) {
      HuffmanNode right = stack.pop();
      HuffmanNode left = stack.pop();
      HuffmanNode parent = new HuffmanNode(left, right);
      stack.push(parent);
      //debugPrintStack(stack);
    }

    // The final node in the stack is the root of the tree
    HuffmanTree tree = new HuffmanTree();
    tree.root = stack.pop();
    return tree;
  }


  public void printTree() {
    printTreeRecursive(this.root, 0);
  }

  private void printTreeRecursive(HuffmanNode node, int level) {
    if (node == null) return;

    // Print current node's symbol and frequency
    String indent = " ".repeat(level * 2);  // Indentation based on tree level
    System.out.println(indent + "<" + node.symbols + ", " + node.frequency + ">");

    // Recursively print left and right children
    printTreeRecursive(node.left, level + 1);
    printTreeRecursive(node.right, level + 1);
  }   

  public static void debugPrintStack(Stack<HuffmanNode> stack) {
    // Check if the stack is empty
    if (stack.isEmpty()) {
        System.out.println("The stack is empty.");
        return;
    }

    // Iterate through the stack and print each node's symbol or structure
    System.out.println("Stack Contents:");
    for (int i = 0; i < stack.size(); i++) {
        HuffmanNode node = stack.get(i);
        System.out.print("Node " + i + ": ");

        // If it's a leaf node (symbol is not null), print the symbol
        if (node.left == null && node.right == null) {
            System.out.println("Symbol: " + node.symbols); // Assuming 'symbols' holds the character(s)
        } else {
            // If it's an internal node, just show it as an internal node
            System.out.println("Internal Node");
        }
    }
    System.out.println();  // Print an empty line for clarity
  }

  // below code used for decode funtionality
  public void resetIterator() {
      this.currentNode = this.root;
  }


public String advanceCurrent(char bit) {
    if (bit == '0') {
        currentNode = currentNode.left;
    } else if (bit == '1') {
        currentNode = currentNode.right;
    } else {
        throw new IllegalArgumentException("Invalid bit: " + bit);
    }

    // If the current node is a leaf, return its symbol
    if (currentNode.left == null && currentNode.right == null) {
        String symbol = convertSymbolToChar(currentNode.symbols);
        currentNode = root; // Reset to root for next traversal
        return symbol;
    }

    return null; // Not a leaf, continue decoding
}

  public HuffmanNode getRoot() {
        return this.root;
  }
}
