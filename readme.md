What does it do
---------------
The NeuroAnalytics is based on neuroph framework and image recognition library and
is used to analyze the change of accuracy (predition capability) of a neural network
by the change of exactly one of some parameters. Currently it can analyze 2 parameters: number of
hidden neurons(in 1 hidden layer), and learning rate. For the evalution, currently 3
methods are used, explained below. The usage is simple: one has to specify
the lower bound, higher bound and step for the parameter and the type of parameter itself
(commandline syntax described below).

How does it do
---------------
The logic works as follows: The program modifies the parameter from lower bound to upper
bound, each time increases the parameter by the "step" and trains a new network with
this parameters(other parameters remain default!). After having learned the network,
it doesnt save it to any file, since it's not necessary, but instead calls the ask function
for each number. The ask function returns a set of probabilities for each number, i.e.
to what degree does it think that on the image provided is 0,1,... 9. Of course,
the best result would be the probability of 1 for the number on the image and 0 for the
rest, which is never the case. But still - the higher the probability for the number
on the image, and the lower the probabilities of the rest, the better. For this quantification
of the accuracy 3 methods are used, explained below. After evaluating the accuracy it prints
it out(for each accuracy evaluation method) and increases the parameters further, makes new brain,
estimates again the accuracies etc.

Evaluation methods
------------------
The overall accuracy of the brain is then just a sum of the particular accuracy for
all number from 0 to 9. There are used three different methods to determine the particular
accuracy for a given result(list of probabilities) and a number(which was on the image):
- simple: gives just the probability of the given number. So if I have a image carrying
number 3 and for 3 the probability was 0.5, returns 0.5.
- first match: returns 1 if the number had the highest probability, else 0. So having
an image with 4, if the 4 has the highest probability(value does not matter), it returns
1. If however the number 3 had higher probability on this image, returns 0.
- relative: there is one big drawback on the previous 2 methods. If you get on image with
4 probability 0.8, it's quite good. But what if 3 had the probability of 0.79? Then a small
change might make the 3 the most probable, meaning it did not really distinguish 4 from 3. 
So what matters is not only if the number has the highest probability and how high it is, 
but also how is it in relation to the other numbers. To quantify this, I just compute the difference between
the actual probability of the given number and the highest probability of not-on-the-image numbers.
Example: I have image with
number 4, for 4 is the probability highest having value 0.8. The second highest might be e.g.
3 with prob. 0.2. Then this relative accuracy is 0.6, which is quite good. If the 3 had probability 0.79, then
the result would be just 0.01. What if the given number doesnt have the highest probability?
Then the result is negative! For having an image 4 on which 4 would have 0.5, but there would
be the highest probability for 3 of 0.65, then the result would be -0.15. And of course,
this might lead to negative overall accuracy for really bad networks.

Note that what is important here is not really the accuracy itself, but the change in it. There
is no sense in comparing these accuracies for a given neural network, but there is a sense - and
actually the motivation of this project - to see the CHANGE in a PARTICULAR accuracy when changing
some parameters.


How to use it
---------------
The commandline arguments are as follows(syntax):
-p PARAM -l DOUBLE/INT -h DOUBLE/INT -s DOUBLE/INT
where PARAM might be hidden or larate.

Example:
-p hidden -l 10 -h 30 -s 5
would be changing the parameter hidden from 10 to 30 by 5 (meaning 10,15,20,25,30)
-p lrate -l 0,1 -h 0,3 -s 0,1 // take care! you might have to use . instead of , as separator
would be changing the parameter learning rate from 0.1 to 0.3 by 0.01(meaning 0.1, 0.2, 0.3)

after each incrementation, the program writes the result of the accuracies based on the 3 methods.

Requirements
-------------
Everything already included in the jar.

Required paths
--------------
in the directory from where analyze.jar is run, must be folder Trainset with sub-directories corresponding to each number
an inside these there must be at least one image file for that number
and there must be folder Evalset in which directly the number images must be, from 0 to 9,
having format (number)_1.png, so i.e. 0_1.png, 1_1.png, 2_1.png, etc.

This is now hardcoded because I dont want to have milion command line arguments are I have no time
now to write it more nicely now so that you could specify them and if unspecified then use these
defaults. Possible todo.


Relation to NeuroNumber
-----------------------
NeuroAnalytics uses the skeleton of NeuroNumber, mainly the BrainFactory. However,
there are many other functions, some specific for NeuroNumber were deleted and some extended, so it's quite
independent and for that reason it's a new project on its own. The purpose is different
as well - not to train one network and save it or use saved network to recognize a file,
but to analyze how the change in network parameters influence the accuracy of a network.


Todo program
---------------
possibly it could be extended in these ways:
- parametrize the number of hidden layers(now there is just one).
- parametrize other things in backpropagation(now just learningrate, we could
modify also maxerror and momentum, whatever this is).
- parametrize of paths to Trainset and Evalset
- think out more methods of accuracy evaluation
- modify the output so that it looks like a table where columns would
represent the accuracy method and rows would correspond to new accuracies
for modifying parameter value.

Toto general
------------
- create more images so that each number has 4 in training set, now
the count if different so some images are recognized easier.
- train it and write down the results in excel, product graphs
- make a document explaining what we have used, what we have
programmed and how it works
- make a presentation
- get drunk