Zak Olyarnik
CS-275
MP


MP
-Downloads tweets from a given user's Twitter account and calculates the grade level of the language used
-Temboo Account and new Twitter app needed
-Temboo is used to do OAuth, and pull tweets into an array
-Each tweet is then sent to Wordnik to count the syllables
-A final grade level is calculated based on a given formula

Notes
-Temboo is used to interface with Twitter due to the simplicity of it, but the Wordnik native API is used because making an
individual call for each word to get the syllables would chew up Temboo calls ridiculously
-The "ScreenName" field needed to retrieve tweets is actually the user's handle, minus the leading "@"
-The Wordnik key can be obtained directly from the API website, by running a sample call
-You must replace all non alphanumeric characters because they cause the call to fail.  (If they're mixed in a word, for
instance any hashtags, they will just return a word of 0 syllables, but if they are alone, such as using the + symbol, the
call will error out completely)
-Counting the number of fields returned by Wordnik will give you a fair estimate of the number of syllables, although words
with multiple ways of hyphenating will actually print the word multiple times, showing all the different possibilities


Midterm completed by:
-Zak Olyarnik	zwo24@drexel.edu

Compiled in Eclipse Helios, ant build file provided.
The main point of contension here was finding the Choreo to actually pull tweets.  The Tweets, Search, and other most-likely
Choreos actually do very different things, and the UserTimeline one is buried and (as far as I'm concerned) misnamed.  The
message boards were very helpful with the Wordnik part, and the hardest part overall might have been the timecrunch on it along
with all of my other midterms.