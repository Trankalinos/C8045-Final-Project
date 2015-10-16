import MySQLdb

name = "Trankalinos"
conn = MySQLdb.connect(host="localhost", user="root", 
		passwd="uest1onQ?", db="nfc_accounts")

cursor = conn.cursor()
cursor.execute("SELECT username, account_number FROM Accounts WHERE username = %s", (name,))

print("\n-------------------- DATABASE RESULTS --------------------\n")

data = cursor.fetchall()

if len(data)==0:
	print("No records")
else:
	for row in data:
		print("\tUsername: " + row[0] + "\tAccount: " + row[1] + "\n")
	
