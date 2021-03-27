from pathlib import Path
from itertools import combinations
import os


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


#######################
####### MAIN #######
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
    '''
    byte_strs = {}

    for f in get_sample_files("files"): 
        byte_strs[f] = Path("./files/"+f).read_bytes()

    for pair in combinations(byte_strs, 2):
        pair_strs = byte_strs[pair[0]], byte_strs[pair[1]]
        info_lst = longest_common_substr(*pair_strs)
        assert(pair_strs[0][info_lst[1]:info_lst[0]+info_lst[1]] == pair_strs[1][info_lst[2]:info_lst[0]+info_lst[2]])
    
if __name__ == "__main__":
    main()
    # import doctest
    # doctest.testmod()