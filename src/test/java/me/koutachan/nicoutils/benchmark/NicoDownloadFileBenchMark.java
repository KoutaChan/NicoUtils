package me.koutachan.nicoutils.benchmark;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.NicoVideoInfo;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NicoDownloadFileBenchMark {

    private static File file = Paths.get("", "test.mp4").toFile();

    @Setup
    public void setup() {
        file.deleteOnExit();
    }

    @Benchmark
    public void run() {

        NicoVideoInfo info = NicoUtils.getVideoBuilder()
                .setHeartBeat(true)
                .setURL("https://www.nicovideo.jp/watch/sm9")
                .setVideoType(VideoType.HTTP)
                .create();

        info.syncDownload(file);
        info.stopHeartBeat();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NicoDownloadFileBenchMark.class.getSimpleName())
                .forks(1)
                .warmupTime(new TimeValue(5, TimeUnit.SECONDS))
                .build();

        new Runner(opt).run();
    }
}
