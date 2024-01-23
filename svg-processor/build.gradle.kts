plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

application {
    mainClass = "app.lawnchair.lawnicons.helper.ApplicationKt"
}

dependencies {
    implementation("com.android.tools:sdk-common:31.2.2")
    implementation("org.dom4j:dom4j:2.1.4")
    implementation("commons-io:commons-io:2.15.1")
}
