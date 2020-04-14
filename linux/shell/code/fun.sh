#!/bin/bash

function sum()
{
  s=0
  s=$[$1+$2]
  echo $s
  return $1
}

sum 3 5 
echo "a"

