GWT_LIBS=$JBOSS_ENDORSED_DIRS/gwt_lib
GWT_ENDORSED_DIRS=""

get_abs_filename() {
	# $1 : relative filename
	echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}
for file in $GWT_LIBS/* ; do
	DIR=$(get_abs_filename $file)
	GWT_ENDORSED_DIRS="$GWT_ENDORSED_DIRS:$DIR";
done
JBOSS_ENDORSED_DIRS=$JBOSS_ENDORSED_DIRS$GWT_ENDORSED_DIRS