package com.github.jspxnet.txweb.table.vote;


import java.util.Comparator;


/**
 * 票数排序
 */
public class SortVotePointComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null) {
            return 0;
        }
        if (o2 == null) {
            return 0;
        }
        int a = ((VoteItem) o1).getVotePoint();
        int b = ((VoteItem) o2).getVotePoint();
        return Integer.compare(a, b);
    }

}
