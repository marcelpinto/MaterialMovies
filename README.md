# Material Movies example
Simple project to show a list of movies and the details of it using
the new AndroidX libraries and the material components.

The intention of the project is to play around and try some new concepts.

To run the sample you must add in your local properties an API Key from
https://www.themoviedb.org/documentation/api

```
api.movies.key="ADD_YOUR_KEY"
```

## Architecture
Uses the Android Architecture components to create a MVVMish architecture.

### Main "Flow"
The main idea is that the MainActivity contains the main view and
attaches itself to the state changes from the MainViewModel.

The role of the activity is to decide how to build the main view
de/attaching the fragments based on the MainState.

The MainState is defined by the MainInteractor, this one is kept
in MainActivity scope (like the MainViewModel). And contains the
"flow" logic of the application

### Screens
The rest of the fragment behaves independently and communicate
state actions to the MainInteractor via interfaces.

All the fragments follows a similar pattern.
- MediatorLiveData adds the data sources needed to create the view
- When any data changes a single update method is called. This method
contains the logic to convert the data to the ViewData that the view understands
- Handles user interactions and forwards the action to the MainInteractor to change the state

### Repository
The repository follows an observer pattern using LiveData mechanism
that tries to fetch from Mem cache or fallbacks to network to then
store again in Mem Cache. The returned LiveData is observing the Mem
Cache changes and updating the value.

To avoid multiple calls on the same time, when the request is the
same the "request task" is kept and shared with the callers.

## Testing
The testing strategy for this project is to use two kinds of testing
- Unit Test: will cover the VM and the domain logic
- Integration test: will test the integration of the full app without the
view and using a MockWebServer to avoid hitting the network.

Because of the architecture setup we can actually test the full
"flow" of the app without the views and using the JVM instead
of running in a device. This of course does not cover the UI
test but it verifies that the full setup of the app is working as
expected, making the test faster and reliable.

See MainFlowTest

## License
```
Copyright 2018 Marcel Pintó Biescas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
