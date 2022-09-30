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

import segfault.ego.symbols.FunctionSymbol;
import segfault.ego.symbols.ParameterSymbol;
import segfault.ego.symbols.Symbol;
import segfault.ego.symbols.SymbolTable;
import segfault.ego.types.FunctionType;
import segfault.ego.types.None;

import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;

import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodHandles.filterArguments;
import static java.lang.invoke.MethodHandles.filterReturnValue;
import static java.lang.invoke.MethodType.methodType;

public class BuiltInScope implements Scope {

    private final SymbolTable symbolTable = new SymbolTable();

    public BuiltInScope() {
        var lookup = MethodHandles.lookup();
        try {
            symbolTable.add(new FunctionSymbol(
                    "print",
                    new FunctionType(List.of(new ParameterSymbol("str", String.class)), None.class),
                    printFunctionMethodHandler(lookup)));

            symbolTable.add(new FunctionSymbol(
                    "+",
                    new FunctionType(
                            List.of(new ParameterSymbol("n0", Number.class), new ParameterSymbol("n1", Number.class)),
                            Number.class),
                    lookup.findStatic(
                            BuiltInScope.class, "plus", methodType(Number.class, Number.class, Number.class))));

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        ;
    }

    /*
     * print builtin function
     * what happens is, we take System.out.println(String);void and we adapt it to print(Object);None function in
     * Ego
     */
    private static MethodHandle printFunctionMethodHandler(MethodHandles.Lookup lookup)
            throws NoSuchMethodException, IllegalAccessException {
        var methodHandle = lookup.findVirtual(PrintStream.class, "println", methodType(Void.TYPE, String.class));
        methodHandle = methodHandle.bindTo(System.out);
        methodHandle = filterReturnValue(methodHandle, constant(None.class, None.none));
        methodHandle = filterArguments(
                methodHandle, 0, lookup.findVirtual(Object.class, "toString", methodType(String.class)));
        return methodHandle;
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

    public static Number plus(Number n1, Number n2) {
        return n1.longValue() + n2.longValue();
    }
}
