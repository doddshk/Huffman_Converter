
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class BitConverter {
  public static char[] HEX_VALUES = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'a', 'b', 'c', 'd', 'e', 'f'
  };

  public static final char[] BASE_64_VALUES = {
    // Uppercase letters
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    // Lowercase letters
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    // Digits
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    // Special characters
    '+', '/'
  };

  public static final String[] POSSIBLE_FORMATS = {
    "hex", "base64", "binary"
  };

  public static final int[] NUM_BITS_FOR_FORMAT = {
    4, 6, 8
  };

  // Usage from the command line:
  // cat bit.txt | java BitConverter encode utf
  // possible formats: hex, base64, utf8, utf16
  public static void main(String [] args) throws IOException {
    String mode = args[0];
    boolean isEncode = (mode.equals("encode"));

    String format = args[1];
    int formatEnum = formatToEnum(format);

    if (isEncode) {
      encode(formatEnum);
    } else {
      decode(formatEnum);
    }
  }

  private static void encode(int formatEnum) throws IOException  {
    int numBitsInChar = NUM_BITS_FOR_FORMAT[formatEnum];
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    char[] lookupTable = formatToTable(formatEnum);

    int current = 0;
    int numBitsSoFar = 0;

    int c;
    while ((c = reader.read()) != -1) {
      boolean bit = (c == '1');


      current = (current << 1) | (bit? 1: 0);
      numBitsSoFar ++;
      if (numBitsSoFar >= numBitsInChar) {
        numBitsSoFar = 0;
        int toPrint = (lookupTable == null)? current: (int)lookupTable[current];
        System.out.write(toPrint);
        
        current = 0;
      }
    }

    if (numBitsSoFar == 0) {
      System.out.flush();
      return;
    }

    // flush out the remaining bits
    current = current << (numBitsInChar - numBitsSoFar);
    char toPrint = (lookupTable == null)? (char)current: lookupTable[current];
    System.out.write(toPrint);

    System.out.flush();
    reader.close();
  }

  private static void decode(int formatEnum) throws IOException  {
    int numBitsInChar = NUM_BITS_FOR_FORMAT[formatEnum];
    char[] lookupTable = formatToTable(formatEnum);

    int[] reverseLookupTable = new int[128];
    if (lookupTable != null) {
      for(int i = 0; i < lookupTable.length; i++) {
        reverseLookupTable[lookupTable[i]] = i;
      }
    }

    int c;
    while ((c = System.in.read()) != -1) {
      if (lookupTable != null) {
        c = reverseLookupTable[c];
      }

      for(int i = 0; i < numBitsInChar; i++) {
        System.out.print(c >> (numBitsInChar - i - 1) & 1);
      }
    }

    System.out.flush();
  }

  private static int formatToEnum(String format) {
    for(int i = 0; i < POSSIBLE_FORMATS.length; i++) {
      if (format.equals(POSSIBLE_FORMATS[i])) return i;
    }
    return 0;
  }

  private static char[] formatToTable(int formatEnum) {
    if (formatEnum == 0) {
      return HEX_VALUES;
    } else if (formatEnum == 1) {
      return BASE_64_VALUES;
    }

    return null;
  }
}
