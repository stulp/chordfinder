BIN_DIR = ./
SOURCES := $(shell find ./ -name "*.java")
TARGETS += $(subst .java,.class,${SOURCES})
TARGETS := $(addprefix ${BIN_DIR}/,${TARGETS})

VERSION = 11

all: ${BIN_DIR} ${TARGETS}

docs: ${SOURCE}
	doxygen doxygen.config
	
run:
	java ChordFinderApplication

clean:
	rm -f ${BIN_DIR}/*.class
	rm -rf ../doxygen-docs

${BIN_DIR}/%.class: %.java
	 javac -d ${BIN_DIR} -source ${VERSION} -target ${VERSION} -Xlint $<

${BIN_DIR}:
	mkdir ${BIN_DIR}