# These rules could be removed after Retrofit 2.10.0 released.
# https://github.com/square/retrofit/blob/ef8d867ffb34b419355a323e11ba89db1904f8c2/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro#L38-L45
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowoptimization,allowshrinking,allowobfuscation class kotlin.coroutines.Continuation
