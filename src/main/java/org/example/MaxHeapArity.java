package org.example;
import java.util.ArrayList;
import java.util.Comparator;

/*
All sets of children should be sorted from smallest to largest, going left to right.
For example, for max heap with arity 4:
[9, 4, 6, 7, 8, 1, 2, 3]

When adding a new element 5:
[9, 4, 6, 7, 8, 1, 2, 3, 5] --(sift up)--> [9, 5, 6, 7, 8, 1, 2, 3, 4]
 */

public class MaxHeapArity<T extends Comparable<? super T>> {
    private final ArrayList<T> heap;
    private Comparator<T> comparator;
    private final int arity;

    public MaxHeapArity(int arityFactor) throws IllegalArgumentException {
        if (arityFactor <= 0) {
            throw new IllegalArgumentException("Given `arityFactor` must be greater than 0!");
        }
        this.heap = new ArrayList<>();
        this.comparator = Comparator.naturalOrder();
        this.arity = (int) Math.pow(2, arityFactor);
    }

    public MaxHeapArity(int arityFactor, Comparator<T> comparator) throws IllegalArgumentException {
        this(arityFactor);
        this.comparator = comparator;
    }

    private boolean indexValid(int index) {
        return (index >= 0) && (index < heap.size());
    }

    private int parent(int index) {
        return (index - 1) / arity;
    }

    private int child(int parent, int index) {
        return (arity * parent) + index;
    }

    private void swap(int i1, int i2) {
        T temp = heap.get(i1);
        heap.set(i1, heap.get(i2));
        heap.set(i2, temp);
    }

    private void siftUp(int childIndex) {
        int parentIndex = parent(childIndex);
        while (
                // Parent is valid and parent is greater than child
                indexValid(parentIndex) && (comparator.compare(
                        heap.get(parentIndex),
                        heap.get(childIndex)
                ) < 0)
        ) {
            swap(childIndex, parentIndex);
            childIndex = parentIndex;
            parentIndex = parent(childIndex);
        }
    }

    private void siftDown(int parentIndex) {
        int largest = parentIndex;
        while (true) {
            for (int index = 0; index < arity; index++) {
                int childIndex = child(parentIndex, index);
                if (indexValid(childIndex) && comparator.compare(
                        heap.get(childIndex),
                        heap.get(largest)
                ) > 0) {
                    largest = childIndex;
                }
            }

            if (largest != parentIndex) {
                swap(largest, parentIndex);
                parentIndex = largest;
            } else { break; }
        }
    }

    public void insert(T value) throws NullPointerException {
        if (value == null) {
            throw new NullPointerException("Given `value` must be non-null!");
        }
        heap.add(value);
        siftUp(heap.size() - 1);
    }

    public T popMax() {
        T maxValue = heap.get(0);

        T lastValue = heap.remove(heap.size() - 1);
        heap.set(0, lastValue);
        siftDown(0);

        return maxValue;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void print() {
        System.out.println(heap);
    }
}
