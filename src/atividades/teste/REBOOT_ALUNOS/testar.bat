@echo off
cd /d "%~dp0"
if not exist build mkdir build
del /q build\*.class 2>nul
where javac >nul 2>nul
if errorlevel 1 (
  java -m jdk.compiler/com.sun.tools.javac.Main -encoding UTF-8 -d build Inventario.java FilaCircular.java Escalonador.java Testes.java
) else (
  javac -encoding UTF-8 -d build Inventario.java FilaCircular.java Escalonador.java Testes.java
)
if errorlevel 1 (
  echo Falha na compilacao. O relatorio anterior nao foi atualizado.
  pause
  exit /b 2
)
java -cp build Testes %*
set "resultado=%errorlevel%"
pause
exit /b %resultado%
