## Eluvio SWE Intern Interview Challenge

Given a large number of binary files, write a program that finds the 
longest strand of bytes that is identical between two or more files 

Use the test set attached (files sample.*) 

Note that the Java implementation is MUCH faster, but asympotitcally the same. 
The time complexity of the algorithm is explained below with n being the number of files and m being the max length of a file:

1. Get all files from the directory O(n)
2. Convert them into Byte Arrays O(nm)
3. Iterate through each combination of files and find the longest common substring in at least two files. O(m^2)
4. Using this substring, find it in any/all other files. O(nm)
5. Return information O(n)

Time Complexity: O(nm+m^2)

Space Complexity: O(nm), but could be further optimizing by using buffered byte string or not putting all files into memory at the same time. 

The program saves the following to output.json: 
- the length of the strand 
- the file names where the largest strand appears 
- the offset where the strand appears in each file 

### To run
    python3 eluvio_clng.py
OR
    javac EluvioClng.java && java EluvioClng
