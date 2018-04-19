#!/usr/bin/env bash
!# /bin/bash

CCG2LAMBDA_LOCATION=$1

source py3/bin/activate

python $CCG2LAMBDA_LOCATION/en/candc2transccg.py $CCG2LAMBDA_LOCATION/sentences.candc.xml > $CCG2LAMBDA_LOCATION/sentences.xml


python $CCG2LAMBDA_LOCATION/scripts/semparse.py $CCG2LAMBDA_LOCATION/sentences.xml $CCG2LAMBDA_LOCATION/en/semantic_templates_en_emnlp2015.yaml $CCG2LAMBDA_LOCATION/sentences.sem.xml


python $CCG2LAMBDA_LOCATION/scripts/prove.py $CCG2LAMBDA_LOCATION/sentences.sem.xml --graph_out graphdebug.html
