from google.appengine.ext import ndb
from footpath.user import *

def authenticate(request):
    auth_header = request.META.get('HTTP_AUTH_HEADER', '')
    
    # Temporary: Backwards compatibility TODO(simon) remove
    if len(auth_header) == 0:
        return int(request.META.get('HTTP_AUTH_USER_ID', 0))

    auth_header = json.loads(auth_header)
    user_id = auth_header['user_id']
    mail_address = auth_header['mail_address']
    token = auth_header['token']
    if user_id <= 0:
        return -1

    user = ndb.Key(User, user_id).get()
    if not user:
        return -1
    if (user.mail_address != mail_address) or (user.db_token != token):
        return -1

    return user_id