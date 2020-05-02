# Running the mod in a development environment
This will briefly explain how to run and debug the mod using IntelliJ IDEA.

* First, you have to configure IDEA to use its own build tool instead of Gradle to
run and debug the mod.
    * To do so, press Ctrl+Alt+S (or CMD+Alt+S) and navigate to 
    `Build, Execution, Deployment > Gradle`. Then set `Build and run using` to
    `IntelliJ IDEA`.
* Then, create a Run/Debug configuration.
    * On the top right of your screen, click `Edit Configurations...`.
    * Create a new "Application" configuration, then select `the-5zig-mod.1.8.9.main` as the module.
    * Put `GradleStart` as the main class.
    * Finally, put `--tweakClass eu.the5zig.mod.asm.ClassTweaker` in the program arguments
    and `-D5zig.debug=true` in the VM options.