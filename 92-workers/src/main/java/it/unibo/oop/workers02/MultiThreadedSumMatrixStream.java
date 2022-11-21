package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

public class MultiThreadedSumMatrixStream implements SumMatrix{
    private int nthread;

    public MultiThreadedSumMatrixStream(final int nthread){
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
        final int size = matrix.length % nthread + matrix.length / nthread;
        return DoubleStream
                        .iterate(0, start -> start + size)
                        .limit(nthread)
                        .mapToObj(start -> new Worker(matrix, (int)start, size))
                        .peek(Thread::start)
                        .peek(MultiThreadedSumMatrixStream::joinUninterruptibly)
                        .mapToDouble(Worker::getResult)
                        .sum();
    }
    
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
