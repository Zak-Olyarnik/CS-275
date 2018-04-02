Zak Olyarnik
CS-275
L4


activity_main.xml
-A simple listView

list_row.xml
-Contains an imageView and four textViews, to be populated into each item of activity_main.xml's listView

CustomArrayAdapter.java
-Dictates how list_row.xml is used to populate activity_main.xml
-An array with all of the necessary information is passed in, and the textViews are updated accordingly
-The imageView must be updated in an async task because only  the image's URL is passed in, and another network call is needed.
This is why the images often take longer to load.
-The adapter's main structure came from https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
-The image generation code came from http://stackoverflow.com/questions/4509912/is-it-possible-to-use-bitmapfactory-decodefile-method-to-decode-a-image-from-htt

Weather.java
-Custom class created with attributes corresponding to the data we were asked to pull down
-Includes an overloaded getForecast function.  One version takes in the JsonArray from the weather website and the database to which
we cache the results.
-The other takes in the 2D array filled with data read out of the database.  No changes are made to the database in this version.
-Both return an array which is sent to the CustomArrayAdapter.

DatabaseManager.java
-Maintains the database.  Each piece of data that is requested is stored in a new column, including one for the retrieval timestamp
-Most code came straight out of the book, but I provided custom removeAll and checkDatabase (for existence) functions
-Most importantly, the retrieveRows was entirely revamped to return a 2D array where each relevant piece of data has been parsed
into a different index.

MainActivity.java
-On launch, checks if the database exists.  If it does, and the latest retrieval time is within the hour, data is read  from the cache.
-Otherwise (whether the database doesn't exist or it's been over an hour), it makes the network call to the weather website.
-EXTRA CREDIT: The Android's GPS feature is used to get current location to make the call.
-The call itself is in an async task, which, onPostExecute, calls the populate function and continues on to fill the listView.

Compile Notes
-Compiled in Eclipse Helios
-The AVD I used for debugging and in the screencast targets Android 5.0.1, API level 21.  The CPU/ABI is Intel Atom (x86_64),
because the default runs painfully slowly
-This program was not tested on an actual Android device, and I'm honestly not so sure about how the database would work on that.
I followed some sample code to provide a simple check to see if one exists or not, but I'm not sure how reliable that is or how
I would go about testing it because my database obviously exists now and I'm not entirely sure how I would delete it (or even
find it) to check.  In the version I submitted, everything worked on the emulator though.

Lab completed by:
-Zak Olyarnik	zwo24@drexel.edu
-Dana Thompson  dft29@drexel.edu

This lab was very involved.  I managed to solve all the environment issues with the last one, but everything from the custom layout
to the pictures, GPS and especially the database required a lot of time and effort.  I'd say my time spent was upwards of 15 hours,
including one in office hours and two in the CLC.  I learned quite a bit of course, but it was a lot for a weekly lab.  Running the
GPS on an emulator is a bit screwy, and overloading functions to support both the json and the database cache may not be the best way
to go about it.  There are also a lot of global variables all over my program, but most functions already pass in two or three objects
without further complicating them.  But what I will say is that having a fully functional app that I can take around with me is worth
the hard time and effort.