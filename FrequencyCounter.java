import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class FrequencyCounter {
  private int[] frequencies;
  
  public FrequencyCounter() {
    frequencies = new int[128];  // ASCII array like in FrequencyCounter
  }
  
  public void fromStdin() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while(reader.ready()) {
      int c = reader.read();
      frequencies[c] += 1;
    }
  }
  
  public void printFrequencies() {
    boolean isFirst = true;
    System.out.print("eom 1");

    for (int i = 0; i < frequencies.length; i++) {
      if (frequencies[i] == 0) continue;

      System.out.print(" ");
      String symbol = (i == 32)? "space": "" + (char)i;

      System.out.print(symbol);
      System.out.print(" ");
      System.out.print(frequencies[i]);
    }
  }
  
  // Utility methods to access the data
  public int getCount(char c) {
    return frequencies[c];
  }
  
  public int getSpaceCount() {
    return frequencies[32];
  }
  
  public int[] getAllFrequencies() {
    return frequencies.clone();  // Return a copy to prevent modification
  }
  
  public static void main(String[] args) throws IOException {
    FrequencyCounter parser = new FrequencyCounter();
    parser.fromStdin();
    parser.printFrequencies();
  }
}