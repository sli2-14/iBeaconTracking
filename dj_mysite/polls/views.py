from django.shortcuts import render, get_object_or_404

from django.http import HttpResponse
from polls.models import Question, Choice

from django.template import RequestContext, loader
from django.http.response import HttpResponseRedirect
from django.core.urlresolvers import reverse
# Create your views here.

def index(request):
    latest_q_list = Question.objects.all()
    #output = ''
    #for p in lastest_q_list:
    #    output = output + p.question_text+ ', '
    #output = '<br> '.join([p.question_text for p in latest_q_list])
    
    #my_template = loader.get_template('polls/index.html')
    #context = RequestContext(request, {'latest_question_list':latest_q_list})
    #return HttpResponse(my_template.render(context))
    return render(request,'polls/index.html',{'latest_question_list':latest_q_list})

def detail(request,question_id):
    #return HttpResponse("you are looking at the details of quesiton no. %s" % question_id)
    question = get_object_or_404(Question, id=question_id)
    return render(request,'polls/detail.html',{'question':question})

def results(request, question_id):
    question = get_object_or_404(Question,pk=question_id)
    
    return render(request,'polls/results.html',{'question':question})

def vote(request, question_id):
    p = get_object_or_404(Question,pk=question_id)
    try:
        selected_choice = p.choice_set.get(pk=request.POST['choice'])
    except (KeyError):
        return render(request,'polls/detail.html',{'question':p , 'error_message':'Please make sure you select one choice before voting!'})
    else:
        selected_choice.votes += 1
        selected_choice.save()
        return HttpResponseRedirect(reverse('polls:results',args=(p.id,)))
    
def pdf(request):
    pass
    