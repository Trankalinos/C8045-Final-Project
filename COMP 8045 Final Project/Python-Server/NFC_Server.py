import serial
import os.path
import sys
import MySQLdb
import base64

from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA256
from AesCrypt import AesCrypt128
from C8045Util import initializeKey, content_split, generateServerKeys

"""
	Python Script: NFC_Server.py
	
	Description:
		This python program is the main driver on the Linux machine to 
		process the incoming NFC serial data from the Arduino NFC Shield.
		The program processes the data by first splitting the content,
		decrypting the first chunk of the content, and then verifies
		that content with the second chunk. Once the data passes through
		"quality inspection", the server welcomes the legitimate user.
		
	Methods:
		None. See C8045Util.py for Utility Methods
		
	Author: David Tran
	Created on: March 9, 2015
	
"""

########################################################################
#                     [START] - GLOBAL VARIABLES                       #
########################################################################	

# These following set of variables are for getting and setting the client's
# public key, and creating the key object, and the expected signature.
keyLocation = "note-4-public-key.txt"
key = None
rsakey = None
signer = None

# The following set of variables are for getting and setting the server's
# public and private key pairs, as well as specifying the number of bits
# for the RSA key generation when the external key files do not exist.
public_key = None
private_key = None
bits = 1024

# The following set of variables are for determining a static constant
# value for the initialization vector and secret key for AES encryption
# and decryption.
aeskey = b'This is a key123'	# Casted as byte
aesiv = b'This is an IV456'		# Casted as byte

# Leave this one alone, as it's only role is a placeholder.
message = ""

divider = "============================================================"

########################################################################
#                      [END] - GLOBAL VARIABLES                        #
########################################################################	

########################################################################
#                    [START] - SERVER INITIALIZERS                     #
########################################################################	

# Generate the key file, the RSA key object and client signature respectively.
key, rsakey, signer = initializeKey(keyLocation)

# Generate the server's public and private key objects.
public_key, private_key = generateServerKeys(bits)

# Initialize the serial object via its Serial Port and the BAUD rate.
# DO NOT MODIFY THIS UNLESS YOU KNOW WHAT YOU'RE DOING
ser = serial.Serial('/dev/ttyACM0', 115200)

# Create the database connection
conn = MySQLdb.connect(host="localhost", user="root", 
					   passwd="uest1onQ?", db="nfc_accounts")

# Initialize the AesCrypt helper. This implementation uses AES-128.
c = AesCrypt128()

########################################################################
#                     [END] - SERVER INITIALIZERS                      #
########################################################################

########################################################################
#                        [START] - MAIN LOOPER                         #
########################################################################

while True:
	incoming = ser.readline()
	# print(incoming + "\n")
	
	if "Response: " in incoming:
		# This should remove the "Response: " tag and prepares for decryption
		message = incoming.strip("Response: ")
		# print(message + "\n")
		
		ciphertext, signature = content_split(message, "::")
		# print(ciphertext)
		
		account = c.decryptB64(aeskey, aesiv, c.pkcs5_pad(ciphertext))
		# Insert a line break here for aesthetics
		print(divider + "\n")
		print("Account number is: " + account + "\n")		
			
		name, accNumber = account.split(" ")
		cursor = conn.cursor()
		cursor.execute("SELECT * FROM Accounts WHERE username = %s", (name,))
		
		data = cursor.fetchall()
		
		if len(data)==0:
			print("No records. " + name + "does not exist in database\n")
			# Insert a line break here for aesthetics
			print(divider + "\n")
		else:
			for row in data:
				if accNumber == row[2]:
					# Insert a line break here for aesthetics
					print(divider + "\n")
					print("Verifying digest and contents...\n")
					
					digest = SHA256.new()
					digest.update(account)
					if signer.verify(digest, base64.b64decode(signature)):
						print("Verified!\nWelcome, account holder: " + name + 
						"\tAccount Number: " + accNumber + "\n")
					else:
						print("Content and digest mismatch. Are the keys correct?")
				else:
					print("Username and account mismatch. Please report to " +
						+ "administrator.\n")
					# Insert a line break here for aesthetics
					print(divider + "\n")

########################################################################
#                          [END] - MAIN LOOPER                         #
########################################################################
