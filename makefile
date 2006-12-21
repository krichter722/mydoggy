CC=gcc

JAVA_HOME="E:/Programmi/Java/jdk1.5.0_09"
JAVA_INCLUDE=-I${JAVA_HOME}/include -I${JAVA_HOME}/include/win32
JAVA_LIB=-L${JAVA_HOME}/lib

MATLAB_HOME="E:/Programmi/MATLAB/R2006b"
MATLAB_INCLUDE=-I${JAVA_HOME}/extern/include

INCLUDE=${JAVA_INCLUDE} ${MATLAB_INCLUDE}

SOURCES=./mydoggy-plaf/src/main/cpp/win32/TransparencyManager.c
LIBS=-luser32 -ljawt
OUTPUT=./lib/win32/TransparencyManager.dll

PARAMS=-Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared

all:
	@echo "Hello World!!!"
	$(CC) ${PARAMS} ${INCLUDE} ${JAVA_LIB} ${SOURCES} ${LIBS} -o ${OUTPUT}
