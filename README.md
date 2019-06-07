# The 5zig Mod
After more than six years it's time to say goodbye! I've had a lot fun integrating my own features into Minecraft and creating a mod that reached so many more people than I have ever anticipated - but now I'm going to move on. Thank you for an amazing time.

Feel free to modify, update and add new features (I heard you like free capes :P) to the mod.

#### Here are a few quick start guides for developing the mod:
- The mod is divided up into several projects: *'The 5zig Mod'* is the core module and contains all (high-level) UI, networking, data and logic code. *'Mod Utils'* provides all abstract UI and Minecraft class components while *'Utils'* provides some more general utility functions. *'Minecraft Utils'* contains a single project for each supported Minecraft version and provides implementations for *'Mod Utils'*. The reason for this high level of abstraction is the fact that *'Minecraft Utils'* works with obfuscated class names of Minecraft, which change with every release (also you have to be in the default package in order to be able to access other classes in the default package).
- On order to support a new Minecraft version *cough\* *1.14* *cough\*, you need to create a new 1.14 project in the *'Minecraft Utils'* project, import all necessary libraries and update the names of all Minecraft classes. Tools like JD-Gui help you understand the Minecraft source code.
- You can run Minecraft and debug the mod from within your IDE. It is a little tricky, but possible! Make sure you linked all lwjgl natives and run *eu.the5zig.mod.util.Start*.
