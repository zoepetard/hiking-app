from google.appengine.ext import ndb
import json
import logging

# Get an instance of a logger
logger = logging.getLogger(__name__)
            
class Image(ndb.Model):
    '''Models an individual Image.'''
    # TODO add indexed=False to properties that should not be indexed
    # Management (set by backend)
    owner_id = ndb.IntegerProperty()
    image_data = ndb.BlobProperty()
    image_thumbnail = ndb.BlobProperty()

    # Set fields, convert image to thumbnail
    def create_thumbnail(self):
        # TODO(simon) convert image into thumbnail
        
        return True

def build_image(owner_id, image_data):
    img = Image(owner_id = owner_id, image_data = image_data)
    img.create_thumbnail()
    return img


import webapp2
from google.appengine.api import images
from google.appengine.ext import blobstore

class Thumbnailer(webapp2.RequestHandler):
    def get(self):
        blob_key = self.request.get("blob_key")
        if blob_key:
            blob_info = blobstore.get(blob_key)
            
            if blob_info:
                img = images.Image(blob_key=blob_key)
                img.resize(width=80, height=100)
                img.im_feeling_lucky()
                thumbnail = img.execute_transforms(output_encoding=images.JPEG)
                
                self.response.headers['Content-Type'] = 'image/jpeg'
                self.response.out.write(thumbnail)
                return

        # Either "blob_key" wasn't provided, or there was no value with that ID
        # in the Blobstore.
        self.error(404)