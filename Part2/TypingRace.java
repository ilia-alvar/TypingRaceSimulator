import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * Typing race engine for Part II.
 * Supports 2-6 typists, passage text, modifiers, statistics,
 * history, leaderboard points, sponsors, prize money, and comparison logic.
 *
 * @author Ilia
 * @version May 2026
 */
public class TypingRace
{
    private String passageText;
    private ArrayList<Typist> typists;
    private ArrayList<Typist> finishingOrder;

    // Global modifiers
    private boolean autocorrectOn;
    private boolean caffeineModeOn;
    private boolean nightShiftOn;

    // Race state
    private int turnCount;
    private boolean raceFinished;

    // Core tuning
    private static final double MISTYPE_BASE_CHANCE = 0.30;
    private static final int NORMAL_SLIDE_BACK_AMOUNT = 2;
    private static final int NORMAL_BURNOUT_DURATION = 3;
    private static final double TURN_DURATION_SECONDS = 0.20;

    private static final int MIN_TYPISTS = 2;
    private static final int MAX_TYPISTS = 6;

    /**
     * Constructor with passage text.
     *
     * @param thePassageText actual passage text for the race
     */
    public TypingRace(String thePassageText)
    {
        if (thePassageText == null || thePassageText.trim().length() == 0)
        {
            passageText = "Default typing passage.";
        }
        else
        {
            passageText = thePassageText;
        }

        typists = new ArrayList<Typist>();
        finishingOrder = new ArrayList<Typist>();

        autocorrectOn = false;
        caffeineModeOn = false;
        nightShiftOn = false;

        turnCount = 0;
        raceFinished = false;
    }

    /**
     * Adds a typist if there is space.
     *
     * @param theTypist typist to add
     */
    public void addTypist(Typist theTypist)
    {
        if (theTypist == null)
        {
            return;
        }

        if (typists.size() < MAX_TYPISTS)
        {
            typists.add(theTypist);
        }
        else
        {
            System.out.println("Cannot add more typists. Maximum is " + MAX_TYPISTS + ".");
        }
    }

    /**
     * Removes all typists.
     */
    public void clearTypists()
    {
        typists.clear();
    }

    /**
     * Starts the race simulation.
     * Continues until all typists finish so that full rankings can be recorded.
     */
    public void startRace()
    {
        if (typists.size() < MIN_TYPISTS)
        {
            System.out.println("Cannot start race: at least " + MIN_TYPISTS + " typists are required.");
            return;
        }

        prepareAllTypistsForRace();

        finishingOrder.clear();
        turnCount = 0;
        raceFinished = false;

        while (!raceFinished)
        {
            turnCount = turnCount + 1;

            int i = 0;
            while (i < typists.size())
            {
                Typist currentTypist = typists.get(i);

                if (!hasFinished(currentTypist))
                {
                    advanceTypist(currentTypist);

                    if (hasFinished(currentTypist) && !finishingOrder.contains(currentTypist))
                    {
                        finishingOrder.add(currentTypist);
                    }
                }

                i = i + 1;
            }

            printRace();

            if (finishingOrder.size() == typists.size())
            {
                raceFinished = true;
            }
            else
            {
                try
                {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                catch (Exception e)
                {
                    // Do nothing
                }
            }
        }

        finalizeRaceResults();
        printPostRaceSummary();
    }

    /**
     * Prepares all typists for the race, including optional rank pressure.
     */
    private void prepareAllTypistsForRace()
    {
        Typist currentLeader = getCurrentLeaderboardLeader();

        int i = 0;
        while (i < typists.size())
        {
            Typist typist = typists.get(i);

            double rankPressurePenalty = 0.0;
            if (currentLeader != null && currentLeader == typist)
            {
                rankPressurePenalty = 0.02; // optional rank pressure
            }

            typist.prepareForRace(nightShiftOn, rankPressurePenalty);
            i = i + 1;
        }
    }

    /**
     * Advances one typist by one turn.
     */
    private void advanceTypist(Typist theTypist)
    {
        if (theTypist == null)
        {
            return;
        }

        if (theTypist.isBurntOut())
        {
            theTypist.recoverFromBurnout();
            return;
        }

        applyTurnAccuracyEffects(theTypist);

        int typingAttempts = calculateTypingAttempts(theTypist);
        int typedSuccessfully = 0;

        int j = 0;
        while (j < typingAttempts)
        {
            if (Math.random() < theTypist.getAccuracy())
            {
                theTypist.typeCharacter();
                typedSuccessfully = typedSuccessfully + 1;
            }
            else
            {
                // unsuccessful attempt but not a mistype event
            }

            j = j + 1;
        }

        double mistypeChance = calculateMistypeChance(theTypist);
        if (Math.random() < mistypeChance)
        {
            theTypist.slideBack(getSlideBackAmount());
        }

        double burnoutChance = calculateBurnoutChance(theTypist, typedSuccessfully);
        if (Math.random() < burnoutChance)
        {
            theTypist.burnOut(getBurnoutDuration(theTypist));
            theTypist.adjustCurrentAccuracy(-0.01);
        }
    }

    /**
     * Applies accuracy effects that change during the race.
     */
    private void applyTurnAccuracyEffects(Typist theTypist)
    {
        // Energy drink: better first half, worse second half
        if (theTypist.hasEnergyDrink())
        {
            if (theTypist.getProgress() < getPassageLength() / 2)
            {
                theTypist.adjustCurrentAccuracy(0.01);
            }
            else
            {
                theTypist.adjustCurrentAccuracy(-0.01);
            }
        }

        // Caffeine crash after first 10 turns
        if (caffeineModeOn && turnCount > 10)
        {
            theTypist.adjustCurrentAccuracy(-0.003);
        }
    }

    /**
     * Calculates how many typing attempts the typist gets this turn.
     */
    private int calculateTypingAttempts(Typist theTypist)
    {
        int attempts = 1;

        double speedChance = 0.0;
        speedChance = speedChance + theTypist.getTypingStyleSpeedBonusChance();
        speedChance = speedChance + theTypist.getKeyboardSpeedBonusChance();

        if (caffeineModeOn && turnCount <= 10)
        {
            attempts = attempts + 1;
        }

        if (Math.random() < speedChance)
        {
            attempts = attempts + 1;
        }

        return attempts;
    }

    /**
     * Calculates mistype chance with modifiers.
     */
    private double calculateMistypeChance(Typist theTypist)
    {
        double chance = (1.0 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE;

        if (theTypist.hasNoiseCancellingHeadphones())
        {
            chance = chance * 0.70;
        }

        if (theTypist.getKeyboardType().equals("Touchscreen"))
        {
            chance = chance * 1.15;
        }

        return chance;
    }

    /**
     * Calculates burnout chance with modifiers.
     */
    private double calculateBurnoutChance(Typist theTypist, int typedSuccessfully)
    {
        double burnoutChance = 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy();

        burnoutChance = burnoutChance * theTypist.getTypingStyleBurnoutMultiplier();
        burnoutChance = burnoutChance * theTypist.getKeyboardBurnoutMultiplier();

        if (typedSuccessfully > 1)
        {
            burnoutChance = burnoutChance * 1.10;
        }

        if (caffeineModeOn && turnCount > 10)
        {
            burnoutChance = burnoutChance * 1.50;
        }

        return burnoutChance;
    }

    /**
     * Slide-back amount depends on autocorrect.
     */
    private int getSlideBackAmount()
    {
        if (autocorrectOn)
        {
            return 1;
        }

        return NORMAL_SLIDE_BACK_AMOUNT;
    }

    /**
     * Burnout duration depends on wrist support.
     */
    private int getBurnoutDuration(Typist theTypist)
    {
        if (theTypist.hasWristSupport())
        {
            return 2;
        }

        return NORMAL_BURNOUT_DURATION;
    }

    /**
     * Checks whether a typist finished the passage.
     */
    private boolean hasFinished(Typist theTypist)
    {
        return theTypist.getProgress() >= getPassageLength();
    }

    /**
     * Finalizes WPM, points, earnings, history, badges.
     */
    private void finalizeRaceResults()
    {
        int i = 0;
        while (i < finishingOrder.size())
        {
            Typist typist = finishingOrder.get(i);
            int position = i + 1;

            typist.finalizeRaceResult(turnCount, position, TURN_DURATION_SECONDS);

            int points = calculatePointsFor(typist, position);
            typist.setPointsEarnedThisRace(points);

            double earnings = calculateEarningsFor(typist, position);
            typist.setEarningsThisRace(earnings);

            i = i + 1;
        }
    }

    /**
     * Leaderboard points algorithm.
     */
    private int calculatePointsFor(Typist typist, int position)
    {
        int points = 0;

        if (position == 1)
        {
            points = 5;
        }
        else if (position == 2)
        {
            points = 3;
        }
        else if (position == 3)
        {
            points = 2;
        }
        else
        {
            points = 1;
        }

        if (typist.getWPMThisRace() >= 70.0)
        {
            points = points + 2;
        }
        else if (typist.getWPMThisRace() >= 50.0)
        {
            points = points + 1;
        }

        if (typist.getBurnoutCountThisRace() == 0)
        {
            points = points + 1;
        }
        else
        {
            points = points - typist.getBurnoutCountThisRace();
        }

        if (points < 0)
        {
            points = 0;
        }

        return points;
    }

    /**
     * Prize / sponsor earnings algorithm.
     */
    private double calculateEarningsFor(Typist typist, int position)
    {
        double earnings = 0.0;

        if (position == 1)
        {
            earnings = 120.0;
        }
        else if (position == 2)
        {
            earnings = 80.0;
        }
        else if (position == 3)
        {
            earnings = 50.0;
        }
        else
        {
            earnings = 25.0;
        }

        if (typist.getWPMThisRace() >= 70.0)
        {
            earnings = earnings + 40.0;
        }
        else if (typist.getWPMThisRace() >= 50.0)
        {
            earnings = earnings + 20.0;
        }

        earnings = earnings - (typist.getBurnoutCountThisRace() * 10.0);

        earnings = earnings + getSponsorBonus(typist);

        if (earnings < 0.0)
        {
            earnings = 0.0;
        }

        return earnings;
    }

    /**
     * Sponsor bonus rules.
     */
    private double getSponsorBonus(Typist typist)
    {
        String sponsor = typist.getSponsorName();

        if (sponsor.equals("KeyCorp"))
        {
            if (typist.getBurnoutCountThisRace() == 0)
            {
                return 50.0;
            }
        }
        else if (sponsor.equals("SwiftKeys"))
        {
            if (typist.getWPMThisRace() >= 60.0)
            {
                return 35.0;
            }
        }
        else if (sponsor.equals("ZenType"))
        {
            if (typist.getAccuracyPercentageThisRace() >= 90.0)
            {
                return 30.0;
            }
        }
        else if (sponsor.equals("LastStand Tech"))
        {
            if (typist.getFinishingPositionThisRace() == typists.size())
            {
                return 20.0;
            }
        }

        return 0.0;
    }

    /**
     * Returns the text a typist has completed.
     * Useful for GUI highlighting.
     */
    public String getCompletedTextFor(Typist typist)
    {
        int progress = typist.getProgress();

        if (progress < 0)
        {
            progress = 0;
        }

        if (progress > getPassageLength())
        {
            progress = getPassageLength();
        }

        return passageText.substring(0, progress);
    }

    /**
     * Returns the remaining text for a typist.
     * Useful for GUI highlighting.
     */
    public String getRemainingTextFor(Typist typist)
    {
        int progress = typist.getProgress();

        if (progress < 0)
        {
            progress = 0;
        }

        if (progress > getPassageLength())
        {
            progress = getPassageLength();
        }

        return passageText.substring(progress);
    }

    /**
     * Compares typists on a metric.
     *
     * Supported metrics:
     * "WPM", "Accuracy", "Points", "Earnings", "Burnouts"
     */
    public String compareTypists(String metric)
    {
        String result = "Comparison by " + metric + ":\n";

        int i = 0;
        while (i < typists.size())
        {
            Typist t = typists.get(i);

            if (metric.equals("WPM"))
            {
                result = result + t.getName() + ": " + format2dp(t.getWPMThisRace()) + "\n";
            }
            else if (metric.equals("Accuracy"))
            {
                result = result + t.getName() + ": " + format2dp(t.getAccuracyPercentageThisRace()) + "\n";
            }
            else if (metric.equals("Points"))
            {
                result = result + t.getName() + ": " + t.getCumulativePoints() + "\n";
            }
            else if (metric.equals("Earnings"))
            {
                result = result + t.getName() + ": " + format2dp(t.getCumulativeEarnings()) + "\n";
            }
            else if (metric.equals("Burnouts"))
            {
                result = result + t.getName() + ": " + t.getBurnoutCountThisRace() + "\n";
            }

            i = i + 1;
        }

        return result;
    }

    /**
     * Returns current leaderboard ranking by cumulative points.
     */
    public ArrayList<Typist> getLeaderboardByPoints()
    {
        ArrayList<Typist> sorted = new ArrayList<Typist>(typists);

        Collections.sort(sorted, new Comparator<Typist>()
        {
            public int compare(Typist a, Typist b)
            {
                return b.getCumulativePoints() - a.getCumulativePoints();
            }
        });

        return sorted;
    }

    /**
     * Returns current financial leaderboard by cumulative earnings.
     */
    public ArrayList<Typist> getLeaderboardByEarnings()
    {
        ArrayList<Typist> sorted = new ArrayList<Typist>(typists);

        Collections.sort(sorted, new Comparator<Typist>()
        {
            public int compare(Typist a, Typist b)
            {
                if (b.getCumulativeEarnings() > a.getCumulativeEarnings())
                {
                    return 1;
                }
                else if (b.getCumulativeEarnings() < a.getCumulativeEarnings())
                {
                    return -1;
                }

                return 0;
            }
        });

        return sorted;
    }

    /**
     * Gets the current points leader before the race starts.
     */
    private Typist getCurrentLeaderboardLeader()
    {
        if (typists.size() == 0)
        {
            return null;
        }

        ArrayList<Typist> leaders = getLeaderboardByPoints();
        return leaders.get(0);
    }

    /**
     * Prints race progress in terminal form.
     * GUI can replace this later.
     */
    private void printRace()
    {

        System.out.println("TYPING RACE");
        System.out.println("Passage: " + passageText);
        System.out.println("Turn: " + turnCount);
        System.out.println("Autocorrect=" + autocorrectOn
            + " | Caffeine=" + caffeineModeOn
            + " | NightShift=" + nightShiftOn);

        printLine('=', getPassageLength() + 15);

        int i = 0;
        while (i < typists.size())
        {
            printSeat(typists.get(i));
            System.out.println();
            i = i + 1;
        }

        printLine('=', getPassageLength() + 15);
        System.out.println("~ = burnt out");
    }

    /**
     * Prints a single typist lane.
     */
    private void printSeat(Typist typist)
    {
        int printedProgress = typist.getProgress();

        if (printedProgress > getPassageLength())
        {
            printedProgress = getPassageLength();
        }

        int spacesBefore = printedProgress;
        int spacesAfter = getPassageLength() - printedProgress;

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        System.out.print(typist.getSymbol());

        if (typist.isBurntOut())
        {
            System.out.print("~");
            spacesAfter = spacesAfter - 1;
        }

        if (spacesAfter < 0)
        {
            spacesAfter = 0;
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');

        System.out.print(" " + typist.getName()
            + " [" + typist.getColourName() + "]"
            + " Style=" + typist.getTypingStyle()
            + " Keyboard=" + typist.getKeyboardType()
            + " Accuracy=" + format2dp(typist.getAccuracy()));
    }
    /**
     * Prints final summary after race.
     */
    private void printPostRaceSummary()
    {
        System.out.println();
        System.out.println("FINAL RESULTS");

        int i = 0;
        while (i < finishingOrder.size())
        {
            Typist t = finishingOrder.get(i);

            System.out.println((i + 1) + ". " + t.getName()
                + " | WPM=" + format2dp(t.getWPMThisRace())
                + " | Accuracy%=" + format2dp(t.getAccuracyPercentageThisRace())
                + " | Burnouts=" + t.getBurnoutCountThisRace()
                + " | Accuracy Change=" + format2dp(t.getAccuracyChangeThisRace())
                + " | Points=" + t.getPointsEarnedThisRace()
                + " | Earnings=" + format2dp(t.getEarningsThisRace()));

            i = i + 1;
        }
    }


/**
 * Prints a character multiple times without moving to a new line.
 * Used when displaying the race lanes so spacing can be controlled.
 *
 * @param aChar the character to print
 * @param times how many times the character should be printed
 */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;

        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }
    
    
    
    
    /**
     * Prints a character multiple times.
     */
    private void printLine(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }

        System.out.println();
    }

    /**
     * Two-decimal formatter.
     */
    private String format2dp(double value)
    {
        return String.format("%.2f", value);
    }

    public void prepareRaceForGUI()
    {
        if (typists.size() < MIN_TYPISTS)
        {
            throw new IllegalStateException("At least " + MIN_TYPISTS + " typists are required.");
        }

        prepareAllTypistsForRace();
        finishingOrder.clear();
        turnCount = 0;
        raceFinished = false;
    }

    public void runOneTurnForGUI()
    {
        if (raceFinished)
        {
            return;
        }

        turnCount = turnCount + 1;

        int i = 0;
        while (i < typists.size())
        {
            Typist currentTypist = typists.get(i);

            if (!hasFinished(currentTypist))
            {
                advanceTypist(currentTypist);

                if (hasFinished(currentTypist) && !finishingOrder.contains(currentTypist))
                {
                    finishingOrder.add(currentTypist);
                }
            }

            i = i + 1;
        }

        if (finishingOrder.size() == typists.size())
        {
            raceFinished = true;
            finalizeRaceResults();
        }
    }


    // ----------------------------
    // Getters / setters
    // ----------------------------

    public String getPassageText()
    {
        return passageText;
    }

    public void setPassageText(String newPassageText)
    {
        if (newPassageText != null && newPassageText.trim().length() > 0)
        {
            passageText = newPassageText;
        }
    }

    public int getPassageLength()
    {
        return passageText.length();
    }

    public ArrayList<Typist> getTypists()
    {
        return typists;
    }

    public ArrayList<Typist> getFinishingOrder()
    {
        return finishingOrder;
    }

    public boolean isAutocorrectOn()
    {
        return autocorrectOn;
    }

    public void setAutocorrectOn(boolean value)
    {
        autocorrectOn = value;
    }

    public boolean isCaffeineModeOn()
    {
        return caffeineModeOn;
    }

    public void setCaffeineModeOn(boolean value)
    {
        caffeineModeOn = value;
    }

    public boolean isNightShiftOn()
    {
        return nightShiftOn;
    }

    public void setNightShiftOn(boolean value)
    {
        nightShiftOn = value;
    }

    public int getTurnCount()
    {
        return turnCount;
    }

    public boolean isRaceFinished()
    {
        return raceFinished;
    }
}