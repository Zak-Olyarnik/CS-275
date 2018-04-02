Zak Olyarnik
CS-275
A1


A1
-Downloads files from Dropbox.
-Temboo Account and new Dropbox app needed.
-Temboo is used to do OAuth, pull all files locally, and re-upload the edited __list file.
-Dropbox stores files in Base64 format, so code taken from 
http://stackoverflow.com/questions/11544568/decoding-a-base64-string-in-java
was used to decode this to ASCII.
-After this conversion, the string is parsed into an array, which is then read to pull the
other files locally.  These are also converted and Java writes the text data to a new file
in the specified location.
-A blank __list is uploaded to Dropbox so that subsequent runs do not recopy the files.

Notes
-All files tested were .txt files.  These were the format of the example, and because of the
instructor-endorsed method of writing the new files, I'm not sure if anything other than plain
text would work by this method.
-The new __list file is a file created with a single CRLF.  It would be better to clear the original
one line at a time with each successful copy, but the original file kind of gets mutilated by the
download and parsing process.  It was confirmed that we can assume all files are present and named
correctly so there should be no unsuccessful copies, and creating a new blank file gets the same
result.
-Actually a lot of things were made simpler by the assumptions list.  The way I parse the string is
entirely dependant on the text file being delimited by spaces and CRLFs, so if there are inaccuracies
in that (such as spaces within fields), the code would require a lot of error handling modifications.
-Testing on tux resulted in new files being created there with the path specified in __list, but I don't
think these are normal .txt files, because I was unable to access them with VIM or cat or anything else 
I tried.  They're not directories either, because I couldn't go into them, so I'm not really sure what
was created by running it on the server.  Everything is fine on my desktop, where C:/ is a real place.


Assignment completed by:
-Zak Olyarnik	zwo24@drexel.edu

Compiled in Eclipse Helios, ant build file provided.
I'm feeling a lot better about OAuth now that we have several good previous examples to refer to.  Getting
to the OAuth token took me less than half an hour here, when the majority of time was spent trying to understand
how Dropbox worked with the GetFile methods, Base64, and that the files had to be physically recreated with a Java
filewriter.  There are a lot of simplifications based on the assumptions list, especially with re-uploading the blank
__list file, but if we were actually moving files around (and not protecting ourselves from accidental deletes, as the
assignment specifies), that step wouldn't be necessary at all.