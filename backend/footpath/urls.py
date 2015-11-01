from django.conf.urls.defaults import *
from footpath.views import get_track, post_track, get_tracks

urlpatterns = patterns('',
    (r'^post_track/$', post_track),
    (r'^get_track/$', get_track),
    (r'^get_tracks/$', get_tracks),
)
