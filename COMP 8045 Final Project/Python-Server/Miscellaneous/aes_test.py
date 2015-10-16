from aes_example import aes_encrypt, aes_decrypt
from AesCrypt import AesCrypt128

key = 'This is a key123' # must be 16 bytes in length
salt = 'This is an IV456' # must be 16 bytes in length
data = "1234567890ABCDEF1234"
c = AesCrypt128()

# encrypted = aes_encrypt(key, salt, data)
# print(encrypted + "\n")

# decrypted = aes_decrypt(key, salt, encrypted)
# print(decrypted + "\n")

# enc = c.encryptB64(key, salt, data)
enc = c.pkcs5_pad(data)
print(enc)
encrypt = c.encryptB64(key, salt, data)
print(encrypt)

decrypt = c.decryptB64(key, salt, encrypt)
print(decrypt)
# enc = c.pkcs5_unpad(enc)
# print(enc)

#dec = c.decryptB64(key, salt, enc)
#print(dec)