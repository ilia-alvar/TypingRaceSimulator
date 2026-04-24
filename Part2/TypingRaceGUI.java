import java.io.PrintStream;
import javax.swing.*;
import java.awt.*;

public class TypingRaceGUI {

    private JTextArea outputArea;

    public TypingRaceGUI() {

        JFrame frame = new JFrame("Typing Race Simulator");
        frame.setSize(700,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JButton startButton = new JButton("Start Race");

        startButton.addActionListener(e -> runRace());

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(startButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void runRace() {

        TypingRace race = new TypingRace("How beautiful a flower is.");

        race.setAutocorrectOn(true);
        race.setCaffeineModeOn(true);

        Typist t1 = new Typist("⏰", "Flint Luckwood", 0.85);
        Typist t2 = new Typist("🌷", "Micheal Wazawski", 0.75);
        Typist t3 = new Typist("🍧", "Ilia Grozer", 0.90);

        race.addTypist(t1);
        race.addTypist(t2);
        race.addTypist(t3);

        // capture console output
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        race.startRace();

        System.out.flush();
        System.setOut(old);

        outputArea.setText(baos.toString());
    }

    public static void startRaceGUI() {
        SwingUtilities.invokeLater(() -> new TypingRaceGUI());
    }

    public static void main(String[] args) {
        startRaceGUI();
    }
}