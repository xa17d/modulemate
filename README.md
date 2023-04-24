# modulemate

Get things done faster with _modulemate_ - the ultimate command-line companion for multi-module Android projects!

## Installation

**Prerequisites:**

- MacOS or Linux
- Java 11 or higher installed

**Installation:**

Run in your _Terminal_:

```shell
./install.sh
```

Installation creates a Bash-Script called `m` that is copied to `/usr/local/bin/m` so you can use _modulemate_ by just
typing `m` in your _Terminal_.
The Bash-Script will link to this repository, so please don't move it.

## Usage

Use `m` in any folder with a multi-module Android project:

**Examples:**

```shell
m pr                # open Pull Request in browser.
m a                 # assemble main and test sources.
m conflictAnalysis  # show what other remote branches touch modules that are also worked on locally.
m help              # get an overview over all commands.
m                   # just start modulemate and use prompt mode.
…

# Filtering

m path/to/module-group/  # open modulemate in prompt mode and use `path/to/module-group/` as prefix filter.
m changedModules         # open modulemate in prompt mode and use only changed modules as filter.
…
```

Use `m help` to get a full list of commands.

If you provide a command shortcut directly at the start (like `m pr`), then _modulemate_ will run the command and exit
immediately.
If the command is not recognized, it is used as prefix filter and the prompt mode is started.
In prompt mode, you can use the same commands as when starting directly.
The main difference is that _modulemate_ will not exit after the command.

### Customize

To get the most out of _modulemate_, you have to customize it!

Configuration files can be located at:

- `{REPOSITORY_ROOT}/.modulemate/config.json`
  For repository specific configurations.
- `~/.modulemate/config.json`
  For configurations on user-level
- `{MODULEMATE_HOME}/.modulemate/defaultConfig.json`
  For built-in _modulemate_ default config.
  Only change this if you're contributing to _modulemate_'s default configuration.

All of those config files are combined at runtime.
The repository level configuration has the highest priority, the default configuration the lowest.

### Config files

```json
{
    "version": "1",
    "module": {
        // defines how modulemate identifies the type of module.
        // if the regex defined here is found in the module's `build.gradle`, it's classified as such.
        // if multiple config files define classifications, only the one config file with the highest priority will be taken into account:
        "classification": {
            "kotlinLib": "...",
            "androidLib": "...",
            "androidApp": "..."
        }
    },
    "commands": [
        // define all commands as dedicated object in this array.
        // commands of all config files are appended.
        {
            // shortcut is the short text you use to trigger the command in the modulemate CLI:
            "shortcut": "pr",
            // more comprehensive name of the command:
            "name": "Pull Request",
            "steps": [
                // define one or more steps for your command.
                // steps are executed sequentially.
                {
                    "type": "browser",
                    // you can use variables to make your command steps more dynamic.
                    // use `m help` to get a list of available variables.
                    "url": "https://{GIT_HOST}/{GIT_OWNER}/{GIT_REPOSITORY_NAME}/pull/{GIT_BRANCH_NAME}"
                }
            ]
        }
    ]
}
```

### Command Steps

All steps have a `type`.
Possible types are described here in subsections.

Also, all steps can define `runWhen` that determines if the step should still run if previous steps failed.
Possible values are:

* `PREVIOUS_SUCCESS`: Only run this step if all previous steps succeeded (this is the default).
* `PREVIOUS_FAILURE`: Only run this step if at least one previous step failed.
* `ALWAYS`: Always run this step, regardless of if a previous step failed or not.

#### Browser

Opens the default browser to a specified URL.

```json
{
    "type": "browser",
    "url": "https://{GIT_HOST}/{GIT_OWNER}/{GIT_REPOSITORY_NAME}/pull/{GIT_BRANCH_NAME}"
}
```

- `url`: The url to open. You may use variables.

#### Gradle

Runs a gradle command.

```json
{
    "type": "gradle",
    "flags": {
        "all": [
            "--no-configure-on-demand"
            …
        ],
        "kotlinLib": [
            …
        ],
        "android": [
            …
        ],
        "androidLib": [
            …
        ],
        "androidApp": [
            …
        ]
    },
    "tasks": {
        "all": [
            "assemble"
            …
        ],
        "kotlinLib": [
            …
        ],
        "android": [
            …
        ],
        "androidLib": [
            …
        ],
        "androidApp": [
            …
        ]
    }
}
```

- `flags` _(optional)_: Defines flags to add to the gradle command.
  Each distinct flag is only added once.
    - `all` _(optional)_: Flags defined here are added if there's a kotlinLib, androidLib or androidApp in the filtered modules. "other" modules are ignored.
    - `kotlinLib` _(optional)_: Flags to add if there is at least one kotlinLib in the filtered modules.
    - `android` _(optional)_: Flags to add if there is at least one androidLib or androidApp in the filtered modules.
    - `androidLib` _(optional)_: Flags to add if there is at least one androidLib in the filtered modules.
    - `androidApp` _(optional)_: Flags to add if there is at least one androidApp in the filtered modules.
- `tasks` _(optional)_: Defines tasks that should be run for each module in the filtered modules
    - `all` _(optional)_: Tasks that are run for all filtered modules, except "other" modules.
    - `kotlinLib` _(optional)_: Tasks that are run for all kotlinLibs in the filtered modules.
    - `android` _(optional)_: Tasks that are run for all androidLibs and androidApps in the filtered modules.
    - `androidLib` _(optional)_: Tasks that are run for all androidLibs in the filtered modules.
    - `androidApp` _(optional)_: Tasks that are run for all androidApps in the filtered modules.

The filtered modules, the `flags` and `tasks` are then compiled into a single gradle command and executed.
The gradle command could look like this, for example:

```shell
./gradlew -PandroidLibFlag1 -PandroidLibFlag2 -PandroidAppFlag1 -PandroidAppFlag2 -PkotlinLibFlag1 -PkotlinLibFlag2 :test-android:androidLibTask1 :test-android:androidLibTask2 :test-app:androidAppTask1 :test-app:androidAppTask2 :test-core:kotlinTask1 :test-core:kotlinTask2
```

#### Shell

Runs a shell command.

```json
{
    "type": "shell",
    "mode": "RUN_ONCE",
    "command": [
        "echo",
        "Hello, modulemate"
    ]
}
```

- `mode` _(optional)_: Defines the conditions that need to be met to execute the shell command.
  Possible values:
    - `RUN_ONCE`: Runs once.
    - `RUN_IF_AT_LEAST_ONE_ANDROID_MODULE`: Runs the command if at least one androidLib or androidApp in the filtered
      modules.
    - `RUN_IF_AT_LEAST_ONE_ANDROID_LIB_MODULE`: Runs the command if at least one androidLib in the filtered modules.
    - `RUN_IF_AT_LEAST_ONE_ANDROID_APP_MODULE`: Runs the command if at least one androidApp in the filtered modules.
    - `RUN_IF_AT_LEAST_ONE_KOTLIN_LIB_MODULE`: Runs the command if at least one kotlinLib in the filtered modules.
    - `RUN_IF_AT_LEAST_ONE_OTHER_MODULE`: Runs the command if at least one other module in the filtered modules.
- `command`: The shell command and it's arguments as array. You may use variables.

#### Report

Shows a report using the system's `open` command.

_Note: Only the report of the "active module" is shown (the one that has the latest file changes within the filtered
modules)._

```json
{
    "type": "report",
    "path": {
        "kotlinLib": [
            "{ACTIVE_MODULE_PATH}/build/reports/tests/test/index.html"
        ],
        "android": [
            "{ACTIVE_MODULE_PATH}/build/reports/testDebugReport/index.html"
        ]
    }
}
```

- `path`: Path to the report.
    - `all` _(optional)_: Path to the report that applies to all modules types, except "other" modules.
    - `kotlinLib` _(optional)_: Path to the report that applies to kotlinLibs.
    - `android` _(optional)_: Path to the report that applies to androidLibs and androidApps.
    - `androidLib` _(optional)_: Path to the report that applies to androidLibs.
    - `androidApp` _(optional)_: Path to the report that applies to androidApps.

_Note: The path for each module type must be unique!
If, for example, a path is provided for `android` and `androidLib`, reading the config file will fail!_

#### Others

There are a few other steps that have little relevance for customization.
See the complete list [here](modulemate/src/main/java/at/xa1/modulemate/config/Config.kt).

## Tech Stack

- Kotlin (JVM)
- Gradle

## Build

**Prerequisites:**

- MacOS or Linux
- Java 11 or higher installed

**Build:**

```shell
./build.sh
```

See [build.sh](build.sh) for detailed command.

**Run:**

```shell
java -jar modulemate/build/libs/modulemate.jar
```

or

```shell
m
```

...if installed via `./install.sh`

## License

Distributed under [BSD 3-Clause License](LICENSE). See [LICENSE](LICENSE) for more information.

## Contact

- Website: [xa1.at/modulemate](https://xa1.at/modulemate/)
- E-Mail: [support@xa1.at](mailto:support@xa1.at?subject=modulemate)

## Backlog

- [x] Installation script
- [x] Command for printing changed modules compared to the main branch
- [x] Show who's working on modules from remote active changes
- [x] Shell command depending on module type
- [ ] Shell command that runs for every module
