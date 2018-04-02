Zak Olyarnik
CS-275
L2


L2_Part1.java
-Lists Google calendars and the events from one of them
-Requires information like Client ID, Client Secret, and Redirect URI obtained by creating a new Google app
-Uses oauth and a Google Post routine to authorize access
-Parses the returned Json like normal
-In order to print events from a specific calendar, that calendar's ID is stored separately so it can be accessed later
-Adding events to a calendar with the "all day" specification creates them without a "dateTime" tag, so "date" must be
returned instead.  This means that it must first be checked to see which of the two should be requested.
-The Post routine and the structure for most of the rest of the code came from the example provided in class.

L2_Part2.java
-Lists Google calendars and the events from one of them, using Temboo
-Requires information like Client ID, Client Secret, and Redirect URI obtained by creating a new Google app
-Requires information like Account Name, App Name, and App Key obtained by creating a Temboo account
-Uses Temboo choreos and a Google Post routine to authorize access
-Parses the returned Json like normal
-In order to print events from a specific calendar, that calendar's ID is stored separately so it can be accessed later
-Adding events to a calendar with the "all day" specification creates them without a "dateTime" tag, so "date" must be
returned instead.  This means that it must first be checked to see which of the two should be requested.
-The Post routine and the structure for most of the rest of the code came from the example provided in class.

Lab completed by:
-Zak Olyarnik	zwo24@drexel.edu
-Dana Thompson  dft29@drexel.edu

Compiled in Eclipse Helios, should be able to be run with the ant build file.
I created a dummy calendar with dates of both "dateTime" and "date" specifications, and all are returned correctly.
Google's API documentation is like having a wild goose chase in a maze.  There are links upon links that never give any
helpful information; a lot of it is step-by-step "Get account information, request access" but without ever providing
example code on how to do such things.  The hardest part was definitely figuring out that we needed to append the oauth
access token to the calendar URL in order to get access.  All the instances we could find in the documentation either only
gave the base URL or said "API Key", which actually didn't turn out to be required once we put in the access token.  The
date/dateTime thing also threw us for a loop, and we got help on Piazza on how to check to see if a tag exists before trying
to use .get() on it.  Overall I find this stuff we're doing really interesting, but also extremely tedious.  I'm hoping that
we're building up to a method that automates all of this, because having to keep track of and keep re-entering all of these
giant character string authentication passwords is pretty ridiculous.