import java.awt.*;
import java.io.*;
import java.util.*;

import netscape.javascript.JSObject;

public class EluvioClng {

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
        int n1 = str1.length;
        int n2 = str2.length;
        int[][] dp = new int[2][Math.max(n1, n2) + 1];
        int[] output = new int[3];
        for (int i = 1; i <= n1; i++) {
            for (int j = 1; j <= n2; j++) {
                if (str1[i-1] == str2[j-1]) { // Update if characters match
                    dp[i % 2][j] = dp[(i - 1) % 2][j - 1] + 1;
                    if (output[0] < dp[i % 2][j]) {
                        output[0] = dp[i % 2][j];
                        output[1] = i - output[0];
                        output[2] = j - output[0];
                    }
                } else { // Else restart the count
                    dp[i % 2][j] = 0;
                }
            }
        }
        return output;
    }

    /**
     * Get hashmap of all files from directory
     * @param path Directory with all binary files
     * @return a hashmap corresponding to each file and it's byte array contents
     */
    private static HashMap<String, byte[]> getByteArrays(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        HashMap<String, byte[]> byteStrings = new HashMap<>();
        InputStream inputStream;
        for (File f : files) {
            try {
                inputStream = new FileInputStream(f);
                byteStrings.put(f.toString(), inputStream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteStrings;
    }

    /**
     * Finds subarray within byte array
     * @param arr The larger array
     * @param subarr subarray to find
     * @return index of offset if subarr in arr else -1
     */
    private static int subArrayInd(byte[] arr, byte[] subarr) {
        return arr.toString().indexOf(subarr.toString());
    }

    /**
     * Creates output array given maxTracker and byteArrays.
     * @param byteArrs hashmap of file names and byte arrys
     * @param maxTracker the max length byte string's information
     * @return [maxStrLen, {file: offset, ...}]
     */
    private static Object[] createOutput(HashMap<String, byte[]> byteArrs, Object[] maxTracker) {
        HashMap<String, Integer> offsetMap = new HashMap<>();
        offsetMap.put((String) maxTracker[0], ((int[]) maxTracker[2])[1]);
        offsetMap.put((String) maxTracker[1], ((int[]) maxTracker[2])[2]);

        byte[] arr = byteArrs.get((String) maxTracker[0]);
        int subStrLen = ((int[]) maxTracker[2])[0];
        int offset = ((int[]) maxTracker[2])[1];
        byte[] subArr = Arrays.copyOfRange(arr, offset, subStrLen + offset);

        for (String k : byteArrs.keySet()) {
            if (k.equals((String) maxTracker[0]) || k.equals((String) maxTracker[1])) continue;
            offset = subArrayInd(byteArrs.get(k), subArr);
            if (offset != -1) {
                offsetMap.put(k, offset);
            }
        }
        return  new Object[]{subStrLen, offsetMap};
    }

    /**
     * Finds the longest common byte string's offset between any TWO files in the given directory
     * @param path directory with binary files
     * @return [byteArrayMap, maxByteStringInfo]
     */
    private static Object[] findLongestInPath(String path) {
        HashMap<String, byte[]> byteArrayMap = getByteArrays(path);
        Object[] maxTracker = new Object[]{"", "", new int[]{0, 0, 0}};
        HashMap<String, ArrayList<String>> seenCombos = new HashMap<>();
        byte[] f1Arr, f2Arr;
        for (String k : byteArrayMap.keySet()) {
            seenCombos.put(k, new ArrayList<>());
        }

        for (String f1 : byteArrayMap.keySet()) { // Iterate through all combinates of files--takes O(n^2)
            for (String f2 : byteArrayMap.keySet()) {
                if (f1.equals(f2) || seenCombos.get(f2).contains(f1)) {
                    continue;
                }
                seenCombos.get(f1).add(f2);
                f1Arr = byteArrayMap.get(f1);
                f2Arr = byteArrayMap.get(f2);
                int[] curTracker = longestCommonSubstr(f1Arr, f2Arr);
                if (curTracker[0] > ((int[]) maxTracker[2])[0]) { // Change maxTracker if a new longest is found
                    maxTracker[0] = f1;
                    maxTracker[1] = f2;
                    maxTracker[2] = curTracker;
                }
            }
        }
        return  new Object[]{byteArrayMap, maxTracker};
    }

    /**
     * Serializes the parameters to output.json
     * @param strLen length of the longest substring
     * @param offsetMap map of offsets for each file
     */
    private static void saveOutput(int strLen, HashMap<String, Integer> offsetMap) {
        try {
            PrintWriter out = new PrintWriter("output.json");
            out.println("[");
            out.println("    "+strLen+",");
            out.println("    {");
            for (String k : offsetMap.keySet()) {
                out.println("        \""+k + "\" : " + offsetMap.get(k)+",");
            }
            out.println("    }");
            out.println("]");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    public static void main(String[] args) {
        // Find the longest string thats in >2 iles
        Object[] longestInfo = findLongestInPath("files");
        // Parse Output
        Object[] maxTracker = (Object[]) longestInfo[1];
        HashMap<String, byte[]> byteArrayMap = (HashMap<String, byte[]>) longestInfo[0];
        int[] info = (int[]) maxTracker[2];
        System.out.println((String) maxTracker[0] + (String) maxTracker[1]);
        System.out.println(Arrays.toString(info));
        // Find it's place in all other files if possible
        Object[] output = createOutput(byteArrayMap, maxTracker);
        int maxLen = (int) output[0];
        HashMap<String, Integer> offsetMap = (HashMap<String, Integer>) output[1];
        // Serialize the output
        saveOutput(maxLen, offsetMap);

    }
}
