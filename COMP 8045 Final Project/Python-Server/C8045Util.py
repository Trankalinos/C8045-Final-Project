import serial
import os.path
import sys
import base64

from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA256

"""
	
	Python Script: C8045Util.py
	
	Description:
		This class is meant to modularize some of the initialization 
		methods away from the main driver of our server. Basically, this
		file is intended to perform housekeeping so that our driver is 
		focused on simply reading NFC serial.
		
	Methods:
		initializeKey(String fileLocation)
		content_split(String content, String delimiter)
		generateServerKeys(int bits)
		
	Author: David Tran
	Created on: March 9, 2015
	
"""

def initializeKey(fileLocation):
	"""
		Method: initializeKey(String fileLocation)
		
		This method creates a key file, and converts the file into an 
		RSA key object, and expected signature in order to verify incoming 
		NFC serial strings.
		
		@returns
					key: a key file for later use if necessary
					rsakey: the RSA key object
					signer: an expected RSA signature
					 
	"""
	
	# Get our client's public key, import it and create a signature.
	
	key = open(fileLocation, "r").read()
	rsakey = RSA.importKey(key)
	signer = PKCS1_v1_5.new(rsakey)
	return key, rsakey, signer
	
def content_split(content, delimiter):
	"""
		Method: content_split(String content, String delimiter)
		
		A utility method that splits a string according to a predetermined
		delimiter. This way, we can easily distinguish which is the first
		chunk and which is the second. If the method does not find the 
		specified delimiter value in the String, it shall just return 
		the String as a whole in the 'first' object, and a null in the 
		'second' object.
		
		@returns
					first: the first chunk after delimiter is successful
					second: if the split was successful, otherwise null
	"""
	
	# Check if the delimiter values exist within our content data.
	# If so, then split them up using the delimiter and return the two 
	# objects.
	
	if delimiter in content:
		first, second = content.split(delimiter, 1)
		# print("First: " + first + "\n")
		# print("Second: " + second + "\n")
		return first, second
	
	# Otherwise, return the entire String in the first object, and a null
	# in the second. We can utilize the second object for error checking
	# to see if this method was successful.
	
	else:
		first = content
		second = None
		return first, second

def generateServerKeys(bits):
	"""
		Method: generateServerKeys(int bits)
		
		This initialization method ensures that there is a public and private
		key set for the server to process incoming NFC communication. If there
		are no external public or private key files located, the server will
		immediately generate a new pair and export them. Otherwise, the external
		files are loaded, stored and returned in the following objects.
		
		NOTE: This method was originally implemented to only utilize RSA 
		encryption and decryption. However, because we have implemented
		a hybrid encryption method, the server public and private keys are
		no longer necessary, and this method is disregarded in this project
		implementation.
		
		@returns
					public_key: the loaded public key object
					private_key: the loaded private key object
					
	"""
	public_key = None
	private_key = None
	
	# Check if the external files exist. If not, then create a new pair.
	if not os.path.exists("server-public-key.pem") or not os.path.exists("server-private-key.pem"):
		new_key = RSA.generate(bits, random_generator)
		public_key = new_key.publickey().exportKey("PEM")
		private_key = new_key.exportKey("PEM", pkcs=8)
		# print(public_key + "\n")
		# print(private_key + "\n")

		publicFile = open('server-public-key.pem', 'w')
		publicFile.write(public_key)
		publicFile.close()
		
		privateFile = open('server-private-key.pem', 'w')
		privateFile.write(private_key)
		privateFile.close()

		print("Keys created!\n")
		return public_key, private_key
		
	# Otherwise, load them and return them as two distinct objects.
	else:
		print("Loading Server's private key...\n")
		privateFile = open('server-private-key.pem', 'r')
		private_key = RSA.importKey(privateFile.read())
		# print(private_key)
		
		print("Loading Server's public key...\n")
		publicFile = open('server-public-key.pem', 'r')
		public_key = RSA.importKey(publicFile.read())
		# print(public_key)
		
		print("\nKeys loaded successfully!\n")
		return public_key, private_key
