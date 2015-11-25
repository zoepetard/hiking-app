from google.appengine.ext import ndb
from footpath.user import *

def authenticate(request):
    # TODO iss105 authenticate
    return int(request.META.get('HTTP_AUTH_USER_ID', 0))