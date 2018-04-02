Zak Olyarnik
CS-275
A2


activity_main.xml
-Text views specifying source and destination station, spinners containing those values, Search and Map buttons, and a two-line list view
to hold the train information.
-Map button is hidden initially, becoming visible when two different stations are selected.  It will return invisible if at any time the 
stations are the same.

map.xml
-Contains a fragment activity used to generate the map

arrays.xml
-Hardcoded station names to populate the station spinners

MainActivity.java
-Search button takes the values from the station spinners and, if they are unique, starts an async task to make a network call and
retrieve the Json for the trains travelling between stations listed.  The train number and departure and arrival times are added to
array lists to be populated into the listview.
-Long-pressing on the item in the listview starts another async task to find additional information like last departed station and the
estimated and actual time to leave there.  This extra information is displayed in a toast.
-Pressing the map button passes the list of train numbers into a new intent to display the map.

Map.java
-For each train in the list passed in (of trains going between the two specified stations), makes a network call to find the last stop it
was at.  This is its last known location and is what is plotted on the map, if that data is available.
-The station name is Geocoded to get a LatLng value, which can be passed into a Marker.  In the onPostExecute of the call, the markers
are added.
-The camera is centered and zoomed based on the number of markers placed.  If only one train is visible, it becomes the center of the
map.  Otherwise the markers are all averaged and the map is zoomed and centered accordingly to display them all.


Compile Notes
-Compiled in Eclipse Helios
-The AVD I used for debugging and in the screencast targets Google APIs, platform  5.0.1, API level 21.  The CPU/ABI is Google APIs Intel
Atom (x86).  My original AVD did not have Google compatibility, and for some reason new ones I created stopped working every time I 
restarted my computer.  This one I used in the screencast was created the morning I finished the assignment and left running all day.
-This program was tested on an actual Android device, and runs perfectly, the same way as shown in the screencast, so further testing can
be done by sideloading the A2.apk (A2/bin/A2.apk).
-I believe that the A2 project is dependent on appcompat_v7 and the google-play-services_lib, so I've included both of those as well in
my bitbucket upload if you want to run the source code on a console.

Assignment completed by:
-Zak Olyarnik	zwo24@drexel.edu

This was a cool assignment.  There was a real learning curve with the map, and all the extra registration needed to get it to display
anything at all.  Then I was stuck in Africa, the default location, for the longest time, with all of my code (based on the linked 
examples) using v1 of the Google Maps API while the rest of the world has upgraded to v2.  The Android and Google documentation are all
trash, but if you look hard enough you can find examples and tutorials in other places, and these are more helpful.  All code taken 
directly from other sources is cited in my program.  My AVDs started having serious problems near the end that I have been unable to
solve without creating a new one for each run.
Total project time I would estimate to be again about 15 hours, but unlike the last lab, this time was spent with measurable progress
and only the map generation was a real stumbling block.