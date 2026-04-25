import java.util.ArrayList;

/**
 * Represents a single competitor in the Part II typing race simulation.
 *
 * This extended Typist class stores both the current race state and long-term
 * performance data for a typist. It supports advanced customisation options,
 * including typing style, keyboard type, visual colour, accessories, sponsor
 * deals, and upgrade choices.
 *
 * The class also tracks race statistics such as WPM, accuracy percentage,
 * burnout count, finishing position, points, earnings, personal bests, badges,
 * and historical performance data.
 *
 * Customisation choices directly affect race behaviour. For example, typing
 * style and keyboard type influence accuracy, speed, and burnout risk, while
 * accessories such as wrist support, energy drink, and noise-cancelling
 * headphones modify burnout duration, accuracy changes, or mistype chance.
 *
 * Encapsulation is used to protect the typist's internal state. Values such as
 * accuracy are clamped to valid ranges, and race statistics are updated through
 * controlled methods rather than direct field access.
 *
 * This class is part of Part II of the TypingRaceSimulator project and is used
 * by both the TypingRace engine and TypingRaceGUI interface.
 *
 * @author Ilia Hajypour Alvar
 * @version May 2026
 */

public class Typist
{
    private String name;
    private String symbol;
    private String colourName;

    private int progress;
    private boolean burntOut;
    private int burnoutTurnsRemaining;

    private double baseAccuracy;
    private double currentAccuracy;

    private String typingStyle;
    private String keyboardType;

    private boolean wristSupport;
    private boolean energyDrink;
    private boolean noiseCancellingHeadphones;

    private String sponsorName;

    private int keystrokesAttemptedThisRace;
    private int correctKeystrokesThisRace;
    private int mistypesThisRace;
    private int burnoutCountThisRace;
    private int finishTurnThisRace;
    private int finishingPositionThisRace;
    private double accuracyBeforeRace;
    private double accuracyAfterRace;
    private double wpmThisRace;
    private int pointsEarnedThisRace;
    private double earningsThisRace;

    private int totalRaces;
    private int totalWins;
    private double personalBestWPM;
    private int cumulativePoints;
    private double cumulativeEarnings;
    private int consecutiveWins;
    private int burnoutFreeRaceStreak;

    private ArrayList<String> badges;
    private ArrayList<String> raceHistory;
    private ArrayList<Double> wpmHistory;
    private ArrayList<Double> accuracyHistory;
    private ArrayList<Integer> positionHistory;
    private ArrayList<Integer> burnoutHistory;

    public Typist(String typistSymbol, String typistName, double typistAccuracy)
    {
        name = typistName;
        symbol = typistSymbol;
        colourName = "Gray";

        progress = 0;
        burntOut = false;
        burnoutTurnsRemaining = 0;

        baseAccuracy = clampAccuracy(typistAccuracy);
        currentAccuracy = baseAccuracy;

        typingStyle = "Touch Typist";
        keyboardType = "Mechanical";

        wristSupport = false;
        energyDrink = false;
        noiseCancellingHeadphones = false;

        sponsorName = "No Sponsor";

        badges = new ArrayList<String>();
        raceHistory = new ArrayList<String>();
        wpmHistory = new ArrayList<Double>();
        accuracyHistory = new ArrayList<Double>();
        positionHistory = new ArrayList<Integer>();
        burnoutHistory = new ArrayList<Integer>();

        totalRaces = 0;
        totalWins = 0;
        personalBestWPM = 0.0;
        cumulativePoints = 0;
        cumulativeEarnings = 0.0;
        consecutiveWins = 0;
        burnoutFreeRaceStreak = 0;

        resetPerRaceStats();
    }

    public Typist(char typistSymbol, String typistName, double typistAccuracy)
    {
        this(String.valueOf(typistSymbol), typistName, typistAccuracy);
    }

    public void prepareForRace(boolean nightShiftOn, double rankPressurePenalty)
    {
        progress = 0;
        burntOut = false;
        burnoutTurnsRemaining = 0;

        resetPerRaceStats();

        accuracyBeforeRace = calculateStartingAccuracy(nightShiftOn, rankPressurePenalty);
        currentAccuracy = accuracyBeforeRace;
        accuracyAfterRace = currentAccuracy;
    }

    public void resetToStart()
    {
        prepareForRace(false, 0.0);
    }

    private void resetPerRaceStats()
    {
        keystrokesAttemptedThisRace = 0;
        correctKeystrokesThisRace = 0;
        mistypesThisRace = 0;
        burnoutCountThisRace = 0;
        finishTurnThisRace = 0;
        finishingPositionThisRace = 0;
        accuracyBeforeRace = baseAccuracy;
        accuracyAfterRace = baseAccuracy;
        wpmThisRace = 0.0;
        pointsEarnedThisRace = 0;
        earningsThisRace = 0.0;
    }

    private double calculateStartingAccuracy(boolean nightShiftOn, double rankPressurePenalty)
    {
        double adjustedAccuracy = baseAccuracy;
        adjustedAccuracy = adjustedAccuracy + getTypingStyleAccuracyModifier();
        adjustedAccuracy = adjustedAccuracy + getKeyboardAccuracyModifier();

        if (nightShiftOn)
        {
            adjustedAccuracy = adjustedAccuracy - 0.05;
        }

        adjustedAccuracy = adjustedAccuracy - rankPressurePenalty;

        return clampAccuracy(adjustedAccuracy);
    }

    public void adjustCurrentAccuracy(double change)
    {
        currentAccuracy = clampAccuracy(currentAccuracy + change);
        accuracyAfterRace = currentAccuracy;
    }

    public void typeCharacter()
    {
        if (!burntOut)
        {
            progress = progress + 1;
            keystrokesAttemptedThisRace = keystrokesAttemptedThisRace + 1;
            correctKeystrokesThisRace = correctKeystrokesThisRace + 1;
        }
    }

    public void typeCharacters(int amount)
    {
        int i = 0;
        while (i < amount)
        {
            typeCharacter();
            i = i + 1;
        }
    }

    public void slideBack(int amount)
    {
        if (amount > 0)
        {
            progress = progress - amount;

            if (progress < 0)
            {
                progress = 0;
            }

            keystrokesAttemptedThisRace = keystrokesAttemptedThisRace + 1;
            mistypesThisRace = mistypesThisRace + 1;
        }
    }

    public void burnOut(int turns)
    {
        if (turns > 0)
        {
            burntOut = true;
            burnoutTurnsRemaining = turns;
            burnoutCountThisRace = burnoutCountThisRace + 1;
        }
        else
        {
            burntOut = false;
            burnoutTurnsRemaining = 0;
        }
    }

    public void recoverFromBurnout()
    {
        if (burntOut)
        {
            burnoutTurnsRemaining = burnoutTurnsRemaining - 1;

            if (burnoutTurnsRemaining <= 0)
            {
                burnoutTurnsRemaining = 0;
                burntOut = false;
            }
        }
    }

    public void finalizeRaceResult(int finishTurn, int position, double turnDurationSeconds)
    {
        finishTurnThisRace = finishTurn;
        finishingPositionThisRace = position;

        double timeMinutes = (finishTurn * turnDurationSeconds) / 60.0;
        double wordsTyped = getProgress() / 5.0;

        if (timeMinutes > 0.0)
        {
            wpmThisRace = wordsTyped / timeMinutes;
        }
        else
        {
            wpmThisRace = 0.0;
        }

        accuracyAfterRace = currentAccuracy;

        totalRaces = totalRaces + 1;

        if (position == 1)
        {
            totalWins = totalWins + 1;
            consecutiveWins = consecutiveWins + 1;
        }
        else
        {
            consecutiveWins = 0;
        }

        if (burnoutCountThisRace == 0)
        {
            burnoutFreeRaceStreak = burnoutFreeRaceStreak + 1;
        }
        else
        {
            burnoutFreeRaceStreak = 0;
        }

        if (wpmThisRace > personalBestWPM)
        {
            personalBestWPM = wpmThisRace;
        }

        wpmHistory.add(Double.valueOf(wpmThisRace));
        accuracyHistory.add(Double.valueOf(getAccuracyPercentageThisRace()));
        positionHistory.add(Integer.valueOf(position));
        burnoutHistory.add(Integer.valueOf(burnoutCountThisRace));

        String summary = "Race " + totalRaces
            + ": position=" + position
            + ", WPM=" + format2dp(wpmThisRace)
            + ", Accuracy%=" + format2dp(getAccuracyPercentageThisRace())
            + ", Burnouts=" + burnoutCountThisRace
            + ", Points=" + pointsEarnedThisRace
            + ", Earnings=" + format2dp(earningsThisRace);

        raceHistory.add(summary);

        updateBadges();
    }

    private void updateBadges()
    {
        addBadgeIfMissing("Racer");

        if (consecutiveWins >= 3)
        {
            addBadgeIfMissing("Speed Demon");
        }

        if (burnoutFreeRaceStreak >= 5)
        {
            addBadgeIfMissing("Iron Fingers");
        }

        if (personalBestWPM >= 80.0)
        {
            addBadgeIfMissing("Keyboard Rocket");
        }

        if (cumulativeEarnings >= 500.0)
        {
            addBadgeIfMissing("Sponsored Star");
        }
    }

    private void addBadgeIfMissing(String badge)
    {
        if (!badges.contains(badge))
        {
            badges.add(badge);
        }
    }

    public boolean buyUpgrade(String upgradeName)
    {
        if (upgradeName.equals("Better Keyboard") && cumulativeEarnings >= 120.0)
        {
            cumulativeEarnings = cumulativeEarnings - 120.0;
            setKeyboardType("Stenography");
            return true;
        }
        else if (upgradeName.equals("Wrist Support") && cumulativeEarnings >= 60.0)
        {
            cumulativeEarnings = cumulativeEarnings - 60.0;
            wristSupport = true;
            return true;
        }
        else if (upgradeName.equals("Noise-Cancelling Headphones") && cumulativeEarnings >= 80.0)
        {
            cumulativeEarnings = cumulativeEarnings - 80.0;
            noiseCancellingHeadphones = true;
            return true;
        }

        return false;
    }

    public double getTypingStyleAccuracyModifier()
    {
        if (typingStyle.equals("Touch Typist"))
        {
            return 0.08;
        }
        else if (typingStyle.equals("Hunt & Peck"))
        {
            return -0.06;
        }
        else if (typingStyle.equals("Phone Thumbs"))
        {
            return -0.03;
        }
        else if (typingStyle.equals("Voice-to-Text"))
        {
            return 0.02;
        }

        return 0.0;
    }

    public double getTypingStyleSpeedBonusChance()
    {
        if (typingStyle.equals("Touch Typist"))
        {
            return 0.35;
        }
        else if (typingStyle.equals("Hunt & Peck"))
        {
            return 0.05;
        }
        else if (typingStyle.equals("Phone Thumbs"))
        {
            return 0.15;
        }
        else if (typingStyle.equals("Voice-to-Text"))
        {
            return 0.45;
        }

        return 0.0;
    }

    public double getTypingStyleBurnoutMultiplier()
    {
        if (typingStyle.equals("Touch Typist"))
        {
            return 1.10;
        }
        else if (typingStyle.equals("Hunt & Peck"))
        {
            return 0.90;
        }
        else if (typingStyle.equals("Phone Thumbs"))
        {
            return 1.00;
        }
        else if (typingStyle.equals("Voice-to-Text"))
        {
            return 0.80;
        }

        return 1.0;
    }

    public double getKeyboardAccuracyModifier()
    {
        if (keyboardType.equals("Mechanical"))
        {
            return 0.04;
        }
        else if (keyboardType.equals("Membrane"))
        {
            return 0.01;
        }
        else if (keyboardType.equals("Touchscreen"))
        {
            return -0.05;
        }
        else if (keyboardType.equals("Stenography"))
        {
            return 0.06;
        }

        return 0.0;
    }

    public double getKeyboardSpeedBonusChance()
    {
        if (keyboardType.equals("Mechanical"))
        {
            return 0.20;
        }
        else if (keyboardType.equals("Membrane"))
        {
            return 0.10;
        }
        else if (keyboardType.equals("Touchscreen"))
        {
            return 0.05;
        }
        else if (keyboardType.equals("Stenography"))
        {
            return 0.30;
        }

        return 0.0;
    }

    public double getKeyboardBurnoutMultiplier()
    {
        if (keyboardType.equals("Mechanical"))
        {
            return 1.05;
        }
        else if (keyboardType.equals("Membrane"))
        {
            return 1.00;
        }
        else if (keyboardType.equals("Touchscreen"))
        {
            return 0.95;
        }
        else if (keyboardType.equals("Stenography"))
        {
            return 1.10;
        }

        return 1.0;
    }

    public double getAccuracyPercentageThisRace()
    {
        if (keystrokesAttemptedThisRace == 0)
        {
            return 0.0;
        }

        return (100.0 * correctKeystrokesThisRace) / keystrokesAttemptedThisRace;
    }

    private double clampAccuracy(double value)
    {
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

    private String format2dp(double value)
    {
        return String.format("%.2f", value);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String newSymbol)
    {
        symbol = newSymbol;
    }

    public void setSymbol(char newSymbol)
    {
        symbol = String.valueOf(newSymbol);
    }

    public String getColourName()
    {
        return colourName;
    }

    public void setColourName(String newColourName)
    {
        colourName = newColourName;
    }

    public int getProgress()
    {
        return progress;
    }

    public boolean isBurntOut()
    {
        return burntOut;
    }

    public int getBurnoutTurnsRemaining()
    {
        return burnoutTurnsRemaining;
    }

    public double getAccuracy()
    {
        return currentAccuracy;
    }

    public void setAccuracy(double newAccuracy)
    {
        currentAccuracy = clampAccuracy(newAccuracy);
        accuracyAfterRace = currentAccuracy;
    }

    public double getBaseAccuracy()
    {
        return baseAccuracy;
    }

    public void setBaseAccuracy(double newAccuracy)
    {
        baseAccuracy = clampAccuracy(newAccuracy);
        currentAccuracy = baseAccuracy;
    }

    public String getTypingStyle()
    {
        return typingStyle;
    }

    public void setTypingStyle(String newTypingStyle)
    {
        typingStyle = newTypingStyle;
    }

    public String getKeyboardType()
    {
        return keyboardType;
    }

    public void setKeyboardType(String newKeyboardType)
    {
        keyboardType = newKeyboardType;
    }

    public boolean hasWristSupport()
    {
        return wristSupport;
    }

    public void setWristSupport(boolean value)
    {
        wristSupport = value;
    }

    public boolean hasEnergyDrink()
    {
        return energyDrink;
    }

    public void setEnergyDrink(boolean value)
    {
        energyDrink = value;
    }

    public boolean hasNoiseCancellingHeadphones()
    {
        return noiseCancellingHeadphones;
    }

    public void setNoiseCancellingHeadphones(boolean value)
    {
        noiseCancellingHeadphones = value;
    }

    public String getSponsorName()
    {
        return sponsorName;
    }

    public void setSponsorName(String newSponsorName)
    {
        sponsorName = newSponsorName;
    }

    public int getKeystrokesAttemptedThisRace()
    {
        return keystrokesAttemptedThisRace;
    }

    public int getCorrectKeystrokesThisRace()
    {
        return correctKeystrokesThisRace;
    }

    public int getMistypesThisRace()
    {
        return mistypesThisRace;
    }

    public int getBurnoutCountThisRace()
    {
        return burnoutCountThisRace;
    }

    public int getFinishTurnThisRace()
    {
        return finishTurnThisRace;
    }

    public int getFinishingPositionThisRace()
    {
        return finishingPositionThisRace;
    }

    public double getAccuracyBeforeRace()
    {
        return accuracyBeforeRace;
    }

    public double getAccuracyAfterRace()
    {
        return accuracyAfterRace;
    }

    public double getAccuracyChangeThisRace()
    {
        return accuracyAfterRace - accuracyBeforeRace;
    }

    public double getWPMThisRace()
    {
        return wpmThisRace;
    }

    public int getPointsEarnedThisRace()
    {
        return pointsEarnedThisRace;
    }

    public void setPointsEarnedThisRace(int points)
    {
        pointsEarnedThisRace = points;
        cumulativePoints = cumulativePoints + points;
    }

    public double getEarningsThisRace()
    {
        return earningsThisRace;
    }

    public void setEarningsThisRace(double amount)
    {
        earningsThisRace = amount;
        cumulativeEarnings = cumulativeEarnings + amount;
    }

    public int getTotalRaces()
    {
        return totalRaces;
    }

    public int getTotalWins()
    {
        return totalWins;
    }

    public double getPersonalBestWPM()
    {
        return personalBestWPM;
    }

    public int getCumulativePoints()
    {
        return cumulativePoints;
    }

    public double getCumulativeEarnings()
    {
        return cumulativeEarnings;
    }

    public int getConsecutiveWins()
    {
        return consecutiveWins;
    }

    public int getBurnoutFreeRaceStreak()
    {
        return burnoutFreeRaceStreak;
    }

    public ArrayList<String> getBadges()
    {
        return badges;
    }

    public ArrayList<String> getRaceHistory()
    {
        return raceHistory;
    }

    public ArrayList<Double> getWPMHistory()
    {
        return wpmHistory;
    }

    public ArrayList<Double> getAccuracyHistory()
    {
        return accuracyHistory;
    }

    public ArrayList<Integer> getPositionHistory()
    {
        return positionHistory;
    }

    public ArrayList<Integer> getBurnoutHistory()
    {
        return burnoutHistory;
    }

    public String toString()
    {
        return "Typist{name=" + name
            + ", symbol=" + symbol
            + ", colour=" + colourName
            + ", style=" + typingStyle
            + ", keyboard=" + keyboardType
            + ", sponsor=" + sponsorName
            + ", baseAccuracy=" + format2dp(baseAccuracy)
            + ", currentAccuracy=" + format2dp(currentAccuracy)
            + "}";
    }
}