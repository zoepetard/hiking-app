# -*- coding: utf-8 -*-
"""
Created on Sat Oct 24 10:04:20 2015

@author: simon
"""

import urllib
import httplib
import json

URL = "localhost:8080"

def post_entry():
    
    h = httplib.HTTPConnection(URL)
    
    data = json.dumps({'some_string': 'python_test_author'})
    headers = {"Content-type": "application/json", "Accept": "text/plain"}

    h.request('POST', '/post/', data, headers)

    r = h.getresponse()
    responsetext = r.read()

    f1=open('./response.html', 'w+')
    f1.write(responsetext)
    print(responsetext)

post_entry()

print('open...')
req = urllib.urlopen("http://localhost:8080")
responsetext = req.read()

f1=open('./response1.html', 'w+')
f1.write(responsetext)
print(responsetext)
exit()

#import sys
#import requests

#client = requests.session()

# Retrieve the CSRF token first
#client.get(URL)  # sets cookie
#csrftoken = client.cookies['csrf']

#login_data = dict(csrfmiddlewaretoken=csrftoken, next='/')
#r = client.post(URL, data=login_data, headers=dict(Referer=URL))

#import urllib, urllib2
#import cookielib

#cj = cookielib.CookieJar()

#opener = urllib2.build_opener(
#    urllib2.HTTPCookieProcessor(cj), 
#    urllib2.HTTPHandler(debuglevel=1)
#)
#login_form = opener.open(URL).read()
#print(login_form)