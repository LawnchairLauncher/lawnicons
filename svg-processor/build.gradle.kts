plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

application {
    mainClass.set("app.lawnchair.lawnicons.helper.ApplicationKt")
}

dependencies {
    implementation("com.android.tools:sdk-common:31.0.0")
    implementation("org.dom4j:dom4j:2.1.4")
    implementation("commons-io:commons-io:2.11.0")
}
