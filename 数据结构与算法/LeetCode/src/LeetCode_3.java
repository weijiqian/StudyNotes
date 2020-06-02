import java.util.ArrayList;
import java.util.List;

/**
 * @Auther Tom
 * @Date 2020-05-05 22:20
 * @描述 TODO
 */
public class LeetCode_3 {
    public static int lengthOfLongestSubstring(String s) {
        if (" ".equals(s)){
            return 0;
        }
        int length = 0 ;
        int result = 0 ;
        List<Character> list = new ArrayList<>();
        for (char c:s.toCharArray()){
            if (!list.contains(c)){
                list.add(c);
                length++;
            }else {
                list.clear();
                list.add(c);
                if (length > result){
                    result = length;
                }
                length = 1;
            }
        }
        return length>result?length:result;
    }

    public static final void main(String[] args){
            String a1 = "abcabcbb";  // abc  3
            String a2 = "bbbbb";  //b  1
            String a3 = "pwwkew"; //wke 3
            String a4 = " ";//1
            String a5 = "a";//1
            String a6 = "dvdf";

        System.out.println(lengthOfLongestSubstring(a6) + "");
    }

}
