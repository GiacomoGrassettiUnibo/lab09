package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class MultiThreadedSumMatrix implements SumMatrix{

    private int nthread;

    public MultiThreadedSumMatrix(final int nthread){
        this.nthread = nthread;
    }

    

    private static class Worker extends Thread {
        double[][] matrix;
        
        private final int startpos;
        private final int nelem;
        private double res;
        
        public Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix  = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (final double col : this.matrix[i]) {
                    this.res += col;
                }
            }
           System.out.println("RES: " + res);
        }

        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        double sum=0;
        final int size = matrix.length % nthread + matrix.length / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        for (final Worker w: workers) {
            w.start();
        }
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
    
}
