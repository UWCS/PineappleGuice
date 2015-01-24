package uk.co.uwcs.pineappleguice.YoutubeDownloader;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * Created by rayhaan on 24/01/15.
 */
public class DownloadService implements Callable<String> {

    @Inject
    @Named("downloaderPath")
    private String downloaderPath;

    private String URL;

    @Inject
    public DownloadService(String downloaderPath) {
        this.downloaderPath = downloaderPath;
    }

    public void setURL(String url) {
        this.URL = url;
    }

    @Override
    public String call() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                downloaderPath, "--get-filename", "-o%(title)s.%(ext)s", "--restrict-filenames", URL);
        Process p = pb.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        ProcessBuilder downloader = new ProcessBuilder(downloaderPath, "-o%(title)s.%(ext)s", "--restrict-filenames", URL);
        Process dl = downloader.start();
        dl.waitFor();

        return sb.toString();
    }
}
