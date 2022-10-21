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
package segfault.ego.todot;

import segfault.ego.parser.AtomLiteral;
import segfault.ego.parser.EgoAlg;
import segfault.ego.parser.FunCall;
import segfault.ego.parser.ListLiteral;
import segfault.ego.parser.NumberLiteral;
import segfault.ego.parser.StringLiteral;

public class ToDOTAlg implements EgoAlg {
    @Override
    public Object funCall(FunCall funCall) {
        return null;
    }

    @Override
    public Object listLiteral(ListLiteral literal) {
        return null;
    }

    @Override
    public Object atomLiteral(AtomLiteral a) {
        return null;
    }

    @Override
    public Object numberLiteral(NumberLiteral n) {
        return null;
    }

    @Override
    public Object stringLiteral(StringLiteral s) {
        return null;
    }

    public Object eval() {
        return null;
    }
}
