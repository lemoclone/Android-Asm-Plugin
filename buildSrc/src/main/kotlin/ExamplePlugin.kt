import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.objectweb.asm.ClassVisitor

abstract class ExamplePlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(ExampleClassVisitorFactory::class.java,
                InstrumentationScope.PROJECT) {
                it.writeToStdout.set(true)
            }
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }

    interface ExampleParams : InstrumentationParameters {
        @get:Input
        val writeToStdout: Property<Boolean>
    }

    abstract class ExampleClassVisitorFactory :
        AsmClassVisitorFactory<ExampleParams> {

        override fun createClassVisitor(
            classContext: ClassContext,
            nextClassVisitor: ClassVisitor
        ): ClassVisitor {
//            return TimeCostClassVisitor(nextClassVisitor, classContext.currentClassData.className)
            return InstrumentExceptionClassVisitor(nextClassVisitor, classContext.currentClassData.className)
//            return if (parameters.get().writeToStdout.get()) {
//                TraceClassVisitor(nextClassVisitor, PrintWriter(System.out))
//            } else {
//                TraceClassVisitor(nextClassVisitor, PrintWriter(File("trace_out")))
//            }
        }

        override fun isInstrumentable(classData: ClassData): Boolean {
            return classData.className.startsWith("com.example")
        }
    }
}