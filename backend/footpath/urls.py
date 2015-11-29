from django.conf.urls.defaults import *
from footpath.views import *

urlpatterns = patterns('',
    (r'^post_hike/$', post_hike),
    (r'^get_hike/$', get_hike),
    (r'^get_hikes/$', get_hikes),
    (r'^get_hikes_in_window/$', get_hikes_in_window),
    (r'^get_hikes_of_user/$', get_hikes_of_user),
    (r'^delete_hike/$', delete_hike),
    (r'^post_user/$', post_user),
    (r'^get_user/$', get_user),
    (r'^delete_user/$', delete_user),
    (r'^login_user/$', login_user),
    (r'^post_image/$', post_image),
    (r'^get_image/$', get_image),
    (r'^delete_image/$', delete_image),
    (r'^clean_datastore/$', clean_datastore),
    (r'^post_comment/$', post_comment),
    (r'^delete_comment/$', delete_comment),
)
