from pathlib import Path
from itertools import combinations
import os
import json

#######################
####### UTILITY #######
#######################

def longest_common_substr(str1: str, str2: str) -> list([int, int, int]):
    '''Finds the longest common substring in str1 and str2 and returns the 
    string and the offsets in the form: 
    [strand_length, offset_str1, offset_str2]

    Because these will be longer strings,
    I'll use a space-optimized dynamic programming approach. 
    Time Complexity: O(len(str1)*len(str2))
    Space Complexity: O(max(len(str1), len(str2))

    >>> longest_common_substr("abcde", "cde")
    (3, 2, 0)

    >>> longest_common_substr("apotatohead", "qqqq")
    (0, None, None)

    >>> longest_common_substr("sample.1", "sample.10")
    (8, 0, 0)

    '''
    n_1, n_2 = len(str1), len(str2)
    dp = [[0 for _ in range(max(n_1, n_2) + 1)] for _ in range(2)] # Initialize DP
    # Offsets are formatted as [offset, len_str]
    str_len, offset_1, offset_2 = 0, None, None
    
    for i in range(n_1+1):
        for j in range(n_2+1):
            # If the characters match increase the length of 
            # a substring by 1, else reset the counter
            if str1[i-1] == str2[j-1]: 
                dp[i % 2][j] = dp[(i-1) % 2][j-1] + 1
                if str_len < dp[i % 2][j]: 
                    str_len = dp[i % 2][j]
                    offset_1 = i - str_len 
                    offset_2 = j - str_len
            else: 
                dp[i % 2][j] = 0

    return str_len, offset_1, offset_2

def get_sample_files(path):
    '''Returns a list of files from a given folder'''
    file_list = os.listdir(path)
    list.sort(file_list) 
    return file_list

def create_output(byte_strs: dict, max_tracker: list) -> list:
    '''
    Creates output list from byte strings and maximum string tracker
    '''
    max_str = byte_strs[max_tracker[0]][max_tracker[2][1]:max_tracker[2][0]+max_tracker[2][1]]
    output_lst = [
        max_tracker[2][0],
        {
            max_tracker[0]:max_tracker[2][1], 
            max_tracker[1]:max_tracker[2][2], 
        }
    ]

    for f in byte_strs.keys():
        if f in max_tracker:
            continue
        else:
            output_lst[1][f] = byte_strs[f].find(max_str)

    return output_lst

#######################
######## MAIN #########
#######################

def main():
    '''
    1) We go through each pair of files and find the longest common substr in those files.
    2) Then we get take the longest of those n(n-1)/2 substrings and search for it in each of the other files
    3) Return 
    - the length of the strand
    - the file names where the largest strand appears
    - the offset where the strand appears in each file
    as a list of the form
    [
        str_len,
        {
            file_name: offset,
            file_name: offset,
            ...
        }
    ]

    Overall Time Complexity: O(n^2*m^2 + nm), where n is the number of files, and m is the length of the longest file
    Overall Space Complexity: O(m + n) if Python buffers Byte strings else O(nm)
    '''
    byte_strs = {}

    for f in get_sample_files("files")[:2]: 
        byte_strs[f] = Path("./files/"+f).read_bytes()

    max_tracker = [None, None, [0, None, None]] # [file1, file2, [max_len, offset1, offset2]]
    for pair in combinations(byte_strs, 2):

        pair_strs = byte_strs[pair[0]], byte_strs[pair[1]]
        info_lst = longest_common_substr(*pair_strs)
        
        if info_lst[0] > max_tracker[2][0]:
            max_tracker = [pair[0], pair[1], info_lst]

    return create_output(byte_strs, max_tracker)

def write_json(output_lst):
    '''
    Save the output to a JSON file
    '''
    with open("output.json", "w") as f:
        json.dump(output_lst, f, indent=4)

    
if __name__ == "__main__":
    write_json(main())
    # import doctest
    # doctest.testmod()