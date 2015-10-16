import serial

ser = serial.Serial('COM8', 115200)

while True:
	message = ser.readline()
	print(message)