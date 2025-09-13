package com.example.demo.service;



import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class VideoCompressorService {

    public String compressVideo(String inputPath, String outputPath) {
        // 🔹 FFmpeg ka exact path
        String ffmpegPath = "C:/Users/Madhuri/Downloads/ffmpeg-2025-09-08-git-45db6945e9-essentials_build/ffmpeg-2025-09-08-git-45db6945e9-essentials_build/bin/ffmpeg.exe";

        // Input file check
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.out.println("❌ Input file does not exist: " + inputPath);
            return null;
        }

        // Output directory check
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // ✅ Optimized FFmpeg command (good quality + smaller size + faster)
        String[] command = {
                ffmpegPath,
                "-y",
                "-i", inputPath,
                "-c:v", "libx264",
                "-crf", "34",
                "-preset", "fast",
                "-threads", "4",   // multi-threading
                "-c:a", "aac",
                "-b:a", "128k",
                "-vf", "scale=1280:720",
                outputPath
        };

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // FFmpeg console output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ Video compressed successfully: " + outputPath);
                return outputPath;
            } else {
                System.out.println("❌ Compression failed. Exit code: " + exitCode);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
