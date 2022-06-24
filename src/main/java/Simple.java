import java.io.*;


import ij.ImageJ;
import jep.Interpreter;
import jep.SharedInterpreter;
import java.lang.Math;

interface Square {
    int calculate(int x);
}

public class Simple {

    static String readFileContent(String path) throws Exception {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String content = new String(data);
        return content;
    }

    public static void main(String[] args) throws Exception {
        final String dir = System.getProperty("user.dir");
        System.out.println("current dir = " + dir);
        // Run the script
        try (Interpreter interp = new SharedInterpreter()) {
            String code = readFileContent(dir + "/src/run.py");
            interp.exec("import asyncio");
            interp.exec("loop = asyncio.new_event_loop()");
            // Object loop = interp.getValue("loop");
            Object running = interp.invoke("loop.is_running");
            System.out.println("loop is running: " + running);
            interp.exec(code);
            interp.exec("print('Serving...')");
            // interp.exec("loop.call_soon_threadsafe(loop.create_task, timer_print())");
            
            // present the object to python
            Square s = (int x) -> x * x;
            interp.set("square", s);
            // register println as a println service
            interp.exec("loop.call_soon_threadsafe(loop.create_task, register_function('println', square.calculate, 12))");
        } catch (Exception e) {
            System.err.println(e);
        }
        // ImageJ ij = new ImageJ();
        // ij.setVisible(true);
        
        // Wait forever
        while (true) {
            Thread.sleep(1000);
        }
    }
}