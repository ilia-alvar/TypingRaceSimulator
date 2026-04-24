import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;

public class TypingRaceGUI
{
    private JFrame frame;
    private JPanel racePanel;
    private JLabel turnLabel;
    private JTextArea resultsArea;
    private JButton startButton;

    private TypingRace race;
    private ArrayList<JTextPane> passageViews;
    private ArrayList<JProgressBar> progressBars;

    private Timer raceTimer;

    public TypingRaceGUI()
    {
        frame = new JFrame("Typing Race Simulator");
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Typing Race Simulator", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        turnLabel = new JLabel("Turn: 0", SwingConstants.CENTER);

        racePanel = new JPanel();
        racePanel.setLayout(new BoxLayout(racePanel, BoxLayout.Y_AXIS));

        resultsArea = new JTextArea(8, 40);
        resultsArea.setEditable(false);

        startButton = new JButton("Start Race");
        startButton.addActionListener(e -> startRaceGUIRun());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(turnLabel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(racePanel), BorderLayout.CENTER);
        frame.add(new JScrollPane(resultsArea), BorderLayout.SOUTH);
        frame.add(startButton, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void startRaceGUIRun()
    {
        race = new TypingRace("How beautiful a flower is.");

        race.setAutocorrectOn(true);
        race.setCaffeineModeOn(true);
        race.setNightShiftOn(false);

        Typist t1 = new Typist("A", "Flint Luckwood", 0.85);
        Typist t2 = new Typist("B", "Micheal Wazawski", 0.75);
        Typist t3 = new Typist("C", "Ilia Grozer", 0.90);

        t1.setColourName("FlintColor");
        t2.setColourName("MichealColor");
        t3.setColourName("IliaColor");

        race.addTypist(t1);
        race.addTypist(t2);
        race.addTypist(t3);

        race.prepareRaceForGUI();

        passageViews = new ArrayList<JTextPane>();
        progressBars = new ArrayList<JProgressBar>();

        racePanel.removeAll();
        resultsArea.setText("");
        startButton.setEnabled(false);

        int i = 0;
        while (i < race.getTypists().size())
        {
            Typist typist = race.getTypists().get(i);

            JLabel nameLabel = new JLabel(
                typist.getSymbol() + " " + typist.getName()
                + " | Style: " + typist.getTypingStyle()
                + " | Keyboard: " + typist.getKeyboardType()
            );

            JTextPane passagePane = new JTextPane();
            passagePane.setEditable(false);
            passagePane.setFont(new Font("Monospaced", Font.PLAIN, 16));

            JProgressBar progressBar = new JProgressBar(0, race.getPassageLength());
            progressBar.setStringPainted(true);

            passageViews.add(passagePane);
            progressBars.add(progressBar);

            JPanel typistPanel = new JPanel(new BorderLayout());
            typistPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            typistPanel.add(nameLabel, BorderLayout.NORTH);
            typistPanel.add(passagePane, BorderLayout.CENTER);
            typistPanel.add(progressBar, BorderLayout.SOUTH);

            racePanel.add(typistPanel);

            i = i + 1;
        }

        racePanel.revalidate();
        racePanel.repaint();

        updateRaceDisplay();

        raceTimer = new Timer(200, e ->
        {
            race.runOneTurnForGUI();
            updateRaceDisplay();

            if (race.isRaceFinished())
            {
                raceTimer.stop();
                showFinalResults();
                startButton.setEnabled(true);
            }
        });

        raceTimer.start();
    }

    private void updateRaceDisplay()
    {
        turnLabel.setText("Turn: " + race.getTurnCount());

        int i = 0;
        while (i < race.getTypists().size())
        {
            Typist typist = race.getTypists().get(i);

            updatePassagePane(passageViews.get(i), typist);
            progressBars.get(i).setValue(typist.getProgress());

            int percent = (int)((100.0 * typist.getProgress()) / race.getPassageLength());
            if (percent > 100)
            {
                percent = 100;
            }

            progressBars.get(i).setString(percent + "%");

            i = i + 1;
        }
    }

    private void updatePassagePane(JTextPane pane, Typist typist)
    {
        StyledDocument doc = pane.getStyledDocument();

        try
        {
            doc.remove(0, doc.getLength());

            Style completedStyle = pane.addStyle("completed", null);
            Color typistColor = getColorFromName(typist.getColourName());
            StyleConstants.setForeground(completedStyle, typistColor);
            StyleConstants.setBold(completedStyle, true);

            Style cursorStyle = pane.addStyle("cursor", null);
            StyleConstants.setForeground(cursorStyle, Color.RED);
            StyleConstants.setBold(cursorStyle, true);

            Style remainingStyle = pane.addStyle("remaining", null);
            StyleConstants.setForeground(remainingStyle, Color.DARK_GRAY);

            String completed = race.getCompletedTextFor(typist);
            String remaining = race.getRemainingTextFor(typist);

            doc.insertString(doc.getLength(), completed, completedStyle);

            if (remaining.length() > 0)
            {
                doc.insertString(doc.getLength(), "|", cursorStyle);
                doc.insertString(doc.getLength(), remaining, remainingStyle);
            }
            else
            {
                doc.insertString(doc.getLength(), "| FINISHED", cursorStyle);
            }

            if (typist.isBurntOut())
            {
                doc.insertString(doc.getLength(),
                    "   BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)",
                    cursorStyle);
            }
        }
        catch (BadLocationException e)
        {
            System.out.println("Error updating passage display.");
        }
    }

   private Color getColorFromName(String name)
    {
    if (name.equalsIgnoreCase("FlintColor"))
    {
        return new Color(38, 157, 226);
    }
    else if (name.equalsIgnoreCase("MichealColor"))
    {
        return new Color(38, 83, 226);
    }
    else if (name.equalsIgnoreCase("IliaColor"))
    {
        return new Color(49, 196, 213);
    }

    return Color.BLACK;
    }


    private void showFinalResults()
    {
        String results = "FINAL RESULTS\n\n";

        int i = 0;
        while (i < race.getFinishingOrder().size())
        {
            Typist t = race.getFinishingOrder().get(i);

            results = results + (i + 1) + ". " + t.getName()
                + " | WPM: " + String.format("%.2f", t.getWPMThisRace())
                + " | Accuracy: " + String.format("%.2f", t.getAccuracyPercentageThisRace()) + "%"
                + " | Burnouts: " + t.getBurnoutCountThisRace()
                + " | Points: " + t.getPointsEarnedThisRace()
                + " | Earnings: " + String.format("%.2f", t.getEarningsThisRace())
                + "\n";

            i = i + 1;
        }

        resultsArea.setText(results);
    }


   

    public static void startRaceGUI()
    {
        SwingUtilities.invokeLater(() -> new TypingRaceGUI());
    }

    public static void main(String[] args)
    {
        startRaceGUI();
    }
}