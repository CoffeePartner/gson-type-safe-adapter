# gson-type-safe-adapter
A type safe gson adapter.

## Introduction

This library can help mismatch input types keep correct default value, never null.
But keep in mind, input JSON should still be legal format.

It doesn't change the Gson original code, just append a TypeAdapterFactory.

### Safe Types

| Type | Default Value |
| --- | --- |
| String | "" |
| byte / Byte | (byte)0 |
| short / Short | (short)0 |
| int / Integer | 0 |
| long / Long | 0L |
| float / Float | 0f |
| double / Double | 0.0 |
| boolen / Boolean | false |
| char / Character | (char)0 |
| Collection | [] |
| Map | {} |
| Array / GenericArray | [] |


More details: see [TypeSafeTest](src/test/java/com/dieyidezui/gson/adapter/TypeSafeTest.java)

## Usage

### Step 1

```groovy
implementation 'com.dieyidezui.gson:type-safe-adapter:1.0.1'
```

### Step 2

```java
 Gson safeGson = new GsonBuilder()
                .registerTypeAdapterFactory(TypeSafeAdapterFactory.newInstance())
                .create();
```

## Caution


1. ```Gson.fromJson(String, Type)``` will check input String.
   If null, null returned immediately.

2. The minimal supported gson version is `2.2.2`.

3. The theory of safe depends Gson's implementation,
   So it may lead to some imcompatible case if Gson changed specific code in future version.

## License

Copyright (c) 2018-present, dieyidezui.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
