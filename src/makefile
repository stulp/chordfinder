BIN_DIR = ./
SOURCES := $(shell find ./ -name "*.java")
TARGETS += $(subst .java,.class,${SOURCES})
TARGETS := $(addprefix ${BIN_DIR}/,${TARGETS})

VERSION = 11

all: ${BIN_DIR} ${TARGETS}

docs: ${SOURCE}
	doxygen doxygen.config
	
run:${TARGETS}
	java ChordFinderApplication

jar: ${TARGETS}
	jar cfe ChordFinderApplication.jar ChordFinderApplication  *.class

clean:
	rm -f ${BIN_DIR}/*.class
	rm -f ${BIN_DIR}/*.jar
	rm -rf ../doxygen-docs

${BIN_DIR}/%.class: %.java
	 javac -d ${BIN_DIR} -source ${VERSION} -target ${VERSION} -Xlint $<

${BIN_DIR}:
	mkdir ${BIN_DIR}