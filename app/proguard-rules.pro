# Add project specific ProGuard / R8 rules here.
# https://developer.android.com/topic/performance/app-optimization
# AGP 9 R8 strict full mode does NOT keep default constructors unless listed.

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

# ObjectBox entities & generated model (constructors required)
-keep class io.objectbox.** { *; }
-dontwarn io.objectbox.**
-keep @io.objectbox.annotation.Entity class * {
    <init>(...);
    *;
}
-keep class com.jorotayo.algorubickrevamped.data.MyObjectBox { *; }
-keepclassmembers class * {
    @io.objectbox.annotation.Id <fields>;
}

# Gson / reflection
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeToken { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data models
-keep class com.jorotayo.algorubickrevamped.data.** {
    <init>(...);
    *;
}

# Kotlin
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# App Startup / any residual WorkManager+Room (constructors matter under AGP 9)
-keep class androidx.startup.** { <init>(...); *; }
-keep class androidx.work.** { <init>(...); *; }
-keep class * extends androidx.room.RoomDatabase {
    <init>();
    androidx.room.InvalidationTracker createInvalidationTracker();
    void clearAllTables();
}

# Image picker / Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
    *;
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
