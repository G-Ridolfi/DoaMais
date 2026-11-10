#!/usr/bin/env bash
# ==========================================================
#  DoaMais - compila e executa (Linux / macOS)
#  Uso:
#    ./build.sh                                   compila e sobe o servidor (com.doamais.Main)
#    ./build.sh compilar                          apenas compila
#    ./build.sh com.doamais.teste.DatabaseTeste   compila e roda a classe informada
#  Requer JDK 17 ou superior.
# ==========================================================
set -e
cd "$(dirname "$0")"
mkdir -p out
find src -name "*.java" > out/sources.txt
if ! javac -encoding UTF-8 -d out -cp "lib/*" @out/sources.txt; then
  echo; echo "*** ERRO DE COMPILACAO ***"; exit 1
fi
echo "Compilacao OK."
[ "$1" = "compilar" ] && exit 0
java -cp "out:lib/*" "${1:-com.doamais.Main}"
