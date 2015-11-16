from django.conf.urls.defaults import *
from footpath.views import post_hike, get_hike, get_hikes, get_hikes_in_window

urlpatterns = patterns('',
    (r'^post_hike/$', post_hike),
    (r'^get_hike/$', get_hike),
    (r'^get_hikes/$', get_hikes),
    (r'^get_hikes_in_window/$', get_hikes_in_window),
)
