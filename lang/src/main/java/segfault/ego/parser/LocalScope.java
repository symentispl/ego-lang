/*
 * Copyright © 2020 Segfault (wiktor@segfault.events,jarek@segfault.events)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package segfault.ego.parser;

import java.util.Objects;
import segfault.ego.symbols.*;

public class LocalScope implements Scope {

    private final SymbolTable symbolTable = new SymbolTable();
    private final Scope enclosingScope;

    public LocalScope(Scope enclosingScope) {
        Objects.requireNonNull(enclosingScope);
        this.enclosingScope = enclosingScope;
    }

    public Symbol resolve(String name) {
        Symbol symbol = symbolTable.get(name);
        if (symbol == null && enclosingScope != null) {
            return enclosingScope.resolve(name);
        }
        return symbol;
    }

    public void define(Symbol symbol) {
        symbolTable.add(symbol);
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }
}
