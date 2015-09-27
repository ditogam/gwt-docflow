copy gwt_endorsed.sh to bin



edit run.sh file like this
# Setup the java endorsed dirs
JBOSS_ENDORSED_DIRS="$JBOSS_HOME/lib/endorsed"                                                                                                                                                                        
. $JBOSS_HOME/bin/gwt_endorsed.sh