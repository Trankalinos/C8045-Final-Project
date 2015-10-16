from Crypto.Cipher import AES
import base64
import random
import hashlib
import os

class AesCrypt128:
	"""
		Aes Crypter based on pyCrypto
		will replace Lib/Norris/AesCrypter.py

		>>> c = AesCrypt128()
		>>> key = 'mysecret'
		>>> text = 'foobar'
		>>> c.decrypt(key,c.encrypt(key,text))
		'foobar'

		>>> c.decryptB64(key,c.encryptB64(key,text))
		'foobar'

		>>> c.pkcs5_unpad(c.pkcs5_pad('foobar'))
		'foobar'

		>>> c.pkcs5_unpad(c.pkcs5_pad('foobar-'*10))
		'foobar-foobar-foobar-foobar-foobar-foobar-foobar-foobar-foobar-foobar-'
	"""

	BLOCK_SIZE = 16

	def pkcs5_pad(self,s):
		"""
			Padding, according to PKCS #5
			
			@param s: String to unpad
			@type s: String
			
			@rtype: String
			
		"""

		length = self.BLOCK_SIZE - (len(s) % self.BLOCK_SIZE)
		s += chr(length)*length
		return s


	def pkcs5_unpad(self,s):
		"""
			unpadding according to PKCS #5

			@param s: string to unpad
			@type s: string

			@rtype: string
		"""
		return s[0:-ord(s[-1])]


	def encrypt(self, key, ivector, value):
		"""
			Encrypt value by key

			@param key: key to encrypt with
			@type key: string
			
			@param ivector: the IV used for encryption
			@type ivector: string
			
			@param value: value to encrypt
			@type value: string

			@rtype: string
		"""

		iv = ivector

		mkey = key
		cipher = AES.new(mkey, AES.MODE_CBC, iv)
		crypted = cipher.encrypt(self.pkcs5_pad(value))
		return crypted


	def decrypt(self, key, ivector, value):
		"""
			Decrypt value by key

			@param key: key to decrypt with
			@type key: string
			
			@param ivector: the IV used for encryption
			@type ivector: string

			@param value: value to decrypt
			@type value: string

			@rtype: string
		"""
		
		mkey = key
		iv = ivector
		crypted = value
		cipher = AES.new(mkey, AES.MODE_CBC, iv)
		return self.pkcs5_unpad(cipher.decrypt(crypted))

	def encryptB64(self, key, ivector, value):
		"""
			Encrypt and return in base64

			@param key: key to encrypt with
			@type key: string

			@param ivector: the IV used for encryption
			@type ivector: string
			
			@param value: value to encrypt
			@type value: string

			@rtype: string
		"""
		return base64.b64encode(self.encrypt(key, ivector, value))

	def decryptB64(self, key, ivector, value):
		"""
			decrypt from base64

			@param key: key to decrypt with
			@type key: string

			@param ivector: the IV used for encryption
			@type ivector: string
			
			@param value: value to decrypt in base64
			@type value: string

			@rtype: string
		"""        
		return self.decrypt(key, ivector, base64.b64decode(value))



# mkey = hashlib.sha256(key).digest()[:self.BLOCK_SIZE]

if __name__ == "__main__":
	import doctest
	doctest.testmod()
