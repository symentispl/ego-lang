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
package segfault.ego.types;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents Ego object. Type of Ego object is represented as set of pairs
 * (name, type).
 */
public record EgoObjectType(List<Member> members) implements Type {

    @Override
    public String getTypeName() {
        return "Object";
    }
}