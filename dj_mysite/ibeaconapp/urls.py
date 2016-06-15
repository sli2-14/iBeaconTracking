from django.conf.urls import patterns, include, url

from ibeaconapp import views

urlpatterns = patterns('',
    url(r'^$', views.index, name = 'index'),
    url(r'floorplan/img/$', views.getfloorplanImg, name = "getfloorplanImg"),
    url(r'floorplan/(?P<fp_id>[0-9]+)/zone/$', views.getZoneForFloorplan, name = "getZoneForFloorplan"),
    url(r'floorplan/$', views.listFloorplan, name = 'listfloorplan'),
    url(r'deployment/beacons/$',views.listBeaconsInDeployment, name = 'listBeaconsInDeployment'),
    url(r'deployment/$',views.listDeployment, name = 'listdeployment'),
    url(r'dataset/$',views.processDataset, name = 'processDataset'),
    url(r'exportcsv/$',views.exportcsv, name='exportcsv'),
    url(r'zone/(?P<zone_id>[0-9]+)/$',views.getZoneInfo, name='getZoneInfo')
)
