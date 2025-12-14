/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of NumberList for variant 23.
 * <p>
 * Characteristics:
 * <ul>
 * <li>List Type: Circular Doubly Linked List</li>
 * <li>Base System: Decimal</li>
 * <li>Additional System: Hexadecimal</li>
 * <li>Additional Operation: Multiplication</li>
 * </ul>
 *
 * @author Trunov Pavlo
 * @author Group IK-31
 * @version 1.0
 */
public class NumberListImpl implements NumberList {

    private static class Node {
        Byte value;
        Node next;
        Node prev;

        Node(Byte value) {
            this.value = value;
        }
    }

    private Node head;
    private int size;
    private int base = 10;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.size = 0;
        this.base = 10;
    }
    /**
     * Constructs new <tt>NumberListImpl</tt>. Returns empty <tt>NumberListImpl</tt>
     * with specified base
     */
    private NumberListImpl(int base) {
        this.head = null;
        this.size = 0;
        this.base = base;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNext()) {
                parseAndAdd(scanner.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        parseAndAdd(value);
    }

    private void parseAndAdd(String value) {
        if (value == null || value.isEmpty()) return;
        value = value.trim();

        // СУВОРА ПЕРЕВІРКА:
        // Якщо рядок містить хоч щось, крім цифр 0-9 -> список лишається порожнім.
        if (!value.matches("\\d+")) {
            return;
        }

        for (char c : value.toCharArray()) {
            this.add((byte) Character.getNumericValue(c));
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(this.toDecimalString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 23;
    }

    private BigInteger toBigInteger() {
        if (isEmpty()) return BigInteger.ZERO;
        
        BigInteger result = BigInteger.ZERO;
        BigInteger baseBI = BigInteger.valueOf(this.base);
        
        Node current = head;
        do {
            BigInteger digit = BigInteger.valueOf(current.value);
            result = result.multiply(baseBI).add(digit);
            current = current.next;
        } while (current != head);
        
        return result;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in hexadecimal notation<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in Hexadecimal notation.
     */
    public NumberListImpl changeScale() {
        BigInteger val = this.toBigInteger();
        String hexString = val.toString(16).toUpperCase();
        
        // ЗМІНА: Використовуємо конструктор з базою 16
        NumberListImpl newList = new NumberListImpl(16);
        
        for (char c : hexString.toCharArray()) {
            if (Character.isDigit(c)) {
                newList.add((byte) (c - '0'));
            } else {
                newList.add((byte) (c - 'A' + 10));
            }
        }
        return newList;
    }
    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * multiplication current number by the given argument.>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        BigInteger num1 = this.toBigInteger();

        StringBuilder sb2 = new StringBuilder();
        for (Object b : arg) {
            sb2.append(b);
        }
        BigInteger num2 = (!sb2.toString().isEmpty()) ? new BigInteger(sb2.toString()) : BigInteger.ZERO;

        BigInteger result = num1.multiply(num2);

        return new NumberListImpl(result.toString());
    }

    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        return toBigInteger().toString(10);
    }


    @Override
    public String toString() {
        if (isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        Node current = head;
        do {
            byte v = current.value;
            if (v >= 0 && v <= 9) {
                sb.append(v);
            } else {
                sb.append((char) ('A' + (v - 10)));
            }
            current = current.next;
        } while (current != head);
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int count = 0;

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                count++;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        if (isEmpty()) return arr;

        Node current = head;
        int i = 0;
        do {
            arr[i++] = current.value;
            current = current.next;
        } while (current != head);

        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Byte e) {
        Node newNode = new Node(e);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node tail = head.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (isEmpty()) return false;

        Node current = head;
        do {
            if (Objects.equals(o, current.value)) {
                unlink(current);
                return true;
            }
            current = current.next;
        } while (current != head);

        return false;
    }

    private void unlink(Node node) {
        if (size == 1) {
            head = null;
        } else {
            Node prevNode = node.prev;
            Node nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            if (node == head) {
                head = nextNode;
            }
        }
        size--;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        boolean modified = false;
        for (Byte e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            while (contains(o)) {
                remove(o);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (isEmpty()) return false;
        boolean modified = false;
        Node current = head;
        int count = 0;
        int originalSize = size;
        do {
            Node next = current.next;
            if (!c.contains(current.value)) {
                unlink(current);
                modified = true;
            }
            current = next;
            count++;
        } while (current != head && !isEmpty() && count < originalSize);
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return getNode(index).value;
    }

    private Node getNode(int index) {
        Node current = head;
        if (index <= size / 2) {
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    @Override
    public Byte set(int index, Byte element) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node node = getNode(index);
        Byte oldVal = node.value;
        node.value = element;
        return oldVal;
    }

    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();

        if (index == size) {
            add(element);
        } else {
            Node newNode = new Node(element);
            Node current = getNode(index);
            Node prev = current.prev;

            prev.next = newNode;
            newNode.prev = prev;
            newNode.next = current;
            current.prev = newNode;

            if (index == 0) {
                head = newNode;
            }
            size++;
        }
    }

    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node toRemove = getNode(index);
        Byte val = toRemove.value;
        unlink(toRemove);
        return val;
    }

    @Override
    public int indexOf(Object o) {
        if (isEmpty()) return -1;
        int index = 0;
        Node current = head;
        do {
            if (Objects.equals(o, current.value)) return index;
            current = current.next;
            index++;
        } while (current != head);
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (isEmpty()) return -1;
        int index = size - 1;
        Node current = head.prev;
        do {
            if (Objects.equals(o, current.value)) return index;
            current = current.prev;
            index--;
        } while (current != head.prev);
        return -1;
    }

    @Override
    public ListIterator<Byte> listIterator() {
        return Collections.emptyListIterator();
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        return Collections.emptyListIterator();
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        NumberListImpl sub = new NumberListImpl();
        for (int i = fromIndex; i < toIndex; i++) {
            sub.add(get(i));
        }
        return sub;
    }

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) return false;
        if (index1 == index2) return true;

        Node n1 = getNode(index1);
        Node n2 = getNode(index2);

        Byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;

        return true;
    }

    @Override
    public void sortAscending() {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            for (int i = 0; i < size - 1; i++) {
                if (current.value > current.next.value) {
                    Byte tmp = current.value;
                    current.value = current.next.value;
                    current.next.value = tmp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    @Override
    public void sortDescending() {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            for (int i = 0; i < size - 1; i++) {
                if (current.value < current.next.value) {
                    Byte tmp = current.value;
                    current.value = current.next.value;
                    current.next.value = tmp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    @Override
    public void shiftLeft() {
        if (size <= 1) return;
        head = head.next;
    }

    @Override
    public void shiftRight() {
        if (size <= 1) return;
        head = head.prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberListImpl)) return false;
        NumberListImpl other = (NumberListImpl) o;
        if (this.size != other.size) return false;

        Node n1 = this.head;
        Node n2 = other.head;

        for (int i = 0; i < size; i++) {
            if (!Objects.equals(n1.value, n2.value)) return false;
            n1 = n1.next;
            n2 = n2.next;
        }
        return true;
    }
}