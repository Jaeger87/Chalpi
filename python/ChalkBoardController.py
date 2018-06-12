import RPi.GPIO as GPIO
import time
from neopixel import *
import os.path
import json


# LED strip configuration:
LED_COUNT      = 60      # Number of LED pixels.
LED_PIN        = 18      # GPIO pin connected to the pixels (18 uses PWM!).
#LED_PIN        = 10      # GPIO pin connected to the pixels (10 uses SPI /dev/spidev0.0).
LED_FREQ_HZ    = 800000  # LED signal frequency in hertz (usually 800khz)
LED_DMA        = 5       # DMA channel to use for generating signal (try 5)
LED_BRIGHTNESS = 255     # Set to 0 for darkest and 255 for brightest
LED_INVERT     = False   # True to invert the signal (when using NPN transistor level shift)
LED_CHANNEL    = 0       # set to '1' for GPIOs 13, 19, 41, 45 or 53
LED_STRIP      = ws.WS2811_STRIP_GRB   # Strip type and colour ordering

strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, LED_FREQ_HZ, LED_DMA, LED_INVERT, LED_BRIGHTNESS, LED_CHANNEL, LED_STRIP)
fname = 'botSettings.txt'
settings = {'lightsOff':False,'rainbow':False,'currentColor':{'red':255,'green':255,'blue':255}}
current_color = Color(255,255,255)
touchPinGPIO = 23
buttonPinGPIO = 16

def stripOn(strip, color):
	for i in range(strip.numPixels()):
		strip.setPixelColor(i, color)
	strip.show()

def stripOff(strip):
	for i in range(strip.numPixels()):
		strip.setPixelColor(i, 0)
	strip.show()


def colorWipe(strip, color, wait_ms=50):
	"""Wipe color across display a pixel at a time."""
	for i in range(strip.numPixels()):
		strip.setPixelColor(i, color)
		strip.show()
		time.sleep(wait_ms/1000.0)

def theaterChase(strip, color, wait_ms=50, iterations=10):
	"""Movie theater light style chaser animation."""
	for j in range(iterations):
		for q in range(3):
			for i in range(0, strip.numPixels(), 3):
				strip.setPixelColor(i+q, color)
			strip.show()
			time.sleep(wait_ms/1000.0)
			for i in range(0, strip.numPixels(), 3):
				strip.setPixelColor(i+q, 0)

def wheel(pos):
	"""Generate rainbow colors across 0-255 positions."""
	if pos < 85:
		return Color(pos * 3, 255 - pos * 3, 0)
	elif pos < 170:
		pos -= 85
		return Color(255 - pos * 3, 0, pos * 3)
	else:
		pos -= 170
		return Color(0, pos * 3, 255 - pos * 3)

def rainbow(strip, wait_ms=20, iterations=1):
	"""Draw rainbow that fades across all pixels at once."""
	for j in range(256*iterations):
		for i in range(strip.numPixels()):
			strip.setPixelColor(i, wheel((i+j) & 255))
		strip.show()
		time.sleep(wait_ms/1000.0)

def rainbowCycle(strip, wait_ms=20, iterations=5):
	"""Draw rainbow that uniformly distributes itself across all pixels."""
	for j in range(256*iterations):
		for i in range(strip.numPixels()):
			strip.setPixelColor(i, wheel((int(i * 256 / strip.numPixels()) + j) & 255))
		strip.show()
		time.sleep(wait_ms/1000.0)

def theaterChaseRainbow(strip, wait_ms=50):
	"""Rainbow movie theater light style chaser animation."""
	for j in range(256):
		for q in range(3):
			for i in range(0, strip.numPixels(), 3):
				strip.setPixelColor(i+q, wheel((i+j) % 255))
			strip.show()
			time.sleep(wait_ms/1000.0)
			for i in range(0, strip.numPixels(), 3):
				strip.setPixelColor(i+q, 0)

def updateRainbowMode(strip, step):
	for i in range(strip.numPixels()):
		strip.setPixelColor(i, wheel((int(i * 256 / strip.numPixels()) + step) & 255))
	strip.show()


def setup(strip):
	GPIO.setmode(GPIO.BCM)
	GPIO.setup(23, GPIO.IN)
	GPIO.setup(16, GPIO.IN)
	strip.begin()

def loop(settings, current_color, strip, fname):
	print 'partito'
	switch_status = False
	commands_from_bot = False
	input_state = True
	change_light = False
	prev_on = False
	stepRainbow = 0
	rainbowMode = False
	while True:
		if os.path.isfile(fname):
			with open(fname,'U') as f:
				commands_from_bot = True
				settings = json.load(f)
			os.remove(fname)
		input_state = not(GPIO.input(touchPinGPIO))
		second_button_state = GPIO.input(buttonPinGPIO)
		if second_button_state:
			current_color = Color(255,255,255)
			change_light = True
		if commands_from_bot:
			cc =  settings['currentColor']
			rainbowMode = settings['rainbow']
			current_color = Color(int(cc['red']),int(cc['green']),int(cc['blue']))
			commands_from_bot = False
			change_light = True
		now_on = not(settings['lightsOff']) and input_state
		if now_on:
			if rainbowMode:
				stepRainbow += 1
				stepRainbow %= 256
				updateRainbowMode(strip, stepRainbow)
			elif change_light or now_on != prev_on:
			    stripOn(strip, current_color)
		else:
			if now_on != prev_on:
			    stripOff(strip)
		change_light = False
		prev_on = now_on
		time.sleep(0.3)

 
setup(strip)

loop(settings, current_color, strip, fname)    
        
        