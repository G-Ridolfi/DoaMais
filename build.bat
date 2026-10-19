@echo off
REM Compila todo o projeto e executa a classe Main (quando existir).
REM Uso: build.bat          -> compila e roda com.doamais.Main
REM      build.bat teste    -> compila e roda o teste do banco
if not exist out mkdir out
dir /s /b src\*.java > out\sources.txt
javac -encoding UTF-8 -d out -cp "lib/*" @out\sources.txt || exit /b 1
if "%1"=="teste" (
  java -cp "out;lib/*" com.doamais.db.DatabaseTeste
) else (
  java -cp "out;lib/*" com.doamais.Main
)
