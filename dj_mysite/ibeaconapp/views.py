from django.shortcuts import render
from django.http import HttpResponse
from django.http import JsonResponse
from ibeaconapp.models import *
from django.core.context_processors import request
from django.contrib.auth.decorators import login_required
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
import sys
import os
import json
import threading
import functools
import logging
import csv
import math
import numpy
from numpy import *
from decimal import *
import urllib
import urllib2

from django.core.servers.basehttp import FileWrapper


# Create your views here.

def async(func):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
            my_thread = threading.Thread(target=func, args=args, kwargs=kwargs)
            my_thread.start()
    return wrapper

@login_required
def index(request):
    
    users = User.objects.exclude(id=request.user.id)
    print 'Main program running'
    variables = RequestContext(request,{'users':users})
    return render_to_response('ibeaconapp/home.html',variables)

@async
def getdata(request):
    floorplan_list = FLOORPLAN.OBJECTS.order_by('-id')
    floorplan_list=1
    print floorplan_list
    return JsonResponse(floorplan_list)

def listFloorplan(request):
    try:
        floorplan_list = FLOORPLAN.objects.all()
        print floorplan_list[0].id
    except BaseException:
        print BaseException.message
    #return_list = {"floorplanlist":[{"name" : "Computer Science floor 3", "id" : "01"}, {"name" : "Computer Science floor 2", "id" : "02"},{"name" : "Computer Science floor 1", "id" : "03"},{"name" : "Computer Science floor 0", "id" : "04"}]}
    return_list = {"floorplanlist":[]}
    for fp in floorplan_list:
        return_list["floorplanlist"].append({"name":fp.NAME,"id":fp.id})
    
    return JsonResponse(return_list)

def listDeployment(request):
    fp_id = request.GET.get('floorplan_id', '')
    # use fp_id to query database
    deployment_list = []
    try:
        deployment_list = DEPLOYMENT.objects.filter(FLOORPLAN=fp_id)
    except BaseException:
        type, value, traceback = sys.exc_info()
        print('Error: %s' % (value))
    return_list =  {"deploymentlist":[]}
    for deployment in deployment_list:
        return_list["deploymentlist"].append({"name":deployment.NAME,"id":deployment.id})
    return JsonResponse(return_list)

def listDataset(request):
    fp_id = request.GET.get('floorplan_id', '')
    dp_id= request.GET.get('deployment_id', '')

    # use fp_id and dp_id to query database
    dataset_list = []
    try:
        dataset_list = DATASET.object.all()
    except BaseException:
        print BaseException.message
    return_list = {"datasetlist":[]}
    for dataset in dataset_list:
        return_list["datasetlist"].append({"name":dataset.NAME,"id":dataset.id})
    
    #dataset_list = {        
    #                "datasetlist":["dataset01","dataset02","dataset03"]       
    #                }
                    
    
    return JsonResponse(dataset_list)

def getfloorplanImg(request):
    fp_id = request.GET.get("floorplan_id","")
    try:
        fp = FLOORPLAN.objects.get(id=fp_id)
    except BaseException:
        print BaseException.message
    rps = {"floorplan_img":fp.FLOOR_MAP}
    return JsonResponse(rps)

def listBeaconsInDeployment(request):
    dp_id= request.GET.get('deployment_id', '')
    # use fp_id to query database
    beacon_list = []
    try:
        beacon_list = BEACON_POSITION.objects.filter(DEPLOYMENT=dp_id)
    except BaseException:
        type, value, traceback = sys.exc_info()
        print('Error: %s' % (value))
    return_list =  {"beaconlist":[]}
    for beacon in beacon_list:
        return_list["beaconlist"].append({"beacon_id":beacon.BEACON_ID,
                                          "beacon_mac":beacon.BEACON_MAC,
                                          "id":beacon.id,
                                          "x":beacon.CORD_X,
                                          "y":beacon.CORD_Y})
    return JsonResponse(return_list)

def processDataset(request):
    print "processing dataset"
    if request.method == 'POST':
        file_name = "temp_dataset.csv"
        path = 'ibeaconapp/static/ibeaconapp/dataset_file/%s' % file_name
        f = request.FILES['dataset']
        deployment_id = request.POST.get("deployment_id","")
        floorplan_id = request.POST.get("floorplan_id","")
        destination = open(path, 'wb+')
        for chunk in f.chunks():
            destination.write(chunk)
        destination.close()
        
        location_history_list = calculateLocationHistory(floorplan_id,deployment_id)
        return_list = {"location_history_list" : location_history_list}
        #return JsonResponse(return_list)
    elif request.method == 'GET':
        fp_id = request.GET.get('floorplan_id', '')
        dp_id= request.GET.get('deployment_id', '')
        # use fp_id and dp_id to query database
        return_list = {        
                        "datasetlist":["dataset01","dataset02","dataset03"]       
                        }
    return JsonResponse(return_list)
    
    
def calculateLocationHistory(floorplan_id,deployment_id):
    print "calculateLocationHistory"
    location_history_list = []
    
    file_name = "temp_dataset.csv"
    path = 'ibeaconapp/static/ibeaconapp/dataset_file/%s' % file_name
    print "opening csv file"
    with open(path,'rb') as loc_record_file:
        loc_record = csv.reader(loc_record_file)
        for record in loc_record:
            dt = record[0]
            sn = record[1]
            print "processing record " + sn
            beacons = []
            beacons.append({"mac":record[2],"dist":convertRssiDist(record[3])})
            beacons.append({"mac":record[4],"dist":convertRssiDist(record[5])})
            beacons.append({"mac":record[6],"dist":convertRssiDist(record[7])})
            loc = calculateLocation(floorplan_id, deployment_id,beacons)
            if loc["calculated"] is True:
                location_history_list.append({"time":dt,"sn":sn,"x":loc["x"],"y":loc["y"]})
    rssi2xy(location_history_list)        
    return location_history_list

def rssi2xy(location_history_list):
    print "rssi2xy"
    with open('rssi2xy.csv', 'wb') as csvfile:
        for location_history in location_history_list :
            xywriter = csv.writer(csvfile, dialect='excel')
            xywriter.writerow([location_history["time"], location_history["sn"], location_history["x"], location_history["y"]])    

def exportcsv(request):
    response = HttpResponse(content_type='text/csv')
    response['Content-Disposition'] = 'attachment; filename="rssi2xy.csv"'
    return response

def calculateLocation(floorplan_id, deployment_id,beacons):
    ranging_list = []
    location = {}
    for beacon in beacons:
        try:
            b = BEACON_POSITION.objects.get(DEPLOYMENT=deployment_id,BEACON_MAC=beacon["mac"])
            ranging_list.append({'x':int(b.CORD_X),"y":int(b.CORD_Y),'ranging':beacon["dist"]})
            location = calRssiLocation(ranging_list)
            location["calculated"] = True 
        except Exception:
            location["calculated"] = False
    
    return location
 
 
def calRssiLocation(ranging_list):
    x0 = calTwoBeacon(ranging_list[0],ranging_list[1],"x")
    x1 = calTwoBeacon(ranging_list[0],ranging_list[2],"x")
    x2 = calTwoBeacon(ranging_list[1],ranging_list[2],"x")
    x = x0+x1+x2
    x = x / 3
       
    y0 = calTwoBeacon(ranging_list[0],ranging_list[1],"y")
    y1 = calTwoBeacon(ranging_list[0],ranging_list[2],"y")
    y2 = calTwoBeacon(ranging_list[1],ranging_list[2],"y")
    y = y0+y1+y2
    y = y / 3    
    
    return {'x':x,'y':y}
         

def calTwoBeacon(b1,b2,cord):
    if(b1[cord]>b2[cord]):
        b = b1[cord]
        a = b2[cord]
        x = a + (b - a) * (b2["ranging"] / ((b1["ranging"]+b2["ranging"])))
    else:
        a = b1[cord]
        b = b2[cord]
        x = a + (b - a) * (b1["ranging"] / ((b1["ranging"]+b2["ranging"])))
    return x

def convertRssiDist(rssi):
    rssi = int(rssi)
    a = -60
    n = 3
    d = 10**((rssi - a)/((-10)*n))
    return d


def getZoneForFloorplan(request,fp_id):
    print "looking for zones of floorplan:"
    print fp_id
    return_list = {"zone_list":[]}
    try:
        zone_list = ZONE.objects.filter(FLOORPLAN=fp_id)
    except BaseException:
        type, value, traceback = sys.exc_info()
        print('Error: %s' % (value))
    for zone in zone_list:
        return_list["zone_list"].append({"zone_id":zone.id,
                                          "zone_no":zone.USER_NO,
                                          "tl_x":zone.CORD_X_TOP_LEFT,
                                          "tl_y":zone.CORD_Y_TOP_LEFT,
                                          "br_x":zone.CORD_X_BOTTOM_RIGHT,
                                          "br_y":zone.CORD_Y_BOTTOM_RIGHT,
                                          "note":zone.NOTES})
    return JsonResponse(return_list)

def getZoneInfo(request,zone_id):
    try:
        zone = ZONE.objects.get(id=zone_id)
    except BaseException:
        type, value, traceback = sys.exc_info()
        print('Error: %s' % (value))
    return_dict = {"zone_id":zone.id,
                  "zone_no":zone.USER_NO,
                  "tl_x":zone.CORD_X_TOP_LEFT,
                  "tl_y":zone.CORD_Y_TOP_LEFT,
                  "br_x":zone.CORD_X_BOTTOM_RIGHT,
                  "br_y":zone.CORD_Y_BOTTOM_RIGHT,
                  "note":zone.NOTES}
    return JsonResponse(return_dict)