/**
 *
 * Copyright 2016 Xiaofei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package xiaofei.library.hermes.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Xiaofei on March 31, 2016.
 */
@Target(ElementType.TYPE)//@Target 表示注解类型所适用的程序元素的种类   ElementType 程序元素类型，用于Target注解类型
@Retention(RetentionPolicy.RUNTIME)//@Retention 表示注解类型的存活时长    RetentionPolicy 注解保留策略，用于Retention注解类型
public @interface ClassId {
    String value();
}
