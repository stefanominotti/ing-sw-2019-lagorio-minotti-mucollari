DIR="$(dirname "$(which "$0")")"
JAR="$DIR/adrenalina-client.jar"
java -jar -Dfile.encoding=UTF8 "$JAR"