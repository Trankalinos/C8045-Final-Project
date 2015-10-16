++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+ 
+ 	README
+ 	Author: David Tran
+ 	Course: COMP8045 9-Credit Half-Practicum
+ 	Project: NFC & Smartphone Security
+ 
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

The libraries for the Arduino Code are not included in this package. If the 
package is required for reference, the link to the library is listed below:

	https://github.com/Seeed-Studio/PN532

The Android application is best imported into Android Studio. However, if only
the .java files are necessary, please navigate to the following directory where
all the .java source code can be located:

	CardEmulation/Application/src/main/java/com/example/android/cardemulaton

Some Python libraries are required for the server to run. Please refer to the
report, Appendix II - Installation & User Manuals on how to install these
prerequisites onto the Linux machine. 

Before running the Python Script, ensure that the Arduino is connected to the 
Linux machine using a USB Serial, and check to see that the correct Serial
device is used in the Python Script. 

	Default value: "/dev/ttyACM0"

To run the Python Script, ensure all prerequisites have been met, and enter
the following command where the following script is located:

	python NFC_Server.py





Created on: March 9, 2015