#!/usr/bin/env bash
!# /bin/bash


virtualenv --no-site-packages --distribute -p /usr/bin/python3 py3

source py3/bin/activate

pip install lxml simplejson pyyaml -I nltk==3.0.5

pip install numpy

pip install chainer==1.23

python -c "import nltk; nltk.download('wordnet')"