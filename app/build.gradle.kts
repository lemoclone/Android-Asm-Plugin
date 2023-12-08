import com.android.build.gradle.options.SyncOptions
import org.jetbrains.kotlin.gradle.internal.kapt.incremental.StructureTransformAction
import java.util.zip.*
import java.io.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("phoenix-plugin")
}

apply<ExamplePlugin>()

android {
    namespace = "com.example.gradletransformactiondemo"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.gradletransformactiondemo"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.google.guava:guava:27.1-jre")
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
}

val artifactType = Attribute.of("artifactType", String::class.java)

abstract class MyTransform : TransformAction<TransformParameters.None> {
//    @get:InputFile @get:Classpath
//    abstract val inputArtifact: Provider<FileSystemLocation>

    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

//    @get:InputArtifactDependencies
//    abstract val dependencies: FileCollection

    override fun transform(transformOutputs: TransformOutputs) {

        val tag = "MyTransform"
        val inputArtifactFile = inputArtifact.get().asFile
        println("${tag}: input=${inputArtifactFile.path}")
        val isJarFile =
            inputArtifactFile.isFile && inputArtifactFile.extension == com.android.SdkConstants.EXT_JAR
        if (isJarFile) {
            val outputFile = transformOutputs.file("instrumented_${inputArtifactFile.name}")

//            println("${tag}: ${outputFile.path}")

            ZipOutputStream(BufferedOutputStream(FileOutputStream(outputFile)))
                .use { instrumentedJar ->
                ZipFile(inputArtifactFile).use { zip ->
                    println("$tag: zipname=${zip.name}, zipsize=${zip.size()}")
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
//                        println("$tag: zip entries = ${entries.toString()}")
                        val entry = entries.nextElement()
                        val entryName = entry.name


                        val data = zip.getInputStream(entry)
                        val nextEntry = ZipEntry(entryName)
                        if (entryName.contains("DispatchQueue")) {
                            println("$tag: entryName=${entryName}, data=${data}")
                        }
                        // Any negative time value sets ZipEntry's xdostime to DOSTIME_BEFORE_1980
                        // constant.
                        nextEntry.time = -1L
                        instrumentedJar.putNextEntry(nextEntry)
                        instrumentedJar.write(data.readBytes())
                        instrumentedJar.closeEntry()
                    }
                }
            }
        } else {


            val outputFile = transformOutputs.file("instrumented_${inputArtifactFile.name}")
//            println("${tag}: dir=${outputFile.path}")
        }
    }
}

abstract class MyTransform2 : StructureTransformAction(){
    override fun transform(outputs: TransformOutputs) {
        super.transform(outputs)
        val tag2 = "MyTransform2"


        println("${tag2} ")
    }
}


abstract class DexingWithClasspathTransform2 : com.android.build.gradle.internal.dependency.BaseDexingTransform<com.android.build.gradle.internal.dependency.BaseDexingTransform.Parameters>() {

    // Use @CompileClasspath instead of @Classpath because non-ABI changes on the classpath do not
    // impact dexing/desugaring of the artifact.

    @get:CompileClasspath
    @get:InputArtifactDependencies
    abstract val classpath: FileCollection

    override fun computeClasspathFiles() = classpath.files.toList()

    override fun transform(outputs: TransformOutputs) {
        super.transform(outputs)
        val classpath = computeClasspathFiles()
        println("DexingWithClasspathTransform2 ${classpath}")
    }
}

dependencies{

//    val t1 = com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.JVM_.type
    val t2= com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.DEX.type

    val bootClasspath = project.files()
    registerTransform(DexingWithClasspathTransform2::class) {
            parameters.projectName.set("Demo")
            parameters.minSdkVersion.set(23)
            parameters.debuggable.set(true)
            parameters.enableDesugaring.set(true)
            // bootclasspath is required by d8 to do API conversion for library desugaring
//            if (needsClasspath || enableCoreLibraryDesugaring) {
                parameters.bootClasspath.from(bootClasspath)
//            }
            parameters.errorFormat.set(SyncOptions.ErrorFormatMode.HUMAN_READABLE)
//            if (enableCoreLibraryDesugaring) {
//                parameters.libConfiguration.set(libConfiguration)
//            }
            parameters.enableGlobalSynthetics.set(true)
            parameters.enableApiModeling.set(true)
//        val type = com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.MANIFEST.type
//        from.attribute(artifactType, t1)
//        to.attribute(artifactType, t2)
        from.attribute(artifactType, "jar")
        to.attribute(artifactType, "android-platform-attr")
    }
}

//val artifactType = Attribute.of("artifactType", String::class.java)
//
//abstract class MyTransform : TransformAction<TransformParameters.None> {
////    @get:InputArtifact
////    abstract val inputArtifact: Provider<FileSystemLocation>
////
////    @get:InputArtifactDependencies
////    abstract val dependencies: FileCollection
//
//    @get:InputArtifact
//    @get:Classpath
//    abstract val inputArtifact: Provider<FileSystemLocation>
//
//    override fun transform(transformOutputs: TransformOutputs) {
//        val tag = "MyTransform"
//        val input = inputArtifact.get().asFile
//        println("$tag name=${input.name}")
//         if (input.isDirectory) {
//            val entryData =
//                org.jetbrains.kotlin.gradle.internal.kapt.incremental.ClasspathEntryData()
//
//            input.walk().filter { it.extension == "class"
//                        && !it.relativeTo(input).toString().toLowerCase().startsWith("meta-inf")
//            }.forEach {
//                val internalName = it.relativeTo(input).invariantSeparatorsPath.dropLast(".class".length)
//                println("$tag internalName = $internalName")
//            }
//        }
//    }
//}
//
//
//
//dependencies{
//    registerTransform(MyTransform::class) {
////        val type = com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.MANIFEST.type
////        from.attribute(artifactType, "jar")
////        from.attribute(artifactType, com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType.DEX.type)
//        from.attribute(artifactType, "android-dex")
//        to.attribute(artifactType, "java-classes-directory")
//    }
//

//abstract class FooClassVisitorFactory :
//    com.android.build.api.instrumentation.AsmClassVisitorFactory<InstrumentationParameters.None> {
//    override fun isInstrumentable(classData: com.android.build.api.instrumentation.ClassData): Boolean {
//        return classData.className.startsWith("com.example")
//    }
//
//    override fun createClassVisitor(
//        classContext: com.android.build.api.instrumentation.ClassContext, nextClassVisitor: org.objectweb.asm.ClassVisitor
//    ): org.objectweb.asm.ClassVisitor {
//        return org.objectweb.asm.util.TraceClassVisitor(nextClassVisitor, PrintWriter(System.out))
//    }
//}
//
//abstract class PhoenixPlugin : Plugin<Project> {
//    override fun apply(project: Project) {
//        androidComponents.onVariants { variant ->
//            variant.instrumentation.transformClassesWith(
//                FooClassVisitorFactory::class.java, com.android.build.api.instrumentation.InstrumentationScope.ALL
//            ) {}
//            variant.instrumentation.setAsmFramesComputationMode(com.android.build.api.instrumentation.FramesComputationMode.COPY_FRAMES)
//        }
//
//    }
//}
