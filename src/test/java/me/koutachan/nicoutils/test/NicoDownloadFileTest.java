package me.koutachan.nicoutils.test;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.NicoVideoInfo;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class NicoDownloadFileTest {

    private static File file = Paths.get("", "test.mp4").toFile();

    @Benchmark
    public void run() {
        NicoVideoInfo info = NicoUtils.getVideoBuilder()
                .setHeartBeat(true)
                .setURL("https://www.nicovideo.jp/watch/sm9")
                .setVideoType(VideoType.HTTP)
                .create();

        info.syncDownload(file);
        info.stopHeartBeat();

        file.deleteOnExit();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NicoDownloadFileTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
