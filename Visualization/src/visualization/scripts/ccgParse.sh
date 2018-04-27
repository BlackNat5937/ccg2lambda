#!/usr/bin/env bash
!# /bin/bash

CCG2LAMBDA_LOCATION=$1
CANDC_LOCATION=$(cat $CCG2LAMBDA_LOCATION/en/candc_location.txt)

$CANDC_LOCATION/bin/candc --models $CANDC_LOCATION/models --candc-printer xml --input $CCG2LAMBDA_LOCATION/sentences.tok > $CCG2LAMBDA_LOCATION/sentences.candc.xml