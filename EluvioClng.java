import java.io.*;
import java.util.*;
import

public class EluvioClng {
    private static final int BUFFER_SIZE = 4096;

    /**
     * Finds the longest common substring in str1 and str2
     *     Because these will be longer strings,
     *     I'll use a space-optimized dynamic programming approach.
     *     Time Complexity: O(len(str1)*len(str2))
     *     Space Complexity: O(max(len(str1), len(str2))
     * @param str1 first byte string
     * @param str2 second byte string
     * @return [strand_length, offset_str1, offset_str2]
     */
    private static int[] longestCommonSubstr(byte[] str1, byte[] str2) {

    }

    private static HashMap<String, byte[]> getByteStreams(String path) throws FileNotFoundException {
        File dir = new File(path);
        File[] files = dir.listFiles();
        HashMap<String, byte[]> byteStrings = new HashMap<>();
        InputStream inputStream;
        for (File f : files) {
            inputStream = new FileInputStream(f);
            byteStrings.put(f, IOUtils.toByteArray(inputStream));
        }
        return byteStrings;
    }

    public static void main(String[] args) {

    }
}
