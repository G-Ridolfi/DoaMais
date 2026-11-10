@echo off
REM ==========================================================
REM  DoaMais - compila e executa (Windows)
REM  Uso:
REM    build.bat                                   compila e sobe o servidor (com.doamais.Main)
REM    build.bat compilar                          apenas compila
REM    build.bat com.doamais.teste.DatabaseTeste   compila e roda a classe informada
REM  Requer JDK 17 ou superior.
REM ==========================================================
setlocal
if not exist out mkdir out
dir /s /b src\*.java > out\sources.txt
javac -encoding UTF-8 -d out -cp "lib/*" @out\sources.txt
if errorlevel 1 (
  echo.
  echo *** ERRO DE COMPILACAO ***
  exit /b 1
)
echo Compilacao OK.
if "%~1"=="compilar" exit /b 0
set CLASSE=%~1
if "%CLASSE%"=="" set CLASSE=com.doamais.Main
java -cp "out;lib/*" %CLASSE%
