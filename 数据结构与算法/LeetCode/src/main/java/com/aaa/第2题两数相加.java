package com.aaa;

/**
 * @Auther Tom
 * @Date 2020-04-30 20:54
 * @描述
 */

//给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
//
// 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
//
// 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
//
// 示例：
//
// 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
//输出：7 -> 0 -> 8
//原因：342 + 465 = 807
//
// Related Topics 链表 数学


//leetcode submit region begin(Prohibit modification and deletion)
class ListNode {
      int val;
      ListNode next;
      ListNode(int x) { val = x; }
  }

class Solution3 {
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int a1 = 0;
        int b1 = 0;
        while (l1 != null){
            a1 += l1.val * Math.pow(10,b1);
            l1 = l1.next;
            b1++;
        }
        int a2 = 0;
        int b2 = 0;
        while (l2 != null){
            a2 += l2.val * Math.pow(10,b2);
            l2 = l2.next;
            b2++;
        }
        int c = a1 + a2;
        ListNode listNode = new ListNode(0);
        while (c != 0){
            ListNode oldNode = listNode;
            ListNode newNode  = new ListNode(c%10);
            c = c/10;
            listNode.next = newNode;

        }

        return listNode;
    }
}

public class 第2题两数相加 {
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
        listNode2.next.next = new ListNode(3);

        ListNode result = Solution3.addTwoNumbers(listNode1, listNode2);

        while (result != null){
            System.out.println(result.val);
            result = result.next;
        }

    }
}
