#!/bin/bash -x
title=$1
list=$2
echo -e $title > /dev/serial0
echo -e $list > /dev/serial0