# Whizzz — release shrinking (additions on top of defaults).

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Koin
-dontwarn org.koin.core.error.**
