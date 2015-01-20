# cultivationProject

A turn-based strategy game with potatoes!

## To make this work in Eclipse

1. Get Gradle from [here](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29#setting-up-eclipse)
2. In Eclipse, choose Import -> Git -> Project from Git -> Clone URI. Then put in link.
3. Choose either branch or both
4. There will be a page that asks to import as -> Import as existing projects
5. Next all the way
6. Once importing is done, select all the projects and right click -> Gradle -> Refresh All
7. Done

## Using Git with Eclipse

 - Before working, trying pulling first (right click, Team -> Pull...)
 - For switching branch, Team -> Switch To -> Other -> Remote Tracking and select branch -> Checkout
 - For pushing: Do changes, Team -> Commit, type a message and commit, then Team -> Push to Upstream
 - If Eclipse is too hard to use, use terminal (I find it a lot easier)

## Running the game

1. Run the server (in core, network package, run `Server.java` as Java application)
2. Run the `DesktopLauncher.java` (in cultivation-desktop)

## Using the TexturePacker

1. In cultivation-desktop, find `MyPacker.java`
2. Specify the paths: `TexturePacker.process("input", "output", "packed")`

This will pack everything (every pictures) in the `input` directory to `output` directory under the name `packed`.
The output will be 2 or more files: `packed.atlas` and `packed.png` (or `packed2.png`, `packed3.png`... if pictures don't fit in one file).

Read up how to use `TextureAtlas` [here](https://github.com/libgdx/libgdx/wiki/Texture-packer#textureatlas)

Read up on `NinePatch` [here](http://radleymarx.com/blog/simple-guide-to-9-patch/) and [here](https://github.com/libgdx/libgdx/wiki/Ninepatches)

## More to come...
