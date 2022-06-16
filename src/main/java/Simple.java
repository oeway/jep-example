import org.bytedeco.javacpp.*;
import org.bytedeco.cpython.*;
import static org.bytedeco.cpython.global.python.*;
import java.io.*;
import java.util.*;

public class Simple {
    static String readFileContent(String path) throws Exception{
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String content = new String(data);
        return content;
    }

    public static void main(String[] args) throws Exception {
        Pointer program = Py_DecodeLocale(Simple.class.getSimpleName(), null);
        if (program == null) {
            System.err.println("Fatal error: cannot decode class name");
            System.exit(1);
        }
        Py_SetProgramName(program);  /* optional but recommended */
        Py_Initialize(cachePackages());

        // Extract to JavaCPP's cache CPython and obtain the path to the executable file
        String python = Loader.load(org.bytedeco.cpython.python.class);
        
        // Run hello world example
        PyRun_SimpleString("print('Hello World')\n");

        final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
        
        // Install requirements
        String req = readFileContent(dir + "/src/requirements.txt");
        String[] requirements = req.split("\n");

        // convert requirements to an List of Strings
        List<String> reqList = new ArrayList<String>();
        reqList.add(python);
        // add "-m", "pip", "install" to the list
        reqList.add("-m");
        reqList.add("pip");
        reqList.add("install");
        // add the requirements to the list
        for (String r : requirements) {
            reqList.add(r);
        }
        new ProcessBuilder(reqList).inheritIO().start().waitFor();
        
        // Run the script
        String code = readFileContent(dir + "/src/run.py");
        PyRun_SimpleString(code);

        if (Py_FinalizeEx() < 0) {
            System.exit(120);
        }
        PyMem_RawFree(program);
        System.exit(0);
    }
}