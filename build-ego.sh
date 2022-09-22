#!/usr/bin/env bash
#
# Copyright © 2020 Segfault (wiktor@segfault.events,jarek@segfault.events)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


set -eux

target_classes="target/classes"

mkdir -p "$target_classes"

sources=$(find src/main/java -iname "*java" -type f | tr "\n" " ")

# shellcheck disable=SC2068
javac -d "$target_classes" --enable-preview -source 14 -Xlint:preview ${sources[@]}

java --enable-preview -ea -cp "$target_classes" segfault.ego.lexer.Lexer

jar cvf target/ego-lang.jar -C "$target_classes" .
