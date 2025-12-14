# Використовуємо зворотні слеші для шляхів Windows, хоча Java розуміє і прямі
SRC_MAIN := src\main\java
SRC_TEST := src\test\java
RES_TEST := src\test\resources

OUT_DIR := out
OUT_MAIN := $(OUT_DIR)\main
OUT_TEST := $(OUT_DIR)\test

LIB_DIR := lib
# У Windows розділювач у classpath — це крапка з комою (;), а не двокрапка (:)
JUNIT := $(LIB_DIR)\junit-platform-console-standalone-6.0.1.jar
CLASSPATH_MAIN := $(OUT_MAIN);$(JUNIT)

.PHONY: all clean test compile compile-main compile-test help deps

help:
	@echo.
	@echo Available targets:
	@echo   help             Show this help message
	@echo   deps             Download dependencies (JUnit)
	@echo   compile          Compile main and test sources
	@echo   compile-main     Compile only main sources
	@echo   compile-test     Compile only test sources
	@echo   test             Run JUnit tests
	@echo   clean            Remove build output
	@echo.

all: compile

compile: deps compile-main compile-test

compile-main:
	@echo == Compiling main sources ==
	@if not exist "$(OUT_MAIN)" mkdir "$(OUT_MAIN)"
	@REM 
	dir /s /B "$(SRC_MAIN)\*.java" > sources_main.txt
	javac -cp "$(JUNIT)" -d "$(OUT_MAIN)" @sources_main.txt
	@del sources_main.txt

compile-test: compile-main
	@echo == Compiling test sources ==
	@if not exist "$(OUT_TEST)" mkdir "$(OUT_TEST)"
	dir /s /B "$(SRC_TEST)\*.java" > sources_test.txt
	javac -cp "$(CLASSPATH_MAIN)" -d "$(OUT_TEST)" @sources_test.txt
	@del sources_test.txt
	@echo == Copying test resources ==
	@if exist "$(RES_TEST)" xcopy /s /e /y /i "$(RES_TEST)" "$(OUT_TEST)" > NUL

test: compile
	@echo == Running JUnit tests ==
	java -jar "$(JUNIT)" execute --classpath "$(OUT_MAIN);$(OUT_TEST)" --scan-classpath

clean:
	@if exist "$(OUT_DIR)" rmdir /s /q "$(OUT_DIR)"

deps: $(JUNIT)

$(JUNIT): | $(LIB_DIR)
	@echo Downloading JUnit...
	@REM curl є у Windows 10/11 за замовчуванням
	curl -L "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.0.1/junit-platform-console-standalone-6.0.1.jar" -o "$(JUNIT)"

$(LIB_DIR):
	@if not exist "$(LIB_DIR)" mkdir "$(LIB_DIR)"