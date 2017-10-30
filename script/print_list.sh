#!/bin/bash -x
title=$1
list=$2
echo $title | lpr -o cpi=7 -o lpi=3
echo $list | lpr