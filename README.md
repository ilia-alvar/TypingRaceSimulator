# **TypingRaceSimulator**







TypingRaceSimulator is a Java project that simulates a competitive typing race between multiple typists.

The project is implemented in multiple stages:





* Part I – Textual Simulation: A terminal-based typing race engine.
* Part II – Graphical Version: A GUI version with additional interaction and visualization.
* Part III – Git Integration: Version control using Git and GitHub.





The simulator models typing behavior including accuracy, burnout, typing styles, keyboard types, and race modifiers.



==========================================================================



###### **Project Structure**

TypingRaceSimulator/

│

├── .git/                # Git repository (hidden)

├── Part1/               # Textual version of the typing race

│   ├── TypingRace.java

│   ├── Typist.java

│   ├── TypingRaceTest.java

|   ├── TypistTest.java

│

├── Part2/               # Extended version (prepared for GUI development)

│   ├── TypingRace.java

│   ├── Typist.java 

|   ├── TypingRaceTest.java

|	├── TypingRaceGUI.java

│

├── README.md            # Project documentation

├── README.txt           # Project documentation

├── .gitignore          # The .gitignore automatically ignores .class files in every folder.



=====================================================================



###### **Requirements**



Before running the project, make sure the following software is installed:



Java JDK 8 or later

Git



To verify Java installation:



java -version

javac -version



==================================================================

###### **Part 1 – Textual Typing Race**



Part 1 implements the core race simulation in the terminal.



**Features include:**



* Multiple typists competing in a race
* Accuracy-based typing
* Mistypes and slide-back penalties
* Burnout mechanics
* Race progression printed in the terminal
* Final race statistics including:

&#x09;Words per minute (WPM)

&#x20;       Accuracy percentage

&#x20;       Burnout count

&#x20;       Points and earnings

==============================================================

###### **How to Compile and Run Part 1**



1. Navigate to the project folder \& Part1:



&#x20;    cd TypingRaceSimulator

&#x20;    cd Part1



2\. – Compile the program



&#x20;    javac \*.java



3\. – Run the simulation



&#x20;    java TypingRaceTest



The terminal will display the race progress turn-by-turn and print the final results at the end of the race.

==========================================================

###### **Part 2 – Extended Typing Race (Preparation for GUI)**



Part 2 extends the simulator with additional mechanics designed for the graphical version.



New features include:



* Support for 2–6 typists
* Custom typing passage
* Race modifiers

&#x20;     Autocorrect

&#x20;     Caffeine Mode

&#x20;     Night Shift

* Typing styles

&#x20;     Touch Typist

&#x20;     Hunt \& Peck

&#x20;     Phone Thumbs

&#x20;     Voice-to-Text

* Keyboard types

&#x20;     Mechanical

&#x20;     Membrane

&#x20;     Touchscreen

&#x20;     Stenography

* Accessories

&#x20;     Wrist Support

&#x20;     Energy Drink

&#x20;     Noise-Cancelling Headphones

* Sponsor bonus system
* Race history tracking
* Leaderboards
* Badge system for achievements



These mechanics form the backend logic that will later be connected to the graphical interface.

============================================================
###### **Interactive Configuration (GUI Enhancements)**


###### Race Setup Options

Users can configure the race before starting:

. Passage Selection
		. Predefined passages (Short, Medium, Long)
		. Custom user-defined passage
Seat Count
		. Adjustable number of typists (2–6)
Difficulty Modifiers
		. Autocorrect
		. Caffeine Mode
		. Night Shift

###### Typist Customization

Each typist can be individually configured through the interface:

. Identity
		. Name
		. Symbol
. Performance Attributes
		. Base accuracy (0.0 – 1.0)
. Visual Customization
		. Color (custom RGB-based themes)
. Typing Behavior
		. Typing style (e.g. Touch Typist, Hunt & Peck)
		. Keyboard type (e.g. Mechanical, Touchscreen)
. Accessories
		. Wrist Support (reduces burnout duration)
		. Energy Drink (boost early performance, later penalty)
		. Noise-Cancelling Headphones (reduces mistype chance)
. Sponsor Selection
		. Enables sponsor-based bonus rewards



###### Behavior Impact

All configuration choices directly influence race performance:

. Accuracy affects typing success rate
. Typing style and keyboard type modify:
		. speed
		. accuracy
		. burnout risk
. Accessories introduce trade-offs between stability and performance
. Modifiers dynamically change behaviour during the race



### GUI Features

The GUI demonstrates the simulation visually, including:

- Real-time typing progress  
- Highlighted completed and remaining text  
- Typist-specific colors and progress bars  
- Final race statistics display  

Note: Typists and settings are currently predefined in code, while the backend fully supports dynamic customization.



========================================================



###### **How to Compile Part 2**



1\. – Navigate to Part2



&#x20;     cd TypingRaceSimulator

&#x20;     cd Part2



2\. – Compile the files



&#x20;     javac \*.java



3\. – Run the program



&#x20;   java TypingRaceGUI

(for openning a window displaying the typing race simulation and the final race results.)
The GUI is launched using the startRaceGUI() method in TypingRaceGUI.java.


OR:

	java TypingRaceTest 
(for textual output on cmd)




===================================================



###### **Git Version Control**



&#x20;  This project uses Git for version control.



**Main Branch**



&#x20;   Contains the stable implementation of the project.



**gui-development Branch**



&#x20;   Used for implementing the graphical interface in Part II.



&#x20;   Example Git workflow used in the project:



&#x20;      git add .

&#x20;      git commit -m "Add extended Typist class with race statistics and accessories"

&#x20;      git push



Descriptive commit messages are used to clearly document changes.



===============================================



###### **Future Work (GUI Integration)**



The graphical version will include:



* Interactive race setup
* Typist configuration screen
* Visual typing progress display
* Highlighted typed passage
* Leaderboard display
* Race statistics dashboard



This functionality will be implemented in the gui-development branch.





^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^



##### Author



Ilia Hajypour Alvar

TypingRaceSimulator Project – 2026 - May

