from google.appengine.ext import ndb
from google.appengine.api import images
import json
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)
            
class Image(ndb.Model):
    '''Models an individual Image.'''
    
    # Management (set by backend)
    owner_id = ndb.IntegerProperty()
    image_data = ndb.BlobProperty()
    image_thumbnail = ndb.BlobProperty()

    # Set fields, convert image to thumbnail
    def create_thumbnail(self):
        # See https://cloud.google.com/appengine/docs/python/images/
        # on how to convert image into thumbnail
        #self.resize(width=80, height=100)
        #self.thumbnail = img.execute_transforms(output_encoding=images.JPEG)
        # The creation of thumbnails is currently not implemented.
        return False

def build_image(owner_id, image_data):
    img = Image(owner_id = owner_id, image_data = image_data)
    img.create_thumbnail()
    return img
