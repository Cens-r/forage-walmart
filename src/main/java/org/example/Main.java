package org.example;

public class Main {
    public static void main(String[] args) {
        MaxHeapArity<Integer> heap = new MaxHeapArity<>(1);
        heap.insert(1);
        heap.insert(2);
        heap.insert(3);
        heap.insert(4);
        heap.insert(5);
        heap.insert(6);
        heap.insert(7);
        heap.insert(8);
        heap.insert(9);
        heap.print();

        heap.popMax();
        heap.print();

        heap.popMax();
        heap.print();
    }
}