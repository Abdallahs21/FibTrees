import java.util.*;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    /**attributes **/
    public static int totalCuts = 0;
    public static int totalLinks = 0;

    protected HeapNode min;
    protected HeapNode first;
    protected int size;
    private int totalMarked;
    public int totalTrees;




    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public boolean isEmpty()
    {
        return min == null;
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key) {
        totalTrees++;
        size++;
        HeapNode node = new HeapNode(key);
        if (isEmpty()){
            min = node;
            min.next = min; min.prev= min;
            first = node;
            return node;
        }
        if(node.key < min.key){
            min = node;
        }
        node.next = first;
        node.prev = first.prev;
        first.prev.next  = node;
        first.prev = node;
        return node;
    }

    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     */
    public void deleteMin()
    {
        if (size() == 1){
            min = null;
            first = null;
            size = 0;
            totalTrees = 0;
            return;
        }

        HeapNode child = min.child;
        /**removing the minimum**/
        HeapNode next = min.next;
        HeapNode prev = min.prev;
        next.prev = prev;
        prev.next = next;
        if (min == first);
            first = next;
        if (child != null) {
            HeapNode lastChild = child.prev;
            first.prev.next = child;
            lastChild.next = first;
            child.prev = first.prev;
            first.prev = lastChild;
        }

        /**finding the new minimum**/
        HeapNode p = first.next;
        min = first;
        while (p!=first){
            if (p.key< min.key){
                min = p;
            }
            p = p.next;
        }

        size--;
        consolidating();

    }


    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     */
    public HeapNode findMin()
    {
        return min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld (FibonacciHeap heap2)
    {
        totalTrees = totalTrees + heap2.totalTrees;
        if (heap2.min.key < min.key){
            min = heap2.min;
        }
        HeapNode first2 = heap2.first;
        HeapNode last2 = heap2.first.prev;
        first.prev.next = first2;
        first2.prev = first.prev;
        first.prev = last2;
        last2.next = first2;
        consolidating();
    }

    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size() {
        return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * (Note: The size of of the array depends on the maximum order of a tree.)
     *
     */
    public int[] countersRep()
    {
        int max = this.calcRank(first);
        HeapNode root = first.next;
        while (root!=first){
            int r = this.calcRank(root);
            if (r > max) {
                max = r;
            }
            root = root.next;
        }
        int[] arr = new int[max];
        int r = this.calcRank(root);
        arr[r]++;
        root = root.next;
        while (root!=first){
            r = this.calcRank(root);
            arr[r]++;
            root = root.next;
        }
        return arr;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     */
    public void delete(HeapNode x)
    {
        int delta = x.key - min.key + 1;
        decreaseKey(x, delta);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.key = x.key - delta;
        if(x.parent.key > x.key){
            cascadeCut(x);
        }
        if (x.key < min.key) {
            min = x;
        }
    }



    /**
     * public int nonMarked()
     *
     * This function returns the current number of non-marked items in the heap
     */
    public int nonMarked()
    {
        return size - totalMarked;
    }

    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     *
     *
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential()
    {
        if (size == 0)
            return 0;

        int cnt = 1;
        HeapNode p = first.next;
        while (p != first){
            cnt++;
        }
        return cnt + 2*totalMarked;
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks()
    {
        return totalLinks;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return totalCuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k){
        if (k == 0 || H.isEmpty())  {return null;}

        int[] arr = new int[k];
        FibonacciHeap assistHeap = new FibonacciHeap();
        assistHeap.insertConsolidating(H.min.key,H.min);
        int i = 0;
        HeapNode p = H.min;
        while(i < k) {
            if(p.child != null) {
                assistHeap.insertChildren(p,p.child.next);
            }
            arr[i] = assistHeap.min.key;
            assistHeap.deleteMin();
            if(!assistHeap.isEmpty()) {
                p = assistHeap.min.pointer;
            }
            i++;

        }
        return arr;
    }



            /**
             * public class HeapNode
             *
             * If you wish to implement classes other than FibonacciHeap
             * (for example HeapNode), do it in this file, not in another file.
             *
             */
            public static class HeapNode {

                public int key;
                /**
                 * new attributes
                 **/
                public int rank;
                public boolean marked = false;
                public HeapNode child;
                public HeapNode prev;
                public HeapNode next;
                public HeapNode parent;
                public HeapNode pointer;

                public HeapNode(int key) {
                    this.key = key;
                }

                public int getKey() {
                    return key;
                }
            }
            /** help methods **/
            private void consolidating () {
                HeapNode[] linkingArray = new HeapNode[1 + (int) Math.log(size + 1)];
                HeapNode node = first.prev;
                while (node != first) {
                    int r = calcRank(node);
                    if (linkingArray[r] == null) {
                        linkingArray[r] = node;
                        node = node.next;
                    } else {
                        HeapNode p = linkingArray[r];
                        HeapNode q = node;
                        while (r < linkingArray.length && p != null) {
                            q = link(p, q);
                            linkingArray[r] = null;
                            p = linkingArray[++r];
                        }
                        if (r == linkingArray.length) {
                            linkingArray[r - 1] = q;
                        } else {
                            linkingArray[r] = q;
                        }
                        node = node.next;
                    }
                }
            }


            private HeapNode link (HeapNode p, HeapNode q){
                totalLinks++;
                if (p.key > q.key) {
                    p.parent = q;
                    HeapNode child = q.child;
                    child.parent.next = p;
                    p.prev = child.prev;
                    child.prev = p;
                    p.next = child;
                    q.rank++;
                    return q;

                } else {
                    q.parent = p;
                    HeapNode child = p.child;
                    child.parent.next = q;
                    q.prev = child.prev;
                    child.prev = q;
                    q.next = child;
                    p.rank++;
                    return p;
                }
            }


            public static int calcRank (HeapNode root){
                HeapNode child = root.child;
                if (child == null) {
                    return 0;
                }
                HeapNode p = child.next;
                int cnt = 1;
                while (p != child) {
                    p = p.next;
                    cnt++;
                }
                return cnt;

            }

            private void cascadeCut (HeapNode x){
                totalCuts++;

                HeapNode p = x.parent;
                HeapNode next = x.next;
                if (p.child == x) {
                    if (next == x) {
                        p.child = null;
                    } else {
                        p.child = next;
                        next.prev = x.prev;
                        x.prev.next = next;
                    }
                }
                else {
                    next.prev = x.prev;
                    x.prev.next = next;
                }
                x.next = first;
                x.prev = first.prev;
                first.prev.next  = x;
                first.prev = x;
                x.parent = null;
                x.marked = false;
                totalTrees++;
                totalMarked--;
                if (p.marked) {
                    cascadeCut(p);
                } else {
                    p.marked = true;
                    totalMarked++;
                }
            }


    private void insertChildren(HeapNode parent, HeapNode p) {
        HeapNode q = parent.child.next;
        if (q != null) {
            this.insertConsolidating(q.key, p);
        }

        while (q != parent.child) {
            this.insertConsolidating(q.key, p);
            q = q.next;
            p = p.next;
        }

    }

    public void insertConsolidating(int x, HeapNode p) {
        HeapNode node = insert(x);
        node.pointer = p;
        consolidating();

    }

}





