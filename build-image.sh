set -e -u -o pipefail

VERSION=1.0.0-SNAPSHOT

if [ $# -ge 1 ]; then
  VERSION=$1
fi

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

(cd "${CURRENT_DIR}"; mvn clean package)

JAR_FILE_COUNT=$(find "${CURRENT_DIR}/target/" -maxdepth 1 -name '*.jar' | wc -l)

if [ "$JAR_FILE_COUNT" == 0  ]; then
    echo "@@@@@@@@@@@ no jar file found in directory:${CURRENT_DIR}/target build failed.....@@@@@@@@@@@@@@@"
    ls -l "${CURRENT_DIR}"/target/
    exit 1
fi

#获取jar包，反解
cd "${CURRENT_DIR}"/target/ && JAR_FILE_NAME=$(ls *.jar|grep -v source)
echo ${JAR_FILE_NAME}

java -Djarmode=layertools -jar ${JAR_FILE_NAME} extract


(cd "${CURRENT_DIR}"; DOCKER_BUILDKIT=1 docker build . -t pt-auto-attendance:${VERSION})
