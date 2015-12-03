from google.appengine.ext import ndb
from footpath.user import *

# TODO(simon) debug remove
import logging
logger = logging.getLogger(__name__)

AUTH_FORBIDDEN = -1
AUTH_ANONYMOUS = 0

def authenticate(request):
    auth_header = request.META.get('HTTP_AUTH_HEADER', '')
    
    logger.error('sent auth_header: ' + auth_header)
    
    if len(auth_header) == 0:
        return AUTH_FORBIDDEN

    auth_header = json.loads(auth_header)
    if not auth_header['logged_in']:
        return AUTH_ANONYMOUS

    user_id = auth_header['user_id']
    mail_address = auth_header['mail_address']
    token = auth_header['token']
    if user_id <= 0:
        return AUTH_FORBIDDEN

    user = ndb.Key(User, user_id).get()
    if not user:
        return AUTH_FORBIDDEN
    if (user.mail_address != mail_address) or (user.db_token != token):
        return AUTH_FORBIDDEN

    return user_id

def has_query_permission(visitor_id):
    # Temporary: Backwards compatibility TODO(simon) remove
    #return True
    return visitor_id >= 0

def has_write_permission(visitor_id, owner_id):
    # Temporary: Backwards compatibility TODO(simon) remove
    #return True
    return visitor_id == owner_id