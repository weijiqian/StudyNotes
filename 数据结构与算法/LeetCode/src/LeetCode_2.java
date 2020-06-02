/**
 * @Auther Tom
 * @Date 2020-05-04 22:48
 * @描述 两数相加
 */
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) { val = x; }
}

public class LeetCode_2 {
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        //头结点
        ListNode pre = new ListNode(0);
        ListNode cur = pre;
        //判断同一位相加是否 大于10 ,是否需要进位.
        boolean isGt10 = false;
        while (l1 != null || l2 != null){
            int x = l1 == null ? 0 : l1.val;
            int y = l2 == null ? 0 : l2.val;
            // isGt10 记录的是上一位相加,是否进位了.
            int sum = x + y + (isGt10?1:0);

            int a = 0;
            // 这一次相加  是否进位.
            if (sum >= 10 ){
                //进位了,但是只取个位数.
                isGt10 = true;
                a = sum-10;
            }else {
                isGt10 = false;
                a = sum;
            }
            //新结点
            ListNode la = new ListNode(a);
            //cur的next 指向新结点
            cur.next = la;
            cur = cur.next;

            //下一位
            if (l1 != null){
                l1 = l1.next;
            }
            if (l2 != null){
                l2 = l2.next;
            }
        }

        //判断最后计算的一位是否进位了
        if (isGt10){
            cur.next = new ListNode(1);
        }
        return pre.next;
    }

    public static void main(String[] args){
    // 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
    //输出：7 -> 0 -> 8
    //原因：342 + 465 = 807

        // 输入 89 + 678
        // 输出
        //
        ListNode listNode1 = new ListNode(2);
        listNode1.next = new ListNode(4);
        listNode1.next.next = new ListNode(3);

        ListNode listNode2 = new ListNode(5);
        listNode2.next = new ListNode(6);
        listNode2.next.next = new ListNode(4);

        ListNode listNode3 = new ListNode(9);
        ListNode listNode4 = new ListNode(1);
        listNode4.next = new ListNode(9);
        listNode4.next.next = new ListNode(9);
        listNode4.next.next.next = new ListNode(9);

        ListNode result = addTwoNumbers(listNode1, listNode2);

        while (result != null){
            System.out.println(result.val);
            result = result.next;
        }

    }
}
