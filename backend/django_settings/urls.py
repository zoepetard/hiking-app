from django.conf.urls import patterns, include, url
from django.conf.urls.defaults import *

urlpatterns = patterns('',
    (r'^', include('footpath.urls')),
)


# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

#urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'Django_AppEngine..views.home', name='home'),
    # url(r'^Django_AppEngine./', include('Django_AppEngine..foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
#)
