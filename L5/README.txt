Zak Olyarnik
CS-275
L5

L5.java
-Signs into Temboo and Cloudmine to retreive Cloudmine database.  Cloudmine database contains each user as a key, with value of another
Json object containing key/value pairs for ID, Secret, Refresh Token, and Calendar.
-Asks user to input their name, and searches Cloudmine database for it.  If a calendar is found, a confirmation message is displayed.
Otherwise the user is asked to enter their credentials, which are then written to the database.
-All of this code is repeated for the second name entered, the one to be compared.
-Next, gets all events from both calendars.  There's a choreo which does this directly, which we used for Lab2.
-Asks the user to enter a date to compare calendar free times for.  This date is converted in Google's format, and loops are run to check
each hour of that day on both users' calendars for events.  Because of the stored time format, several separate loops are needed to
accomplish this.
-If an event is found, a note is made in an array representing each hour.  So after searching through both calendars, the remaining free
times are found and displayed.

Screencast Notes
-I say that I'm going to compare "March 1" but then actually compare April 1.  This is when our dates are stored and it runs correctly, I
just confused the months when I was speaking.
-13:00 is 1:00pm,  14:00 is 2:00pm, and so on.  Again, the code is correct and uses the correct times, but I misspoke.

Compile Notes
-Compiled in Eclipse Helios
-Just the main java file (L5.java), screencast (L5.mp4), and this README are included with this upload.  

Lab completed by:
-Zak Olyarnik	zwo24@drexel.edu
-Dana Thompson	dft29@drexel.edu

The only main problems I encountered with this assignment were confusing which set of keys, IDs, and accounts to be using at any given
step in the authentication process.  It doesn't make a lot of sense intutively, for instance, to use my app and secret when I'm pulling
my partner's calendars, but that's just the way it works.  What is more important is using their Google login information to be signed in
as them to press the button to grant permission to access calendars, because whoever's currently signed into Google is whose calendars
are retrieved.  So you're basically impersonating them to request access, but this is a one-time-only thing, because once they're in the
database, the permissions can all be bypassed.
There was also some confusion with exactly what the SearchEvents choreo does, but some experimentation cleared that up and I tried to
explain as such in the screencast.  For once, it actually made what we're trying to do easier than it would have been if it actually
worked the way as described.