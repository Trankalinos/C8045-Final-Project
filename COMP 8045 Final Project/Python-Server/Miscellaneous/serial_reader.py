import serial

ser = serial.Serial('/dev/ttyACM0', 115200)

while True:
	message = ser.readline()
	print(message)
	
