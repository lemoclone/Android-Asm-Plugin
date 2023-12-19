import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

open class InstrumentExceptionClassVisitor(
    nextVisitor: ClassVisitor,
    private val className: String
) : ClassVisitor(
    Opcodes.ASM5, nextVisitor
) {
    override fun visitMethod(
        access: Int, name: String?, descriptor: String?, signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val newMethodVisitor =
            object : AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, descriptor) {
                private val labelStart = Label()
                private val labelEnd = Label()
                private val labelTarget = Label()

                @Override
                override fun onMethodEnter() {
                    mv.visitLabel(labelStart)
                    mv.visitTryCatchBlock(labelStart, labelEnd, labelTarget, "java/lang/Exception")
                }

                override fun visitMaxs(maxStack: Int, maxLocals: Int) {
                    mv.visitLabel(labelEnd)
                    mv.visitLabel(labelTarget)
                    val local1 = newLocal(Type.getType("Ljava/lang/Exception"))
                    mv.visitVarInsn(Opcodes.ASTORE, local1)
                    mv.visitVarInsn(Opcodes.ALOAD, local1)
//                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false)
                    mv.visitMethodInsn(
                        INVOKESTATIC,
                        "com/example/gradletransformactiondemo/ExceptionHandler",
                        "logException",
                        "(Ljava/lang/Throwable;)V",
                        false
                    )

                    mv.visitInsn(getReturnCode(descriptor = descriptor))
                    super.visitMaxs(maxStack, maxLocals)
                }

                private fun getReturnCode(descriptor: String?): Int {
                    return when (descriptor!!.subSequence(
                        descriptor.indexOf(")") + 1,
                        descriptor.length
                    )) {
                        "V" -> Opcodes.RETURN
                        "I", "Z", "B", "C", "S" -> {
                            mv.visitInsn(Opcodes.ICONST_0)
                            Opcodes.IRETURN
                        }

                        "D" -> {
                            mv.visitInsn(Opcodes.DCONST_0)
                            Opcodes.DRETURN
                        }

                        "J" -> {
                            mv.visitInsn(Opcodes.LCONST_0)
                            Opcodes.LRETURN
                        }

                        "F" -> {
                            mv.visitInsn(Opcodes.FCONST_0)
                            Opcodes.FRETURN
                        }

                        else -> {
                            mv.visitInsn(Opcodes.ACONST_NULL)
                            Opcodes.ARETURN
                        }
                    }
                }
            }
        return newMethodVisitor
    }
}