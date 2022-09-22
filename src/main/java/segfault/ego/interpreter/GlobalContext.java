/**
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
package segfault.ego.interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import segfault.ego.parser.GlobalScope;
import segfault.ego.symbols.Symbol;
import segfault.ego.types.EgoObject;
import segfault.ego.types.None;

public class GlobalContext implements Context{

    public static GlobalContextBuilder create( GlobalScope globalScope) {
        return new GlobalContextBuilder(globalScope);
    }

    public static GlobalContext defaulGlobalContext(GlobalScope builtInScope) {
        Function<List<Object>, Object> printFunction = (List<Object> s) -> {
            System.out.println(s.stream().map(Object::toString).collect(Collectors.joining(" ")));
            return None.none;
        };
        Function<List<Object>, Object> greetFunction = (List<Object> s) -> {
            return "Hello "+s.get(0).toString();
        };
        Function<List<Object>, Object> getFunction = (List<Object> s) -> {
            return ((EgoObject)s.get(1)).get(s.get(0).toString());
        };
        Function<List<Object>, Object> gtFunction = (List<Object> s) -> {
            return ((Number)s.get(0)).intValue() > ((Number)s.get(1)).intValue(); 
        };
        Function<List<Object>, Object> ifFunction = (List<Object> s) -> {
            return ((Boolean)s.get(0))?s.get(1):s.get(2); 
        };
        Function<List<Object>, Object> concatFunction = (List<Object> s) -> {
            return s.get(0).toString() + s.get(1).toString(); 
        };


        return create(builtInScope)
                .set("print", printFunction)
                // .set("greet", greetFunction)
                .set("get", getFunction )
                .set("gt", gtFunction)
                .set("if",ifFunction)
                .set("+",concatFunction)
                .build();
    }

    static class GlobalContextBuilder {

        private final GlobalScope globalScope;
        private final Map<Symbol, Object> context = new HashMap<>();

        private GlobalContextBuilder( GlobalScope globalScope) {
            this.globalScope = globalScope;
        }

        GlobalContextBuilder set(String name, Object object) {
            var symbol = globalScope.resolve( name);
            if (symbol == null) {
                throw new UnsupportedOperationException("unresolved variable " + name);
            }
            context.put(symbol, object);
            return this;
        }

        GlobalContext build() {
            return new GlobalContext( globalScope, context);
        }

    }

    private final GlobalScope scope;
    private final Map<Symbol,Object> context;

    private GlobalContext( GlobalScope scope, Map<Symbol, Object> context) {
        this.scope = scope;
        this.context = context;
    }

    @Override
    public void set( Symbol symbol, Object object ) {
            context.put( symbol, object );
    }

    @Override
    public Object get( Symbol symbol ) {
        return context.get( symbol );
    }

    public Object get(String name){
        return context.keySet().stream().filter( symbole -> symbole.name().equals( name ) ).map( context::get ).findFirst().get();
    }

}