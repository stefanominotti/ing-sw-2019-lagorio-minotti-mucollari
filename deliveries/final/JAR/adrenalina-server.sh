DIR="$(dirname "$(which "$0")")"
JAR="$DIR/adrenalina-server.jar"
java -jar -Dfile.encoding=UTF8 "$JAR"