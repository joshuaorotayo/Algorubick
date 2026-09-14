# Add project specific ProGuard / R8 rules here.
# https://developer.android.com/topic/performance/app-optimization

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

# ObjectBox entities & generated model
-keep class io.objectbox.** { *; }
-dontwarn io.objectbox.**
-keep @io.objectbox.annotation.Entity class * { *; }
-keepclassmembers class * {
    @io.objectbox.annotation.Id <fields>;
}

# Gson / reflection DTOs used with JSON defaults
-keepattributes Signature
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data models that may be serialized or reflected
-keep class com.jorotayo.algorubickrevamped.data.** { *; }

# Compose / Kotlin
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }

# Image picker / Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
