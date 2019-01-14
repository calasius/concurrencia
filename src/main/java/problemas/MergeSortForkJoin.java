package problemas;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
/*  w  w  w .j  av  a2 s.c  o  m*/
public class MergeSortForkJoin {
    private final ForkJoinPool pool;

    private static class MergeSortTask extends RecursiveAction {
        private final int[] array;
        private final int low;
        private final int high;
        private static final int THRESHOLD = 1000;

        protected MergeSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        @Override
        protected void compute() {
            if (high - low <= THRESHOLD) {
                Arrays.sort(array, low, high);
            } else {
                int middle = low + ((high - low) >> 1);
                // Execute the sub tasks and wait for them to finish
                invokeAll(new MergeSortTask(array, low, middle), new MergeSortTask(array, middle, high));
                // Then merge the results
                merge(middle);
            }
        }

        private void merge(int middle) {
            if (array[middle - 1] < array[middle]) {
                return; // the arrays are already correctly sorted, so we can skip the merge
            }
            int[] copy = new int[high - low];
            System.arraycopy(array, low, copy, 0, copy.length);
            int copyLow = 0;
            int copyHigh = high - low;
            int copyMiddle = middle - low;

            for (int i = low, p = copyLow, q = copyMiddle; i < high; i++) {
                if (q >= copyHigh || (p < copyMiddle && copy[p] < copy[q]) ) {
                    array[i] = copy[p++];
                } else {
                    array[i] = copy[q++];
                }
            }
        }
    }

    public MergeSortForkJoin(int parallelism) {
        pool = new ForkJoinPool(parallelism);
    }

    public void sort(int[] array) {
        ForkJoinTask<Void> job = pool.submit(new MergeSortTask(array, 0, array.length));
        job.join();
    }

    public static void main(String ... args) {
        for (int i = 0; i < 20; i ++) {
            int[] array = new Random().ints(10000000, 10, 1000).toArray();
            MergeSortForkJoin mergeSort = new MergeSortForkJoin(8);
            long start = System.currentTimeMillis();
            mergeSort.sort(array);
            long end = System.currentTimeMillis();

            long diffForkJoin = end - start;

            start = System.currentTimeMillis();
            Arrays.sort(array);
            end = System.currentTimeMillis();

            long diffSortJava = end - start;

            System.out.println(String.format("forkjoin = %s, java = %s", diffForkJoin, diffSortJava));
        }
    }
}