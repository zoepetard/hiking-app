from django.http import HttpResponseRedirect, HttpResponse
#from django.views.generic.simple import direct_to_template
from django.core import serializers

from google.appengine.api import users

from footpath.models import Track

import json

import urllib

def get_track(request):
    track_id = request.GET.get('track_id', 0)
    
    # Ancestor Queries, as shown here, are strongly consistent with the High
    # Replication Datastore. Queries that span entity groups are eventually
    # consistent. If we omitted the ancestor from this query there would be
    # a slight chance that Greeting that had just been written would not
    # show up in a query.
    random_track = Track.query()
    tracks = Track.query().fetch()
    random_track = tracks[0]

    track_data = {
        'author': random_track.author,
        'date': random_track.date.strftime("%Y-%m-%d %H:%M:%S"),
    }
    return HttpResponse(json.dumps(track_data), content_type='application/javascript')
    #return HttpResponse(serializers.serialize("json", random_track), content_type='application/json')
    
def post_track(request):
    if request.method == 'POST':
        author = request.POST.get('author')
        track = Track()
    
        track.author = author
    
        #track.content = request.POST.get('content')
        track.put()
        #return HttpResponseRedirect('/' + urllib.urlencode({'ID': id}))
    return HttpResponse(status=201)