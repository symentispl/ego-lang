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

import java.util.List;
import segfault.ego.symbols.FunctionSymbol;
import segfault.ego.symbols.ParameterSymbol;
import segfault.ego.symbols.Symbol;
import segfault.ego.symbols.SymbolTable;
import segfault.ego.symbols.TypeSymbol;
import segfault.ego.types.EgoObject;
import segfault.ego.types.FunctionType;
import segfault.ego.types.None;

public class BuiltInScope implements Scope {

    private final SymbolTable symbolTable = new SymbolTable();

    public BuiltInScope() {
        symbolTable.add(new TypeSymbol("String", String.class));
        symbolTable.add(new FunctionSymbol(
                "print", new FunctionType(List.of(new ParameterSymbol("str", String.class)), None.class)));
        // symbolTable.add(new FunctionSymbol("greet", new FunctionType( List.of(
        // String.class), String.class)));
        symbolTable.add(new FunctionSymbol(
                "+",
                new FunctionType(
                        List.of(new ParameterSymbol("op1", String.class), new ParameterSymbol("op2", String.class)),
                        String.class)));
        symbolTable.add(new FunctionSymbol(
                "get",
                new FunctionType(
                        List.of(
                                new ParameterSymbol("property", String.class),
                                new ParameterSymbol("obj", EgoObject.class)),
                        Object.class)));
        symbolTable.add(new FunctionSymbol(
                "gt",
                new FunctionType(
                        List.of(new ParameterSymbol("op1", Number.class), new ParameterSymbol("op2", Number.class)),
                        Boolean.class)));
        symbolTable.add(new FunctionSymbol(
                "if",
                new FunctionType(
                        List.of(
                                new ParameterSymbol("predicate", Boolean.class),
                                new ParameterSymbol("ifThen", Object.class),
                                new ParameterSymbol("else", Object.class)),
                        Object.class)));
    }

    @Override
    public Scope getEnclosingScope() {
        return null;
    }

    @Override
    public Symbol resolve(String name) {
        return symbolTable.get(name);
    }

    @Override
    public void define(Symbol symbol) {
        throw new UnsupportedOperationException("built-in scope is read-only");
    }
}
