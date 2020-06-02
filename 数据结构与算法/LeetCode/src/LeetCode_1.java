import java.util.HashMap;
import java.util.Map;

/**
 * @Auther Tom
 * @Date 2020-05-04 22:43
 * @描述 TODO
 */


public class LeetCode_1 {

    public static int[] twoSum1(int[] nums, int target) {
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

    public static int[] twoSum2(int[] nums, int target) {
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


    public static void main(String[] args){
        int nums[] = new int[]{2,5,7,9,12,15,16};
        int target = 9;
        int[] result = twoSum1(nums, target);
        for (int item : result) {
            System.out.println(item);

        }
    }




}
