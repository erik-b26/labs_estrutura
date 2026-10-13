#!/usr/bin/env sh
# REBOOT - seleciona o Java do laboratorio apenas para esta execucao.
# Evita "Unrecognized option: -m" quando o PATH aponta para um Java antigo.
if [ -x /usr/lib/jvm/java-21-openjdk-amd64/bin/java ]; then
  export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
  export PATH="$JAVA_HOME/bin:$PATH"
fi

cd "$(dirname "$0")" || exit 2
if ! command -v java >/dev/null 2>&1; then
  echo "Java nao encontrado. Consulte AJUDA_JAVA_LINUX.txt ou avise o professor."
  exit 2
fi

# Confira o compilador antes de usar -m: um Java antigo nao aceita essa opcao.
if ! java --list-modules 2>/dev/null | grep -q '^jdk.compiler@'; then
  echo "Nao foi encontrado um compilador adequado no Java selecionado."
  java -version 2>&1
  echo "Use um JDK 17 ou superior. Consulte AJUDA_JAVA_LINUX.txt."
  echo "Se existir Java 21 no caminho do laboratorio, execute:"
  echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64'
  echo 'export PATH="$JAVA_HOME/bin:$PATH"'
  echo 'sh testar.sh'
  echo "O relatorio anterior nao foi atualizado."
  exit 2
fi

echo "Java utilizado:"
java -version 2>&1
mkdir -p build || exit 2
rm -f build/*.class
# Usa o compilador do mesmo Java que executara os testes, evitando misturas.
java -m jdk.compiler/com.sun.tools.javac.Main -encoding UTF-8 -d build Inventario.java FilaCircular.java Escalonador.java Testes.java
if [ "$?" -ne 0 ]; then
  echo "Falha na compilacao. Corrija o erro acima. O relatorio anterior nao foi atualizado."
  exit 2
fi
java -cp build Testes "$@"
