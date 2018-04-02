Zak Olyarnik
CS-275
L3


activity_main.xml
-The interface is very simple: a Relative Layout containing a text box, a button, and a grid layout which itself contains
the nine buttons making up the board
-The top text box is used to display messages such as whose turn it is and the endgame win or draw messages
-The board contains nine buttons which start blank but change to X's or O's based on whose turn it is when they are clicked
A button which is re-clicked once it has been set will not change, and blank spaces clicked after a win will not change until
a new game is started
-The final button resets the board and variables necessary to start over

MainActivity.java
-There are a total of ten buttons and one text box, each mapped to a variable.  The buttons all have an OnClickListener; the
board buttons call the "move" routine while the new game button calls (appropriately) "newgame"
-newgame resets all the buttons, resets the turn counter and turn boolean, and the winner variable
-move uses a switch statement to determine which of the other nine buttons was clicked, and, if it was a legal move, increments
the turn counter, switches the turn boolean to the other player, and checks to see if someone has won
-This "check" subroutine checks the text on the buttons for all possible combinations of three-in-a-row for X and O both, and
sets the winner variable if a case is found
-A win message is displayed if a winner is found, or a draw message if the total number of turns reaches nine

Compile Notes
-Compiled in Eclipse Helios
-The AVD I used for debugging and in the screencast targets Android 5.0.1, API level 21.  The CPU/ABI is Intel Atom (x86_64),
because the default runs painfully slowly
-I was not able to actually test this on an Android device because my phone and laptop would not cooperate
-I have submitted the entire L3 project folder.  This includes the MainActivity.java (located L3\src\com\example\l3), and the 
L3.apk (L3\bin) which I understand is the complete, working app. I used the link on the Piazza thread to remove the appcompat_v7
dependency, so you should be able to just import the L3 folder into the Eclipse workspace and be good to go

Lab completed by:
-Zak Olyarnik	zwo24@drexel.edu
-Dana Thompson  dft29@drexel.edu

The hard part of this lab was all the configuration.  I spent hours just trying to create a new default project, but I was getting
errors about Android 5.0.1 not loading, R.java not being generated, and other things that neither Stack Overflow nor the TA's could
provide fixes for.  Eventually I just deleted everything and did a clean install of Eclipse, Java, and the Android SDK, and that did
the trick.  Of course, I went into the lab and spent the entire hour-and-a-half there trying to get Hello World to compile and run on
the AVD, before downloading the HAXM add-on to make it actually run in a timely manner.  The Java code wasn't much of a problem after
all of those hurdles.  I would recommend putting out some kind of step-by-step guide to configuring the environment completely and
running a sample program to make sure everything is in order.  Or honestly even holding some kind of optional pre-req class that we
could take to be taught how to do all the set up so that during class we can launch right in.