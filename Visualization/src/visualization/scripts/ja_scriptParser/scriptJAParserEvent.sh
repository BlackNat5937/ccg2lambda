#!/usr/bin/env bash
!# /bin/bash

CCG2LAMBDA_LOCATION=$1

source py3/bin/activate

cd $1

./ja/rte_ja_mp.sh sentences.txt ja/semantic_templates_ja_event.yaml