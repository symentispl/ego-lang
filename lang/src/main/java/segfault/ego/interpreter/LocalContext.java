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
package segfault.ego.interpreter;

import java.util.HashMap;
import java.util.Map;

import segfault.ego.symbols.Symbol;

public class LocalContext implements Context{

    private final Context parentContext;
    private final Map<Symbol,Object> context = new HashMap<>();

    public LocalContext(Context parentContext) {
        this.parentContext = parentContext;
    }

	@Override
	public void set(Symbol symbol, Object object) {
		context.put(symbol, object);
	}

	@Override
	public Object get(Symbol symbol) {
        Object object = context.get(symbol);
        if(object == null && parentContext!=null){
            return parentContext.get(symbol);
        }
		return object;
	}


    @Override
    public Object get(String name){
        throw new UnsupportedOperationException("");
    }
}