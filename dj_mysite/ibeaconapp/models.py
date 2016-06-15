from django.db import models
from django.template.defaultfilters import default
from xmlrpclib import Fault

# Create your models here.
class FLOORPLAN(models.Model):
    NAME = models.CharField(max_length = 250, blank=True, null=True)
    AUTHOR = models.CharField(max_length = 250, blank=True, null=True)
    FLOOR_MAP = models.CharField(max_length = 250, blank=True, null=True)
    NOTES = models.CharField(max_length = 250, blank=True, null=True)
    UPLOAD_TIME = models.DateTimeField('date uploaded', blank=True, null=True)
    X_ZERO = models.IntegerField(default = 0, blank=True, null=True)
    Y_ZERO = models.IntegerField(default = 0, blank=True, null=True)
    SCALE = models.IntegerField(default = 0, blank=True, null=True)
    
    def __unicode__(self):
        return self.NAME
    
class DEPLOYMENT(models.Model):
    FLOORPLAN = models.ForeignKey(FLOORPLAN, null=False)
    NAME = models.CharField(max_length = 250, blank=True, null=True)
    NOTES = models.CharField(max_length = 250, blank=True, null=True)
    UPLOAD_TIME = models.DateTimeField('date uploaded', blank=True, null=True)
    AUTHOR = models.CharField(max_length = 250, blank=True, null=True)
    
    def __unicode__(self):
        print "test2"
        return self.NAME

    

class BEACON_POSITION(models.Model):
    DEPLOYMENT = models.ForeignKey(DEPLOYMENT, null=False)
    TYPE = models.CharField(max_length = 250, blank=True, null=True)
    BEACON_ID = models.CharField(max_length = 250, blank=True, null=True)
    BEACON_MAC = models.CharField(max_length = 250, blank=True, null=True)
    CREATED_BY = models.CharField(max_length = 250, blank=True, null=True)
    CREATE_TIME = models.DateTimeField('date uploaded', blank=True, null=True)
    CORD_X = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    CORD_Y = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    NOTES = models.CharField(max_length = 250, blank=True, null=True)
    
    def __unicode__(self):
        return self.BEACON_MAC
    
    

class LOC_RECORD(models.Model):
    DEPLOYMENT = models.ForeignKey(DEPLOYMENT, null=False)
    NAME = models.CharField(max_length = 250, blank=True, null=True)
    NOTES = models.CharField(max_length = 250, blank=True, null=True)
    UPLOAD_TIME = models.DateTimeField('date uploaded', blank=True, null=True)
    AUTHOR = models.CharField(max_length = 250, blank=True, null=True)
    
    def __unicode__(self):
        return self.NAME
    


class TARGET(models.Model):
    NAME = models.CharField(max_length = 250, blank=True, null=True)
    MOBILE = models.CharField(max_length = 250, blank=True, null=True)
    NOTES = models.CharField(max_length = 250, blank=True, null=True)
    MAC = models.CharField(max_length = 250, blank=True, null=True)
    REFERECE = models.CharField(max_length = 250, blank=True, null=True)
    
    def __unicode__(self):
        return self.NAME
    


class TARGET_LOC(models.Model):
    LOC_RECORD = models.ForeignKey(LOC_RECORD, blank=True, null=True)
    TARGET_ID = models.ForeignKey(TARGET, null=False)
    TYPE = models.CharField(max_length = 250, blank=True, null=True)
    ALGORITHM = models.CharField(max_length = 250, blank=True, null=True)
    LOC_TIME = models.DateTimeField('date calculated', blank=True, null=True)
    CORD_X = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    CORD_Y = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    NOTES = models.CharField(max_length = 250, blank=True, null=True)
    
    def __unicode__(self):
        return self.NOTES
    
    

class RSSI(models.Model):
    LOC_RECORD = models.ForeignKey(LOC_RECORD, blank=True, null=True)
    BEACON_POSITION = models.ForeignKey(BEACON_POSITION, blank=True, null=True)
    RSSI = models.IntegerField(default = 0, blank=True, null=True)
    
    def __unicode__(self):
        return self.RSSI
    
class ZONE(models.Model):
    FLOORPLAN = models.ForeignKey(FLOORPLAN, null=False)
    USER_NO = models.IntegerField(default = 0, null=False)
    CORD_X_TOP_LEFT = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    CORD_Y_TOP_LEFT = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    CORD_X_BOTTOM_RIGHT = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    CORD_Y_BOTTOM_RIGHT = models.DecimalField(default = 0.0, decimal_places=2, max_digits=10, blank=True, null=True)
    NOTES = models.CharField(max_length = 250, blank=True, null=True)
    
    def __unicode__(self):
        return self.NOTES