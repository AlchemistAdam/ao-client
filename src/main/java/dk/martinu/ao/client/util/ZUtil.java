package dk.martinu.ao.client.util;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ZUtil {

    /**
     * Returns <code>true</code> if the specified object is contained in the
     * specified array.
     * <p>
     * More formally, for each index <code>i</code> in <code>array</code>, if
     * <code>object</code> is not equal to <code>null</code> and
     * <code>object.equals(array[i])</code> returns <code>true</code> or
     * <code>object</code> is equal to <code>null</code> and
     * <code>array[i] == null</code> yields <code>true</code>, then
     * <code>true</code> is returned. Otherwise <code>false</code> is returned.
     *
     * @param <T>    The runtime type of the array.
     * @param object The object whose presence is to be determined.
     * @param array  The array to search in.
     * @return <code>true</code> if <code>object</code> is contained in
     * <code>array</code>, otherwise <code>false</code>.
     */
    public static <T> boolean contains(final Object object, final T[] array) {
        Objects.requireNonNull(array, "array is null");
        if (object == null)
            for (final T t : array) {
                if (t == null)
                    return true;
            }
        else
            for (final T t : array)
                if (object.equals(t))
                    return true;
        return false;
    }

    public static <T> T getFirstMatch(final Collection<T> collection, final Predicate<T> predicate) {
        Objects.requireNonNull(collection, "collection is null");
        Objects.requireNonNull(predicate, "predicate is null");
        for (final T t : collection)
            if (predicate.test(t))
                return t;
        return null;
    }

    public static <T> T getFirstMatch(final T[] array, final Predicate<T> predicate) {
        Objects.requireNonNull(array, "array is null");
        Objects.requireNonNull(predicate, "predicate is null");
        for (final T t : array)
            if (predicate.test(t))
                return t;
        return null;
    }

    public static <T> T getOrDefault(final T value, final T def) {
        return value != null ? value : def;
    }

    public static <T> int indexOf(final Object object, final T[] array) {
        Objects.requireNonNull(array, "array is null");
        if (object == null)
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null)
                    return i;
            }
        else
            for (int i = 0; i < array.length; i++)
                if (object.equals(array[i]))
                    return i;
        return -1;
    }

    public static int max(int... numbers) {
        Objects.requireNonNull(numbers, "numbers is null");
        if (numbers.length == 0)
            throw new IllegalArgumentException("numbers is empty");
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++)
            if (numbers[i] > max)
                max = numbers[i];
        return max;
    }

    public static long max(long... numbers) {
        Objects.requireNonNull(numbers, "numbers is null");
        if (numbers.length == 0)
            throw new IllegalArgumentException("numbers is empty");
        long max = numbers[0];
        for (int i = 1; i < numbers.length; i++)
            if (numbers[i] > max)
                max = numbers[i];
        return max;
    }

    public static int min(int... numbers) {
        Objects.requireNonNull(numbers, "numbers is null");
        if (numbers.length == 0)
            throw new IllegalArgumentException("numbers is empty");
        int min = numbers[0];
        for (int i = 1; i < numbers.length; i++)
            if (numbers[i] < min)
                min = numbers[i];
        return min;
    }

    public static long min(long... numbers) {
        Objects.requireNonNull(numbers, "numbers is null");
        if (numbers.length == 0)
            throw new IllegalArgumentException("numbers is empty");
        long min = numbers[0];
        for (int i = 1; i < numbers.length; i++)
            if (numbers[i] < min)
                min = numbers[i];
        return min;
    }

    public static long requireGreaterThan(long n, long than, String msg) {
        if (n <= than)
            if (msg != null)
                throw new IllegalArgumentException(String.format(msg, n, than));
            else
                throw new IllegalArgumentException(String.valueOf(n));
        return n;
    }

    public static int requireGreaterThan(int n, int than, String msg) {
        if (n <= than)
            if (msg != null)
                throw new IllegalArgumentException(String.format(msg, n, than));
            else
                throw new IllegalArgumentException(String.valueOf(n));
        return n;
    }

    public static <T> T[] reverse(final T[] array) {
        Objects.requireNonNull(array, "array is null");
        for (int n = 0, m = array.length - 1, mid = array.length / 2; n < mid; n++, m--) {
            final T temp = array[n];
            array[n] = array[m];
            array[m] = temp;
        }
        return array;
    }
}
