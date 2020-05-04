package com.aaa;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther Tom
 * @Date 2020-04-30 20:14
 * @描述
 */
//给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
//
// 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
//
//
//
// 示例:
//
// 给定 nums = [2, 7, 11, 15], target = 9
//
//因为 nums[0] + nums[1] = 2 + 7 = 9
//所以返回 [0, 1]
//
// Related Topics 数组 哈希表


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public static int[] twoSum(int[] nums, int target) {
        //nums = [2, 7, 11, 15], target = 9
        Map<Integer,Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            int a = target - nums[i];
            if (map.containsKey(a)){
                return new int[]{i,map.get(a)};
            }
            map.put(nums[i],i); // {(2,0),}
        }
        return null;
    }
}

class Solution2{
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement) && map.get(complement) != i) {
                return new int[] { i, map.get(complement) };
            }
        }
        throw new IllegalArgumentException("No two sum solution");
    }



}

public class 第1题两个数字的和 {

    public static void main(String[] args){
        int nums[] = new int[]{2,5,7,9,12,15,16};
        int target = 9;
        int[] result = Solution.twoSum(nums, target);
        for (int item : result) {
            System.out.println(item);

        }
    }

}
