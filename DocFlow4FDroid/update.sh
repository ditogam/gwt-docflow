#!/bin/bash
PATH=$PATH:../ant/bin
ANT_HOME=../ant


svn checkout  https://gwt-common-library.googlecode.com/svn/trunk/CommonLibray
svn checkout  https://gwt-common-library.googlecode.com/svn/trunk/SocarGassWS
svn checkout https://gwt-docflow.googlecode.com/svn/trunk/DocFlow4FDroid



LC_CTYPE=en_US.UTF-8
LC_ALL=C
export LC_CTYPE
export LC_ALL


export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8

locale
rm -r -f DocFlow/war/WEB-INF/classes
echo 'updating Comm'
#svn update  CommonLibray
echo 'updating DocFlow'
#svn update DocFlow
#cd DocFlow
#ant -buildfile build_docflow.xml
#cd ..