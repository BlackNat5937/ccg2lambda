#!/usr/bin/env bash
!# /bin/bash

CCG2LAMBDA_LOCATION=$1

source py3/bin/activate


cd $1
$1/en/rte_en.sh sentences.txt $1/en/semantic_templates_en_event.yaml