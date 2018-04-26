#!/usr/bin/env bash
!# /bin/bash

CCG2LAMBDA_LOCATION=$1

source py3/bin/activate

./en/rte_en.sh sentences.txt en/semantic_templates_en_event.yaml