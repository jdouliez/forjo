# coding: utf8
import os
import json
from lxml import etree

# Define global variable
translate_file_template = "messages.$lang.xlf"
local_path = "src/locale/"
translate_file = "translates.json"
translate_file_path = os.path.abspath(local_path + translate_file)

if not os.path.isfile(translate_file_path):
	print "ERROR : File < " + translate_file_path + " > doesn't exist."
	exit()

with open(translate_file_path) as json_file:
	data = json.load(json_file)
	accept_languages = data["languages"]
	default_language = data["default_language"]
	for l in accept_languages:
		if l != default_language:
			print l
	# # Add translation to XML file
	# for translate in data["translates"]:
	# 	print translate