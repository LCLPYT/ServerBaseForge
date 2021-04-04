# ServerBaseForge
Forge version ServerBase for Spigot. Used to help creating survival servers.

This mod depends on <a href="https://github.com/LCLPYT/Core">Core</a> and it's dependencies.

<hr>

### Using ServerBaseForge in your project
Of course, you are welcome to use ServerBaseForge in your own projects.
<br>
To do this, just add this repository to your build.gradle:
```groovy
repositories {
    maven { url 'https://repo.lclpnet.work/repository/internal' }
}
```
Now add the following dependencies:
```groovy
dependencies {
    implementation fg.deobf("work.lclpnet.mods:CoreBase:VERSION_COREBASE") // required by ServerBaseForge
    implementation fg.deobf("work.lclpnet.mods:Core:VERSION_CORE") // required by ServerBaseForge
    implementation fg.deobf("work.lclpnet.mods:ServerBaseForge:VERSION")
}
```
You need to replace `VERSION` with the version you want to use.
To see all versions available, you can [check the repository](https://repo.lclpnet.work/#artifact~internal/work.lclpnet.mods/ServerBaseForge).<br>
<br>
Please note that `VERSION_COREBASE` should match the version required of your target ServerBaseForge build. To find the correct version, please check the [gradle.properties file](https://github.com/LCLPYT/ServerBaseForge/blob/master/gradle.properties), in which `corebase_version` specifies the required version. Just keep in mind to find the correct commit if you are not using the latest version.<br>
Do the same with `VERSION_CORE` but this time, look for the `core_version` key.
