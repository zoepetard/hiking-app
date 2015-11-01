from django.conf.urls.defaults import *
from footpath.views import get_track, post_track

urlpatterns = patterns('',
    (r'^post_track/$', post_track),
    (r'^get_track/$', get_track),
)
