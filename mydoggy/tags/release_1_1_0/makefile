#----------------------------------
# TransparencyManager dll makefile
#----------------------------------

CC=gcc

JAVA_HOME=E:/Programmi/Java/jdk1.5.0_09
JAVA_INCLUDE=-I${JAVA_HOME}/include -I${JAVA_HOME}/include/win32
JAVA_LIB=-L${JAVA_HOME}/lib

PARAMS=-Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared
INCLUDE=${JAVA_INCLUDE}
LIB_DIRS=${JAVA_LIB}
SOURCES=./mydoggy-plaf/src/main/cpp/win32/TransparencyManager.c
LIBS=-luser32 -ljawt
OUTPUT=.\lib\win32\TransparencyManager.dll

all: clean
	$(CC) ${PARAMS} ${INCLUDE} ${LIB_DIRS} ${SOURCES} ${LIBS} -o ${OUTPUT}

clean:
	del ${OUTPUT}
