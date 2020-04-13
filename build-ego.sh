#!/usr/bin/env bash

set -eux

mkdir -p target/classes

sources=$(find src/main/java -iname "*java" -type f | tr "\n" " ")

# shellcheck disable=SC2068
javac -d out --enable-preview -source 14 -Xlint:preview ${sources[@]}

java --enable-preview -ea -cp target/classes segfault.ego.lexer.Lexer

java --enable-preview -ea -cp target/classes segfault.ego.parser.Parser

jar cvf target/ego-lang.jar -C target/classes .
