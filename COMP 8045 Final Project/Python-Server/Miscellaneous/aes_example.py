from Crypto.Cipher import AES
from base64 import b64encode, b64decode

def aes_encrypt(KEY, IV, plain):
	# Encryption
	encryption_suite = AES.new(KEY, AES.MODE_CFB, IV, segment_size=128)
	cipher_text = encryption_suite.encrypt(plain)
	return b64encode(cipher_text)

def aes_decrypt(KEY, IV, cipher):
	# Decryption
	# print("Cipher text after decode: " + b64decode(cipher))
	decryption_suite = AES.new(KEY, AES.MODE_CFB, IV, segment_size=128)
	plain_text = decryption_suite.decrypt(b64decode(cipher))
	return plain_text

