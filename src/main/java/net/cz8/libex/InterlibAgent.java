package net.cz8.libex;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class InterlibAgent {
    private static final Logger logger = Logger.getLogger(InterlibAgent.class.getName());

    public static void premain(String agentArgs, Instrumentation inst) {
        logger.info("InterlibAgent: agent working...");
        inst.addTransformer(new InterlibTransformer(), true);
    }

    static class InterlibTransformer implements ClassFileTransformer {

        private static final Logger logger = Logger.getLogger(InterlibTransformer.class.getName());


        @Override
        public byte[] transform(ClassLoader loader, String className, Class <?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer ) throws IllegalClassFormatException {

            if ("com/libwan/utils/validUser".equals(className)) {
                System.out.println("Agent: load class -> " + className);
                logger.info("Agent: load class -> " + className);
                try {
                    final ClassPool classPool = ClassPool.getDefault();
                    final CtClass clazz = classPool.get("com.libwan.utils.validUser");
                    CtMethod newValidMethod = clazz.getDeclaredMethod("valid");
                    String methodBody = "{"
                            + " code = 0; return 0; } ";
                    newValidMethod.setBody(methodBody);

                    byte[] byteCode = clazz.toBytecode();
                    clazz.detach();

                    return byteCode;
                } catch( Exception e) {
                    e.printStackTrace();
                }

            }
            return classfileBuffer;
        }
    }

}
