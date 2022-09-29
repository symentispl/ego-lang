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
import segfault.ego.symbols.Symbol;
import segfault.ego.symbols.SymbolTable;

public class GlobalScope implements Scope {
    private final BuiltInScope builtInScope;
    private final SymbolTable symbolTable = new SymbolTable();

    public GlobalScope(BuiltInScope builtInScope) {
        Objects.requireNonNull(builtInScope);
        this.builtInScope = builtInScope;
    }

    @Override
    public Symbol resolve(String name) {
        var symbol = symbolTable.get(name);
        if (symbol == null) {
            return builtInScope.resolve(name);
        }
        return symbol;
    }

    @Override
    public void define(Symbol symbol) {
        symbolTable.add(symbol);
    }

    @Override
    public Scope getEnclosingScope() {
        return builtInScope;
    }

    public Object get(String atom) {
        return symbolTable.get(atom);
    }
}
