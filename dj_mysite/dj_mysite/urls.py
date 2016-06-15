from django.conf.urls import patterns, include, url
from django.contrib import admin
admin.autodiscover()
#from test.test_xml_etree import namespace

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'dj_mysite.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    #url(r'^$', include('personal_site.urls', namespace='personal_site')),
    #url(r'^personal_site/', include('personal_site.urls', namespace='personal_site')),
    #url(r'^$', 'ibeaconapp.views.index', name = 'index'),
    url(r'^polls/', include('polls.urls', namespace='polls')),
    url(r'^ibeaconapp/', include('ibeaconapp.urls', namespace='ibeaconapp')),
    url(r'^admin/', include(admin.site.urls)),
    url(r'^index/$', 'ibeaconapp.views.getdata', name='getdata'),
    url(r'^accounts/login/$', 'django.contrib.auth.views.login', {'template_name': 'admin/login.html'},name="my_login"),
)
