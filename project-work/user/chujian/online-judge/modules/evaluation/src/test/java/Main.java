import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    private static final String CLIENT_DIR = "D:\\code\\test\\client\\";
    private static final String COMPILE_DIR = "D:\\code\\temp\\compile\\";
    private static final String RUN_DIR = "D:\\code\\temp\\run\\";
    private static final int BUFFER_SIZE = 1024;

    private static final BlockingQueue<String> taskQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        // 添加任务到队列
        taskQueue.add("main.cpp");

        // 从队列中取出任务并执行
        while (!taskQueue.isEmpty()) {
            String fileName = taskQueue.poll();
            try {
                // 复制文件到编译目录
                copyFile(CLIENT_DIR + fileName, COMPILE_DIR + fileName);
                String compileResult = compileCpp(fileName);
                System.out.println("Compile Result: " + compileResult);

                // 运行程序
                String output = runCpp(fileName, null);
                System.out.println("Output: " + output);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 清理文件
                deleteFile(CLIENT_DIR + fileName);
            }
        }
    }

    /**
     * 沙盒策略
     * @param args
     */
    public static void sandboxExecutor(String[] args) {
        // 添加任务到队列
        taskQueue.add("main.cpp");

        // 从队列中取出任务并执行
        while (!taskQueue.isEmpty()) {
            String fileName = taskQueue.poll();
            try {
                // 使用 Docker 容器进行沙盒化
                executeTaskInSandbox(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 清理文件
                deleteFile(CLIENT_DIR + fileName);
            }
        }
    }

    private static void executeTaskInSandbox(String fileName) throws IOException {
        // 构建 Docker 命令
        String dockerCommand = "docker run --rm ";
        dockerCommand += "--read-only ";
        dockerCommand += "--network none ";
        dockerCommand += "--user nobody "; // 使用非特权用户
        dockerCommand += "--cpus 0.5 "; // 限制 CPU 资源
        dockerCommand += "--memory 512m "; // 限制内存资源
        dockerCommand += "-v " + CLIENT_DIR + ":" + CLIENT_DIR + " ";
        dockerCommand += "-v " + COMPILE_DIR + ":" + COMPILE_DIR + " ";
        dockerCommand += "sandbox-image "; // 替换为你的沙盒化 Docker 镜像名

        // 添加任务文件名作为参数
        dockerCommand += "java -jar sandbox.jar " + fileName;

        // 执行 Docker 命令
        Process process = Runtime.getRuntime().exec(dockerCommand);

        // 处理容器的输出
        // 例如，你可以读取容器的输出流并打印到控制台
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String line;
         while ((line = reader.readLine()) != null) {
             System.out.println(line);
         }
    }


    private static void copyFile(String sourcePath, String destinationPath) throws IOException {
        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);

        // 检查源文件是否存在
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("Source file does not exist.");
        }

        // 检查目标目录是否存在，如果不存在，则创建目录
        File destinationDir = destinationFile.getParentFile();
        if (destinationDir != null && !destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void createDirectories(String filePath){
        try {
            Path path = Paths.get(filePath).getParent();
            if (path != null) {
                Files.createDirectories(path); // 创建所有不存在的文件夹路径
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully: " + fileName);
            } else {
                System.out.println("Failed to delete the file: " + fileName);
            }
        } else {
            System.out.println("File does not exist: " + fileName);
        }
    }

    /**
     * 去后缀
     * @param filePath
     * @return
     */
    private static String removeFileExtension(String filePath){
        return filePath.substring(0, filePath.lastIndexOf("."));
    }


    private static String compileCpp(String fileName) {
        String compileFile = COMPILE_DIR + fileName;
        String runFileDir = RUN_DIR + removeFileExtension(fileName);
        createDirectories(compileFile);
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "g++", "-o", runFileDir, compileFile);
            builder.directory(new File(COMPILE_DIR));
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            if (process.waitFor() != 0) {
                return output.toString();
            }
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }finally {
            deleteFile(compileFile);
        }
    }

    private static String runCpp(String fileName, String input) {
        String runFile = RUN_DIR + removeFileExtension(fileName) + ".exe";
        createDirectories(runFile);
        try {
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", runFile);
            builder.directory(new File(RUN_DIR));
            Process process = builder.start();

            // 如果需要输入数据，将其写入到进程的输入流
            if(input != null && input.isEmpty()){
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                writer.write(input);
                writer.flush();
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }finally {
            deleteFile(runFile);
        }
    }

}
