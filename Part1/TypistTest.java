// Test class for Typist

public class TypistTest
{
    public static void main(String[] args)
    {
        Typist t = new Typist('A', "Mario", 0.85);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("TEST 1: slideBack() cannot go below 0");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("Initial progress: " + t.getProgress());

        t.slideBack(6);

        System.out.println("After slideBack(6): " + t.getProgress());
        System.out.println("Expected result: progress should still be 0");
        System.out.println();


        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("TEST 2: Burnout countdown works");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        t.burnOut(3);

        System.out.println("Burnout started:");
        System.out.println("Burnt out? " + t.isBurntOut());
        System.out.println("Turns remaining: " + t.getBurnoutTurnsRemaining());

        t.recoverFromBurnout();
        System.out.println("\nAfter 1st recovery:");
        System.out.println("Burnt out? " + t.isBurntOut());
        System.out.println("Turns remaining: " + t.getBurnoutTurnsRemaining());

        t.recoverFromBurnout();
        System.out.println("\nAfter 2nd recovery:");
        System.out.println("Burnt out? " + t.isBurntOut());
        System.out.println("Turns remaining: " + t.getBurnoutTurnsRemaining());

        t.recoverFromBurnout();
        System.out.println("\nAfter 3rd recovery:");
        System.out.println("Burnt out? " + t.isBurntOut());
        System.out.println("Turns remaining: " + t.getBurnoutTurnsRemaining());
        System.out.println("Expected result: burnout cleared and turns = 0");
        System.out.println();


        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("TEST 3: resetToStart() resets state");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        t.typeCharacter();
        t.typeCharacter();
        t.burnOut(2);

        System.out.println("Before reset:");
        System.out.println("Progress: " + t.getProgress());
        System.out.println("Burnt out: " + t.isBurntOut());
        System.out.println("Burnout turns: " + t.getBurnoutTurnsRemaining());

        t.resetToStart();

        System.out.println("\nAfter reset:");
        System.out.println("Progress: " + t.getProgress());
        System.out.println("Burnt out: " + t.isBurntOut());
        System.out.println("Burnout turns: " + t.getBurnoutTurnsRemaining());
        System.out.println("Expected result: progress = 0 and burnout cleared");
        System.out.println();


        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("TEST 4: setAccuracy() clamps values");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        t.setAccuracy(1.75);
        System.out.println("After setAccuracy(1.75): " + t.getAccuracy());

        t.setAccuracy(-0.13);
        System.out.println("After setAccuracy(-0.13): " + t.getAccuracy());

        t.setAccuracy(0.65);
        System.out.println("After setAccuracy(0.65): " + t.getAccuracy());

        System.out.println("Expected result: values limited between 0.0 and 1.0");
        System.out.println();


        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("TEST 5: typeCharacter() moves forward");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        t.resetToStart();

        System.out.println("Initial progress: " + t.getProgress());

        t.typeCharacter();
        System.out.println("After 1st typeCharacter(): " + t.getProgress());

        t.typeCharacter();
        System.out.println("After 2nd typeCharacter(): " + t.getProgress());

        System.out.println("Expected result: progress increases normally");
    }
}