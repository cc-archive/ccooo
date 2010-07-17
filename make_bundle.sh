#!/bin/bash

IFS=$'\n'
for NAME in $((find . -name "*.po") | cut -d / -f 3)
do
  msgfmt --java2 -d resources -r Messages -l $NAME i18n/$NAME/cc_org.po	
  #echo "Created translation file for : \"$NAME\""
done

#(find . -name "*.po") | grep -A 0 'si' | cut -c1-21
