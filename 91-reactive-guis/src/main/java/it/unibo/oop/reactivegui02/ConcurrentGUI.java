package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JPanel panel;
    private final JLabel labelCounter = new JLabel("0");
    private final JButton btnUp = new JButton("up");
    private final JButton btnDown = new JButton("down");
    private final JButton btnStop = new JButton("stop");

    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        this.panel = new JPanel();
        this.panel.add(this.labelCounter);
        this.panel.add(this.btnUp);
        this.panel.add(this.btnDown);
        this.panel.add(this.btnStop);

        this.getContentPane().add(this.panel);
        this.setVisible(true);

        final Agent agent= new Agent();
        new Thread(agent).start();


        this.btnUp.addActionListener((e) -> agent.startUp());
        this.btnDown.addActionListener((e) -> agent.startDown());
        this.btnStop.addActionListener((e) -> agent.stopCounting());
    
    }

    private class Agent implements Runnable {
        private volatile boolean isUp;
        private volatile boolean isDown;
        
        private int counter;

        @Override
        public void run() {
            while(true){
                final var nextText = Integer.toString(this.counter);
                try {
                    if(this.isUp) {
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.labelCounter.setText(nextText));
                        increment();
                    }else if(this.isDown){
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.labelCounter.setText(nextText));
                        decrement();
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        
        }

        public void startUp(){
            this.isUp = true;
            this.isDown = false;
        }

        public void startDown(){
            this.isUp = false;
            this.isDown = true;
        }
        
        public void stopCounting(){
            this.isUp = false;
            this.isDown = false;
        }

        private void increment(){
            this.counter++;
        }

        private void decrement(){
            this.counter--;
        }
    }
}
