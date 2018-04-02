Zak Olyarnik
CS-275
L1


L1.java
-Uses a Java BufferedReader with the openStream() command to get the html from the lab webpage.
-The java.net library had exactly what we needed to do this, so no further time was spent looking for an alternative.

L1_Part2.java
-Prints information about the user's current location (zip code, city, state, latitude and longitude)
-Based on a template provided in class, but modified to include the information asked for in the lab.  This is where the majority of the structure
and syntax for the connecting to the URL, converting to JSON, and actually collecting the data came from
-We changed the field tags to the ones requested in the lab
-User must enter a key obtained from the wunderground website, but by using "autoip" we can automatically get their location without needing to ask for that

L1_Part3.java
-Prints the hourly forecast for the user's current location (about 36 hours)
-The first half of the program is virtually the same as Part2.  We decided to use geolookup again first to get the location information to make the new part easier
-The "hourly" tag requires a key like before, plus city and state information (which we now have)
-Next we create a JsonArray to store the hourly data, and use a for loop to pull out and print each requested component of that data.

Lab completed by:
-Zak Olyarnik	zwo24@drexel.edu
-Harshil Patel	hrp27@drexel.edu
-Dana Thompson  dft29@drexel.edu
-Sam Caulker	src92@drexel.edu

Compiled in Eclipse Helios, should be able to be run with the ant build file.
Results were logical in all tests.
This lab was fairly easy with all of the sample code we had to look at, but most of the time working on it was spent trying to correctly configure the environment,
for whihc there were no really good instructions to be found.