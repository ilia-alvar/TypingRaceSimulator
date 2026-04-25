/**
 * TypingRaceGUI
 *
 * This class provides the graphical user interface (GUI) for the Typing Race Simulator.
 * It visualises the race in real time using Java Swing components and connects directly
 * to the TypingRace backend engine.
 *
 * Key Features:
 * - Displays live typing progress for each typist
 * - Highlights completed text, remaining text, and current cursor position
 * - Uses typist-specific colours for visual distinction
 * - Shows progress bars representing passage completion
 * - Displays burnout status dynamically during the race
 * - Presents final race results including WPM, accuracy, burnouts, points, and earnings
 *
 * Interactive Configuration:
 * - Allows selection of passage text
 * - Supports enabling/disabling race modifiers (Autocorrect, Caffeine Mode, Night Shift)
 * - Allows configuration of multiple typists (2–6 participants)
 * - Supports custom typist attributes such as typing style, keyboard type, and colour
 *
 * This class is part of Part II of the project and extends the textual simulation
 * by providing a user-friendly and interactive visual representation of the race.
 *
 * Note:
 * The GUI interacts with the TypingRace class using helper methods such as:
 * - prepareRaceForGUI()
 * - runOneTurnForGUI()
 * - getCompletedTextFor()
 * - getRemainingTextFor()
 *
 * Author: Ilia Hajypour Alvar
 * Version: May 2026
 */



import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;

public class TypingRaceGUI
{
    private JFrame frame;
    private JPanel setupPanel;
    private JPanel racePanel;
    private JLabel turnLabel;
    private JTextArea resultsArea;
    private JButton startButton;

    private JComboBox<String> passageBox;
    private JTextField customPassageField;
    private JComboBox<Integer> seatCountBox;

    private JCheckBox autocorrectBox;
    private JCheckBox caffeineBox;
    private JCheckBox nightShiftBox;

    private JTextField[] nameFields;
    private JTextField[] symbolFields;
    private JTextField[] accuracyFields;
    private JComboBox<String>[] colourBoxes;
    private JComboBox<String>[] styleBoxes;
    private JComboBox<String>[] keyboardBoxes;
    private JCheckBox[] wristBoxes;
    private JCheckBox[] energyBoxes;
    private JCheckBox[] headphonesBoxes;
    private JComboBox<String>[] sponsorBoxes;
    private JPanel[] typistPanels;

    private TypingRace race;
    private ArrayList<JTextPane> passageViews;
    private ArrayList<JProgressBar> progressBars;

    private Timer raceTimer;

    public TypingRaceGUI()
    {
        frame = new JFrame("Typing Race Simulator");
        frame.setSize(1100, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("Typing Race Simulator", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        turnLabel = new JLabel("Turn: 0", SwingConstants.CENTER);

        setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));

        racePanel = new JPanel();
        racePanel.setLayout(new BoxLayout(racePanel, BoxLayout.Y_AXIS));

        resultsArea = new JTextArea(8, 40);
        resultsArea.setEditable(false);

        startButton = new JButton("Start Race");
        startButton.addActionListener(e -> startRaceGUIRun());

        buildSetupPanel();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(turnLabel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(setupPanel), BorderLayout.WEST);
        frame.add(new JScrollPane(racePanel), BorderLayout.CENTER);
        frame.add(new JScrollPane(resultsArea), BorderLayout.SOUTH);
        frame.add(startButton, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void buildSetupPanel()
    {
        setupPanel.add(new JLabel("Race Configuration"));

        passageBox = new JComboBox<String>(new String[] {
            "Short", "Medium", "Long", "Custom"
        });

        customPassageField = new JTextField("How beautiful a flower is.", 20);

        seatCountBox = new JComboBox<Integer>(new Integer[] {2, 3, 4, 5, 6});
        seatCountBox.setSelectedItem(3);
        seatCountBox.addActionListener(e -> updateTypistFieldVisibility());

        autocorrectBox = new JCheckBox("Autocorrect");
        caffeineBox = new JCheckBox("Caffeine Mode");
        nightShiftBox = new JCheckBox("Night Shift");

        setupPanel.add(new JLabel("Passage:"));
        setupPanel.add(passageBox);
        setupPanel.add(new JLabel("Custom passage:"));
        setupPanel.add(customPassageField);

        setupPanel.add(new JLabel("Seat count:"));
        setupPanel.add(seatCountBox);

        setupPanel.add(autocorrectBox);
        setupPanel.add(caffeineBox);
        setupPanel.add(nightShiftBox);

        setupPanel.add(new JLabel("Typists"));

        nameFields = new JTextField[6];
        symbolFields = new JTextField[6];
        accuracyFields = new JTextField[6];
        colourBoxes = new JComboBox[6];
        styleBoxes = new JComboBox[6];
        keyboardBoxes = new JComboBox[6];
        wristBoxes = new JCheckBox[6];
        energyBoxes = new JCheckBox[6];
        headphonesBoxes = new JCheckBox[6];
        sponsorBoxes = new JComboBox[6];
        sponsorBoxes = new JComboBox[6];
        typistPanels = new JPanel[6]; 

        String[] defaultNames = {
            "Flint Luckwood", "Micheal Wazawski", "Ilia Grozer",
            "Typist Four", "Typist Five", "Typist Six"
        };

        String[] defaultSymbols = {"A", "B", "C", "D", "E", "F"};
        String[] defaultAccuracies = {"0.85", "0.75", "0.90", "0.70", "0.80", "0.65"};

        String[] colours = {"FlintColor", "MichealColor", "IliaColor", "Red", "Orange", "Purple"};
        String[] styles = {"Touch Typist", "Hunt & Peck", "Phone Thumbs", "Voice-to-Text"};
        String[] keyboards = {"Mechanical", "Membrane", "Touchscreen", "Stenography"};
        String[] sponsors = {"No Sponsor", "KeyCorp", "SwiftKeys", "ZenType", "LastStand Tech"};

        int i = 0;
        while (i < 6)
        {
            JPanel typistPanel = new JPanel(new GridLayout(0, 1));
            typistPanel.setBorder(BorderFactory.createTitledBorder("Typist " + (i + 1)));
            typistPanels[i] = typistPanel;

            nameFields[i] = new JTextField(defaultNames[i]);
            symbolFields[i] = new JTextField(defaultSymbols[i]);
            accuracyFields[i] = new JTextField(defaultAccuracies[i]);

            colourBoxes[i] = new JComboBox<String>(colours);
            colourBoxes[i].setSelectedIndex(i % colours.length);

            styleBoxes[i] = new JComboBox<String>(styles);
            keyboardBoxes[i] = new JComboBox<String>(keyboards);
            sponsorBoxes[i] = new JComboBox<String>(sponsors);

            wristBoxes[i] = new JCheckBox("Wrist Support");
            energyBoxes[i] = new JCheckBox("Energy Drink");
            headphonesBoxes[i] = new JCheckBox("Noise-Cancelling Headphones");

            typistPanel.add(new JLabel("Name:"));
            typistPanel.add(nameFields[i]);

            typistPanel.add(new JLabel("Symbol:"));
            typistPanel.add(symbolFields[i]);

            typistPanel.add(new JLabel("Base accuracy 0.0–1.0:"));
            typistPanel.add(accuracyFields[i]);

            typistPanel.add(new JLabel("Colour:"));
            typistPanel.add(colourBoxes[i]);

            typistPanel.add(new JLabel("Typing style:"));
            typistPanel.add(styleBoxes[i]);

            typistPanel.add(new JLabel("Keyboard:"));
            typistPanel.add(keyboardBoxes[i]);

            typistPanel.add(wristBoxes[i]);
            typistPanel.add(energyBoxes[i]);
            typistPanel.add(headphonesBoxes[i]);

            typistPanel.add(new JLabel("Sponsor:"));
            typistPanel.add(sponsorBoxes[i]);

            setupPanel.add(typistPanel);

            i = i + 1;
        }

        updateTypistFieldVisibility();
    }

    private void updateTypistFieldVisibility()
    {
        int seatCount = (Integer) seatCountBox.getSelectedItem();

        int i = 0;
        while (i < 6)
        {
            typistPanels[i].setVisible(i < seatCount);
            i = i + 1;
        }

        setupPanel.revalidate();
        setupPanel.repaint();
    }

    private String getSelectedPassage()
    {
        String selected = (String) passageBox.getSelectedItem();

        if (selected.equals("Short"))
        {
            return "How beautiful a flower is.";
        }
        else if (selected.equals("Medium"))
        {
            return "Typing races reward speed, accuracy, and calm concentration.";
        }
        else if (selected.equals("Long"))
        {
            return "A strong typist balances speed with accuracy while avoiding burnout and recovering quickly from mistakes.";
        }

        return customPassageField.getText();
    }

    private void startRaceGUIRun()
    {
        String passage = getSelectedPassage();
        race = new TypingRace(passage);

        race.setAutocorrectOn(autocorrectBox.isSelected());
        race.setCaffeineModeOn(caffeineBox.isSelected());
        race.setNightShiftOn(nightShiftBox.isSelected());

        int seatCount = (Integer) seatCountBox.getSelectedItem();

        int i = 0;
        while (i < seatCount)
        {
            String symbol = symbolFields[i].getText();
            if (symbol.length() == 0)
            {
                symbol = "?";
            }

            String name = nameFields[i].getText();
            double accuracy = parseAccuracy(accuracyFields[i].getText());

            Typist typist = new Typist(symbol, name, accuracy);

            typist.setColourName((String) colourBoxes[i].getSelectedItem());
            typist.setTypingStyle((String) styleBoxes[i].getSelectedItem());
            typist.setKeyboardType((String) keyboardBoxes[i].getSelectedItem());

            typist.setWristSupport(wristBoxes[i].isSelected());
            typist.setEnergyDrink(energyBoxes[i].isSelected());
            typist.setNoiseCancellingHeadphones(headphonesBoxes[i].isSelected());

            typist.setSponsorName((String) sponsorBoxes[i].getSelectedItem());

            race.addTypist(typist);

            i = i + 1;
        }

        race.prepareRaceForGUI();

        passageViews = new ArrayList<JTextPane>();
        progressBars = new ArrayList<JProgressBar>();

        racePanel.removeAll();
        resultsArea.setText("");
        startButton.setEnabled(false);

        i = 0;
        while (i < race.getTypists().size())
        {
            Typist typist = race.getTypists().get(i);

            JLabel nameLabel = new JLabel(
                typist.getSymbol() + " " + typist.getName()
                + " | Style: " + typist.getTypingStyle()
                + " | Keyboard: " + typist.getKeyboardType()
                + " | Sponsor: " + typist.getSponsorName()
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

    private double parseAccuracy(String text)
    {
        try
        {
            double value = Double.parseDouble(text);

            if (value < 0.0)
            {
                return 0.0;
            }
            else if (value > 1.0)
            {
                return 1.0;
            }

            return value;
        }
        catch (NumberFormatException e)
        {
            return 0.75;
        }
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
        else if (name.equalsIgnoreCase("Red"))
        {
            return Color.RED;
        }
        else if (name.equalsIgnoreCase("Orange"))
        {
            return Color.ORANGE;
        }
        else if (name.equalsIgnoreCase("Purple"))
        {
            return new Color(128, 0, 128);
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